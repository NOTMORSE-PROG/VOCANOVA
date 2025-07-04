package com.example.vocanova.utils

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import java.util.concurrent.ConcurrentHashMap

/**
 * Singleton manager for ExoPlayer instances to maintain playback state across configuration changes
 */
object VideoPlayerManager {
    // Map to store ExoPlayer instances by video ID
    private val players = ConcurrentHashMap<String, ExoPlayerInfo>()

    // Current active video ID
    private var currentVideoId: String? = null

    data class ExoPlayerInfo(
        val player: ExoPlayer,
        var isPlaying: Boolean = false,
        var playbackPosition: Long = 0L,
        var playbackSpeed: Float = 1.0f,
        var isMuted: Boolean = false
    )

    /**
     * Get or create an ExoPlayer for a specific video
     */
    fun getPlayer(context: Context, videoId: String, videoUri: Uri): ExoPlayerInfo {
        // Set as current active video
        currentVideoId = videoId

        // Return existing player or create a new one
        return players.getOrPut(videoId) {
            val exoPlayer = ExoPlayer.Builder(context).build().apply {
                val mediaItem = MediaItem.fromUri(videoUri)
                setMediaItem(mediaItem)
                prepare()
            }

            ExoPlayerInfo(exoPlayer)
        }
    }

    /**
     * Update player state
     */
    fun updatePlayerState(
        videoId: String,
        isPlaying: Boolean? = null,
        playbackPosition: Long? = null,
        playbackSpeed: Float? = null,
        isMuted: Boolean? = null
    ) {
        players[videoId]?.let { info ->
            isPlaying?.let { info.isPlaying = it }
            playbackPosition?.let { info.playbackPosition = it }
            playbackSpeed?.let { info.playbackSpeed = it }
            isMuted?.let { info.isMuted = it }
        }
    }

    /**
     * Release all players when app is closing
     */
    fun releaseAll() {
        players.forEach { (_, info) ->
            info.player.release()
        }
        players.clear()
    }

    /**
     * Release a specific player
     */
    fun releasePlayer(videoId: String) {
        players[videoId]?.let { info ->
            // Save position before releasing
            updatePlayerState(videoId, playbackPosition = info.player.currentPosition)

            // Only release if this is not the current active video
            if (videoId != currentVideoId) {
                info.player.release()
                players.remove(videoId)
            }
        }
    }

    /**
     * Pause all players except the current one
     */
    fun pauseOthers(currentVideoId: String) {
        players.forEach { (id, info) ->
            if (id != currentVideoId && info.isPlaying) {
                info.player.pause()
                updatePlayerState(id, isPlaying = false)
            }
        }
    }
}
