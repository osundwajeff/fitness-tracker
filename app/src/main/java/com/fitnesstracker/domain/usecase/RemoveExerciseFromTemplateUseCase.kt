package com.fitnesstracker.domain.usecase

import com.fitnesstracker.domain.model.ExerciseTemplate
import com.fitnesstracker.domain.repository.WorkoutTemplateRepository
import com.fitnesstracker.domain.util.Result
import javax.inject.Inject

/** Remove an exercise from a workout template. */
class RemoveExerciseFromTemplateUseCase @Inject constructor(
    private val repository: WorkoutTemplateRepository
) {
    suspend operator fun invoke(exercise: ExerciseTemplate): Result<Unit> = try {
        repository.removeExerciseFromTemplate(exercise)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }
}
