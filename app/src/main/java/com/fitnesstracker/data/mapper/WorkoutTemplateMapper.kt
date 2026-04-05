package com.fitnesstracker.data.mapper

import com.fitnesstracker.data.local.entity.ExerciseTemplateEntity
import com.fitnesstracker.data.local.entity.WorkoutTemplateEntity
import com.fitnesstracker.domain.model.ExerciseTemplate
import com.fitnesstracker.domain.model.Intensity
import com.fitnesstracker.domain.model.WorkoutTemplate
import java.time.DayOfWeek

// ---------------------------------------------------------------------------
// WorkoutTemplate: Entity → Domain
// ---------------------------------------------------------------------------

/**
 * Converts a WorkoutTemplateEntity to a domain WorkoutTemplate.
 * day and intensity are stored as enum name strings and parsed back here.
 */
fun WorkoutTemplateEntity.toDomain(exercises: List<ExerciseTemplate> = emptyList()): WorkoutTemplate =
    WorkoutTemplate(
        id = id,
        name = name,
        day = DayOfWeek.valueOf(day),
        intensity = Intensity.valueOf(intensity),
        exercises = exercises
    )

suspend fun List<WorkoutTemplateEntity>.toDomain(
    exercisesProvider: suspend (Long) -> List<ExerciseTemplate> = { emptyList() }
): List<WorkoutTemplate> = map { entity ->
    entity.toDomain(exercises = exercisesProvider(entity.id))
}

// ---------------------------------------------------------------------------
// WorkoutTemplate: Domain → Entity
// ---------------------------------------------------------------------------

fun WorkoutTemplate.toEntity(): WorkoutTemplateEntity = WorkoutTemplateEntity(
    id = id,
    name = name,
    day = day.name,
    intensity = intensity.name
)

// ---------------------------------------------------------------------------
// ExerciseTemplate: Entity → Domain
// ---------------------------------------------------------------------------

fun ExerciseTemplateEntity.toDomain(): ExerciseTemplate = ExerciseTemplate(
    id = id,
    templateId = templateId,
    name = name,
    muscleGroup = muscleGroup,
    orderIndex = orderIndex
)

fun List<ExerciseTemplateEntity>.toDomain(): List<ExerciseTemplate> = map { it.toDomain() }

// ---------------------------------------------------------------------------
// ExerciseTemplate: Domain → Entity
// ---------------------------------------------------------------------------

fun ExerciseTemplate.toEntity(): ExerciseTemplateEntity = ExerciseTemplateEntity(
    id = id,
    templateId = templateId,
    name = name,
    muscleGroup = muscleGroup,
    orderIndex = orderIndex
)
