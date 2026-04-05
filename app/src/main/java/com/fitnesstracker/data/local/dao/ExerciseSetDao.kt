package com.fitnesstracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fitnesstracker.data.local.entity.ExerciseSetEntity

/**
 * DAO for the exercise_sets table.
 *
 * Sets are always accessed as a list belonging to an ExerciseLog.
 *
 * The key progress query is getBestSetForLog — returns the heaviest set in
 * a given exercise log, used to compute "best set per session" for progress history.
 */
@Dao
interface ExerciseSetDao {

    /** Get all sets for an exercise log (one-shot snapshot). */
    @Query("SELECT * FROM exercise_sets WHERE exerciseLogId = :exerciseLogId ORDER BY setId ASC")
    suspend fun getSetsForLog(exerciseLogId: Long): List<ExerciseSetEntity>

    /**
     * Get the single heaviest set for an exercise log.
     * Used when building the progress history: one data point per session.
     */
    @Query("""
        SELECT * FROM exercise_sets
        WHERE exerciseLogId = :exerciseLogId
        ORDER BY weight DESC, reps DESC
        LIMIT 1
    """)
    suspend fun getBestSetForLog(exerciseLogId: Long): ExerciseSetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSet(set: ExerciseSetEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSets(sets: List<ExerciseSetEntity>): List<Long>

    @Update
    suspend fun updateSet(set: ExerciseSetEntity)

    @Delete
    suspend fun deleteSet(set: ExerciseSetEntity)

    /** Delete all sets for an exercise log — used when replacing sets on update. */
    @Query("DELETE FROM exercise_sets WHERE exerciseLogId = :exerciseLogId")
    suspend fun deleteSetsForLog(exerciseLogId: Long)
}
