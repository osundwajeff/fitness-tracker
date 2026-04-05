package com.fitnesstracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.fitnesstracker.ui.navigation.FitnessTrackerNavHost
import com.fitnesstracker.ui.theme.FitnessTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * The single Activity for the app.
 *
 * @AndroidEntryPoint: Required on every Activity/Fragment that uses Hilt injection.
 * It generates the Hilt component that makes @Inject fields work in this Activity.
 *
 * The Activity itself is kept minimal - just sets up the theme and navigation host.
 * All business logic lives in ViewModels; all UI logic lives in Composables.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FitnessTrackerTheme {
                // FitnessTrackerNavHost owns the NavController and wires all screens together.
                FitnessTrackerNavHost()
            }
        }
    }
}
