package com.jcu.brainbuddy.data

import com.jcu.brainbuddy.data.remote.TriviaApiService
import com.jcu.brainbuddy.data.remote.dto.TriviaQuestionDto
import com.jcu.brainbuddy.data.remote.dto.TriviaResponseDto
import com.jcu.brainbuddy.data.repository.QuestionRepositoryImpl
import com.jcu.brainbuddy.domain.Difficulty
import com.jcu.brainbuddy.domain.QuizCategory
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class QuestionRepositoryImplTest {

    private val api = mockk<TriviaApiService>()
    private val repository = QuestionRepositoryImpl(api)

    private val dto = TriviaQuestionDto("Q%3F", "yes", listOf("no", "maybe", "never"))

    @Test
    fun `successful response is mapped and marked online`() = runTest {
        coEvery { api.getQuestions(any(), any(), any(), any(), any()) } returns
            TriviaResponseDto(responseCode = 0, results = listOf(dto, dto))

        val batch = repository.getQuestions(QuizCategory.SCIENCE, Difficulty.MEDIUM, 2)

        assertTrue(batch.isOnline)
        assertEquals(2, batch.questions.size)
        assertEquals("Q?", batch.questions.first().text)
        coVerify { api.getQuestions(2, QuizCategory.SCIENCE.apiId, "medium", any(), any()) }
    }

    @Test
    fun `network failure falls back to the built-in questions`() = runTest {
        coEvery { api.getQuestions(any(), any(), any(), any(), any()) } throws IOException("offline")

        val batch = repository.getQuestions(QuizCategory.ANIMALS, Difficulty.EASY, 5)

        assertFalse(batch.isOnline)
        assertEquals(5, batch.questions.size)
    }

    @Test
    fun `api error code falls back to the built-in questions`() = runTest {
        coEvery { api.getQuestions(any(), any(), any(), any(), any()) } returns
            TriviaResponseDto(responseCode = 1, results = emptyList())

        val batch = repository.getQuestions(QuizCategory.MATH, Difficulty.HARD, 5)

        assertFalse(batch.isOnline)
        assertEquals(5, batch.questions.size)
    }

    @Test
    fun `empty result list falls back even when the code is ok`() = runTest {
        coEvery { api.getQuestions(any(), any(), any(), any(), any()) } returns
            TriviaResponseDto(responseCode = 0, results = emptyList())

        assertFalse(repository.getQuestions(QuizCategory.GEOGRAPHY, Difficulty.EASY, 5).isOnline)
    }

    @Test(expected = CancellationException::class)
    fun `cancellation is not swallowed by the fallback`() = runTest {
        coEvery { api.getQuestions(any(), any(), any(), any(), any()) } throws CancellationException("cancelled")

        repository.getQuestions(QuizCategory.MATH, Difficulty.EASY, 5)
    }
}
