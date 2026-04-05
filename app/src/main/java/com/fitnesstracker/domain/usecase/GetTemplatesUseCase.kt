package com.fitnesstracker.domain.usecase

import com.fitnesstracker.domain.model.WorkoutTemplate
import com.fitnesstracker.domain.repository.WorkoutTemplateRepository
import com.fitnesstracker.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** Observe all workout templates as a reactive stream. */
class GetTemplatesUseCase @Inject constructor(
    private val repository: WorkoutTemplateRepository
) {
    operator fun invoke(): Flow<Result<List<WorkoutTemplate>>> =
        repository.getAllTemplates()
            .map { Result.Success(it) as Result<List<WorkoutTemplate>> }
            .catch { emit(Result.Error(Exception(it))) }
}
