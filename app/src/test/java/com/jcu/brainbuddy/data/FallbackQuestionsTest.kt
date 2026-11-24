package com.jcu.brainbuddy.data

import com.jcu.brainbuddy.data.repository.FallbackQuestions
import com.jcu.brainbuddy.domain.QuizCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FallbackQuestionsTest {

    @Test
    fun `every category can supply a full ten question quiz`() {
        QuizCategory.entries.forEach { category ->
            assertEquals(10, FallbackQuestions.forCategory(category, 10).size)
        }
    }

    @Test
    fun `requested count is respected`() {
        assertEquals(5, FallbackQuestions.forCategory(QuizCategory.MATH, 5).size)
    }

    @Test
    fun `questions are well formed`() {
        QuizCategory.entries.forEach { category ->
            FallbackQuestions.forCategory(category, 10).forEach { question ->
                assertTrue(question.text.isNotBlank())
                assertEquals(4, question.options.size)
                assertEquals(question.options.size, question.options.toSet().size)
                assertTrue(question.correctIndex in question.options.indices)
            }
        }
    }
}
