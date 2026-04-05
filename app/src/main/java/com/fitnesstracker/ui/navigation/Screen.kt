package com.fitnesstracker.ui.navigation

/**
 * Defines all navigation destinations in the app as a sealed class.
 *
 * Using a sealed class (rather than plain strings scattered around the code)
 * means all routes are defined in ONE place. The compiler ensures you can't
 * navigate to a route that doesn't exist.
 *
 * Each destination object holds its route string and any helper functions
 * to build parameterized routes.
 */
sealed class Screen(val route: String) {

    /**
     * The workout list - the app's home screen.
     * No parameters needed.
     */
    data object WorkoutList : Screen("workout_list")

    /**
     * The workout detail screen.
     * {workoutId} is a route parameter - NavController replaces it with the actual ID.
     *
     * Route: "workout_detail/42" for workout with ID 42.
     */
    data object WorkoutDetail : Screen("workout_detail/{workoutId}") {
        /**
         * Builds the actual navigation route with a specific ID.
         * Usage: navController.navigate(WorkoutDetail.createRoute(workoutId))
         */
        fun createRoute(workoutId: Long) = "workout_detail/$workoutId"

        /** The argument name - must match {workoutId} in the route string */
        const val ARG_WORKOUT_ID = "workoutId"
    }
}
