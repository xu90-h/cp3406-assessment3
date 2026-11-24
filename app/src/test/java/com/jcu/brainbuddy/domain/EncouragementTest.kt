package com.jcu.brainbuddy.domain

import org.junit.Assert.assertTrue
import org.junit.Test

class EncouragementTest {

    @Test
    fun `high scores get celebratory feedback`() {
        assertTrue(Encouragement.forScore(100).startsWith("Amazing"))
        assertTrue(Encouragement.forScore(90).startsWith("Amazing"))
    }

    @Test
    fun `low scores are still encouraging and never negative`() {
        val message = Encouragement.forScore(0)
        assertTrue(message.startsWith("Good try"))
        assertTrue(!message.contains("fail", ignoreCase = true))
    }

    @Test
    fun `wrong answer feedback reveals the right answer`() {
        assertTrue(Encouragement.forAnswer(false, "Paris").contains("Paris"))
        assertTrue(Encouragement.forAnswer(true, "Paris").startsWith("That's right"))
    }
}
