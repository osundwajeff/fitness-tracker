package com.fitnesstracker.domain.usecase

import com.fitnesstracker.domain.model.ExerciseLog
import com.fitnesstracker.domain.repository.WorkoutSessionRepository
import com.fitnesstracker.domain.util.Result
import javax.inject.Inject

/** Delete an exercise log and all its sets from a session. */
class DeleteExerciseLogUseCase @Inject constructor(
    private val repository: WorkoutSessionRepository
) {
    suspend operator fun invoke(log: ExerciseLog): Result<Unit> = try {
        repository.deleteExerciseLog(log)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }
}
