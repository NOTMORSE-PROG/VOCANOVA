package com.example.vocanova.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * Utility class for managing video progress tracking without restrictions
 */
class VideoProgressTracker(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("video_progress", Context.MODE_PRIVATE)

    /**
     * Save the current progress for a video
     * @param videoId The ID of the video
     * @param progress The current progress (0.0 to 1.0)
     */
    fun saveProgress(videoId: String, progress: Float) {
        prefs.edit().putFloat("video_$videoId", progress).apply()

        // Update max progress if current progress is greater
        val maxProgress = getMaxProgress(videoId)
        if (progress > maxProgress) {
            saveMaxProgress(videoId, progress)
        }
    }

    /**
     * Get the saved progress for a video
     * @param videoId The ID of the video
     * @return The saved progress (0.0 to 1.0)
     */
    fun getProgress(videoId: String): Float {
        return prefs.getFloat("video_$videoId", 0f)
    }

    /**
     * Save the maximum allowed progress for a video
     * @param videoId The ID of the video
     * @param maxProgress The maximum progress (0.0 to 1.0)
     */
    private fun saveMaxProgress(videoId: String, maxProgress: Float) {
        prefs.edit().putFloat("max_video_$videoId", maxProgress).apply()
    }

    /**
     * Get the maximum allowed progress for a video
     * @param videoId The ID of the video
     * @return The maximum allowed progress (0.0 to 1.0)
     */
    fun getMaxProgress(videoId: String): Float {
        return prefs.getFloat("max_video_$videoId", 0f)
    }

    /**
     * Check if a video is completed (progress >= 0.95)
     * @param videoId The ID of the video
     * @return True if the video is completed
     */
    fun isVideoCompleted(videoId: String): Boolean {
        return getMaxProgress(videoId) >= 0.95f
    }

    /**
     * Reset progress for all videos (for testing)
     */
    fun resetAllProgress() {
        prefs.edit().clear().apply()
    }
}

/**
 * Composable function to get a VideoProgressTracker
 */
@Composable
fun rememberVideoProgressTracker(): VideoProgressTracker {
    val context = LocalContext.current
    val tracker = remember { VideoProgressTracker(context) }

    DisposableEffect(Unit) {
        onDispose { }
    }

    return tracker
}
