package com.fitnesstracker.data.repository

import com.fitnesstracker.data.local.dao.ExerciseTemplateDao
import com.fitnesstracker.data.local.dao.WorkoutTemplateDao
import com.fitnesstracker.data.local.entity.WorkoutTemplateEntity
import com.fitnesstracker.data.mapper.toDomain
import com.fitnesstracker.data.mapper.toEntity
import com.fitnesstracker.domain.model.ExerciseTemplate
import com.fitnesstracker.domain.model.Intensity
import com.fitnesstracker.domain.model.WorkoutTemplate
import com.fitnesstracker.domain.repository.WorkoutTemplateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.DayOfWeek
import javax.inject.Inject

class WorkoutTemplateRepositoryImpl @Inject constructor(
    private val templateDao: WorkoutTemplateDao,
    private val exerciseTemplateDao: ExerciseTemplateDao
) : WorkoutTemplateRepository {

    override fun getAllTemplates(): Flow<List<WorkoutTemplate>> = flow {
        templateDao.getAllTemplates().collect { entities ->
            val templates = entities.toDomain { templateId ->
                exerciseTemplateDao.getExercisesForTemplateOnce(templateId).toDomain()
            }
            emit(templates)
        }
    }

    override suspend fun getTemplateById(id: Long): WorkoutTemplate? {
        val entity = templateDao.getTemplateById(id) ?: return null
        val exercises = exerciseTemplateDao.getExercisesForTemplateOnce(id).toDomain()
        return entity.toDomain(exercises)
    }

    override suspend fun addExerciseToTemplate(templateId: Long, exercise: ExerciseTemplate): Long {
        return exerciseTemplateDao.insertExercise(
            exercise.copy(templateId = templateId).toEntity()
        )
    }

    override suspend fun removeExerciseFromTemplate(exercise: ExerciseTemplate) {
        exerciseTemplateDao.deleteExercise(exercise.toEntity())
    }

    override suspend fun updateExerciseOnTemplate(exercise: ExerciseTemplate) {
        exerciseTemplateDao.updateExercise(exercise.toEntity())
    }

    /**
     * Pre-seeds the 3 default templates on first launch.
     *
     * Uses OnConflictStrategy.IGNORE on the DAO insert so this is safe to call
     * every startup — if rows already exist, nothing happens.
     * We check count() first to avoid the 3 insert calls on every launch.
     */
    override suspend fun seedDefaultTemplatesIfEmpty() {
        if (templateDao.count() > 0) return

        val defaults = listOf(
            WorkoutTemplateEntity(name = "Tuesday Heavy",    day = DayOfWeek.TUESDAY.name,   intensity = Intensity.HEAVY.name),
            WorkoutTemplateEntity(name = "Thursday Light",   day = DayOfWeek.THURSDAY.name,  intensity = Intensity.LIGHT.name),
            WorkoutTemplateEntity(name = "Saturday Moderate",day = DayOfWeek.SATURDAY.name,  intensity = Intensity.MODERATE.name)
        )
        defaults.forEach { templateDao.insertTemplate(it) }
    }
}
