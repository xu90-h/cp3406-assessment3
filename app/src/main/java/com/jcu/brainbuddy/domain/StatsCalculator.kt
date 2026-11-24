package com.jcu.brainbuddy.domain

import java.time.Instant
import java.time.ZoneId

data class CategoryStats(
    val category: QuizCategory,
    val quizzes: Int,
    val questions: Int,
    val correct: Int
) {
    val accuracyPercent: Int get() = if (questions == 0) 0 else correct * 100 / questions
}

data class UserStats(
    val totalQuizzes: Int = 0,
    val totalQuestions: Int = 0,
    val totalCorrect: Int = 0,
    val bestScorePercent: Int = 0,
    val daysPracticed: Int = 0,
    val perCategory: List<CategoryStats> = emptyList(),
    val recent: List<QuizResult> = emptyList()
) {
    val accuracyPercent: Int get() = if (totalQuestions == 0) 0 else totalCorrect * 100 / totalQuestions
    val hasData: Boolean get() = totalQuizzes > 0
}

object StatsCalculator {

    const val RECENT_LIMIT = 5

    fun compute(results: List<QuizResult>, zone: ZoneId = ZoneId.systemDefault()): UserStats {
        if (results.isEmpty()) return UserStats()

        val perCategory = QuizCategory.entries.map { category ->
            val forCategory = results.filter { it.category == category }
            CategoryStats(
                category = category,
                quizzes = forCategory.size,
                questions = forCategory.sumOf { it.totalQuestions },
                correct = forCategory.sumOf { it.correctAnswers }
            )
        }

        val days = results
            .map { Instant.ofEpochMilli(it.finishedAtMillis).atZone(zone).toLocalDate() }
            .toSet()

        return UserStats(
            totalQuizzes = results.size,
            totalQuestions = results.sumOf { it.totalQuestions },
            totalCorrect = results.sumOf { it.correctAnswers },
            bestScorePercent = results.maxOf { it.percent },
            daysPracticed = days.size,
            perCategory = perCategory,
            recent = results.sortedByDescending { it.finishedAtMillis }.take(RECENT_LIMIT)
        )
    }
}
