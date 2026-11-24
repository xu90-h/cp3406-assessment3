package com.jcu.brainbuddy.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.jcu.brainbuddy.domain.AppSettings
import com.jcu.brainbuddy.domain.Difficulty
import com.jcu.brainbuddy.domain.QuizCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    override val settings: Flow<AppSettings> = dataStore.data.map { prefs ->
        val defaults = AppSettings()
        AppSettings(
            category = QuizCategory.entries.firstOrNull { it.name == prefs[CATEGORY] } ?: defaults.category,
            difficulty = Difficulty.entries.firstOrNull { it.name == prefs[DIFFICULTY] } ?: defaults.difficulty,
            questionCount = prefs[QUESTION_COUNT]
                ?.takeIf { it in AppSettings.QUESTION_COUNT_OPTIONS }
                ?: defaults.questionCount,
            largeText = prefs[LARGE_TEXT] ?: defaults.largeText,
            hapticsEnabled = prefs[HAPTICS] ?: defaults.hapticsEnabled,
            challengeMode = prefs[CHALLENGE_MODE] ?: defaults.challengeMode
        )
    }

    override suspend fun setCategory(category: QuizCategory) {
        dataStore.edit { it[CATEGORY] = category.name }
    }

    override suspend fun setDifficulty(difficulty: Difficulty) {
        dataStore.edit { it[DIFFICULTY] = difficulty.name }
    }

    override suspend fun setQuestionCount(count: Int) {
        dataStore.edit { it[QUESTION_COUNT] = count }
    }

    override suspend fun setLargeText(enabled: Boolean) {
        dataStore.edit { it[LARGE_TEXT] = enabled }
    }

    override suspend fun setHaptics(enabled: Boolean) {
        dataStore.edit { it[HAPTICS] = enabled }
    }

    override suspend fun setChallengeMode(enabled: Boolean) {
        dataStore.edit { it[CHALLENGE_MODE] = enabled }
    }

    private companion object {
        val CATEGORY = stringPreferencesKey("category")
        val DIFFICULTY = stringPreferencesKey("difficulty")
        val QUESTION_COUNT = intPreferencesKey("question_count")
        val LARGE_TEXT = booleanPreferencesKey("large_text")
        val HAPTICS = booleanPreferencesKey("haptics")
        val CHALLENGE_MODE = booleanPreferencesKey("challenge_mode")
    }
}
