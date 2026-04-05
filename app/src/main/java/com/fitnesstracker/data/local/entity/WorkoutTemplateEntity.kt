package com.fitnesstracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for the workout_templates table.
 *
 * Stores the 3 pre-seeded training day blueprints:
 *   - Tuesday Heavy
 *   - Thursday Light
 *   - Saturday Moderate
 *
 * day is stored as String (name of java.time.DayOfWeek enum) — Room doesn't know
 * about DayOfWeek natively, so we store the name and convert in the mapper.
 *
 * intensity is stored as String (name of Intensity enum) — same reason.
 */
@Entity(tableName = "workout_templates")
data class WorkoutTemplateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val day: String,        // DayOfWeek.name(), e.g. "TUESDAY"
    val intensity: String   // Intensity.name(), e.g. "HEAVY"
)
