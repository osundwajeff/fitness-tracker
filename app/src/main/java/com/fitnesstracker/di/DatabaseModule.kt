package com.fitnesstracker.di

import android.content.Context
import androidx.room.Room
import com.fitnesstracker.data.local.AppDatabase
import com.fitnesstracker.data.local.dao.ExerciseLogDao
import com.fitnesstracker.data.local.dao.ExerciseSetDao
import com.fitnesstracker.data.local.dao.ExerciseTemplateDao
import com.fitnesstracker.data.local.dao.WorkoutSessionDao
import com.fitnesstracker.data.local.dao.WorkoutTemplateDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides the Room database and all DAOs.
 *
 * fallbackToDestructiveMigration: since there is no production data yet,
 * we simply drop and recreate the database when the schema version changes.
 * Once the app ships, replace this with proper Migration objects.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "fitness_tracker_db"
        )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    @Provides
    @Singleton
    fun provideWorkoutTemplateDao(db: AppDatabase): WorkoutTemplateDao = db.workoutTemplateDao()

    @Provides
    @Singleton
    fun provideExerciseTemplateDao(db: AppDatabase): ExerciseTemplateDao = db.exerciseTemplateDao()

    @Provides
    @Singleton
    fun provideWorkoutSessionDao(db: AppDatabase): WorkoutSessionDao = db.workoutSessionDao()

    @Provides
    @Singleton
    fun provideExerciseLogDao(db: AppDatabase): ExerciseLogDao = db.exerciseLogDao()

    @Provides
    @Singleton
    fun provideExerciseSetDao(db: AppDatabase): ExerciseSetDao = db.exerciseSetDao()
}
