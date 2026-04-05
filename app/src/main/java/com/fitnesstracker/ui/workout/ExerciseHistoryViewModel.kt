package com.fitnesstracker.ui.workout

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitnesstracker.domain.model.ExerciseBestSet
import com.fitnesstracker.domain.usecase.GetExerciseHistoryUseCase
import com.fitnesstracker.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ExerciseHistoryUiState {
    data object Loading : ExerciseHistoryUiState()
    data class Success(val history: List<ExerciseBestSet>) : ExerciseHistoryUiState()
    data class Error(val message: String) : ExerciseHistoryUiState()
}

/**
 * ViewModel for the exercise history / progress screen.
 *
 * Reads exerciseName and templateId from the navigation back-stack entry,
 * then fetches the best set per session for that exercise.
 */
@HiltViewModel
class ExerciseHistoryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getExerciseHistoryUseCase: GetExerciseHistoryUseCase
) : ViewModel() {

    val exerciseName: String = checkNotNull(savedStateHandle["exerciseName"])
    private val templateId: Long = checkNotNull(savedStateHandle["templateId"])

    private val _uiState = MutableStateFlow<ExerciseHistoryUiState>(ExerciseHistoryUiState.Loading)
    val uiState: StateFlow<ExerciseHistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            when (val result = getExerciseHistoryUseCase(exerciseName, templateId)) {
                is Result.Success -> _uiState.update {
                    ExerciseHistoryUiState.Success(result.data)
                }
                is Result.Error -> _uiState.update {
                    ExerciseHistoryUiState.Error(
                        result.exception.message ?: "Failed to load history"
                    )
                }
            }
        }
    }
}
