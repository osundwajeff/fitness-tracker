package com.fitnesstracker.di

import com.fitnesstracker.data.repository.WorkoutRepositoryImpl
import com.fitnesstracker.domain.repository.WorkoutRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt Module that binds repository interfaces to implementations.
 * 
 * @Module marks this as a dependency provider
 * @InstallIn(SingletonComponent::class) makes it available app-wide
 * 
 * @Binds is used for interfaces - it tells Hilt:
 * "When someone asks for WorkoutRepository, use WorkoutRepositoryImpl"
 * 
 * This is preferred over @Provides for interfaces because it's more efficient
 * (no extra wrapper function needed).
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * Binds the WorkoutRepository interface to its implementation.
     * 
     * The implementation (WorkoutRepositoryImpl) must have:
     * - An @Inject annotated constructor
     * - All its dependencies available in Hilt modules
     */
    @Binds
    @Singleton
    abstract fun bindWorkoutRepository(
        workoutRepositoryImpl: WorkoutRepositoryImpl
    ): WorkoutRepository
}
