package com.jcu.brainbuddy.data.repository

import com.jcu.brainbuddy.domain.AppSettings
import com.jcu.brainbuddy.domain.Difficulty
import com.jcu.brainbuddy.domain.QuizCategory
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settings: Flow<AppSettings>
    suspend fun setCategory(category: QuizCategory)
    suspend fun setDifficulty(difficulty: Difficulty)
    suspend fun setQuestionCount(count: Int)
    suspend fun setLargeText(enabled: Boolean)
    suspend fun setHaptics(enabled: Boolean)
}
