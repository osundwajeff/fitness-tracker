package com.fitnesstracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fitnesstracker.data.local.dao.ExerciseLogDao
import com.fitnesstracker.data.local.dao.ExerciseSetDao
import com.fitnesstracker.data.local.dao.ExerciseTemplateDao
import com.fitnesstracker.data.local.dao.WorkoutSessionDao
import com.fitnesstracker.data.local.dao.WorkoutTemplateDao
import com.fitnesstracker.data.local.entity.ExerciseLogEntity
import com.fitnesstracker.data.local.entity.ExerciseSetEntity
import com.fitnesstracker.data.local.entity.ExerciseTemplateEntity
import com.fitnesstracker.data.local.entity.WorkoutSessionEntity
import com.fitnesstracker.data.local.entity.WorkoutTemplateEntity

/**
 * The Room database for the fitness tracker.
 *
 * Version 2: new schema — workout_templates, exercise_templates, workout_sessions,
 * exercise_logs replace the old workouts / exercises tables.
 * Since no production data exists we use fallbackToDestructiveMigration to
 * drop-and-recreate rather than writing a migration.
 *
 * Pre-seeding the 3 default templates is handled in DatabaseModule via a
 * RoomDatabase.Callback — this runs after the DB is created on first launch.
 */
@Database(
    entities = [
        WorkoutTemplateEntity::class,
        ExerciseTemplateEntity::class,
        WorkoutSessionEntity::class,
        ExerciseLogEntity::class,
        ExerciseSetEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workoutTemplateDao(): WorkoutTemplateDao
    abstract fun exerciseTemplateDao(): ExerciseTemplateDao
    abstract fun workoutSessionDao(): WorkoutSessionDao
    abstract fun exerciseLogDao(): ExerciseLogDao
    abstract fun exerciseSetDao(): ExerciseSetDao
}
