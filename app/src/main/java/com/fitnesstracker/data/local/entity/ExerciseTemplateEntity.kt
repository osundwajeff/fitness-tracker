package com.fitnesstracker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for the exercise_templates table.
 *
 * Each row is one exercise defined on a WorkoutTemplate (e.g. "Squat" on Tuesday Heavy).
 * orderIndex controls the display order on the template detail screen and during
 * session pre-population.
 */
@Entity(
    tableName = "exercise_templates",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutTemplateEntity::class,
            parentColumns = ["id"],
            childColumns = ["templateId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("templateId")]
)
data class ExerciseTemplateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val templateId: Long,
    val name: String,
    val muscleGroup: String? = null,
    val orderIndex: Int = 0
)
