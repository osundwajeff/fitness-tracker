package com.fitnesstracker.ui.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fitnesstracker.domain.model.ExerciseTemplate
import com.fitnesstracker.domain.model.WorkoutSession
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun TemplateDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSession: (Long) -> Unit,
    onNavigateToHistory: (Long, String) -> Unit,
    viewModel: TemplateDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    LaunchedEffect(state.navigateToSessionId) {
        state.navigateToSessionId?.let { sessionId ->
            onNavigateToSession(sessionId)
            viewModel.onNavigationConsumed()
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            val title = (state.uiState as? TemplateDetailUiState.Success)?.template?.name ?: "Template"
            LargeTopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::onAddExerciseClick) {
                Icon(Icons.Default.Add, contentDescription = "Add exercise to template")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val uiState = state.uiState) {
                is TemplateDetailUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is TemplateDetailUiState.Success -> {
                    TemplateDetailContent(
                        template = uiState.template,
                        sessions = uiState.sessions,
                        onDeleteExercise = viewModel::onRemoveExercise,
                        onStartSession = viewModel::onStartSession,
                        onDeleteSession = viewModel::onDeleteSession,
                        onExerciseHistoryClick = { exerciseName ->
                            onNavigateToHistory(uiState.template.id, exerciseName)
                        }
                    )
                }
                is TemplateDetailUiState.Error -> {
                    Text(
                        text = "Error: ${uiState.message}",
                        modifier = Modifier.align(Alignment.Center).padding(32.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    if (state.showAddExerciseDialog) {
        val existingNames = (state.uiState as? TemplateDetailUiState.Success)
            ?.template?.exercises
            ?.map { it.name.lowercase() }
            ?.toSet()
            ?: emptySet()
        AddExerciseToTemplateDialog(
            existingNames = existingNames,
            onConfirm = viewModel::onAddExerciseConfirm,
            onDismiss = viewModel::onAddExerciseDialogDismiss
        )
    }
}

@Composable
private fun TemplateDetailContent(
    template: com.fitnesstracker.domain.model.WorkoutTemplate,
    sessions: List<WorkoutSession>,
    onDeleteExercise: (ExerciseTemplate) -> Unit,
    onStartSession: () -> Unit,
    onDeleteSession: (WorkoutSession) -> Unit,
    onExerciseHistoryClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatter = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }

    LazyColumn(modifier = modifier.fillMaxSize()) {

        // — Start session button
        item {
            Button(
                onClick = onStartSession,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Start Today's Session", fontWeight = FontWeight.SemiBold)
            }
        }

        // — Exercises section header
        item {
            SectionHeader(title = "Exercises")
        }

        if (template.exercises.isEmpty()) {
            item {
                Text(
                    text = "No exercises yet. Tap + to add some.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        } else {
            items(template.exercises, key = { it.id }) { exercise ->
                ExerciseTemplateRow(
                    exercise = exercise,
                    onDeleteClick = { onDeleteExercise(exercise) },
                    onHistoryClick = { onExerciseHistoryClick(exercise.name) }
                )
            }
        }

        // — Past sessions section header
        item {
            Spacer(modifier = Modifier.height(8.dp))
            SectionHeader(title = "Past Sessions (${sessions.size})")
        }

        if (sessions.isEmpty()) {
            item {
                Text(
                    text = "No sessions logged yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        } else {
            items(sessions, key = { it.id }) { session ->
                SessionRow(
                    session = session,
                    dateFormatter = dateFormatter,
                    onDeleteClick = { onDeleteSession(session) }
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
}

@Composable
private fun ExerciseTemplateRow(
    exercise: ExerciseTemplate,
    onDeleteClick: () -> Unit,
    onHistoryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(exercise.name, style = MaterialTheme.typography.bodyLarge)
            if (!exercise.muscleGroup.isNullOrBlank()) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = exercise.muscleGroup,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }
        // History button — tap to see progress for this exercise
        TextButton(onClick = onHistoryClick) {
            Text("History", style = MaterialTheme.typography.labelMedium)
        }
        IconButton(onClick = onDeleteClick) {
            Icon(
                Icons.Outlined.Delete,
                contentDescription = "Remove exercise",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SessionRow(
    session: WorkoutSession,
    dateFormatter: java.text.SimpleDateFormat,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = dateFormatter.format(session.date),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                if (session.exercises.isNotEmpty()) {
                    Text(
                        text = "${session.exercises.size} exercise${if (session.exercises.size == 1) "" else "s"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = onDeleteClick) {
                Icon(
                    Icons.Outlined.Delete,
                    contentDescription = "Delete session",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AddExerciseToTemplateDialog(
    existingNames: Set<String>,
    onConfirm: (String, String?) -> Unit,
    onDismiss: () -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var muscleGroup by rememberSaveable { mutableStateOf("") }

    // isDuplicate: case-insensitive check — "squat" and "Squat" are the same exercise.
    val isDuplicate = name.trim().isNotBlank() && name.trim().lowercase() in existingNames
    val isValid = name.isNotBlank() && !isDuplicate

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Exercise") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Exercise name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    // isError turns the field outline and label red when duplicate detected.
                    isError = isDuplicate,
                    supportingText = if (isDuplicate) {
                        { Text("Already on this template") }
                    } else null
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = muscleGroup,
                    onValueChange = { muscleGroup = it },
                    label = { Text("Muscle group (optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name, muscleGroup.ifBlank { null }) },
                enabled = isValid
            ) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
