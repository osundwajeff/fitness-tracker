package com.fitnesstracker.domain.repository

import com.fitnesstracker.domain.model.ExerciseBestSet
import com.fitnesstracker.domain.model.ExerciseLog
import com.fitnesstracker.domain.model.WorkoutSession
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for WorkoutSession operations.
 *
 * A session is a single logged training day, always tied to a template.
 */
interface WorkoutSessionRepository {

    /** Observe all sessions for a template, newest first. */
    fun getSessionsForTemplate(templateId: Long): Flow<List<WorkoutSession>>

    /** Get a single session with all its exercise logs and sets. */
    suspend fun getSessionById(id: Long): WorkoutSession?

    /**
     * Create a new session and return its generated ID.
     * The session should be pre-populated with ExerciseLogs copied from the template.
     */
    suspend fun createSession(session: WorkoutSession): Long

    /** Update a session (e.g. edit the note). */
    suspend fun updateSession(session: WorkoutSession)

    /** Delete a session and all its exercise logs and sets (via CASCADE). */
    suspend fun deleteSession(session: WorkoutSession)

    /** Add an exercise log to an existing session. Returns the generated log ID. */
    suspend fun addExerciseLog(sessionId: Long, log: ExerciseLog): Long

    /** Update an exercise log and replace all its sets. */
    suspend fun updateExerciseLog(log: ExerciseLog)

    /** Delete an exercise log and all its sets (via CASCADE). */
    suspend fun deleteExerciseLog(log: ExerciseLog)

    /**
     * Get the best set (heaviest weight) per session for a named exercise,
     * across all sessions of a given template.
     *
     * This is the core data source for the progress history screen.
     */
    suspend fun getExerciseHistory(exerciseName: String, templateId: Long): List<ExerciseBestSet>
}
