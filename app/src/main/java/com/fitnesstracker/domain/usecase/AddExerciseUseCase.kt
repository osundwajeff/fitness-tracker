package com.fitnesstracker.domain.usecase

import com.fitnesstracker.domain.model.Exercise
import com.fitnesstracker.domain.repository.WorkoutRepository
import com.fitnesstracker.domain.util.Result
import javax.inject.Inject

/**
 * Use Case: Add an exercise (with its sets) to a workout.
 *
 * Business rules:
 * - Exercise name cannot be blank
 * - An exercise must have at least one set
 *
 * Returns the generated exercise ID so the UI can immediately navigate
 * to the new exercise or reference it.
 */
class AddExerciseUseCase @Inject constructor(
    private val repository: WorkoutRepository
) {

    suspend operator fun invoke(workoutId: Long, exercise: Exercise): Result<Long> {
        if (exercise.name.isBlank()) {
            return Result.Error(IllegalArgumentException("Exercise name cannot be blank"))
        }
        if (exercise.sets.isEmpty()) {
            return Result.Error(IllegalArgumentException("Exercise must have at least one set"))
        }

        return try {
            val id = repository.addExercise(workoutId, exercise)
            Result.Success(id)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
