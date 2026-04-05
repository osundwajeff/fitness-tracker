package com.fitnesstracker.domain.usecase

import com.fitnesstracker.domain.model.Exercise
import com.fitnesstracker.domain.repository.WorkoutRepository
import com.fitnesstracker.domain.util.Result
import javax.inject.Inject

/**
 * Use Case: Update an exercise and replace all its sets.
 */
class UpdateExerciseUseCase @Inject constructor(
    private val repository: WorkoutRepository
) {

    suspend operator fun invoke(workoutId: Long, exercise: Exercise): Result<Unit> {
        if (exercise.name.isBlank()) {
            return Result.Error(IllegalArgumentException("Exercise name cannot be blank"))
        }

        return try {
            repository.updateExercise(workoutId, exercise)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
