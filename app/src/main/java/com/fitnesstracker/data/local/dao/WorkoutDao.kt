package com.fitnesstracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fitnesstracker.data.local.entity.WorkoutEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the workouts table.
 *
 * @Dao marks this as a Room DAO - Room generates the SQL implementation at compile time.
 *
 * Key concepts:
 * - Flow<List<T>>: Reactive stream - the UI automatically gets updated when DB changes
 * - suspend fun: Runs on a background thread, must be called from a coroutine
 * - @Query: Raw SQL - gives full flexibility
 * - @Insert/@Update/@Delete: Room generates the SQL for you
 */
@Dao
interface WorkoutDao {

    /**
     * Observe all workouts, ordered by newest first.
     *
     * Returns Flow - every time the workouts table changes, a new list is emitted.
     * This is the reactive pattern: no need to manually refresh the UI.
     */
    @Query("SELECT * FROM workouts ORDER BY date DESC")
    fun getAllWorkouts(): Flow<List<WorkoutEntity>>

    /**
     * Get a single workout by ID - one-shot operation.
     * Returns null if not found (nullable type).
     */
    @Query("SELECT * FROM workouts WHERE id = :id")
    suspend fun getWorkoutById(id: Long): WorkoutEntity?

    /**
     * Insert a workout and return the new row ID.
     * OnConflictStrategy.REPLACE: if a workout with the same ID exists, replace it.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutEntity): Long

    /**
     * Update an existing workout.
     * Room matches by primary key (id).
     */
    @Update
    suspend fun updateWorkout(workout: WorkoutEntity)

    /**
     * Delete a workout. Room matches by primary key.
     * Note: ExerciseEntity has CASCADE delete, so all exercises are deleted too.
     */
    @Delete
    suspend fun deleteWorkout(workout: WorkoutEntity)
}
