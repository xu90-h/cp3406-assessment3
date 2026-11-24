package com.jcu.brainbuddy.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TriviaResponseDto(
    @Json(name = "response_code") val responseCode: Int,
    @Json(name = "results") val results: List<TriviaQuestionDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class TriviaQuestionDto(
    @Json(name = "question") val question: String,
    @Json(name = "correct_answer") val correctAnswer: String,
    @Json(name = "incorrect_answers") val incorrectAnswers: List<String>
)
