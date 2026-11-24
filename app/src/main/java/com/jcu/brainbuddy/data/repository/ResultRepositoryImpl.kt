package com.jcu.brainbuddy.data.repository

import com.jcu.brainbuddy.data.local.QuizResultDao
import com.jcu.brainbuddy.data.local.toDomain
import com.jcu.brainbuddy.data.local.toEntity
import com.jcu.brainbuddy.domain.QuizResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ResultRepositoryImpl @Inject constructor(
    private val dao: QuizResultDao
) : ResultRepository {

    override fun observeResults(): Flow<List<QuizResult>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun save(result: QuizResult) = dao.insert(result.toEntity())

    override suspend fun clearAll() = dao.clearAll()
}
