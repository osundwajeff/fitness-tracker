package com.fitnesstracker.domain.usecase

import com.fitnesstracker.domain.model.ExerciseTemplate
import com.fitnesstracker.domain.repository.WorkoutTemplateRepository
import com.fitnesstracker.domain.util.Result
import javax.inject.Inject

/** Add an exercise to a workout template. */
class AddExerciseToTemplateUseCase @Inject constructor(
    private val repository: WorkoutTemplateRepository
) {
    suspend operator fun invoke(templateId: Long, exercise: ExerciseTemplate): Result<Long> = try {
        val id = repository.addExerciseToTemplate(templateId, exercise)
        Result.Success(id)
    } catch (e: Exception) {
        Result.Error(e)
    }
}
