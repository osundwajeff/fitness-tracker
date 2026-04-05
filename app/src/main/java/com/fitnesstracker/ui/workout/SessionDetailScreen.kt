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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fitnesstracker.domain.model.ExerciseLog
import com.fitnesstracker.domain.model.ExerciseSet
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun SessionDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: SessionDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            val session = (state.uiState as? SessionDetailUiState.Success)?.session
            val dateFormatter = SimpleDateFormat("EEE, MMM d yyyy", Locale.getDefault())
            val title = session?.let { dateFormatter.format(it.date) } ?: "Session"
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
            if (state.uiState is SessionDetailUiState.Success) {
                FloatingActionButton(onClick = viewModel::onAddExerciseClick) {
                    Icon(Icons.Default.Add, contentDescription = "Add exercise log")
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
                is SessionDetailUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is SessionDetailUiState.Success -> {
                    val session = uiState.session
                    if (session.exercises.isEmpty()) {
                        Text(
                            text = "No exercises logged yet. Tap + to add one.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(32.dp)
                        )
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(session.exercises, key = { it.id }) { log ->
                                ExerciseLogCard(
                                    log = log,
                                    onDeleteClick = { viewModel.onDeleteExerciseLog(log) },
                                    onAddSetClick = { viewModel.onAddSetClick(log) }
                                )
                            }
                        }
                    }
                }
                is SessionDetailUiState.Error -> {
                    Text(
                        text = "Error: ${uiState.message}",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    if (state.showAddExerciseDialog) {
        val existingNames = (state.uiState as? SessionDetailUiState.Success)
            ?.session?.exercises
            ?.map { it.name.lowercase() }
            ?.toSet()
            ?: emptySet()
        AddExerciseLogDialog(
            existingNames = existingNames,
            onConfirm = viewModel::onAddExerciseConfirm,
            onDismiss = viewModel::onAddExerciseDialogDismiss
        )
    }

    // Show the add-set dialog when the user taps "+ Add set" on an exercise card.
    state.addSetTargetLog?.let { targetLog ->
        AddSetDialog(
            log = targetLog,
            onConfirm = { newSet -> viewModel.onAddSetConfirm(targetLog, newSet) },
            onDismiss = viewModel::onAddSetDialogDismiss
        )
    }
}

@Composable
private fun ExerciseLogCard(
    log: ExerciseLog,
    onDeleteClick: () -> Unit,
    onAddSetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(log.name, style = MaterialTheme.typography.titleMedium)
                    if (!log.muscleGroup.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = log.muscleGroup,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        Icons.Outlined.Delete,
                        contentDescription = "Delete exercise log",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (log.sets.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("Set",         style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(40.dp))
                    Text("Reps",        style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(60.dp))
                    Text("Weight (kg)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                log.sets.forEachIndexed { index, set ->
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text("${index + 1}", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.width(40.dp))
                        Text("${set.reps}",  style = MaterialTheme.typography.bodyMedium, modifier = Modifier.width(60.dp))
                        Text("${set.weight}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            // "+ Add set" button — always visible at the bottom of the card.
            Spacer(modifier = Modifier.height(4.dp))
            TextButton(
                onClick = onAddSetClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .height(16.dp)
                        .width(16.dp)
                )
                Text("Add set", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
private fun AddExerciseLogDialog(
    existingNames: Set<String>,
    onConfirm: (ExerciseLog) -> Unit,
    onDismiss: () -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var muscleGroup by rememberSaveable { mutableStateOf("") }
    var reps by rememberSaveable { mutableStateOf("10") }
    var weight by rememberSaveable { mutableStateOf("0.0") }

    // isDuplicate: case-insensitive check — "squat" and "Squat" are the same exercise.
    val isDuplicate = name.trim().isNotBlank() && name.trim().lowercase() in existingNames
    val isValid = name.isNotBlank() && !isDuplicate

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Exercise") },
        text = {
            Column {
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text("Exercise name *") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    // isError turns the field outline and label red when duplicate detected.
                    isError = isDuplicate,
                    supportingText = if (isDuplicate) {
                        { Text("Already in this session") }
                    } else null
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = muscleGroup, onValueChange = { muscleGroup = it },
                    label = { Text("Muscle group (optional)") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = reps, onValueChange = { reps = it },
                        label = { Text("Reps") }, singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = weight, onValueChange = { weight = it },
                        label = { Text("Weight (kg)") }, singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(ExerciseLog(
                        name = name,
                        muscleGroup = muscleGroup.ifBlank { null },
                        sets = listOf(ExerciseSet(
                            reps = reps.toIntOrNull() ?: 0,
                            weight = weight.toDoubleOrNull() ?: 0.0
                        ))
                    ))
                },
                enabled = isValid
            ) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

/**
 * Small dialog for appending a set to an existing exercise log.
 *
 * Pre-fills reps/weight from the last recorded set so the user only has to
 * change what's different (e.g. bump weight by 2.5 kg for a progressive set).
 */
@Composable
private fun AddSetDialog(
    log: ExerciseLog,
    onConfirm: (ExerciseSet) -> Unit,
    onDismiss: () -> Unit
) {
    // Pre-fill from the last set if one exists, otherwise sensible defaults.
    val lastSet = log.sets.lastOrNull()
    var reps   by rememberSaveable { mutableStateOf(lastSet?.reps?.toString()   ?: "10") }
    var weight by rememberSaveable { mutableStateOf(lastSet?.weight?.toString() ?: "0.0") }

    val isValid = reps.isNotBlank() && weight.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add set — ${log.name}") },
        text = {
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = reps, onValueChange = { reps = it },
                    label = { Text("Reps") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = weight, onValueChange = { weight = it },
                    label = { Text("Weight (kg)") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(ExerciseSet(
                        reps = reps.toIntOrNull() ?: 0,
                        weight = weight.toDoubleOrNull() ?: 0.0
                    ))
                },
                enabled = isValid
            ) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
