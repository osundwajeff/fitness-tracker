package com.fitnesstracker.domain.model

import java.time.DayOfWeek

/**
 * A recurring, fixed workout plan — the blueprint for a training day.
 *
 * There are exactly 3 templates pre-seeded on first launch:
 *   - Tuesday Heavy
 *   - Thursday Light
 *   - Saturday Moderate
 *
 * Users never create templates manually; they just add/remove exercises from them.
 * Each time a user trains, a WorkoutSession is created from a template.
 *
 * @param id         Database primary key
 * @param name       Display name, e.g. "Tuesday Heavy"
 * @param day        The day of the week this session belongs to
 * @param intensity  The training intensity (HEAVY / LIGHT / MODERATE)
 * @param exercises  The exercises that make up this template (editable)
 */
data class WorkoutTemplate(
    val id: Long = 0,
    val name: String,
    val day: DayOfWeek,
    val intensity: Intensity,
    val exercises: List<ExerciseTemplate> = emptyList()
)

/**
 * A single exercise defined on a WorkoutTemplate.
 *
 * This is the "blueprint" exercise — it defines WHAT to do, not the actual
 * weights/reps performed (that lives in ExerciseLog on a WorkoutSession).
 *
 * @param id           Database primary key
 * @param templateId   Foreign key → WorkoutTemplate
 * @param name         Exercise name, e.g. "Squat"
 * @param muscleGroup  Optional, e.g. "Quads"
 * @param orderIndex   Display order within the template (user can reorder)
 */
data class ExerciseTemplate(
    val id: Long = 0,
    val templateId: Long = 0,
    val name: String,
    val muscleGroup: String? = null,
    val orderIndex: Int = 0
)
