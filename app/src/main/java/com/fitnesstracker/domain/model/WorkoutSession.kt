package com.fitnesstracker.domain.model

import java.util.Date

/**
 * A single logged training session — an instance of a WorkoutTemplate on a specific date.
 *
 * Every time the user trains, a WorkoutSession is created from the active template.
 * The exercises are pre-populated from the template; the user fills in the actual
 * weights and reps for each set.
 *
 * @param id          Database primary key
 * @param templateId  Foreign key → WorkoutTemplate (which plan this session came from)
 * @param date        The date the session was performed
 * @param note        Optional free-text note (e.g. "felt tired today")
 * @param exercises   The exercise logs recorded during this session
 */
data class WorkoutSession(
    val id: Long = 0,
    val templateId: Long,
    val date: Date,
    val note: String? = null,
    val exercises: List<ExerciseLog> = emptyList()
)
