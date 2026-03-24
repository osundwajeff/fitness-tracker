package com.fitnesstracker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "exercise_sets",
    foreignKeys = [
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["exerciseId"],
            childColumns = ["exerciseOwnerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("exerciseOwnerId")]
)
data class ExerciseSetEntity(
    @PrimaryKey(autoGenerate = true) val setId: Long = 0,
    val exerciseOwnerId: Long,
    val reps: Int,
    val weight: Double,
    val isCompleted: Boolean
)
