package com.fitnesstracker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for the exercise_sets table.
 *
 * One set = one row of reps + weight within an ExerciseLog.
 * CASCADE delete means removing an ExerciseLog removes all its sets automatically.
 */
@Entity(
    tableName = "exercise_sets",
    foreignKeys = [
        ForeignKey(
            entity = ExerciseLogEntity::class,
            parentColumns = ["id"],
            childColumns = ["exerciseLogId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("exerciseLogId")]
)
data class ExerciseSetEntity(
    @PrimaryKey(autoGenerate = true) val setId: Long = 0,
    val exerciseLogId: Long,
    val reps: Int,
    val weight: Double,
    val isCompleted: Boolean
)
