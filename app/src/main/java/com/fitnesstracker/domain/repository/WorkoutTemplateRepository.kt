package com.fitnesstracker.domain.repository

import com.fitnesstracker.domain.model.ExerciseTemplate
import com.fitnesstracker.domain.model.WorkoutTemplate
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for WorkoutTemplate operations.
 *
 * Lives in the domain layer — no Room, no Android imports.
 * The implementation (WorkoutTemplateRepositoryImpl) is in the data layer.
 */
interface WorkoutTemplateRepository {

    /** Observe all templates as a reactive stream. */
    fun getAllTemplates(): Flow<List<WorkoutTemplate>>

    /** Get a single template with its exercises. Returns null if not found. */
    suspend fun getTemplateById(id: Long): WorkoutTemplate?

    /** Add an exercise to a template. Returns the generated exercise ID. */
    suspend fun addExerciseToTemplate(templateId: Long, exercise: ExerciseTemplate): Long

    /** Remove an exercise from a template. */
    suspend fun removeExerciseFromTemplate(exercise: ExerciseTemplate)

    /** Update an exercise on a template (e.g. rename, change muscle group). */
    suspend fun updateExerciseOnTemplate(exercise: ExerciseTemplate)

    /**
     * Ensure the 3 default templates exist in the database.
     * Called once on app startup — a no-op if templates already exist.
     */
    suspend fun seedDefaultTemplatesIfEmpty()
}
