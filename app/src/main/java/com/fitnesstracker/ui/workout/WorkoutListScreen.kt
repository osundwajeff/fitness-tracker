package com.fitnesstracker.ui.workout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fitnesstracker.domain.model.Workout
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Workout list screen composable.
 *
 * This function is STATELESS from a navigation perspective - it receives callbacks
 * rather than a NavController. This makes it:
 * - Independently previewable (pass a fake lambda)
 * - Testable without a real NavController
 *
 * The ViewModel is obtained via hiltViewModel() which ties it to this NavBackStackEntry's
 * lifecycle. It's created on first composition and cleared when this destination is popped.
 *
 * @param onNavigateToDetail Called with a workout ID when the user taps a workout.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun WorkoutListScreen(
    onNavigateToDetail: (Long) -> Unit,
    viewModel: WorkoutListViewModel = hiltViewModel()
) {
    // collectAsStateWithLifecycle: only collects while the UI is visible (STARTED lifecycle state).
    // This prevents background processing when the app is backgrounded.
    val state by viewModel.state.collectAsStateWithLifecycle()

    // LaunchedEffect with a key: runs the block whenever navigateToWorkoutId changes.
    // This is the pattern for one-time events: observe a state field and consume it.
    LaunchedEffect(state.navigateToWorkoutId) {
        state.navigateToWorkoutId?.let { id ->
            onNavigateToDetail(id)
            viewModel.onNavigationConsumed()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("My Workouts") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::onCreateWorkoutClick) {
                Icon(Icons.Default.Add, contentDescription = "Create workout")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val uiState = state.uiState) {
                is WorkoutListUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is WorkoutListUiState.Success -> {
                    if (uiState.workouts.isEmpty()) {
                        EmptyWorkoutsMessage(modifier = Modifier.align(Alignment.Center))
                    } else {
                        WorkoutList(
                            workouts = uiState.workouts,
                            onWorkoutClick = onNavigateToDetail,
                            onDeleteClick = viewModel::onDeleteWorkout
                        )
                    }
                }
                is WorkoutListUiState.Error -> {
                    ErrorMessage(
                        message = uiState.message,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }

    // Show create dialog when requested
    if (state.showCreateDialog) {
        CreateWorkoutDialog(
            onConfirm = viewModel::onCreateWorkoutConfirm,
            onDismiss = viewModel::onCreateDialogDismiss
        )
    }
}

// ---------------------------------------------------------------------------
// Sub-composables
// ---------------------------------------------------------------------------

@Composable
private fun WorkoutList(
    workouts: List<Workout>,
    onWorkoutClick: (Long) -> Unit,
    onDeleteClick: (Workout) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(workouts, key = { it.id }) { workout ->
            WorkoutCard(
                workout = workout,
                onClick = { onWorkoutClick(workout.id) },
                onDeleteClick = { onDeleteClick(workout) }
            )
        }
    }
}

@Composable
private fun WorkoutCard(
    workout: Workout,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatter = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = workout.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dateFormatter.format(workout.date),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (workout.exercises.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${workout.exercises.size} exercise${if (workout.exercises.size == 1) "" else "s"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = onDeleteClick) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete workout",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun EmptyWorkoutsMessage(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No workouts yet",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tap + to create your first workout",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ErrorMessage(message: String, modifier: Modifier = Modifier) {
    Text(
        text = "Error: $message",
        modifier = modifier.padding(32.dp),
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodyMedium
    )
}

/**
 * Dialog for creating a new workout.
 *
 * Uses local remember state for the text field - this is UI-only state
 * that doesn't need to survive configuration changes or go into the ViewModel.
 */
@Composable
private fun CreateWorkoutDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var workoutName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Workout") },
        text = {
            OutlinedTextField(
                value = workoutName,
                onValueChange = { workoutName = it },
                label = { Text("Workout name") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(workoutName) },
                enabled = workoutName.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
