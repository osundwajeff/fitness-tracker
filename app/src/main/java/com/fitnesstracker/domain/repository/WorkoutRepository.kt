package com.fitnesstracker.domain.repository

import com.fitnesstracker.domain.model.Exercise
import com.fitnesstracker.domain.model.Workout
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for workout data operations.
 *
 * This interface lives in the DOMAIN layer - it defines WHAT the app can do
 * with workout data, without knowing HOW it's done (no Room, no SQL here).
 *
 * This is the Dependency Inversion principle (SOLID D):
 * - High-level modules (Use Cases, ViewModels) depend on this abstraction
 * - Low-level modules (WorkoutRepositoryImpl) implement this abstraction
 * - Neither knows about each other directly
 *
 * Benefits:
 * - Easy to test: swap in a fake implementation during tests
 * - Easy to change: replace Room with a network API without touching Use Cases
 */
interface WorkoutRepository {

    /**
     * Observe all workouts as a reactive stream.
     * The Flow emits a new list every time the data changes.
     * Use this in ViewModels with collectAsState() in Compose.
     */
    fun getAllWorkouts(): Flow<List<Workout>>

    /**
     * Get a single workout with all its exercises and sets.
     * Returns null if no workout with that ID exists.
     */
    suspend fun getWorkoutById(id: Long): Workout?

    /**
     * Create a new workout and return its generated ID.
     * The ID can be used to immediately navigate to the new workout's detail screen.
     */
    suspend fun createWorkout(workout: Workout): Long

    /**
     * Update an existing workout (name, date, note).
     * Does not affect exercises - use addExercise/deleteExercise for that.
     */
    suspend fun updateWorkout(workout: Workout)

    /**
     * Delete a workout and all its exercises and sets (via CASCADE).
     */
    suspend fun deleteWorkout(workout: Workout)

    /**
     * Add an exercise (with its sets) to a workout.
     * Returns the generated exercise ID.
     */
    suspend fun addExercise(workoutId: Long, exercise: Exercise): Long

    /**
     * Update an exercise and replace all its sets.
     */
    suspend fun updateExercise(workoutId: Long, exercise: Exercise)

    /**
     * Delete an exercise and all its sets (via CASCADE).
     */
    suspend fun deleteExercise(exercise: Exercise)
}
