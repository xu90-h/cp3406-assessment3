package com.jcu.brainbuddy.domain

enum class Difficulty(val apiValue: String, val label: String) {
    EASY("easy", "Easy"),
    MEDIUM("medium", "Medium"),
    HARD("hard", "Hard")
}

enum class QuizCategory(val apiId: Int, val label: String) {
    MATH(19, "Maths"),
    SCIENCE(17, "Science"),
    ANIMALS(27, "Animals"),
    GEOGRAPHY(22, "Geography")
}

data class Question(
    val text: String,
    val options: List<String>,
    val correctIndex: Int
) {
    init {
        require(correctIndex in options.indices) { "correctIndex must point at one of the options" }
    }

    val correctAnswer: String get() = options[correctIndex]
}

data class QuizResult(
    val id: Long = 0,
    val category: QuizCategory,
    val difficulty: Difficulty,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val finishedAtMillis: Long
) {
    val percent: Int
        get() = if (totalQuestions == 0) 0 else correctAnswers * 100 / totalQuestions
}

data class AppSettings(
    val category: QuizCategory = QuizCategory.MATH,
    val difficulty: Difficulty = Difficulty.EASY,
    val questionCount: Int = DEFAULT_QUESTION_COUNT,
    val largeText: Boolean = false,
    val hapticsEnabled: Boolean = true,
    /** Online questions from Open Trivia DB; off by default because they are not written for children. */
    val challengeMode: Boolean = false
) {
    companion object {
        const val DEFAULT_QUESTION_COUNT = 5
        val QUESTION_COUNT_OPTIONS = listOf(5, 10)
    }
}
