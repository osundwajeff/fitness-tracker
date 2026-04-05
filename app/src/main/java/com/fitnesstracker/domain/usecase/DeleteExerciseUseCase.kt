package com.fitnesstracker.domain.usecase

import com.fitnesstracker.domain.model.Exercise
import com.fitnesstracker.domain.repository.WorkoutRepository
import com.fitnesstracker.domain.util.Result
import javax.inject.Inject

/**
 * Use Case: Delete an exercise and all its sets.
 *
 * Sets are cleaned up automatically via foreign key CASCADE in Room.
 */
class DeleteExerciseUseCase @Inject constructor(
    private val repository: WorkoutRepository
) {

    suspend operator fun invoke(exercise: Exercise): Result<Unit> {
        return try {
            repository.deleteExercise(exercise)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
