package com.fitnesstracker.ui.navigation

/**
 * All navigation destinations in the app.
 *
 * Navigation hierarchy:
 *   TemplateList (home)
 *     └── TemplateDetail/{templateId}
 *           └── SessionDetail/{sessionId}
 *                 └── ExerciseHistory/{templateId}/{exerciseName}
 */
sealed class Screen(val route: String) {

    /** Home screen: the 3 template cards (Tue Heavy / Thu Light / Sat Moderate). */
    data object TemplateList : Screen("template_list")

    /** Template detail: exercise list + session history for one template. */
    data object TemplateDetail : Screen("template_detail/{templateId}") {
        fun createRoute(templateId: Long) = "template_detail/$templateId"
        const val ARG_TEMPLATE_ID = "templateId"
    }

    /** Session detail: the exercise logs for a single training session. */
    data object SessionDetail : Screen("session_detail/{sessionId}") {
        fun createRoute(sessionId: Long) = "session_detail/$sessionId"
        const val ARG_SESSION_ID = "sessionId"
    }

    /**
     * Exercise history / progress: best-set-per-session list for one exercise.
     *
     * exerciseName is URL-encoded in the route so spaces are handled correctly.
     */
    data object ExerciseHistory : Screen("exercise_history/{templateId}/{exerciseName}") {
        fun createRoute(templateId: Long, exerciseName: String) =
            "exercise_history/$templateId/${java.net.URLEncoder.encode(exerciseName, "UTF-8")}"
        const val ARG_TEMPLATE_ID = "templateId"
        const val ARG_EXERCISE_NAME = "exerciseName"
    }
}
