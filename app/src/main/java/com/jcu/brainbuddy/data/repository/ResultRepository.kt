package com.jcu.brainbuddy.data.repository

import com.jcu.brainbuddy.domain.QuizResult
import kotlinx.coroutines.flow.Flow

interface ResultRepository {
    fun observeResults(): Flow<List<QuizResult>>
    suspend fun save(result: QuizResult)
    suspend fun clearAll()
}
