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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fitnesstracker.domain.model.ExerciseBestSet
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun ExerciseHistoryScreen(
    onNavigateBack: () -> Unit,
    viewModel: ExerciseHistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(viewModel.exerciseName) },
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
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is ExerciseHistoryUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ExerciseHistoryUiState.Success -> {
                    if (state.history.isEmpty()) {
                        EmptyHistory(modifier = Modifier.align(Alignment.Center))
                    } else {
                        HistoryList(history = state.history)
                    }
                }
                is ExerciseHistoryUiState.Error -> {
                    Text(
                        text = "Error: ${state.message}",
                        modifier = Modifier.align(Alignment.Center).padding(32.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryList(
    history: List<ExerciseBestSet>,
    modifier: Modifier = Modifier
) {
    val dateFormatter = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }

    LazyColumn(modifier = modifier.fillMaxSize()) {
        item {
            // Column headers
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("#",          style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(32.dp))
                Text("Date",       style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                Text("Best set",   style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(100.dp))
                Text("Weight",     style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(80.dp))
            }
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        }

        // History is ordered oldest → newest. We reverse for display (newest first)
        // but keep original index for "session #" numbering.
        itemsIndexed(history.reversed()) { reversedIndex, entry ->
            val sessionNumber = history.size - reversedIndex
            val isPersonalBest = entry.weight == history.maxOf { it.weight }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$sessionNumber",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(32.dp)
                )
                Text(
                    text = dateFormatter.format(entry.sessionDate),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${entry.reps} reps",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.width(100.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.width(80.dp)
                ) {
                    Text(
                        text = "${entry.weight}kg",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isPersonalBest) FontWeight.Bold else FontWeight.Normal,
                        color = if (isPersonalBest) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface
                    )
                    if (isPersonalBest) {
                        Spacer(modifier = Modifier.width(4.dp))
                        // PB indicator
                        Surface(
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "PB",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
            }
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }
    }
}

@Composable
private fun EmptyHistory(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.FitnessCenter,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("No history yet", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Log a session with this exercise to start tracking progress.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
