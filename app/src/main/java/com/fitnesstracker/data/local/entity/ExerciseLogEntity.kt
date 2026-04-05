package com.fitnesstracker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for the exercise_logs table.
 *
 * Records one exercise as actually performed during a WorkoutSession.
 * name and muscleGroup are copied (denormalized) from ExerciseTemplateEntity
 * at session creation time — so history is never broken if the template changes.
 */
@Entity(
    tableName = "exercise_logs",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sessionId")]
)
data class ExerciseLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val name: String,
    val muscleGroup: String? = null
)
