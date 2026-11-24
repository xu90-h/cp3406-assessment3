package com.jcu.brainbuddy.data

import com.jcu.brainbuddy.data.remote.dto.TriviaQuestionDto
import com.jcu.brainbuddy.data.remote.toDomain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class TriviaMapperTest {

    private val dto = TriviaQuestionDto(
        question = "What%20is%202%20%2B%202%3F",
        correctAnswer = "4",
        incorrectAnswers = listOf("3", "5", "22")
    )

    @Test
    fun `url encoded text is decoded`() {
        val question = dto.toDomain(Random(1))
        assertEquals("What is 2 + 2?", question.text)
    }

    @Test
    fun `correct index points at the correct answer after shuffling`() {
        repeat(20) { seed ->
            val question = dto.toDomain(Random(seed))
            assertEquals("4", question.correctAnswer)
            assertEquals(4, question.options.size)
            assertTrue(question.options.containsAll(listOf("3", "4", "5", "22")))
        }
    }

    @Test
    fun `special characters in answers survive decoding`() {
        val special = TriviaQuestionDto(
            question = "Caf%C3%A9%3F",
            correctAnswer = "A%20%26%20B",
            incorrectAnswers = listOf("C%2BD", "E", "F")
        ).toDomain(Random(3))
        assertEquals("Café?", special.text)
        assertEquals("A & B", special.correctAnswer)
        assertTrue(special.options.contains("C+D"))
    }
}
