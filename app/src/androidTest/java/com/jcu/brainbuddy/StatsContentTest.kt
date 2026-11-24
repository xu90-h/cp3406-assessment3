package com.jcu.brainbuddy

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.jcu.brainbuddy.domain.Difficulty
import com.jcu.brainbuddy.domain.QuizCategory
import com.jcu.brainbuddy.domain.QuizResult
import com.jcu.brainbuddy.domain.StatsCalculator
import com.jcu.brainbuddy.domain.UserStats
import com.jcu.brainbuddy.ui.stats.StatsContent
import com.jcu.brainbuddy.ui.theme.BrainBuddyTheme
import org.junit.Rule
import org.junit.Test

class StatsContentTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun emptyStateInvitesTheLearnerToPlay() {
        composeRule.setContent { BrainBuddyTheme { StatsContent(UserStats()) } }

        composeRule.onNodeWithTag("empty_stats_title").assertIsDisplayed()
    }

    @Test
    fun savedResultsAppearInTheSummary() {
        val stats = StatsCalculator.compute(
            listOf(
                QuizResult(
                    category = QuizCategory.SCIENCE,
                    difficulty = Difficulty.EASY,
                    totalQuestions = 5,
                    correctAnswers = 4,
                    finishedAtMillis = 1_700_000_000_000
                )
            )
        )
        composeRule.setContent { BrainBuddyTheme { StatsContent(stats) } }

        composeRule.onNodeWithText("Quizzes done").assertIsDisplayed()
        composeRule.onNodeWithText("4/5").assertIsDisplayed()
    }
}
