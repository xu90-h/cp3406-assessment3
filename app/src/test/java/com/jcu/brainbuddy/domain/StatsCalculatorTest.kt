package com.jcu.brainbuddy.domain

import com.jcu.brainbuddy.result
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.ZoneOffset

class StatsCalculatorTest {

    private val utc = ZoneOffset.UTC
    private val day = 24 * 60 * 60 * 1000L

    @Test
    fun `no results gives empty stats`() {
        val stats = StatsCalculator.compute(emptyList(), utc)
        assertFalse(stats.hasData)
        assertEquals(0, stats.accuracyPercent)
        assertTrue(stats.recent.isEmpty())
    }

    @Test
    fun `totals and accuracy are summed over all results`() {
        val stats = StatsCalculator.compute(
            listOf(result(correct = 4, total = 5), result(correct = 1, total = 5)),
            utc
        )
        assertEquals(2, stats.totalQuizzes)
        assertEquals(10, stats.totalQuestions)
        assertEquals(5, stats.totalCorrect)
        assertEquals(50, stats.accuracyPercent)
    }

    @Test
    fun `best score is the highest single quiz`() {
        val stats = StatsCalculator.compute(
            listOf(result(correct = 2, total = 5), result(correct = 5, total = 5), result(correct = 3, total = 5)),
            utc
        )
        assertEquals(100, stats.bestScorePercent)
    }

    @Test
    fun `days practised counts distinct calendar days`() {
        val stats = StatsCalculator.compute(
            listOf(
                result(at = 1_000L),
                result(at = 2_000L),
                result(at = day + 1_000L),
                result(at = 5 * day)
            ),
            utc
        )
        assertEquals(3, stats.daysPracticed)
    }

    @Test
    fun `every category is reported even when unplayed`() {
        val stats = StatsCalculator.compute(
            listOf(result(category = QuizCategory.ANIMALS, correct = 3, total = 4)),
            utc
        )
        assertEquals(QuizCategory.entries.size, stats.perCategory.size)
        val animals = stats.perCategory.first { it.category == QuizCategory.ANIMALS }
        assertEquals(75, animals.accuracyPercent)
        val math = stats.perCategory.first { it.category == QuizCategory.MATH }
        assertEquals(0, math.questions)
        assertEquals(0, math.accuracyPercent)
    }

    @Test
    fun `recent list is newest first and limited`() {
        val results = (1..8).map { result(at = it * 1_000L) }
        val stats = StatsCalculator.compute(results, utc)
        assertEquals(StatsCalculator.RECENT_LIMIT, stats.recent.size)
        assertEquals(8_000L, stats.recent.first().finishedAtMillis)
        assertEquals(4_000L, stats.recent.last().finishedAtMillis)
    }

    @Test
    fun `quiz result percent handles zero questions`() {
        assertEquals(0, result(correct = 0, total = 0).percent)
        assertEquals(60, result(correct = 3, total = 5).percent)
    }
}
