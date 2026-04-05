package com.fitnesstracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fitnesstracker.data.local.entity.WorkoutSessionEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for the workout_sessions table.
 *
 * Sessions are always associated with a template. The most common queries are:
 *   - Get all sessions for a template (template history screen)
 *   - Get a single session by ID (session detail screen)
 */
@Dao
interface WorkoutSessionDao {

    /** Observe all sessions for a template, newest first. */
    @Query("SELECT * FROM workout_sessions WHERE templateId = :templateId ORDER BY date DESC")
    fun getSessionsForTemplate(templateId: Long): Flow<List<WorkoutSessionEntity>>

    /** One-shot fetch of a single session. */
    @Query("SELECT * FROM workout_sessions WHERE id = :id")
    suspend fun getSessionById(id: Long): WorkoutSessionEntity?

    /** Insert a new session and return its generated ID. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: WorkoutSessionEntity): Long

    @Update
    suspend fun updateSession(session: WorkoutSessionEntity)

    @Delete
    suspend fun deleteSession(session: WorkoutSessionEntity)
}
