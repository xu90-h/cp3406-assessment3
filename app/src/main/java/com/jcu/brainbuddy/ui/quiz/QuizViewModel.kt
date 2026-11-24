package com.jcu.brainbuddy.ui.quiz

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jcu.brainbuddy.data.repository.QuestionRepository
import com.jcu.brainbuddy.data.repository.ResultRepository
import com.jcu.brainbuddy.data.repository.SettingsRepository
import com.jcu.brainbuddy.domain.Difficulty
import com.jcu.brainbuddy.domain.QuizCategory
import com.jcu.brainbuddy.domain.QuizProgress
import com.jcu.brainbuddy.domain.QuizResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizUiState(
    val isLoading: Boolean = true,
    val category: QuizCategory = QuizCategory.MATH,
    val difficulty: Difficulty = Difficulty.EASY,
    val progress: QuizProgress? = null,
    val isOnline: Boolean = true,
    val hapticsEnabled: Boolean = true,
    /** Set once the quiz is over and its result has been saved; the screen then navigates away. */
    val completedResult: QuizResult? = null
)

@HiltViewModel
class QuizViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val questionRepository: QuestionRepository,
    private val resultRepository: ResultRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val categoryArg: QuizCategory? =
        savedStateHandle.get<String>(CATEGORY_ARG)?.let { name -> QuizCategory.entries.firstOrNull { it.name == name } }

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    init {
        loadQuiz()
    }

    fun loadQuiz() {
        _uiState.update { it.copy(isLoading = true, progress = null, completedResult = null) }
        viewModelScope.launch {
            val settings = settingsRepository.settings.first()
            val category = categoryArg ?: settings.category
            val batch = questionRepository.getQuestions(category, settings.difficulty, settings.questionCount)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    category = category,
                    difficulty = settings.difficulty,
                    progress = QuizProgress(batch.questions),
                    isOnline = batch.isOnline,
                    hapticsEnabled = settings.hapticsEnabled
                )
            }
        }
    }

    fun onAnswerSelected(index: Int) {
        _uiState.update { state -> state.progress?.let { state.copy(progress = it.select(index)) } ?: state }
    }

    fun onNext() {
        val state = _uiState.value
        val progress = state.progress ?: return
        val updated = progress.next()
        if (updated == progress) return

        _uiState.update { it.copy(progress = updated) }
        if (updated.isFinished) saveResult(state, updated)
    }

    private fun saveResult(state: QuizUiState, progress: QuizProgress) {
        val result = QuizResult(
            category = state.category,
            difficulty = state.difficulty,
            totalQuestions = progress.total,
            correctAnswers = progress.correctCount,
            finishedAtMillis = System.currentTimeMillis()
        )
        viewModelScope.launch {
            resultRepository.save(result)
            _uiState.update { it.copy(completedResult = result) }
        }
    }

    companion object {
        const val CATEGORY_ARG = "category"
    }
}
