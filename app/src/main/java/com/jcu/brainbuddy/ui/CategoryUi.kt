package com.jcu.brainbuddy.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Science
import androidx.compose.ui.graphics.vector.ImageVector
import com.jcu.brainbuddy.domain.QuizCategory

val QuizCategory.icon: ImageVector
    get() = when (this) {
        QuizCategory.MATH -> Icons.Filled.Calculate
        QuizCategory.SCIENCE -> Icons.Filled.Science
        QuizCategory.ANIMALS -> Icons.Filled.Pets
        QuizCategory.GEOGRAPHY -> Icons.Filled.Public
    }
