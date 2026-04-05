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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fitnesstracker.domain.model.Exercise
import com.fitnesstracker.domain.model.ExerciseSet
import com.fitnesstracker.domain.model.Workout

/**
 * Workout detail screen.
 *
 * Shows the full workout: name, date, and all exercises with their sets.
 * User can add/delete exercises.
 *
 * @param onNavigateBack Called when the user presses the back arrow.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun WorkoutDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: WorkoutDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val title = (state.uiState as? WorkoutDetailUiState.Success)?.workout?.name
                        ?: "Workout"
                    Text(title)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (state.uiState is WorkoutDetailUiState.Success) {
                FloatingActionButton(onClick = viewModel::onAddExerciseClick) {
                    Icon(Icons.Default.Add, contentDescription = "Add exercise")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val uiState = state.uiState) {
                is WorkoutDetailUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is WorkoutDetailUiState.Success -> {
                    WorkoutDetailContent(
                        workout = uiState.workout,
                        onDeleteExercise = viewModel::onDeleteExercise
                    )
                }
                is WorkoutDetailUiState.Error -> {
                    Text(
                        text = "Error: ${uiState.message}",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                is WorkoutDetailUiState.Deleted -> {
                    // This state triggers back navigation - handled via LaunchedEffect if needed
                }
            }
        }
    }

    if (state.showAddExerciseDialog) {
        AddExerciseDialog(
            onConfirm = viewModel::onAddExerciseConfirm,
            onDismiss = viewModel::onAddExerciseDialogDismiss
        )
    }
}

// ---------------------------------------------------------------------------
// Sub-composables
// ---------------------------------------------------------------------------

@Composable
private fun WorkoutDetailContent(
    workout: Workout,
    onDeleteExercise: (Exercise) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        if (workout.exercises.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No exercises yet. Tap + to add one.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(workout.exercises, key = { it.id }) { exercise ->
                ExerciseCard(
                    exercise = exercise,
                    onDeleteClick = { onDeleteExercise(exercise) }
                )
            }
        }
    }
}

@Composable
private fun ExerciseCard(
    exercise: Exercise,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = exercise.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (!exercise.muscleGroup.isNullOrBlank()) {
                        Text(
                            text = exercise.muscleGroup,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete exercise",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            if (exercise.sets.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                // Header row
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Set",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.width(40.dp)
                    )
                    Text(
                        text = "Reps",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.width(60.dp)
                    )
                    Text(
                        text = "Weight (kg)",
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                // Set rows
                exercise.sets.forEachIndexed { index, set ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "${index + 1}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.width(40.dp)
                        )
                        Text(
                            text = "${set.reps}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.width(60.dp)
                        )
                        Text(
                            text = "${set.weight}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

/**
 * Dialog to add a new exercise.
 *
 * rememberSaveable vs remember:
 * - remember: survives recomposition but not configuration changes
 * - rememberSaveable: survives both recomposition AND configuration changes (rotation)
 * Use rememberSaveable for text field state in dialogs so typing is not lost on rotation.
 */
@Composable
private fun AddExerciseDialog(
    onConfirm: (Exercise) -> Unit,
    onDismiss: () -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var muscleGroup by rememberSaveable { mutableStateOf("") }
    var reps by rememberSaveable { mutableStateOf("10") }
    var weight by rememberSaveable { mutableStateOf("0.0") }

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
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = muscleGroup,
                    onValueChange = { muscleGroup = it },
                    label = { Text("Muscle group (optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = reps,
                        onValueChange = { reps = it },
                        label = { Text("Reps") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text("Weight (kg)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val exercise = Exercise(
                        name = name,
                        muscleGroup = muscleGroup.ifBlank { null },
                        sets = listOf(
                            ExerciseSet(
                                reps = reps.toIntOrNull() ?: 0,
                                weight = weight.toDoubleOrNull() ?: 0.0
                            )
                        )
                    )
                    onConfirm(exercise)
                },
                enabled = name.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
