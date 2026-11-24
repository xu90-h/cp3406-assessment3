package com.jcu.brainbuddy

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.jcu.brainbuddy.domain.AppSettings
import com.jcu.brainbuddy.domain.QuizCategory
import com.jcu.brainbuddy.domain.UserStats
import com.jcu.brainbuddy.ui.home.HomeContent
import com.jcu.brainbuddy.ui.home.HomeUiState
import com.jcu.brainbuddy.ui.theme.BrainBuddyTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class HomeContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun show(state: HomeUiState, onStart: (QuizCategory) -> Unit = {}) {
        composeRule.setContent {
            BrainBuddyTheme { HomeContent(state, onStart) }
        }
    }

    @Test
    fun startButtonUsesTheSavedTopic() {
        var started: QuizCategory? = null
        show(HomeUiState(AppSettings(category = QuizCategory.SCIENCE)), onStart = { started = it })

        composeRule.onNodeWithTag("start_quiz_button").assertTextContains("Science", substring = true)
        composeRule.onNodeWithTag("start_quiz_button").performClick()

        assertEquals(QuizCategory.SCIENCE, started)
    }

    @Test
    fun topicShortcutStartsThatTopic() {
        var started: QuizCategory? = null
        show(HomeUiState(), onStart = { started = it })

        composeRule.onNodeWithTag("category_ANIMALS").performClick()

        assertEquals(QuizCategory.ANIMALS, started)
    }

    @Test
    fun newPlayerSeesAnInvitationInsteadOfEmptyNumbers() {
        show(HomeUiState(stats = UserStats()))

        composeRule.onNodeWithTag("no_progress_text").assertIsDisplayed()
    }
}
