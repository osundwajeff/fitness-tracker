package com.fitnesstracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fitnesstracker.data.local.entity.ExerciseEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the exercises table.
 *
 * Notice the @Query filters by workoutId - this respects the foreign key
 * relationship defined on ExerciseEntity. Each exercise belongs to one workout.
 */
@Dao
interface ExerciseDao {

    /**
     * Observe all exercises belonging to a specific workout.
     * Returns Flow so the UI updates automatically when exercises change.
     */
    @Query("SELECT * FROM exercises WHERE workoutId = :workoutId ORDER BY exerciseId ASC")
    fun getExercisesForWorkout(workoutId: Long): Flow<List<ExerciseEntity>>

    /**
     * Get all exercises for a workout once (not reactive).
     * Used in mappers where we need a snapshot, not a stream.
     */
    @Query("SELECT * FROM exercises WHERE workoutId = :workoutId ORDER BY exerciseId ASC")
    suspend fun getExercisesForWorkoutOnce(workoutId: Long): List<ExerciseEntity>

    /**
     * Get a single exercise by ID.
     */
    @Query("SELECT * FROM exercises WHERE exerciseId = :id")
    suspend fun getExerciseById(id: Long): ExerciseEntity?

    /**
     * Insert an exercise and return the new row ID.
     * The returned ID is used to associate ExerciseSets with this exercise.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: ExerciseEntity): Long

    /**
     * Insert multiple exercises at once - more efficient than inserting one by one.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<ExerciseEntity>): List<Long>

    @Update
    suspend fun updateExercise(exercise: ExerciseEntity)

    /**
     * Delete an exercise. ExerciseSetEntity has CASCADE delete,
     * so all sets for this exercise are deleted automatically.
     */
    @Delete
    suspend fun deleteExercise(exercise: ExerciseEntity)
}
