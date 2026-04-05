package com.fitnesstracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fitnesstracker.data.local.entity.ExerciseSetEntity

/**
 * Data Access Object for the exercise_sets table.
 *
 * ExerciseSets are the individual rows of reps/weight within an exercise.
 * They are always fetched as a list belonging to an exercise - never standalone.
 */
@Dao
interface ExerciseSetDao {

    /**
     * Get all sets for a specific exercise - one-shot snapshot.
     * Used in mappers when building the full Exercise domain model.
     */
    @Query("SELECT * FROM exercise_sets WHERE exerciseOwnerId = :exerciseId ORDER BY setId ASC")
    suspend fun getSetsForExercise(exerciseId: Long): List<ExerciseSetEntity>

    /**
     * Insert a single set and return its new row ID.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSet(set: ExerciseSetEntity): Long

    /**
     * Insert multiple sets at once - used when saving a full exercise with sets.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSets(sets: List<ExerciseSetEntity>): List<Long>

    @Update
    suspend fun updateSet(set: ExerciseSetEntity)

    @Delete
    suspend fun deleteSet(set: ExerciseSetEntity)

    /**
     * Delete all sets belonging to an exercise.
     * Useful when replacing all sets during an update.
     */
    @Query("DELETE FROM exercise_sets WHERE exerciseOwnerId = :exerciseId")
    suspend fun deleteSetsForExercise(exerciseId: Long)
}
