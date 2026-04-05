package com.fitnesstracker.ui.workout

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitnesstracker.domain.model.ExerciseTemplate
import com.fitnesstracker.domain.model.WorkoutTemplate
import com.fitnesstracker.domain.model.WorkoutSession
import com.fitnesstracker.domain.usecase.AddExerciseToTemplateUseCase
import com.fitnesstracker.domain.usecase.DeleteSessionUseCase
import com.fitnesstracker.domain.usecase.GetSessionsForTemplateUseCase
import com.fitnesstracker.domain.usecase.GetTemplateByIdUseCase
import com.fitnesstracker.domain.usecase.RemoveExerciseFromTemplateUseCase
import com.fitnesstracker.domain.usecase.StartSessionUseCase
import com.fitnesstracker.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class TemplateDetailUiState {
    data object Loading : TemplateDetailUiState()
    data class Success(
        val template: WorkoutTemplate,
        val sessions: List<WorkoutSession>
    ) : TemplateDetailUiState()
    data class Error(val message: String) : TemplateDetailUiState()
}

data class TemplateDetailScreenState(
    val uiState: TemplateDetailUiState = TemplateDetailUiState.Loading,
    val showAddExerciseDialog: Boolean = false,
    val navigateToSessionId: Long? = null
)

/**
 * ViewModel for the template detail screen.
 *
 * Shows the template's exercise list, past sessions, and provides
 * "Start Session" and "Add/Remove Exercise" actions.
 */
@HiltViewModel
class TemplateDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTemplateByIdUseCase: GetTemplateByIdUseCase,
    private val getSessionsForTemplateUseCase: GetSessionsForTemplateUseCase,
    private val startSessionUseCase: StartSessionUseCase,
    private val addExerciseToTemplateUseCase: AddExerciseToTemplateUseCase,
    private val removeExerciseFromTemplateUseCase: RemoveExerciseFromTemplateUseCase,
    private val deleteSessionUseCase: DeleteSessionUseCase
) : ViewModel() {

    private val templateId: Long = checkNotNull(savedStateHandle["templateId"])

    private val _state = MutableStateFlow(TemplateDetailScreenState())
    val state: StateFlow<TemplateDetailScreenState> = _state.asStateFlow()

    init {
        loadTemplate()
        observeSessions()
    }

    private fun loadTemplate() {
        viewModelScope.launch {
            when (val result = getTemplateByIdUseCase(templateId)) {
                is Result.Success -> {
                    val current = _state.value.uiState
                    val sessions = if (current is TemplateDetailUiState.Success) current.sessions else emptyList()
                    _state.update { it.copy(uiState = TemplateDetailUiState.Success(result.data, sessions)) }
                }
                is Result.Error -> _state.update {
                    it.copy(uiState = TemplateDetailUiState.Error(
                        result.exception.message ?: "Failed to load template"
                    ))
                }
            }
        }
    }

    private fun observeSessions() {
        getSessionsForTemplateUseCase(templateId)
            .onEach { result ->
                when (result) {
                    is Result.Success -> {
                        val current = _state.value.uiState
                        val template = if (current is TemplateDetailUiState.Success) current.template else return@onEach
                        _state.update { it.copy(uiState = TemplateDetailUiState.Success(template, result.data)) }
                    }
                    is Result.Error -> _state.update {
                        it.copy(uiState = TemplateDetailUiState.Error(
                            result.exception.message ?: "Unknown error"
                        ))
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun onStartSession() {
        viewModelScope.launch {
            when (val result = startSessionUseCase(templateId)) {
                is Result.Success -> _state.update { it.copy(navigateToSessionId = result.data) }
                is Result.Error -> _state.update {
                    it.copy(uiState = TemplateDetailUiState.Error(
                        result.exception.message ?: "Failed to start session"
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

    fun onAddExerciseConfirm(name: String, muscleGroup: String?) {
        viewModelScope.launch {
            val exercise = ExerciseTemplate(
                name = name,
                muscleGroup = muscleGroup.takeIf { !it.isNullOrBlank() }
            )
            addExerciseToTemplateUseCase(templateId, exercise)
            _state.update { it.copy(showAddExerciseDialog = false) }
            loadTemplate()
        }
    }

    fun onRemoveExercise(exercise: ExerciseTemplate) {
        viewModelScope.launch {
            removeExerciseFromTemplateUseCase(exercise)
            loadTemplate()
        }
    }

    fun onDeleteSession(session: WorkoutSession) {
        viewModelScope.launch {
            deleteSessionUseCase(session)
            // sessions Flow auto-updates via observeSessions()
        }
    }

    fun onNavigationConsumed() {
        _state.update { it.copy(navigateToSessionId = null) }
    }
}
