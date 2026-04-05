package com.fitnesstracker.data.mapper

import com.fitnesstracker.data.local.entity.WorkoutEntity
import com.fitnesstracker.domain.model.Exercise
import com.fitnesstracker.domain.model.Workout

/**
 * Mappers for Workout.
 *
 * The Workout entity and domain model differ in one important way:
 * - WorkoutEntity: flat, no exercises (exercises are a separate table)
 * - Workout (domain): has a List<Exercise> embedded
 *
 * This is the nature of relational databases - we normalize data into tables
 * and then reassemble the full object in the repository layer.
 */

// ---------------------------------------------------------------------------
// Workout: Entity → Domain
// ---------------------------------------------------------------------------

/**
 * Converts WorkoutEntity to domain Workout.
 *
 * Exercises are passed in separately because WorkoutEntity doesn't know
 * about them - they must be fetched from ExerciseDao and mapped first.
 *
 * This forces the repository to be explicit about what data it's loading,
 * avoiding accidental N+1 query problems.
 */
fun WorkoutEntity.toDomain(exercises: List<Exercise> = emptyList()): Workout = Workout(
    id = id,
    name = name,
    date = date,
    note = note,
    exercises = exercises
)

/**
 * Convenience: convert a list of workout entities to domain models.
 * The exercisesProvider suspend lambda supplies the exercises for each workout.
 *
 * suspend is required because fetching exercises involves DAO calls.
 *
 * Example usage in repository:
 *   workoutEntities.toDomain { workoutId ->
 *       exerciseEntities.filter { it.workoutId == workoutId }.toDomain(...)
 *   }
 */
suspend fun List<WorkoutEntity>.toDomain(
    exercisesProvider: suspend (Long) -> List<Exercise> = { emptyList() }
): List<Workout> = map { entity ->
    entity.toDomain(exercises = exercisesProvider(entity.id))
}

// ---------------------------------------------------------------------------
// Workout: Domain → Entity
// ---------------------------------------------------------------------------

/**
 * Converts domain Workout to WorkoutEntity for storage.
 *
 * Notice exercises are dropped here - they are stored separately
 * via ExerciseDao, not embedded in WorkoutEntity.
 */
fun Workout.toEntity(): WorkoutEntity = WorkoutEntity(
    id = id,
    name = name,
    date = date,
    note = note
)
