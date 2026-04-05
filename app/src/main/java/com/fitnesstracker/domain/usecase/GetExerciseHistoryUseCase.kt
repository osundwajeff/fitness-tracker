package com.fitnesstracker.domain.usecase

import com.fitnesstracker.domain.model.ExerciseBestSet
import com.fitnesstracker.domain.repository.WorkoutSessionRepository
import com.fitnesstracker.domain.util.Result
import javax.inject.Inject

/**
 * Get the progress history for one exercise across all sessions of a template.
 *
 * Returns one ExerciseBestSet per session (best = highest weight that session).
 * Ordered by date ascending — oldest to newest — for a progress view.
 */
class GetExerciseHistoryUseCase @Inject constructor(
    private val repository: WorkoutSessionRepository
) {
    suspend operator fun invoke(
        exerciseName: String,
        templateId: Long
    ): Result<List<ExerciseBestSet>> = try {
        val history = repository.getExerciseHistory(exerciseName, templateId)
        Result.Success(history)
    } catch (e: Exception) {
        Result.Error(e)
    }
}
