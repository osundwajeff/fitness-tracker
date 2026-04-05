package com.fitnesstracker.domain.model

/**
 * The actual performance of one exercise during a WorkoutSession.
 *
 * Name and muscleGroup are copied (denormalized) from the ExerciseTemplate at session
 * creation time. This is intentional: if you later rename an exercise on the template,
 * historical sessions should still show the name that was used at the time.
 *
 * @param id          Database primary key
 * @param sessionId   Foreign key → WorkoutSession
 * @param name        Exercise name (copied from template at session start)
 * @param muscleGroup Optional muscle group (copied from template)
 * @param sets        The sets performed for this exercise
 */
data class ExerciseLog(
    val id: Long = 0,
    val sessionId: Long = 0,
    val name: String,
    val muscleGroup: String? = null,
    val sets: List<ExerciseSet> = emptyList()
)

/**
 * A single set within an ExerciseLog.
 *
 * @param reps        Number of repetitions performed
 * @param weight      Weight used in kilograms
 * @param isCompleted Whether the set was completed (useful for in-progress sessions)
 */
data class ExerciseSet(
    val reps: Int,
    val weight: Double,
    val isCompleted: Boolean = false
)

/**
 * A summary of the best set for a given exercise in one session.
 * Used for the progress / history view.
 *
 * "Best" = the set with the highest weight in that session.
 *
 * @param sessionDate  The date the session was performed
 * @param reps         Reps in the best set
 * @param weight       Weight of the best set (kg)
 */
data class ExerciseBestSet(
    val sessionDate: java.util.Date,
    val reps: Int,
    val weight: Double
)
