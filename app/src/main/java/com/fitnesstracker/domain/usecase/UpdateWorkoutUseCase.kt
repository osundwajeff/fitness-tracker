package com.fitnesstracker.domain.usecase

import com.fitnesstracker.domain.model.Workout
import com.fitnesstracker.domain.repository.WorkoutRepository
import com.fitnesstracker.domain.util.Result
import javax.inject.Inject

/**
 * Use Case: Update an existing workout's metadata (name, date, note).
 *
 * Same pattern: validate first, then delegate to repository.
 * Note we do NOT update exercises here - that's handled by AddExerciseUseCase
 * and DeleteExerciseUseCase. Each use case does one thing.
 */
class UpdateWorkoutUseCase @Inject constructor(
    private val repository: WorkoutRepository
) {

    suspend operator fun invoke(workout: Workout): Result<Unit> {
        if (workout.name.isBlank()) {
            return Result.Error(IllegalArgumentException("Workout name cannot be blank"))
        }

        return try {
            repository.updateWorkout(workout)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
