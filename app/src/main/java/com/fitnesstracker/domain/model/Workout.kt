package com.fitnesstracker.domain.model

import java.util.Date

data class Workout(
    val id: Long = 0,
    val name: String,
    val date: Date,
    val exercises: List<Exercise> = emptyList(),
    val note: String? = null
)