package com.fitnesstracker.data.mapper

import com.fitnesstracker.data.local.entity.ExerciseEntity
import com.fitnesstracker.data.local.entity.ExerciseSetEntity
import com.fitnesstracker.domain.model.Exercise
import com.fitnesstracker.domain.model.ExerciseSet

/**
 * Mappers for Exercise and ExerciseSet.
 *
 * These are Kotlin extension functions - they extend the entity/domain classes
 * with conversion methods without modifying the original classes.
 *
 * This is the Open/Closed principle (SOLID O): we extend behaviour
 * without modifying the original classes.
 *
 * Usage:
 *   val domainExercise = exerciseEntity.toDomain(sets = setEntities.toDomain())
 *   val entityExercise = domainExercise.toEntity(workoutId = 1L)
 */

// ---------------------------------------------------------------------------
// ExerciseSet: Entity → Domain
// ---------------------------------------------------------------------------

/**
 * Converts a single ExerciseSetEntity to domain ExerciseSet.
 * Note: 'id' is dropped - the domain model doesn't expose DB row IDs for sets.
 */
fun ExerciseSetEntity.toDomain(): ExerciseSet = ExerciseSet(
    reps = reps,
    weight = weight,
    isCompleted = isCompleted
)

/**
 * Convenience: convert a list of entities to domain models.
 * The 'map' call applies toDomain() to every item in the list.
 */
fun List<ExerciseSetEntity>.toDomain(): List<ExerciseSet> = map { it.toDomain() }

// ---------------------------------------------------------------------------
// ExerciseSet: Domain → Entity
// ---------------------------------------------------------------------------

/**
 * Converts domain ExerciseSet to ExerciseSetEntity for storage.
 * Requires the parent exerciseId because the entity needs the foreign key.
 *
 * setId = 0 means "auto-generate the ID" (Room's @PrimaryKey autoGenerate)
 */
fun ExerciseSet.toEntity(exerciseId: Long, setId: Long = 0): ExerciseSetEntity = ExerciseSetEntity(
    setId = setId,
    exerciseOwnerId = exerciseId,
    reps = reps,
    weight = weight,
    isCompleted = isCompleted
)

// ---------------------------------------------------------------------------
// Exercise: Entity → Domain
// ---------------------------------------------------------------------------

/**
 * Converts ExerciseEntity to domain Exercise.
 * Requires pre-fetched sets because entities don't embed child relations.
 *
 * This is intentional: Room doesn't auto-fetch relations unless you use
 * @Relation or @Transaction. We manage this manually in the repository
 * for clarity and control.
 */
fun ExerciseEntity.toDomain(sets: List<ExerciseSet> = emptyList()): Exercise = Exercise(
    id = exerciseId,
    name = name,
    muscleGroup = muscleGroup,
    notes = notes,
    sets = sets
)

/**
 * Convenience: convert a list of exercise entities to domain models.
 * The setsProvider suspend lambda lets the caller supply the sets for each exercise.
 *
 * suspend is required because fetching sets involves a DAO call.
 */
suspend fun List<ExerciseEntity>.toDomain(
    setsProvider: suspend (Long) -> List<ExerciseSet>
): List<Exercise> = map { entity ->
    entity.toDomain(sets = setsProvider(entity.exerciseId))
}

// ---------------------------------------------------------------------------
// Exercise: Domain → Entity
// ---------------------------------------------------------------------------

/**
 * Converts domain Exercise to ExerciseEntity for storage.
 * Requires the parent workoutId as the foreign key.
 *
 * exerciseId = 0 means "auto-generate the ID"
 */
fun Exercise.toEntity(workoutId: Long): ExerciseEntity = ExerciseEntity(
    exerciseId = id,
    workoutId = workoutId,
    name = name,
    muscleGroup = muscleGroup,
    notes = notes
)
