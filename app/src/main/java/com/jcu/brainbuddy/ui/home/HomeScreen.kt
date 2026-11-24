package com.jcu.brainbuddy.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.PaddingValues
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jcu.brainbuddy.domain.QuizCategory
import com.jcu.brainbuddy.domain.UserStats
import com.jcu.brainbuddy.ui.icon

@Composable
fun HomeScreen(
    onStartQuiz: (QuizCategory) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    HomeContent(state = state, onStartQuiz = onStartQuiz)
}

@Composable
fun HomeContent(
    state: HomeUiState,
    onStartQuiz: (QuizCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { WelcomeCard(state, onStartQuiz) }
        item {
            Text("Pick a topic", style = MaterialTheme.typography.titleLarge)
        }
        item { CategoryGrid(onStartQuiz) }
        item { ProgressSummaryCard(state.stats) }
    }
}

@Composable
private fun WelcomeCard(state: HomeUiState, onStartQuiz: (QuizCategory) -> Unit) {
    val settings = state.settings
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(
                "Hi, explorer!",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Ready for a quick brain workout?",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { onStartQuiz(settings.category) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("start_quiz_button")
            ) {
                Icon(Icons.Filled.PlayArrow, contentDescription = null)
                Spacer(Modifier.size(8.dp))
                Text("Start ${settings.category.label} quiz")
            }
            Spacer(Modifier.height(8.dp))
            Text(
                if (settings.challengeMode) {
                    "${settings.questionCount} questions - Challenge mode - ${settings.difficulty.label}"
                } else {
                    "${settings.questionCount} questions"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CategoryGrid(onStartQuiz: (QuizCategory) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        QuizCategory.entries.chunked(2).forEach { pair ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                pair.forEach { category ->
                    CategoryCard(category, onStartQuiz, Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun CategoryCard(
    category: QuizCategory,
    onStartQuiz: (QuizCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { onStartQuiz(category) },
        modifier = modifier.testTag("category_${category.name}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                category.icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(Modifier.height(8.dp))
            Text(
                category.label,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
private fun ProgressSummaryCard(stats: UserStats) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp)) {
            Text("Your progress", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))
            if (stats.hasData) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem("${stats.totalQuizzes}", "Quizzes")
                    StatItem("${stats.accuracyPercent}%", "Correct")
                    StatItem("${stats.daysPracticed}", "Days played")
                }
            } else {
                Text(
                    "Your first quiz is waiting. Pick a topic to begin!",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.testTag("no_progress_text")
                )
            }
        }
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Text(label, style = MaterialTheme.typography.bodyMedium)
    }
}
