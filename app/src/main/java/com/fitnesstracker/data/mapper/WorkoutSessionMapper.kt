package com.fitnesstracker.data.mapper

import com.fitnesstracker.data.local.entity.ExerciseLogEntity
import com.fitnesstracker.data.local.entity.ExerciseSetEntity
import com.fitnesstracker.data.local.entity.WorkoutSessionEntity
import com.fitnesstracker.domain.model.ExerciseLog
import com.fitnesstracker.domain.model.ExerciseSet
import com.fitnesstracker.domain.model.WorkoutSession
import java.util.Date

// ---------------------------------------------------------------------------
// ExerciseSet: Entity → Domain
// ---------------------------------------------------------------------------

fun ExerciseSetEntity.toDomain(): ExerciseSet = ExerciseSet(
    reps = reps,
    weight = weight,
    isCompleted = isCompleted
)

fun List<ExerciseSetEntity>.toDomain(): List<ExerciseSet> = map { it.toDomain() }

// ---------------------------------------------------------------------------
// ExerciseSet: Domain → Entity
// ---------------------------------------------------------------------------

fun ExerciseSet.toEntity(exerciseLogId: Long, setId: Long = 0): ExerciseSetEntity =
    ExerciseSetEntity(
        setId = setId,
        exerciseLogId = exerciseLogId,
        reps = reps,
        weight = weight,
        isCompleted = isCompleted
    )

// ---------------------------------------------------------------------------
// ExerciseLog: Entity → Domain
// ---------------------------------------------------------------------------

fun ExerciseLogEntity.toDomain(sets: List<ExerciseSet> = emptyList()): ExerciseLog = ExerciseLog(
    id = id,
    sessionId = sessionId,
    name = name,
    muscleGroup = muscleGroup,
    sets = sets
)

@JvmName("exerciseLogEntitiesToDomain")
suspend fun List<ExerciseLogEntity>.toDomain(
    setsProvider: suspend (Long) -> List<ExerciseSet>
): List<ExerciseLog> = map { entity ->
    entity.toDomain(sets = setsProvider(entity.id))
}

// ---------------------------------------------------------------------------
// ExerciseLog: Domain → Entity
// ---------------------------------------------------------------------------

fun ExerciseLog.toEntity(): ExerciseLogEntity = ExerciseLogEntity(
    id = id,
    sessionId = sessionId,
    name = name,
    muscleGroup = muscleGroup
)

// ---------------------------------------------------------------------------
// WorkoutSession: Entity → Domain
// ---------------------------------------------------------------------------

fun WorkoutSessionEntity.toDomain(exercises: List<ExerciseLog> = emptyList()): WorkoutSession =
    WorkoutSession(
        id = id,
        templateId = templateId,
        date = date,
        note = note,
        exercises = exercises
    )

@JvmName("workoutSessionEntitiesToDomain")
suspend fun List<WorkoutSessionEntity>.toDomain(
    exercisesProvider: suspend (Long) -> List<ExerciseLog> = { emptyList() }
): List<WorkoutSession> = map { entity ->
    entity.toDomain(exercises = exercisesProvider(entity.id))
}

// ---------------------------------------------------------------------------
// WorkoutSession: Domain → Entity
// ---------------------------------------------------------------------------

fun WorkoutSession.toEntity(): WorkoutSessionEntity = WorkoutSessionEntity(
    id = id,
    templateId = templateId,
    date = date,
    note = note
)
