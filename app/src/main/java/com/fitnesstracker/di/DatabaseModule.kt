package com.fitnesstracker.di

import android.content.Context
import androidx.room.Room
import com.fitnesstracker.data.local.AppDatabase
import com.fitnesstracker.data.local.dao.ExerciseDao
import com.fitnesstracker.data.local.dao.ExerciseSetDao
import com.fitnesstracker.data.local.dao.WorkoutDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt Module that provides database-related dependencies.
 * 
 * @Module marks this class as a module containing dependency definitions.
 * @InstallIn(SingletonComponent::class) makes these dependencies available
 * app-wide and creates them once (singleton).
 * 
 * Think of this as a "recipe book" - it tells Hilt HOW to create objects.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Provides the Room database instance.
     * 
     * @Provides tells Hilt: "When someone asks for an AppDatabase, 
     * here's how you create it."
     * 
     * @Singleton ensures only one instance exists throughout the app.
     * 
     * @ApplicationContext is automatically injected by Hilt - it's the
     * app's context needed to build the database.
     */
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "fitness_tracker_db"
        ).build()
    }

    /**
     * Provides WorkoutDao from the database.
     * Hilt can inject database instance because we provided it above.
     */
    @Provides
    @Singleton
    fun provideWorkoutDao(database: AppDatabase): WorkoutDao {
        return database.workoutDao()
    }

    /**
     * Provides ExerciseDao from the database.
     */
    @Provides
    @Singleton
    fun provideExerciseDao(database: AppDatabase): ExerciseDao {
        return database.exerciseDao()
    }

    /**
     * Provides ExerciseSetDao from the database.
     */
    @Provides
    @Singleton
    fun provideExerciseSetDao(database: AppDatabase): ExerciseSetDao {
        return database.exerciseSetDao()
    }
}
