package com.fitnesstracker.ui.workout

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitnesstracker.domain.model.ExerciseLog
import com.fitnesstracker.domain.model.ExerciseSet
import com.fitnesstracker.domain.model.WorkoutSession
import com.fitnesstracker.domain.usecase.AddExerciseLogUseCase
import com.fitnesstracker.domain.usecase.AddSetToExerciseLogUseCase
import com.fitnesstracker.domain.usecase.DeleteExerciseLogUseCase
import com.fitnesstracker.domain.usecase.GetSessionByIdUseCase
import com.fitnesstracker.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SessionDetailUiState {
    data object Loading : SessionDetailUiState()
    data class Success(val session: WorkoutSession) : SessionDetailUiState()
    data class Error(val message: String) : SessionDetailUiState()
}

data class SessionDetailScreenState(
    val uiState: SessionDetailUiState = SessionDetailUiState.Loading,
    val showAddExerciseDialog: Boolean = false,
    // Non-null while the "add set" dialog is open; holds the log being targeted.
    val addSetTargetLog: ExerciseLog? = null
)

/**
 * ViewModel for the session detail screen.
 *
 * Displays the exercise logs for a single training session.
 * Users can add sets to exercises, or add extra exercises to the session.
 */
@HiltViewModel
class SessionDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getSessionByIdUseCase: GetSessionByIdUseCase,
    private val addExerciseLogUseCase: AddExerciseLogUseCase,
    private val addSetToExerciseLogUseCase: AddSetToExerciseLogUseCase,
    private val deleteExerciseLogUseCase: DeleteExerciseLogUseCase
) : ViewModel() {

    private val sessionId: Long = checkNotNull(savedStateHandle["sessionId"])

    private val _state = MutableStateFlow(SessionDetailScreenState())
    val state: StateFlow<SessionDetailScreenState> = _state.asStateFlow()

    init {
        loadSession()
    }

    fun loadSession() {
        viewModelScope.launch {
            _state.update { it.copy(uiState = SessionDetailUiState.Loading) }
            when (val result = getSessionByIdUseCase(sessionId)) {
                is Result.Success -> _state.update {
                    it.copy(uiState = SessionDetailUiState.Success(result.data))
                }
                is Result.Error -> _state.update {
                    it.copy(uiState = SessionDetailUiState.Error(
                        result.exception.message ?: "Failed to load session"
                    ))
                }
            }
        }
    }

    fun onAddExerciseClick() {
        _state.update { it.copy(showAddExerciseDialog = true) }
    }

    fun onAddExerciseDialogDismiss() {
        _state.update { it.copy(showAddExerciseDialog = false) }
    }

    fun onAddExerciseConfirm(log: ExerciseLog) {
        viewModelScope.launch {
            addExerciseLogUseCase(sessionId, log)
            _state.update { it.copy(showAddExerciseDialog = false) }
            loadSession()
        }
    }

    fun onDeleteExerciseLog(log: ExerciseLog) {
        viewModelScope.launch {
            deleteExerciseLogUseCase(log)
            loadSession()
        }
    }

    // --- Add-set dialog ---

    /** Open the add-set dialog for a specific exercise log. */
    fun onAddSetClick(log: ExerciseLog) {
        _state.update { it.copy(addSetTargetLog = log) }
    }

    fun onAddSetDialogDismiss() {
        _state.update { it.copy(addSetTargetLog = null) }
    }

    /** Append [newSet] to [log] and refresh the screen. */
    fun onAddSetConfirm(log: ExerciseLog, newSet: ExerciseSet) {
        viewModelScope.launch {
            addSetToExerciseLogUseCase(log, newSet)
            _state.update { it.copy(addSetTargetLog = null) }
            loadSession()
        }
    }
}
