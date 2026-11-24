package com.jcu.brainbuddy.data.repository

import com.jcu.brainbuddy.domain.Difficulty
import com.jcu.brainbuddy.domain.Question
import com.jcu.brainbuddy.domain.QuizCategory

enum class QuestionSource {
    /** The built-in, age-appropriate question bank (default). */
    BUILT_IN,

    /** Questions fetched from the internet (challenge mode). */
    ONLINE,

    /** Challenge mode was requested but the internet request failed. */
    OFFLINE_FALLBACK
}

data class QuestionBatch(val questions: List<Question>, val source: QuestionSource)

interface QuestionRepository {
    /** Never throws for network problems: falls back to the built-in question bank instead. */
    suspend fun getQuestions(
        category: QuizCategory,
        difficulty: Difficulty,
        count: Int,
        challengeMode: Boolean
    ): QuestionBatch
}
