package com.jcu.brainbuddy

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.jcu.brainbuddy.domain.Question
import com.jcu.brainbuddy.domain.QuizProgress
import com.jcu.brainbuddy.ui.quiz.QuizContent
import com.jcu.brainbuddy.ui.quiz.QuizUiState
import com.jcu.brainbuddy.ui.theme.BrainBuddyTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class QuizContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val questions = listOf(
        Question("What is 2 + 2?", listOf("3", "4", "5", "6"), correctIndex = 1),
        Question("What is 3 + 3?", listOf("6", "7", "8", "9"), correctIndex = 0)
    )

    private fun show(
        state: QuizUiState,
        onSelect: (Int) -> Unit = {},
        onNext: () -> Unit = {}
    ) {
        composeRule.setContent {
            BrainBuddyTheme {
                QuizContent(state, onSelect, onNext, onQuitRequested = {})
            }
        }
    }

    @Test
    fun showsQuestionAndAllOptions() {
        show(QuizUiState(isLoading = false, progress = QuizProgress(questions)))

        composeRule.onNodeWithText("Question 1 of 2").assertIsDisplayed()
        composeRule.onNodeWithTag("question_text").assertTextContains("What is 2 + 2?")
        (0..3).forEach { composeRule.onNodeWithTag("option_$it").assertIsDisplayed() }
        composeRule.onNodeWithTag("next_button").assertDoesNotExist()
    }

    @Test
    fun tappingAnOptionReportsItsIndex() {
        var selected = -1
        show(
            QuizUiState(isLoading = false, progress = QuizProgress(questions)),
            onSelect = { selected = it }
        )

        composeRule.onNodeWithTag("option_2").performClick()

        assertEquals(2, selected)
    }

    @Test
    fun correctAnswerShowsPositiveFeedbackAndNextButton() {
        val answered = QuizProgress(questions).select(1)
        show(QuizUiState(isLoading = false, progress = answered))

        composeRule.onNodeWithTag("feedback_text").assertTextContains("That's right", substring = true)
        composeRule.onNodeWithTag("next_button").assertIsDisplayed().assertTextContains("Next question")
        composeRule.onNodeWithTag("option_0").assertIsNotEnabled()
    }

    @Test
    fun wrongAnswerRevealsTheCorrectOne() {
        val answered = QuizProgress(questions).select(0)
        show(QuizUiState(isLoading = false, progress = answered))

        composeRule.onNodeWithTag("feedback_text").assertTextContains("The answer is 4", substring = true)
    }

    @Test
    fun lastQuestionOffersResults() {
        val last = QuizProgress(questions).select(0).next().select(0)
        show(QuizUiState(isLoading = false, progress = last))

        composeRule.onNodeWithTag("next_button").assertTextContains("See my results")
    }

    @Test
    fun offlineBannerOnlyShowsWhenOffline() {
        show(QuizUiState(isLoading = false, progress = QuizProgress(questions), isOnline = false))

        composeRule.onNodeWithTag("offline_banner").assertIsDisplayed()
    }

    @Test
    fun loadingStateShowsMessage() {
        show(QuizUiState(isLoading = true))

        composeRule.onNodeWithText("Getting your questions ready...").assertIsDisplayed()
    }
}
