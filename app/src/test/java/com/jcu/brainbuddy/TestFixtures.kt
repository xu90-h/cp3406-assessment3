package com.jcu.brainbuddy

import com.jcu.brainbuddy.data.repository.QuestionBatch
import com.jcu.brainbuddy.data.repository.QuestionRepository
import com.jcu.brainbuddy.data.repository.ResultRepository
import com.jcu.brainbuddy.data.repository.SettingsRepository
import com.jcu.brainbuddy.domain.AppSettings
import com.jcu.brainbuddy.domain.Difficulty
import com.jcu.brainbuddy.domain.Question
import com.jcu.brainbuddy.domain.QuizCategory
import com.jcu.brainbuddy.domain.QuizResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

fun question(text: String = "Q?", correctIndex: Int = 0) =
    Question(text, listOf("right", "wrong 1", "wrong 2", "wrong 3"), correctIndex)

fun result(
    category: QuizCategory = QuizCategory.MATH,
    correct: Int = 3,
    total: Int = 5,
    at: Long = 0L,
    difficulty: Difficulty = Difficulty.EASY
) = QuizResult(
    category = category,
    difficulty = difficulty,
    totalQuestions = total,
    correctAnswers = correct,
    finishedAtMillis = at
)

class FakeQuestionRepository(
    var batch: QuestionBatch = QuestionBatch(listOf(question("one"), question("two")), isOnline = true)
) : QuestionRepository {
    var lastCategory: QuizCategory? = null
    var lastDifficulty: Difficulty? = null
    var lastCount: Int? = null

    override suspend fun getQuestions(category: QuizCategory, difficulty: Difficulty, count: Int): QuestionBatch {
        lastCategory = category
        lastDifficulty = difficulty
        lastCount = count
        return batch
    }
}

class FakeResultRepository : ResultRepository {
    private val results = MutableStateFlow<List<QuizResult>>(emptyList())

    override fun observeResults(): Flow<List<QuizResult>> = results
    override suspend fun save(result: QuizResult) {
        results.value = results.value + result
    }

    override suspend fun clearAll() {
        results.value = emptyList()
    }
}

class FakeSettingsRepository(initial: AppSettings = AppSettings()) : SettingsRepository {
    private val state = MutableStateFlow(initial)

    override val settings: Flow<AppSettings> = state

    override suspend fun setCategory(category: QuizCategory) {
        state.value = state.value.copy(category = category)
    }

    override suspend fun setDifficulty(difficulty: Difficulty) {
        state.value = state.value.copy(difficulty = difficulty)
    }

    override suspend fun setQuestionCount(count: Int) {
        state.value = state.value.copy(questionCount = count)
    }

    override suspend fun setLargeText(enabled: Boolean) {
        state.value = state.value.copy(largeText = enabled)
    }

    override suspend fun setHaptics(enabled: Boolean) {
        state.value = state.value.copy(hapticsEnabled = enabled)
    }
}
