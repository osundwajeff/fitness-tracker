package com.fitnesstracker.domain.usecase

import com.fitnesstracker.domain.model.WorkoutTemplate
import com.fitnesstracker.domain.repository.WorkoutTemplateRepository
import com.fitnesstracker.domain.util.Result
import javax.inject.Inject

/** Get a single template with its exercises. */
class GetTemplateByIdUseCase @Inject constructor(
    private val repository: WorkoutTemplateRepository
) {
    suspend operator fun invoke(id: Long): Result<WorkoutTemplate> = try {
        val template = repository.getTemplateById(id)
        if (template != null) Result.Success(template)
        else Result.Error(Exception("Template not found"))
    } catch (e: Exception) {
        Result.Error(e)
    }
}
