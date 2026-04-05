package com.fitnesstracker.domain.usecase

import com.fitnesstracker.domain.model.WorkoutSession
import com.fitnesstracker.domain.repository.WorkoutSessionRepository
import com.fitnesstracker.domain.util.Result
import javax.inject.Inject

/** Get a single session with all its exercise logs. */
class GetSessionByIdUseCase @Inject constructor(
    private val repository: WorkoutSessionRepository
) {
    suspend operator fun invoke(id: Long): Result<WorkoutSession> = try {
        val session = repository.getSessionById(id)
        if (session != null) Result.Success(session)
        else Result.Error(Exception("Session not found"))
    } catch (e: Exception) {
        Result.Error(e)
    }
}
