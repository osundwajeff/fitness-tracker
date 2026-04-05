package com.fitnesstracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fitnesstracker.data.local.entity.WorkoutTemplateEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for the workout_templates table.
 *
 * Templates are mostly read-only at runtime (pre-seeded on first launch).
 * The only user-driven mutations are via ExerciseTemplateDao (adding/removing exercises).
 */
@Dao
interface WorkoutTemplateDao {

    /** Observe all templates — emits whenever templates or their metadata change. */
    @Query("SELECT * FROM workout_templates ORDER BY id ASC")
    fun getAllTemplates(): Flow<List<WorkoutTemplateEntity>>

    /** One-shot fetch of a single template by ID. */
    @Query("SELECT * FROM workout_templates WHERE id = :id")
    suspend fun getTemplateById(id: Long): WorkoutTemplateEntity?

    /** Insert a template (used during DB pre-seed). Returns the generated row ID. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTemplate(template: WorkoutTemplateEntity): Long

    @Update
    suspend fun updateTemplate(template: WorkoutTemplateEntity)

    @Delete
    suspend fun deleteTemplate(template: WorkoutTemplateEntity)

    /** Returns count of templates — used to detect whether pre-seed is needed. */
    @Query("SELECT COUNT(*) FROM workout_templates")
    suspend fun count(): Int
}
