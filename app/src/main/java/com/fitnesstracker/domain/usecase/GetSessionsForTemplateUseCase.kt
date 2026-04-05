package com.fitnesstracker.domain.usecase

import com.fitnesstracker.domain.model.WorkoutSession
import com.fitnesstracker.domain.repository.WorkoutSessionRepository
import com.fitnesstracker.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** Observe all sessions for a given template as a reactive stream. */
class GetSessionsForTemplateUseCase @Inject constructor(
    private val repository: WorkoutSessionRepository
) {
    operator fun invoke(templateId: Long): Flow<Result<List<WorkoutSession>>> =
        repository.getSessionsForTemplate(templateId)
            .map { Result.Success(it) as Result<List<WorkoutSession>> }
            .catch { emit(Result.Error(Exception(it))) }
}
