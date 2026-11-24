package com.jcu.brainbuddy.data.repository

import com.jcu.brainbuddy.data.remote.TriviaApiService
import com.jcu.brainbuddy.data.remote.toDomain
import com.jcu.brainbuddy.domain.Difficulty
import com.jcu.brainbuddy.domain.QuizCategory
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

class QuestionRepositoryImpl @Inject constructor(
    private val api: TriviaApiService
) : QuestionRepository {

    override suspend fun getQuestions(
        category: QuizCategory,
        difficulty: Difficulty,
        count: Int
    ): QuestionBatch {
        val online = try {
            val response = api.getQuestions(count, category.apiId, difficulty.apiValue)
            if (response.responseCode == API_OK && response.results.isNotEmpty()) {
                response.results.map { it.toDomain() }
            } else {
                null
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // Offline, rate limited (HTTP 429) or malformed response: use the built-in bank.
            null
        }

        return if (online != null) {
            QuestionBatch(online, isOnline = true)
        } else {
            QuestionBatch(FallbackQuestions.forCategory(category, count), isOnline = false)
        }
    }

    private companion object {
        const val API_OK = 0
    }
}
