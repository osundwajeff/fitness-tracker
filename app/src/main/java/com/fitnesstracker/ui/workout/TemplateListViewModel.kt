package com.fitnesstracker.ui.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitnesstracker.domain.model.WorkoutTemplate
import com.fitnesstracker.domain.usecase.GetTemplatesUseCase
import com.fitnesstracker.domain.usecase.SeedDefaultTemplatesUseCase
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

sealed class TemplateListUiState {
    data object Loading : TemplateListUiState()
    data class Success(val templates: List<WorkoutTemplate>) : TemplateListUiState()
    data class Error(val message: String) : TemplateListUiState()
}

data class TemplateListScreenState(
    val uiState: TemplateListUiState = TemplateListUiState.Loading,
    val navigateToSessionId: Long? = null
)

/**
 * ViewModel for the home screen (template list).
 *
 * On init it:
 * 1. Seeds the 3 default templates if the database is empty (first launch)
 * 2. Starts observing the template list
 */
@HiltViewModel
class TemplateListViewModel @Inject constructor(
    private val getTemplatesUseCase: GetTemplatesUseCase,
    private val seedDefaultTemplatesUseCase: SeedDefaultTemplatesUseCase,
    private val startSessionUseCase: StartSessionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(TemplateListScreenState())
    val state: StateFlow<TemplateListScreenState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            // Seed first — then start observing so the list emits populated data
            seedDefaultTemplatesUseCase()
            observeTemplates()
        }
    }

    private fun observeTemplates() {
        getTemplatesUseCase()
            .onEach { result ->
                when (result) {
                    is Result.Success -> _state.update {
                        it.copy(uiState = TemplateListUiState.Success(result.data))
                    }
                    is Result.Error -> _state.update {
                        it.copy(uiState = TemplateListUiState.Error(
                            result.exception.message ?: "Unknown error"
                        ))
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    /** User taps "Start Session" on a template card. */
    fun onStartSession(templateId: Long) {
        viewModelScope.launch {
            when (val result = startSessionUseCase(templateId)) {
                is Result.Success -> _state.update {
                    it.copy(navigateToSessionId = result.data)
                }
                is Result.Error -> _state.update {
                    it.copy(uiState = TemplateListUiState.Error(
                        result.exception.message ?: "Failed to start session"
                    ))
                }
            }
        }
    }

    fun onNavigationConsumed() {
        _state.update { it.copy(navigateToSessionId = null) }
    }
}
