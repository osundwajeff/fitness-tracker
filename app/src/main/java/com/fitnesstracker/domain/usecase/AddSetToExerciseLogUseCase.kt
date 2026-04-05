package com.fitnesstracker.domain.usecase

import com.fitnesstracker.domain.model.ExerciseLog
import com.fitnesstracker.domain.model.ExerciseSet
import com.fitnesstracker.domain.repository.WorkoutSessionRepository
import com.fitnesstracker.domain.util.Result
import javax.inject.Inject

/**
 * Append a new set to an existing ExerciseLog.
 *
 * Rather than inserting a single row, we replace the entire set list
 * for the log. This keeps the data layer simple: updateExerciseLog
 * already deletes old sets and re-inserts the full list atomically.
 *
 * @param log    The ExerciseLog that already exists in the database.
 * @param newSet The new ExerciseSet to append.
 */
class AddSetToExerciseLogUseCase @Inject constructor(
    private val repository: WorkoutSessionRepository
) {
    suspend operator fun invoke(log: ExerciseLog, newSet: ExerciseSet): Result<Unit> = try {
        // Copy the log with the new set appended to the existing list.
        val updated = log.copy(sets = log.sets + newSet)
        repository.updateExerciseLog(updated)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }
}
