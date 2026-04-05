package com.fitnesstracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fitnesstracker.ui.workout.WorkoutDetailScreen
import com.fitnesstracker.ui.workout.WorkoutListScreen

/**
 * The root navigation graph for the app.
 *
 * NavHost wires together all screens and their routes.
 * rememberNavController() creates a NavController that survives recomposition.
 *
 * Each composable { } block is a navigation destination. The lambda receives a
 * NavBackStackEntry which gives access to route arguments.
 */
@Composable
fun FitnessTrackerNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.WorkoutList.route
    ) {

        // -----------------------------------------------------------------------
        // Workout List Screen
        // -----------------------------------------------------------------------
        composable(route = Screen.WorkoutList.route) {
            WorkoutListScreen(
                onNavigateToDetail = { workoutId ->
                    navController.navigate(Screen.WorkoutDetail.createRoute(workoutId))
                }
            )
        }

        // -----------------------------------------------------------------------
        // Workout Detail Screen
        // -----------------------------------------------------------------------
        composable(
            route = Screen.WorkoutDetail.route,
            arguments = listOf(
                // Declare the workoutId argument type so Navigation can parse it.
                // NavType.LongType means it's a Long - Navigation extracts it from the route string.
                navArgument(Screen.WorkoutDetail.ARG_WORKOUT_ID) {
                    type = NavType.LongType
                }
            )
        ) {
            WorkoutDetailScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
