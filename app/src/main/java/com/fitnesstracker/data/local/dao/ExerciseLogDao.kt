package com.fitnesstracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fitnesstracker.data.local.entity.ExerciseLogEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for the exercise_logs table.
 *
 * An ExerciseLog records one exercise as actually performed in a session.
 * The key extra query here is getByNameAcrossSessions — this powers the progress
 * history view: "show me all times I did Squat, across all sessions".
 */
@Dao
interface ExerciseLogDao {

    /** Observe all exercise logs for a session. */
    @Query("SELECT * FROM exercise_logs WHERE sessionId = :sessionId ORDER BY id ASC")
    fun getLogsForSession(sessionId: Long): Flow<List<ExerciseLogEntity>>

    /** One-shot snapshot — used in mappers to assemble the full session object. */
    @Query("SELECT * FROM exercise_logs WHERE sessionId = :sessionId ORDER BY id ASC")
    suspend fun getLogsForSessionOnce(sessionId: Long): List<ExerciseLogEntity>

    /**
     * Get all exercise logs with a given name across ALL sessions for a template.
     * Orders by session date ascending for progress charts/lists.
     *
     * This is the core query for the progress history screen.
     * We join through workout_sessions to filter by templateId and get the date.
     */
    @Query("""
        SELECT el.* FROM exercise_logs el
        INNER JOIN workout_sessions ws ON el.sessionId = ws.id
        WHERE el.name = :exerciseName
        AND ws.templateId = :templateId
        ORDER BY ws.date ASC
    """)
    suspend fun getLogsByNameForTemplate(
        exerciseName: String,
        templateId: Long
    ): List<ExerciseLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ExerciseLogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogs(logs: List<ExerciseLogEntity>): List<Long>

    @Update
    suspend fun updateLog(log: ExerciseLogEntity)

    @Delete
    suspend fun deleteLog(log: ExerciseLogEntity)
}
