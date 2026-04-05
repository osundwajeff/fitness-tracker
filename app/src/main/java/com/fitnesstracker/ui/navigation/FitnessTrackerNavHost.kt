package com.fitnesstracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fitnesstracker.ui.workout.ExerciseHistoryScreen
import com.fitnesstracker.ui.workout.SessionDetailScreen
import com.fitnesstracker.ui.workout.TemplateDetailScreen
import com.fitnesstracker.ui.workout.TemplateListScreen

@Composable
public fun FitnessTrackerNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.TemplateList.route
    ) {

        // Home: template list
        composable(route = Screen.TemplateList.route) {
            TemplateListScreen(
                onNavigateToTemplate = { templateId ->
                    navController.navigate(Screen.TemplateDetail.createRoute(templateId))
                }
            )
        }

        // Template detail
        composable(
            route = Screen.TemplateDetail.route,
            arguments = listOf(navArgument(Screen.TemplateDetail.ARG_TEMPLATE_ID) {
                type = NavType.LongType
            })
        ) {
            TemplateDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSession = { sessionId ->
                    navController.navigate(Screen.SessionDetail.createRoute(sessionId))
                },
                onNavigateToHistory = { templateId, exerciseName ->
                    navController.navigate(Screen.ExerciseHistory.createRoute(templateId, exerciseName))
                }
            )
        }

        // Session detail
        composable(
            route = Screen.SessionDetail.route,
            arguments = listOf(navArgument(Screen.SessionDetail.ARG_SESSION_ID) {
                type = NavType.LongType
            })
        ) {
            SessionDetailScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Exercise history / progress
        composable(
            route = Screen.ExerciseHistory.route,
            arguments = listOf(
                navArgument(Screen.ExerciseHistory.ARG_TEMPLATE_ID) { type = NavType.LongType },
                navArgument(Screen.ExerciseHistory.ARG_EXERCISE_NAME) { type = NavType.StringType }
            )
        ) {
            ExerciseHistoryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
