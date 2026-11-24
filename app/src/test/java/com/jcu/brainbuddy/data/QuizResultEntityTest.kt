package com.jcu.brainbuddy.data

import com.jcu.brainbuddy.data.local.QuizResultEntity
import com.jcu.brainbuddy.data.local.toDomain
import com.jcu.brainbuddy.data.local.toEntity
import com.jcu.brainbuddy.domain.Difficulty
import com.jcu.brainbuddy.domain.QuizCategory
import com.jcu.brainbuddy.result
import org.junit.Assert.assertEquals
import org.junit.Test

class QuizResultEntityTest {

    @Test
    fun `domain result survives a round trip through the entity`() {
        val original = result(QuizCategory.GEOGRAPHY, correct = 4, total = 5, at = 123L, difficulty = Difficulty.HARD)
        assertEquals(original, original.toEntity().toDomain())
    }

    @Test
    fun `unknown stored values fall back to safe defaults`() {
        val entity = QuizResultEntity(
            category = "REMOVED_TOPIC",
            difficulty = "IMPOSSIBLE",
            totalQuestions = 5,
            correctAnswers = 2,
            finishedAtMillis = 1L
        )
        val domain = entity.toDomain()
        assertEquals(QuizCategory.MATH, domain.category)
        assertEquals(Difficulty.EASY, domain.difficulty)
    }
}
