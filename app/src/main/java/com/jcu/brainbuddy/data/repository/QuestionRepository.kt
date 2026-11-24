package com.jcu.brainbuddy.data.repository

import com.jcu.brainbuddy.domain.Difficulty
import com.jcu.brainbuddy.domain.Question
import com.jcu.brainbuddy.domain.QuizCategory

data class QuestionBatch(val questions: List<Question>, val isOnline: Boolean)

interface QuestionRepository {
    /** Never throws for network problems: falls back to the built-in question bank instead. */
    suspend fun getQuestions(category: QuizCategory, difficulty: Difficulty, count: Int): QuestionBatch
}
