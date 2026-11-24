package com.jcu.brainbuddy.ui.settings

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jcu.brainbuddy.domain.AppSettings
import com.jcu.brainbuddy.domain.Difficulty
import com.jcu.brainbuddy.domain.QuizCategory

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    SettingsContent(
        settings = settings,
        onCategoryChange = viewModel::setCategory,
        onDifficultyChange = viewModel::setDifficulty,
        onQuestionCountChange = viewModel::setQuestionCount,
        onLargeTextChange = viewModel::setLargeText,
        onHapticsChange = viewModel::setHaptics,
        onClearProgress = viewModel::clearProgress
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    settings: AppSettings,
    onCategoryChange: (QuizCategory) -> Unit,
    onDifficultyChange: (Difficulty) -> Unit,
    onQuestionCountChange: (Int) -> Unit,
    onLargeTextChange: (Boolean) -> Unit,
    onHapticsChange: (Boolean) -> Unit,
    onClearProgress: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showClearDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = { CenterAlignedTopAppBar(title = { Text("Settings") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingsCard("Topic") {
                Row(
                    Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuizCategory.entries.forEach { category ->
                        FilterChip(
                            selected = settings.category == category,
                            onClick = { onCategoryChange(category) },
                            label = { Text(category.label) },
                            modifier = Modifier.testTag("chip_${category.name}")
                        )
                    }
                }
            }

            SettingsCard("Difficulty") {
                SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                    Difficulty.entries.forEachIndexed { index, difficulty ->
                        SegmentedButton(
                            selected = settings.difficulty == difficulty,
                            onClick = { onDifficultyChange(difficulty) },
                            shape = SegmentedButtonDefaults.itemShape(index, Difficulty.entries.size),
                            modifier = Modifier.testTag("difficulty_${difficulty.name}")
                        ) { Text(difficulty.label) }
                    }
                }
            }

            SettingsCard("Questions per quiz") {
                val options = AppSettings.QUESTION_COUNT_OPTIONS
                SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                    options.forEachIndexed { index, count ->
                        SegmentedButton(
                            selected = settings.questionCount == count,
                            onClick = { onQuestionCountChange(count) },
                            shape = SegmentedButtonDefaults.itemShape(index, options.size),
                            modifier = Modifier.testTag("count_$count")
                        ) { Text("$count") }
                    }
                }
            }

            SettingsCard("Comfort") {
                SwitchRow("Larger text", settings.largeText, onLargeTextChange, "large_text_switch")
                SwitchRow("Vibration when answering", settings.hapticsEnabled, onHapticsChange, "haptics_switch")
            }

            SettingsCard("Privacy") {
                Text(
                    "BrainBuddy never asks for your name, email or location. Your scores stay on this " +
                        "device and are never shared. Questions come from the Open Trivia Database, " +
                        "and no personal information is sent with the request.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { showClearDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("clear_progress_button")
                ) {
                    Icon(Icons.Filled.DeleteOutline, contentDescription = null)
                    Spacer(Modifier.padding(horizontal = 4.dp))
                    Text("Delete my progress")
                }
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Delete all progress?") },
            text = { Text("Every saved quiz result will be removed from this device. This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = { showClearDialog = false; onClearProgress() }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun SettingsCard(title: String, content: @Composable () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit, tag: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(value = checked, role = Role.Switch, onValueChange = onCheckedChange)
            .padding(vertical = 8.dp)
            .testTag(tag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = null)
    }
}
