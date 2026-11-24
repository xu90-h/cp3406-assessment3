package com.jcu.brainbuddy.ui.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jcu.brainbuddy.domain.CategoryStats
import com.jcu.brainbuddy.domain.QuizResult
import com.jcu.brainbuddy.domain.UserStats
import com.jcu.brainbuddy.ui.icon
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun StatsScreen(viewModel: StatsViewModel = hiltViewModel()) {
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    StatsContent(stats)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsContent(stats: UserStats, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier,
        topBar = { CenterAlignedTopAppBar(title = { Text("My progress") }) }
    ) { padding ->
        if (!stats.hasData) {
            EmptyState(Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { SummaryGrid(stats) }
                item { Text("By topic", style = MaterialTheme.typography.titleLarge) }
                items(stats.perCategory) { CategoryProgressRow(it) }
                item { Text("Recent quizzes", style = MaterialTheme.typography.titleLarge) }
                items(stats.recent) { RecentResultRow(it) }
            }
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Filled.Insights,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "No quizzes yet",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.testTag("empty_stats_title")
            )
            Text(
                "Finish a quiz and your progress will show up here.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SummaryGrid(stats: UserStats) {
    val items = listOf(
        "Quizzes done" to "${stats.totalQuizzes}",
        "Questions answered" to "${stats.totalQuestions}",
        "Correct answers" to "${stats.accuracyPercent}%",
        "Best score" to "${stats.bestScorePercent}%",
        "Days played" to "${stats.daysPracticed}"
    )
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { (label, value) -> StatCard(label, value, Modifier.weight(1f)) }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.semantics(mergeDescendants = true) {},
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                value,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun CategoryProgressRow(stats: CategoryStats) {
    val played = stats.questions > 0
    Column(
        Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {
                contentDescription = if (played) {
                    "${stats.category.label}: ${stats.accuracyPercent} percent correct"
                } else {
                    "${stats.category.label}: not played yet"
                }
            }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(stats.category.icon, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
            Spacer(Modifier.size(8.dp))
            Text(stats.category.label, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            Text(
                if (played) "${stats.accuracyPercent}%" else "Not played yet",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Spacer(Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { stats.accuracyPercent / 100f },
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

private val dateFormatter = DateTimeFormatter.ofPattern("d MMM, h:mm a")

@Composable
private fun RecentResultRow(result: QuizResult) {
    val date = Instant.ofEpochMilli(result.finishedAtMillis)
        .atZone(ZoneId.systemDefault())
        .format(dateFormatter)
    Card(Modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(result.category.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.size(12.dp))
            Column(Modifier.weight(1f)) {
                Text(result.category.label, style = MaterialTheme.typography.titleMedium)
                Text("${result.difficulty.label} - $date", style = MaterialTheme.typography.bodyMedium)
            }
            Text(
                "${result.correctAnswers}/${result.totalQuestions}",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}
