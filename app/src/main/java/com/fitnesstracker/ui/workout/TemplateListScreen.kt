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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fitnesstracker.domain.model.Intensity
import com.fitnesstracker.domain.model.WorkoutTemplate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun TemplateListScreen(
    onNavigateToTemplate: (Long) -> Unit,
    viewModel: TemplateListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.navigateToSessionId) {
        state.navigateToSessionId?.let { sessionId ->
            // When a session is started directly from home, navigate to it
            onNavigateToTemplate(sessionId)
            viewModel.onNavigationConsumed()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Training Plan") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val uiState = state.uiState) {
                is TemplateListUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is TemplateListUiState.Success -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(uiState.templates, key = { it.id }) { template ->
                            TemplateCard(
                                template = template,
                                onCardClick = { onNavigateToTemplate(template.id) },
                                onStartSession = { viewModel.onStartSession(template.id) }
                            )
                        }
                    }
                }
                is TemplateListUiState.Error -> {
                    Text(
                        text = "Error: ${uiState.message}",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun TemplateCard(
    template: WorkoutTemplate,
    onCardClick: () -> Unit,
    onStartSession: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        onClick = onCardClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Intensity colour dot / icon
            Surface(
                shape = androidx.compose.foundation.shape.CircleShape,
                color = intensityColor(template.intensity),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.FitnessCenter,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = template.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Day badge
                    IntensityChip(label = template.day.name.lowercase().replaceFirstChar { it.uppercase() })
                    // Intensity badge
                    IntensityChip(label = template.intensity.name.lowercase().replaceFirstChar { it.uppercase() })
                }
                if (template.exercises.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${template.exercises.size} exercise${if (template.exercises.size == 1) "" else "s"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = onStartSession) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Start", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun IntensityChip(label: String) {
    Surface(
        shape = androidx.compose.foundation.shape.RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun intensityColor(intensity: Intensity) = when (intensity) {
    Intensity.HEAVY    -> MaterialTheme.colorScheme.error
    Intensity.MODERATE -> MaterialTheme.colorScheme.primary
    Intensity.LIGHT    -> MaterialTheme.colorScheme.tertiary
}
