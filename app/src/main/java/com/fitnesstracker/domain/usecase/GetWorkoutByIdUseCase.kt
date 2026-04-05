package com.fitnesstracker.domain.usecase

import com.fitnesstracker.domain.model.Workout
import com.fitnesstracker.domain.repository.WorkoutRepository
import com.fitnesstracker.domain.util.Result
import javax.inject.Inject

/**
 * Use Case: Get a single workout by ID with all its exercises and sets.
 *
 * Returns Result.Success(workout) if found, Result.Success(null) if not found,
 * or Result.Error if a database exception occurred.
 *
 * Note on nullable Success: returning Success(null) vs Error distinguishes
 * "not found" (expected, recoverable) from "database crashed" (unexpected).
 */
class GetWorkoutByIdUseCase @Inject constructor(
    private val repository: WorkoutRepository
) {

    /**
     * Fetches a workout by its ID.
     * This is a one-shot suspend function - not a Flow - because detail screens
     * typically load once on open rather than needing a live stream.
     */
    suspend operator fun invoke(id: Long): Result<Workout?> {
        return try {
            val workout = repository.getWorkoutById(id)
            Result.Success(workout)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
