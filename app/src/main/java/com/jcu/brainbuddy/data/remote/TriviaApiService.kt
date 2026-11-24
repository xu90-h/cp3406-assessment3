package com.jcu.brainbuddy.data.remote

import com.jcu.brainbuddy.data.remote.dto.TriviaResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface TriviaApiService {

    /** Open Trivia DB. `encode=url3986` avoids HTML entities in the returned text. */
    @GET("api.php")
    suspend fun getQuestions(
        @Query("amount") amount: Int,
        @Query("category") category: Int,
        @Query("difficulty") difficulty: String,
        @Query("type") type: String = "multiple",
        @Query("encode") encode: String = "url3986"
    ): TriviaResponseDto
}
