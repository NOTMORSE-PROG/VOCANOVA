package com.example.vocanova.ui.screens

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.videoLessonNavigation(navController: NavController) {
    composable("video_lesson/{weekNumber}") { backStackEntry ->
        val weekNumber = backStackEntry.arguments?.getString("weekNumber")?.toIntOrNull() ?: 1
        VideoLessonScreen(navController = navController, weekNumber = weekNumber)
    }

    // For backward compatibility
    composable("video_lesson") {
        VideoLessonScreen(navController = navController, weekNumber = 1)
    }
}
