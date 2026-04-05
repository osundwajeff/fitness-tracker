package com.fitnesstracker.domain.usecase

import com.fitnesstracker.domain.model.Workout
import com.fitnesstracker.domain.repository.WorkoutRepository
import com.fitnesstracker.domain.util.Result
import javax.inject.Inject

/**
 * Use Case: Delete a workout and all its exercises and sets.
 *
 * The cascade deletion is handled at the database level (foreign key CASCADE),
 * but knowing that is the repository's concern. The use case just says "delete this workout".
 *
 * Result<Unit>: Unit is Kotlin's equivalent of void - success means "it worked",
 * and there's no meaningful data to return.
 */
class DeleteWorkoutUseCase @Inject constructor(
    private val repository: WorkoutRepository
) {

    suspend operator fun invoke(workout: Workout): Result<Unit> {
        return try {
            repository.deleteWorkout(workout)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
