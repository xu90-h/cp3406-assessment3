package com.jcu.brainbuddy.ui.navigation

import com.jcu.brainbuddy.domain.QuizCategory

object Routes {
    const val HOME = "home"
    const val STATS = "stats"
    const val SETTINGS = "settings"

    const val CATEGORY_ARG = "category"
    const val CORRECT_ARG = "correct"
    const val TOTAL_ARG = "total"

    const val QUIZ = "quiz/{$CATEGORY_ARG}"
    const val RESULT = "result/{$CATEGORY_ARG}/{$CORRECT_ARG}/{$TOTAL_ARG}"

    fun quiz(category: QuizCategory) = "quiz/${category.name}"
    fun result(category: QuizCategory, correct: Int, total: Int) = "result/${category.name}/$correct/$total"
}
