package com.fitnesstracker

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class annotated with @HiltAndroidApp.
 * 
 * This annotation tells Hilt to:
 * 1. Generate a container for dependencies at application level
 * 2. Use this class as the entry point for dependency injection
 * 3. Create a SingletonComponent that lives as long as the app
 * 
 * Without this, Hilt cannot inject dependencies anywhere in the app.
 */
@HiltAndroidApp
class FitnessTrackerApp : Application()
