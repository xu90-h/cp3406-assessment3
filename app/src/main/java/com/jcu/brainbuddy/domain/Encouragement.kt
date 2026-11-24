package com.jcu.brainbuddy.domain

/** Feedback is always positive and never compares the child with other people. */
object Encouragement {

    fun forScore(percent: Int): String = when {
        percent >= 90 -> "Amazing work! You really know your stuff!"
        percent >= 70 -> "Great job! Keep it up!"
        percent >= 50 -> "Nice effort! Every quiz makes you stronger."
        else -> "Good try! Learning takes practice - have another go whenever you like."
    }

    fun forAnswer(correct: Boolean, correctAnswer: String): String =
        if (correct) "That's right! Well done." else "Not quite. The answer is $correctAnswer."
}
