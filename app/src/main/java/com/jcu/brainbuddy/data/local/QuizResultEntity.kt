package com.jcu.brainbuddy.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jcu.brainbuddy.domain.Difficulty
import com.jcu.brainbuddy.domain.QuizCategory
import com.jcu.brainbuddy.domain.QuizResult

@Entity(tableName = "quiz_results")
data class QuizResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String,
    val difficulty: String,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val finishedAtMillis: Long
)

fun QuizResultEntity.toDomain(): QuizResult = QuizResult(
    id = id,
    category = QuizCategory.entries.firstOrNull { it.name == category } ?: QuizCategory.MATH,
    difficulty = Difficulty.entries.firstOrNull { it.name == difficulty } ?: Difficulty.EASY,
    totalQuestions = totalQuestions,
    correctAnswers = correctAnswers,
    finishedAtMillis = finishedAtMillis
)

fun QuizResult.toEntity(): QuizResultEntity = QuizResultEntity(
    id = id,
    category = category.name,
    difficulty = difficulty.name,
    totalQuestions = totalQuestions,
    correctAnswers = correctAnswers,
    finishedAtMillis = finishedAtMillis
)
