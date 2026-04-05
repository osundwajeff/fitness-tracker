package com.fitnesstracker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Room entity for the workout_sessions table.
 *
 * A WorkoutSession is one logged training day — an instance of a WorkoutTemplate.
 * templateId links this session back to its template so we can group sessions
 * by plan and show history per template.
 */
@Entity(
    tableName = "workout_sessions",
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
data class WorkoutSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val templateId: Long,
    val date: Date,
    val note: String? = null
)
