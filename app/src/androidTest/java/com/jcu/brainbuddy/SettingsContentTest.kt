package com.jcu.brainbuddy

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.jcu.brainbuddy.domain.AppSettings
import com.jcu.brainbuddy.domain.Difficulty
import com.jcu.brainbuddy.domain.QuizCategory
import com.jcu.brainbuddy.ui.settings.SettingsContent
import com.jcu.brainbuddy.ui.theme.BrainBuddyTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SettingsContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    private var category: QuizCategory? = null
    private var difficulty: Difficulty? = null
    private var count: Int? = null
    private var largeText: Boolean? = null
    private var challenge: Boolean? = null
    private var cleared = false

    private fun show(settings: AppSettings = AppSettings()) {
        composeRule.setContent {
            BrainBuddyTheme {
                SettingsContent(
                    settings = settings,
                    onCategoryChange = { category = it },
                    onDifficultyChange = { difficulty = it },
                    onQuestionCountChange = { count = it },
                    onLargeTextChange = { largeText = it },
                    onHapticsChange = {},
                    onChallengeModeChange = { challenge = it },
                    onClearProgress = { cleared = true }
                )
            }
        }
    }

    @Test
    fun choosingTopicDifficultyAndCountReportsChanges() {
        show()

        composeRule.onNodeWithTag("chip_GEOGRAPHY").performScrollTo().performClick()
        composeRule.onNodeWithTag("difficulty_HARD").performClick()
        composeRule.onNodeWithTag("count_10").performClick()

        assertEquals(QuizCategory.GEOGRAPHY, category)
        assertEquals(Difficulty.HARD, difficulty)
        assertEquals(10, count)
    }

    @Test
    fun largeTextSwitchTogglesTheSetting() {
        show(AppSettings(largeText = false))

        composeRule.onNodeWithTag("large_text_switch").performScrollTo().performClick()

        assertEquals(true, largeText)
    }

    @Test
    fun challengeModeSwitchTogglesTheSetting() {
        show(AppSettings(challengeMode = false))

        composeRule.onNodeWithTag("challenge_switch").performScrollTo().performClick()

        assertEquals(true, challenge)
    }

    @Test
    fun deletingProgressNeedsConfirmation() {
        show()

        composeRule.onNodeWithTag("clear_progress_button").performScrollTo().performClick()
        composeRule.onNodeWithText("Delete all progress?").assertIsDisplayed()
        assertFalse(cleared)

        composeRule.onNodeWithText("Delete").performClick()
        assertTrue(cleared)
    }

    @Test
    fun cancellingTheDialogKeepsProgress() {
        show()

        composeRule.onNodeWithTag("clear_progress_button").performScrollTo().performClick()
        composeRule.onNodeWithText("Cancel").performClick()

        assertFalse(cleared)
    }
}
