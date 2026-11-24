package com.jcu.brainbuddy.data.remote

import com.jcu.brainbuddy.data.remote.dto.TriviaQuestionDto
import com.jcu.brainbuddy.domain.Question
import java.net.URLDecoder
import kotlin.random.Random

private fun String.decodeUrl(): String = URLDecoder.decode(this, "UTF-8")

/** Decodes the url3986-encoded text and shuffles the answer options. */
fun TriviaQuestionDto.toDomain(random: Random = Random.Default): Question {
    val correct = correctAnswer.decodeUrl()
    val options = (incorrectAnswers.map { it.decodeUrl() } + correct).shuffled(random)
    return Question(
        text = question.decodeUrl(),
        options = options,
        correctIndex = options.indexOf(correct)
    )
}
