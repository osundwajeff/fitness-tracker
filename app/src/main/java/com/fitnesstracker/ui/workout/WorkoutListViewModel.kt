package com.fitnesstracker.ui.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitnesstracker.domain.model.Workout
import com.fitnesstracker.domain.usecase.CreateWorkoutUseCase
import com.fitnesstracker.domain.usecase.DeleteWorkoutUseCase
import com.fitnesstracker.domain.usecase.GetWorkoutsUseCase
import com.fitnesstracker.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

/**
 * UI state for the workout list screen.
 *
 * Sealed class used here because the screen has clearly distinct visual states:
 * - Loading: show a progress indicator
 * - Success: show the list of workouts
 * - Error: show an error message
 *
 * Why sealed class instead of separate booleans?
 * Multiple booleans (isLoading, hasError, isEmpty) can get into impossible states.
 * A sealed class makes illegal states unrepresentable.
 */
sealed class WorkoutListUiState {
    data object Loading : WorkoutListUiState()
    data class Success(val workouts: List<Workout>) : WorkoutListUiState()
    data class Error(val message: String) : WorkoutListUiState()
}

/**
 * State for transient UI events (dialogs, navigation triggers).
 *
 * Separated from WorkoutListUiState because events are consumed once,
 * whereas UI state is a persistent snapshot.
 */
data class WorkoutListScreenState(
    val uiState: WorkoutListUiState = WorkoutListUiState.Loading,
    val showCreateDialog: Boolean = false,
    val navigateToWorkoutId: Long? = null
)

/**
 * ViewModel for the workout list screen.
 *
 * @HiltViewModel: Hilt knows how to create this ViewModel and inject its dependencies.
 * The ViewModel survives configuration changes (screen rotation, etc.).
 *
 * Key patterns:
 * - StateFlow: a hot flow with a current value. UI reads _state.asStateFlow() (read-only).
 * - MutableStateFlow is private so only this ViewModel can update state.
 * - viewModelScope: coroutines launched here are automatically cancelled when ViewModel is cleared.
 */
@HiltViewModel
class WorkoutListViewModel @Inject constructor(
    private val getWorkoutsUseCase: GetWorkoutsUseCase,
    private val createWorkoutUseCase: CreateWorkoutUseCase,
    private val deleteWorkoutUseCase: DeleteWorkoutUseCase
) : ViewModel() {

    // The backing mutable state - private so only this class can mutate it.
    private val _state = MutableStateFlow(WorkoutListScreenState())

    // Exposed to the UI as read-only StateFlow.
    val state: StateFlow<WorkoutListScreenState> = _state.asStateFlow()

    init {
        // Start observing workouts as soon as the ViewModel is created.
        observeWorkouts()
    }

    // ---------------------------------------------------------------------------
    // Private: data loading
    // ---------------------------------------------------------------------------

    /**
     * Collect the workout stream and translate each Result into UI state.
     * launchIn(viewModelScope) keeps collecting until the ViewModel is destroyed.
     */
    private fun observeWorkouts() {
        getWorkoutsUseCase()
            .onEach { result ->
                when (result) {
                    is Result.Success -> _state.update { it.copy(
                        uiState = WorkoutListUiState.Success(result.data)
                    )}
                    is Result.Error -> _state.update { it.copy(
                        uiState = WorkoutListUiState.Error(
                            result.exception.message ?: "Unknown error"
                        )
                    )}
                }
            }
            .launchIn(viewModelScope)
    }

    // ---------------------------------------------------------------------------
    // Public: UI events
    // ---------------------------------------------------------------------------

    /** Called when user taps the "+" button */
    fun onCreateWorkoutClick() {
        _state.update { it.copy(showCreateDialog = true) }
    }

    /** Called when the create dialog is dismissed */
    fun onCreateDialogDismiss() {
        _state.update { it.copy(showCreateDialog = false) }
    }

    /**
     * Called when user confirms creating a new workout.
     * Creates the workout and sets navigateToWorkoutId so the UI can navigate.
     */
    fun onCreateWorkoutConfirm(name: String) {
        viewModelScope.launch {
            val workout = Workout(
                name = name,
                date = Date()
                // exercises defaults to emptyList()
            )
            when (val result = createWorkoutUseCase(workout)) {
                is Result.Success -> _state.update { it.copy(
                    showCreateDialog = false,
                    navigateToWorkoutId = result.data
                )}
                is Result.Error -> _state.update { it.copy(
                    showCreateDialog = false,
                    uiState = WorkoutListUiState.Error(
                        result.exception.message ?: "Failed to create workout"
                    )
                )}
            }
        }
    }

    /** Called when user confirms deleting a workout */
    fun onDeleteWorkout(workout: Workout) {
        viewModelScope.launch {
            deleteWorkoutUseCase(workout)
            // The workout list Flow automatically emits the updated list -
            // no manual state update needed here.
        }
    }

    /** Called after navigation has been consumed so we don't navigate twice */
    fun onNavigationConsumed() {
        _state.update { it.copy(navigateToWorkoutId = null) }
    }
}
