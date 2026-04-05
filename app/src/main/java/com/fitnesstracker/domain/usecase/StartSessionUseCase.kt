package com.fitnesstracker.domain.usecase

import com.fitnesstracker.domain.model.ExerciseLog
import com.fitnesstracker.domain.model.WorkoutSession
import com.fitnesstracker.domain.repository.WorkoutSessionRepository
import com.fitnesstracker.domain.repository.WorkoutTemplateRepository
import com.fitnesstracker.domain.util.Result
import java.util.Date
import javax.inject.Inject

/**
 * Start a new training session from a template.
 *
 * This use case:
 * 1. Loads the template's exercises
 * 2. Creates a new WorkoutSession pre-populated with ExerciseLogs (no sets yet —
 *    the user fills those in during the session)
 * 3. Persists the session and returns its generated ID
 *
 * The user then navigates to the session detail screen to log their sets.
 */
class StartSessionUseCase @Inject constructor(
    private val templateRepository: WorkoutTemplateRepository,
    private val sessionRepository: WorkoutSessionRepository
) {
    suspend operator fun invoke(templateId: Long): Result<Long> = try {
        val template = templateRepository.getTemplateById(templateId)
            ?: return Result.Error(Exception("Template not found"))

        // Pre-populate exercise logs from the template (empty sets — user fills them in)
        val exerciseLogs = template.exercises.map { exerciseTemplate ->
            ExerciseLog(
                name = exerciseTemplate.name,
                muscleGroup = exerciseTemplate.muscleGroup,
                sets = emptyList()
            )
        }

        val session = WorkoutSession(
            templateId = templateId,
            date = Date(),
            exercises = exerciseLogs
        )

        val sessionId = sessionRepository.createSession(session)
        Result.Success(sessionId)
    } catch (e: Exception) {
        Result.Error(e)
    }
}
