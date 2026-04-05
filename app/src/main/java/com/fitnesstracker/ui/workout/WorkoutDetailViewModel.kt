package com.fitnesstracker.ui.workout

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitnesstracker.domain.model.Exercise
import com.fitnesstracker.domain.model.Workout
import com.fitnesstracker.domain.usecase.AddExerciseUseCase
import com.fitnesstracker.domain.usecase.DeleteExerciseUseCase
import com.fitnesstracker.domain.usecase.GetWorkoutByIdUseCase
import com.fitnesstracker.domain.usecase.UpdateWorkoutUseCase
import com.fitnesstracker.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for the workout detail screen.
 */
sealed class WorkoutDetailUiState {
    data object Loading : WorkoutDetailUiState()
    data class Success(val workout: Workout) : WorkoutDetailUiState()
    data class Error(val message: String) : WorkoutDetailUiState()
    data object Deleted : WorkoutDetailUiState()
}

data class WorkoutDetailScreenState(
    val uiState: WorkoutDetailUiState = WorkoutDetailUiState.Loading,
    val showAddExerciseDialog: Boolean = false
)

/**
 * ViewModel for the workout detail screen.
 *
 * SavedStateHandle: automatically provided by Hilt. It holds navigation arguments
 * (like the workout ID) and survives process death — more robust than just ViewModel.
 *
 * The workout ID is passed via navigation as a route argument and read from SavedStateHandle.
 */
@HiltViewModel
class WorkoutDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getWorkoutByIdUseCase: GetWorkoutByIdUseCase,
    private val updateWorkoutUseCase: UpdateWorkoutUseCase,
    private val addExerciseUseCase: AddExerciseUseCase,
    private val deleteExerciseUseCase: DeleteExerciseUseCase
) : ViewModel() {

    /**
     * Read the workoutId from navigation arguments.
     * "workoutId" must match the route parameter name in Navigation setup.
     * The !! crash here is intentional: a detail screen without an ID is a
     * programmer error, not a runtime condition to gracefully handle.
     */
    private val workoutId: Long = checkNotNull(savedStateHandle["workoutId"])

    private val _state = MutableStateFlow(WorkoutDetailScreenState())
    val state: StateFlow<WorkoutDetailScreenState> = _state.asStateFlow()

    init {
        loadWorkout()
    }

    // ---------------------------------------------------------------------------
    // Private: data loading
    // ---------------------------------------------------------------------------

    private fun loadWorkout() {
        viewModelScope.launch {
            _state.update { it.copy(uiState = WorkoutDetailUiState.Loading) }
            when (val result = getWorkoutByIdUseCase(workoutId)) {
                is Result.Success -> {
                    val workout = result.data
                    if (workout == null) {
                        _state.update { it.copy(
                            uiState = WorkoutDetailUiState.Error("Workout not found")
                        )}
                    } else {
                        _state.update { it.copy(
                            uiState = WorkoutDetailUiState.Success(workout)
                        )}
                    }
                }
                is Result.Error -> _state.update { it.copy(
                    uiState = WorkoutDetailUiState.Error(
                        result.exception.message ?: "Failed to load workout"
                    )
                )}
            }
        }
    }

    // ---------------------------------------------------------------------------
    // Public: UI events
    // ---------------------------------------------------------------------------

    fun onAddExerciseClick() {
        _state.update { it.copy(showAddExerciseDialog = true) }
    }

    fun onAddExerciseDialogDismiss() {
        _state.update { it.copy(showAddExerciseDialog = false) }
    }

    fun onAddExerciseConfirm(exercise: Exercise) {
        viewModelScope.launch {
            when (val result = addExerciseUseCase(workoutId, exercise)) {
                is Result.Success -> {
                    _state.update { it.copy(showAddExerciseDialog = false) }
                    // Reload to reflect the new exercise in the UI
                    loadWorkout()
                }
                is Result.Error -> _state.update { it.copy(
                    showAddExerciseDialog = false,
                    uiState = WorkoutDetailUiState.Error(
                        result.exception.message ?: "Failed to add exercise"
                    )
                )}
            }
        }
    }

    fun onDeleteExercise(exercise: Exercise) {
        viewModelScope.launch {
            deleteExerciseUseCase(exercise)
            loadWorkout()
        }
    }

    fun onUpdateWorkoutName(newName: String) {
        val currentWorkout = (state.value.uiState as? WorkoutDetailUiState.Success)?.workout
            ?: return

        viewModelScope.launch {
            val updated = currentWorkout.copy(name = newName)
            when (val result = updateWorkoutUseCase(updated)) {
                is Result.Success -> loadWorkout()
                is Result.Error -> _state.update { it.copy(
                    uiState = WorkoutDetailUiState.Error(
                        result.exception.message ?: "Failed to update workout"
                    )
                )}
            }
        }
    }
}
