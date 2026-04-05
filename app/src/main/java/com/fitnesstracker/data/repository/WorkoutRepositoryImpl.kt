package com.fitnesstracker.data.repository

import com.fitnesstracker.data.local.dao.ExerciseDao
import com.fitnesstracker.data.local.dao.ExerciseSetDao
import com.fitnesstracker.data.local.dao.WorkoutDao
import com.fitnesstracker.data.mapper.toDomain
import com.fitnesstracker.data.mapper.toEntity
import com.fitnesstracker.domain.model.Exercise
import com.fitnesstracker.domain.model.Workout
import com.fitnesstracker.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

/**
 * Concrete implementation of WorkoutRepository.
 *
 * This class lives in the DATA layer - it knows about Room, DAOs, and mappers.
 * The domain layer never sees this class directly (only via the interface).
 *
 * @Inject constructor: Hilt sees this and knows it can create WorkoutRepositoryImpl
 * by providing WorkoutDao, ExerciseDao, ExerciseSetDao from DatabaseModule.
 *
 * The repository's job:
 * 1. Call the right DAOs
 * 2. Map entities ↔ domain models
 * 3. Coordinate multi-table operations (e.g. insert workout + its exercises)
 */
class WorkoutRepositoryImpl @Inject constructor(
    private val workoutDao: WorkoutDao,
    private val exerciseDao: ExerciseDao,
    private val exerciseSetDao: ExerciseSetDao
) : WorkoutRepository {

    /**
     * Observe all workouts.
     *
     * We use a flow { } builder so we can call suspend functions (DAO queries)
     * inside the transform. The outer workoutDao.getAllWorkouts() Flow is collected
     * and for each emission we fetch exercises (suspend) then re-emit the mapped list.
     *
     * Note: for a list view, we load exercises per workout. For large datasets
     * you might skip loading exercises here and load them lazily on detail screen.
     */
    override fun getAllWorkouts(): Flow<List<Workout>> = flow {
        workoutDao.getAllWorkouts().collect { workoutEntities ->
            val workouts = workoutEntities.toDomain { workoutId ->
                // For each workout, fetch its exercises with their sets
                val exerciseEntities = exerciseDao.getExercisesForWorkoutOnce(workoutId)
                exerciseEntities.toDomain { exerciseId ->
                    exerciseSetDao.getSetsForExercise(exerciseId).toDomain()
                }
            }
            emit(workouts)
        }
    }

    /**
     * Get a single workout by ID with all its exercises and sets.
     *
     * This assembles the full object graph:
     *   Workout → List<Exercise> → List<ExerciseSet>
     */
    override suspend fun getWorkoutById(id: Long): Workout? {
        val workoutEntity = workoutDao.getWorkoutById(id) ?: return null

        val exercises = exerciseDao.getExercisesForWorkoutOnce(id).toDomain { exerciseId ->
            exerciseSetDao.getSetsForExercise(exerciseId).toDomain()
        }

        return workoutEntity.toDomain(exercises)
    }

    /**
     * Create a new workout and return its generated ID.
     * Exercises are stored separately after the workout is created.
     */
    override suspend fun createWorkout(workout: Workout): Long {
        val workoutId = workoutDao.insertWorkout(workout.toEntity())

        // Insert each exercise and its sets using the newly generated workoutId
        workout.exercises.forEach { exercise ->
            insertExerciseWithSets(workoutId, exercise)
        }

        return workoutId
    }

    override suspend fun updateWorkout(workout: Workout) {
        workoutDao.updateWorkout(workout.toEntity())
    }

    override suspend fun deleteWorkout(workout: Workout) {
        workoutDao.deleteWorkout(workout.toEntity())
        // Exercises and sets are deleted automatically via CASCADE foreign keys
    }

    /**
     * Add an exercise with all its sets to a workout.
     * Returns the generated exercise ID.
     */
    override suspend fun addExercise(workoutId: Long, exercise: Exercise): Long {
        return insertExerciseWithSets(workoutId, exercise)
    }

    /**
     * Update an exercise: update the exercise row and replace all its sets.
     *
     * We delete existing sets and re-insert rather than diffing, which keeps
     * the logic simple. For a production app you might diff to preserve set IDs.
     */
    override suspend fun updateExercise(workoutId: Long, exercise: Exercise) {
        exerciseDao.updateExercise(exercise.toEntity(workoutId))

        // Replace all sets: delete old ones and insert the new ones
        exerciseSetDao.deleteSetsForExercise(exercise.id)
        val setEntities = exercise.sets.map { it.toEntity(exerciseId = exercise.id) }
        exerciseSetDao.insertSets(setEntities)
    }

    override suspend fun deleteExercise(exercise: Exercise) {
        exerciseDao.deleteExercise(exercise.toEntity(workoutId = 0))
        // Sets are deleted automatically via CASCADE foreign keys
    }

    // ---------------------------------------------------------------------------
    // Private helpers
    // ---------------------------------------------------------------------------

    /**
     * Internal helper: inserts an exercise and all its sets in one operation.
     * Returns the generated exercise ID.
     *
     * This is a private function because it's an implementation detail -
     * callers use addExercise() or createWorkout() instead.
     */
    private suspend fun insertExerciseWithSets(workoutId: Long, exercise: Exercise): Long {
        val exerciseId = exerciseDao.insertExercise(exercise.toEntity(workoutId))
        val setEntities = exercise.sets.map { it.toEntity(exerciseId = exerciseId) }
        if (setEntities.isNotEmpty()) {
            exerciseSetDao.insertSets(setEntities)
        }
        return exerciseId
    }
}
