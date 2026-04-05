package com.fitnesstracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fitnesstracker.data.local.entity.ExerciseTemplateEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for the exercise_templates table.
 *
 * Exercises on a template are the user-editable part of a template —
 * they can add/remove exercises (e.g. add Squat to Tuesday Heavy).
 */
@Dao
interface ExerciseTemplateDao {

    /** Observe all exercises for a given template, in display order. */
    @Query("SELECT * FROM exercise_templates WHERE templateId = :templateId ORDER BY orderIndex ASC")
    fun getExercisesForTemplate(templateId: Long): Flow<List<ExerciseTemplateEntity>>

    /** One-shot snapshot — used when pre-populating a new session from a template. */
    @Query("SELECT * FROM exercise_templates WHERE templateId = :templateId ORDER BY orderIndex ASC")
    suspend fun getExercisesForTemplateOnce(templateId: Long): List<ExerciseTemplateEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: ExerciseTemplateEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<ExerciseTemplateEntity>): List<Long>

    @Update
    suspend fun updateExercise(exercise: ExerciseTemplateEntity)

    @Delete
    suspend fun deleteExercise(exercise: ExerciseTemplateEntity)
}
