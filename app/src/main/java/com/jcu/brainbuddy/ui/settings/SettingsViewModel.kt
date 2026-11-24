package com.jcu.brainbuddy.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jcu.brainbuddy.data.repository.ResultRepository
import com.jcu.brainbuddy.data.repository.SettingsRepository
import com.jcu.brainbuddy.domain.AppSettings
import com.jcu.brainbuddy.domain.Difficulty
import com.jcu.brainbuddy.domain.QuizCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val resultRepository: ResultRepository
) : ViewModel() {

    val settings: StateFlow<AppSettings> = settingsRepository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppSettings())

    fun setCategory(category: QuizCategory) = launch { settingsRepository.setCategory(category) }

    fun setDifficulty(difficulty: Difficulty) = launch { settingsRepository.setDifficulty(difficulty) }

    fun setQuestionCount(count: Int) = launch { settingsRepository.setQuestionCount(count) }

    fun setLargeText(enabled: Boolean) = launch { settingsRepository.setLargeText(enabled) }

    fun setHaptics(enabled: Boolean) = launch { settingsRepository.setHaptics(enabled) }

    /** Lets the learner (or a parent) erase everything the app has stored about them. */
    fun clearProgress() = launch { resultRepository.clearAll() }

    private fun launch(block: suspend () -> Unit) {
        viewModelScope.launch { block() }
    }
}
