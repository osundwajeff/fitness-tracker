package com.fitnesstracker.di

import com.fitnesstracker.data.repository.WorkoutSessionRepositoryImpl
import com.fitnesstracker.data.repository.WorkoutTemplateRepositoryImpl
import com.fitnesstracker.domain.repository.WorkoutSessionRepository
import com.fitnesstracker.domain.repository.WorkoutTemplateRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that binds repository interfaces to their implementations.
 *
 * @Binds is more efficient than @Provides for interface bindings — Room doesn't
 * need to generate a wrapper function, it just resolves the type directly.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWorkoutTemplateRepository(
        impl: WorkoutTemplateRepositoryImpl
    ): WorkoutTemplateRepository

    @Binds
    @Singleton
    abstract fun bindWorkoutSessionRepository(
        impl: WorkoutSessionRepositoryImpl
    ): WorkoutSessionRepository
}
