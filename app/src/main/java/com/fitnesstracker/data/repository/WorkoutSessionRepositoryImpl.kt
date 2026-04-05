package com.fitnesstracker.data.repository

import com.fitnesstracker.data.local.dao.ExerciseLogDao
import com.fitnesstracker.data.local.dao.ExerciseSetDao
import com.fitnesstracker.data.local.dao.WorkoutSessionDao
import com.fitnesstracker.data.mapper.toDomain
import com.fitnesstracker.data.mapper.toEntity
import com.fitnesstracker.domain.model.ExerciseBestSet
import com.fitnesstracker.domain.model.ExerciseLog
import com.fitnesstracker.domain.model.WorkoutSession
import com.fitnesstracker.domain.repository.WorkoutSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WorkoutSessionRepositoryImpl @Inject constructor(
    private val sessionDao: WorkoutSessionDao,
    private val exerciseLogDao: ExerciseLogDao,
    private val exerciseSetDao: ExerciseSetDao
) : WorkoutSessionRepository {

    override fun getSessionsForTemplate(templateId: Long): Flow<List<WorkoutSession>> = flow {
        sessionDao.getSessionsForTemplate(templateId).collect { entities ->
            val sessions = entities.toDomain { sessionId ->
                exerciseLogDao.getLogsForSessionOnce(sessionId).toDomain { logId ->
                    exerciseSetDao.getSetsForLog(logId).toDomain()
                }
            }
            emit(sessions)
        }
    }

    override suspend fun getSessionById(id: Long): WorkoutSession? {
        val entity = sessionDao.getSessionById(id) ?: return null
        val exercises = exerciseLogDao.getLogsForSessionOnce(id).toDomain { logId ->
            exerciseSetDao.getSetsForLog(logId).toDomain()
        }
        return entity.toDomain(exercises)
    }

    override suspend fun createSession(session: WorkoutSession): Long {
        val sessionId = sessionDao.insertSession(session.toEntity())
        session.exercises.forEach { log ->
            insertLogWithSets(sessionId, log)
        }
        return sessionId
    }

    override suspend fun updateSession(session: WorkoutSession) {
        sessionDao.updateSession(session.toEntity())
    }

    override suspend fun deleteSession(session: WorkoutSession) {
        sessionDao.deleteSession(session.toEntity())
        // ExerciseLogs and their sets are deleted via CASCADE
    }

    override suspend fun addExerciseLog(sessionId: Long, log: ExerciseLog): Long {
        return insertLogWithSets(sessionId, log)
    }

    override suspend fun updateExerciseLog(log: ExerciseLog) {
        exerciseLogDao.updateLog(log.toEntity())
        exerciseSetDao.deleteSetsForLog(log.id)
        exerciseSetDao.insertSets(log.sets.map { it.toEntity(exerciseLogId = log.id) })
    }

    override suspend fun deleteExerciseLog(log: ExerciseLog) {
        exerciseLogDao.deleteLog(log.toEntity())
        // Sets deleted via CASCADE
    }

    /**
     * Builds the progress history for one exercise across all sessions of a template.
     *
     * For each session that contains the named exercise, we find the best set
     * (highest weight) and return one ExerciseBestSet data point per session.
     * The result is ordered by session date ascending — ready for a progress list/chart.
     */
    override suspend fun getExerciseHistory(
        exerciseName: String,
        templateId: Long
    ): List<ExerciseBestSet> {
        val logs = exerciseLogDao.getLogsByNameForTemplate(exerciseName, templateId)
        return logs.mapNotNull { logEntity ->
            val bestSet = exerciseSetDao.getBestSetForLog(logEntity.id) ?: return@mapNotNull null
            // Fetch the session date for this log
            val session = sessionDao.getSessionById(logEntity.sessionId) ?: return@mapNotNull null
            ExerciseBestSet(
                sessionDate = session.date,
                reps = bestSet.reps,
                weight = bestSet.weight
            )
        }
    }

    // ---------------------------------------------------------------------------
    // Private helpers
    // ---------------------------------------------------------------------------

    private suspend fun insertLogWithSets(sessionId: Long, log: ExerciseLog): Long {
        val logId = exerciseLogDao.insertLog(log.copy(sessionId = sessionId).toEntity())
        if (log.sets.isNotEmpty()) {
            exerciseSetDao.insertSets(log.sets.map { it.toEntity(exerciseLogId = logId) })
        }
        return logId
    }
}
