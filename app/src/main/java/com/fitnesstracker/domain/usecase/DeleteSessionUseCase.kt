package com.fitnesstracker.domain.usecase

import com.fitnesstracker.domain.model.WorkoutSession
import com.fitnesstracker.domain.repository.WorkoutSessionRepository
import com.fitnesstracker.domain.util.Result
import javax.inject.Inject

/** Delete a session and all its exercise logs and sets. */
class DeleteSessionUseCase @Inject constructor(
    private val repository: WorkoutSessionRepository
) {
    suspend operator fun invoke(session: WorkoutSession): Result<Unit> = try {
        repository.deleteSession(session)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }
}
