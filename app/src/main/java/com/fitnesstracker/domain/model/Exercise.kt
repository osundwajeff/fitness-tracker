package com.fitnesstracker.domain.model

data class Exercise(
    val id: Long = 0,
    val name: String,
    val sets: List<ExerciseSet>,
    val muscleGroup: String? = null,
    val notes: String? = null
)

data class ExerciseSet(
    val reps: Int,
    val weight: Double,
    val isCompleted: Boolean = false
)