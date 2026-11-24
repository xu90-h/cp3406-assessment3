package com.jcu.brainbuddy.ui.quiz

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jcu.brainbuddy.domain.Encouragement
import com.jcu.brainbuddy.domain.QuizProgress
import com.jcu.brainbuddy.domain.QuizResult
import com.jcu.brainbuddy.ui.theme.CorrectContainer
import com.jcu.brainbuddy.ui.theme.OnCorrectContainer

@Composable
fun QuizScreen(
    onQuit: () -> Unit,
    onFinished: (QuizResult) -> Unit,
    viewModel: QuizViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val haptics = LocalHapticFeedback.current
    var showQuitDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.completedResult) {
        state.completedResult?.let(onFinished)
    }
    BackHandler { showQuitDialog = true }

    QuizContent(
        state = state,
        onAnswerSelected = { index ->
            if (state.hapticsEnabled) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            viewModel.onAnswerSelected(index)
        },
        onNext = viewModel::onNext,
        onQuitRequested = { showQuitDialog = true }
    )

    if (showQuitDialog) {
        AlertDialog(
            onDismissRequest = { showQuitDialog = false },
            title = { Text("Stop this quiz?") },
            text = { Text("You can always start a new one. This quiz will not be saved.") },
            confirmButton = {
                TextButton(onClick = { showQuitDialog = false; onQuit() }) { Text("Stop quiz") }
            },
            dismissButton = {
                TextButton(onClick = { showQuitDialog = false }) { Text("Keep playing") }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizContent(
    state: QuizUiState,
    onAnswerSelected: (Int) -> Unit,
    onNext: () -> Unit,
    onQuitRequested: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = state.progress
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (progress != null) "Question ${progress.currentIndex + 1} of ${progress.total}"
                        else state.category.label
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onQuitRequested) {
                        Icon(Icons.Filled.Close, contentDescription = "Quit quiz")
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading || progress == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(Modifier.size(16.dp))
                    Text("Getting your questions ready...", style = MaterialTheme.typography.bodyLarge)
                }
            }
        } else {
            QuestionBody(state, progress, onAnswerSelected, onNext, Modifier.padding(padding))
        }
    }
}

@Composable
private fun QuestionBody(
    state: QuizUiState,
    progress: QuizProgress,
    onAnswerSelected: (Int) -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val answeredCount = progress.currentIndex + if (progress.isAnswered) 1 else 0
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LinearProgressIndicator(
            progress = { answeredCount.toFloat() / progress.total },
            modifier = Modifier.fillMaxWidth()
        )

        if (!state.isOnline) OfflineBanner()

        Card(Modifier.fillMaxWidth()) {
            Text(
                progress.current.text,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .padding(20.dp)
                    .testTag("question_text")
            )
        }

        progress.current.options.forEachIndexed { index, option ->
            OptionItem(
                label = ('A' + index).toString(),
                text = option,
                state = optionStateFor(progress, index),
                enabled = !progress.isAnswered,
                onClick = { onAnswerSelected(index) },
                modifier = Modifier.testTag("option_$index")
            )
        }

        if (progress.isAnswered) {
            FeedbackCard(progress)
            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("next_button")
            ) {
                Text(if (progress.isLastQuestion) "See my results" else "Next question")
            }
        }
    }
}

private enum class OptionState(val description: String?) {
    Idle(null),
    Correct("Correct answer"),
    Wrong("Your answer, not correct"),
    Dimmed(null)
}

private fun optionStateFor(progress: QuizProgress, index: Int): OptionState = when {
    !progress.isAnswered -> OptionState.Idle
    index == progress.current.correctIndex -> OptionState.Correct
    index == progress.selectedIndex -> OptionState.Wrong
    else -> OptionState.Dimmed
}

@Composable
private fun OptionItem(
    label: String,
    text: String,
    state: OptionState,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val container: Color
    val content: Color
    when (state) {
        OptionState.Correct -> { container = CorrectContainer; content = OnCorrectContainer }
        OptionState.Wrong -> {
            container = MaterialTheme.colorScheme.errorContainer
            content = MaterialTheme.colorScheme.onErrorContainer
        }
        else -> { container = MaterialTheme.colorScheme.surface; content = MaterialTheme.colorScheme.onSurface }
    }
    val border = if (state == OptionState.Idle || state == OptionState.Dimmed) {
        BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    } else {
        null
    }

    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        color = container,
        contentColor = content,
        border = border,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .semantics { state.description?.let { stateDescription = it } }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(shape = CircleShape, color = content.copy(alpha = 0.12f), contentColor = content) {
                Text(
                    label,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(text, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            // An icon accompanies the colour so feedback is never colour-only.
            when (state) {
                OptionState.Correct -> Icon(Icons.Filled.CheckCircle, contentDescription = null)
                OptionState.Wrong -> Icon(Icons.Filled.Error, contentDescription = null)
                else -> Unit
            }
        }
    }
}

@Composable
private fun FeedbackCard(progress: QuizProgress) {
    Text(
        Encouragement.forAnswer(progress.isSelectedCorrect, progress.current.correctAnswer),
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("feedback_text")
    )
}

@Composable
private fun OfflineBanner() {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("offline_banner")
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.CloudOff, contentDescription = null)
            Spacer(Modifier.width(12.dp))
            Text(
                "You are offline, so these questions come from the app.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
