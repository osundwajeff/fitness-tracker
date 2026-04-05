package com.fitnesstracker.domain.usecase

import com.fitnesstracker.domain.model.Workout
import com.fitnesstracker.domain.repository.WorkoutRepository
import com.fitnesstracker.domain.util.Result
import javax.inject.Inject

/**
 * Use Case: Create a new workout.
 *
 * Business rules enforced here (not in ViewModel, not in Repository):
 * - Workout name cannot be blank
 * - These validations are pure Kotlin - no Android/Room dependency needed to test them
 *
 * This is the key advantage of the domain layer: business rules live in one place,
 * independently testable, reusable across different entry points (UI, background job, etc.)
 */
class CreateWorkoutUseCase @Inject constructor(
    private val repository: WorkoutRepository
) {

    suspend operator fun invoke(workout: Workout): Result<Long> {
        // Business rule: name must not be blank
        if (workout.name.isBlank()) {
            return Result.Error(IllegalArgumentException("Workout name cannot be blank"))
        }

        return try {
            val id = repository.createWorkout(workout)
            Result.Success(id)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
