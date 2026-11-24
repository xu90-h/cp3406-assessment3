package com.jcu.brainbuddy.domain

import com.jcu.brainbuddy.question
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class QuizProgressTest {

    private fun newQuiz() = QuizProgress(listOf(question("a"), question("b")))

    @Test
    fun `starts on the first question with nothing answered`() {
        val quiz = newQuiz()
        assertEquals(0, quiz.currentIndex)
        assertFalse(quiz.isAnswered)
        assertEquals(0, quiz.correctCount)
        assertEquals(2, quiz.total)
    }

    @Test
    fun `correct answer increases the score`() {
        val quiz = newQuiz().select(0)
        assertTrue(quiz.isAnswered)
        assertTrue(quiz.isSelectedCorrect)
        assertEquals(1, quiz.correctCount)
    }

    @Test
    fun `wrong answer does not increase the score`() {
        val quiz = newQuiz().select(2)
        assertTrue(quiz.isAnswered)
        assertFalse(quiz.isSelectedCorrect)
        assertEquals(0, quiz.correctCount)
    }

    @Test
    fun `an answered question cannot be changed`() {
        val answered = newQuiz().select(2)
        assertSame(answered, answered.select(0))
    }

    @Test
    fun `out of range selection is ignored`() {
        val quiz = newQuiz()
        assertSame(quiz, quiz.select(9))
        assertSame(quiz, quiz.select(-1))
    }

    @Test
    fun `next is ignored until the question is answered`() {
        val quiz = newQuiz()
        assertSame(quiz, quiz.next())
    }

    @Test
    fun `next moves on and clears the selection`() {
        val quiz = newQuiz().select(0).next()
        assertEquals(1, quiz.currentIndex)
        assertFalse(quiz.isAnswered)
        assertEquals(1, quiz.correctCount)
        assertTrue(quiz.isLastQuestion)
    }

    @Test
    fun `finishing the last question ends the quiz`() {
        val quiz = newQuiz().select(0).next().select(1).next()
        assertTrue(quiz.isFinished)
        assertEquals(1, quiz.correctCount)
        assertEquals(1, quiz.currentIndex)
    }

    @Test
    fun `nothing changes after the quiz is finished`() {
        val finished = newQuiz().select(0).next().select(0).next()
        assertSame(finished, finished.select(0))
        assertSame(finished, finished.next())
        assertEquals(2, finished.correctCount)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `a quiz without questions is rejected`() {
        QuizProgress(emptyList())
    }
}
