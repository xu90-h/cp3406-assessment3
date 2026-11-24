package com.jcu.brainbuddy.domain

/** Immutable state of one quiz run. All transitions return a new instance. */
data class QuizProgress(
    val questions: List<Question>,
    val currentIndex: Int = 0,
    val selectedIndex: Int? = null,
    val correctCount: Int = 0,
    val isFinished: Boolean = false
) {
    init {
        require(questions.isNotEmpty()) { "A quiz needs at least one question" }
    }

    val current: Question get() = questions[currentIndex]
    val total: Int get() = questions.size
    val isAnswered: Boolean get() = selectedIndex != null
    val isLastQuestion: Boolean get() = currentIndex == questions.lastIndex
    val isSelectedCorrect: Boolean get() = selectedIndex == current.correctIndex

    /** Locks in an answer. Ignored once the question is answered or the quiz is over. */
    fun select(index: Int): QuizProgress {
        if (isFinished || isAnswered || index !in current.options.indices) return this
        val gained = if (index == current.correctIndex) 1 else 0
        return copy(selectedIndex = index, correctCount = correctCount + gained)
    }

    /** Moves on after an answer was chosen; finishes the quiz after the last question. */
    fun next(): QuizProgress {
        if (isFinished || !isAnswered) return this
        return if (isLastQuestion) {
            copy(isFinished = true)
        } else {
            copy(currentIndex = currentIndex + 1, selectedIndex = null)
        }
    }
}
