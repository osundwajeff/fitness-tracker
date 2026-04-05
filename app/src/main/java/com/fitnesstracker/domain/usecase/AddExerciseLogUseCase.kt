package com.fitnesstracker.domain.usecase

import com.fitnesstracker.domain.model.ExerciseLog
import com.fitnesstracker.domain.repository.WorkoutSessionRepository
import com.fitnesstracker.domain.util.Result
import javax.inject.Inject

/** Add an exercise log entry to an existing session. */
class AddExerciseLogUseCase @Inject constructor(
    private val repository: WorkoutSessionRepository
) {
    suspend operator fun invoke(sessionId: Long, log: ExerciseLog): Result<Long> = try {
        val id = repository.addExerciseLog(sessionId, log)
        Result.Success(id)
    } catch (e: Exception) {
        Result.Error(e)
    }
}
