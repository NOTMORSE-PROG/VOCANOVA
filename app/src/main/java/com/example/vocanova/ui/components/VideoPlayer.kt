package com.example.vocanova.ui.components

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.view.WindowManager
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.vocanova.MainActivity
import com.example.vocanova.utils.VideoPlayerManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * A YouTube-like video player with fullscreen support and bottom controls
 *
 * @param videoId The unique identifier for the video
 * @param videoUri The URI of the video to play
 * @param onFullscreenToggle Callback when fullscreen is toggled
 */
@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(
    videoId: String,
    videoUri: Uri,
    onFullscreenToggle: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val lifecycleOwner = LocalLifecycleOwner.current

    // Get the player from the manager (creates a new one if needed)
    val playerInfo = remember { VideoPlayerManager.getPlayer(context, videoId, videoUri) }
    val exoPlayer = playerInfo.player

    // Player state - use rememberSaveable to preserve state during configuration changes
    var isPlaying by rememberSaveable { mutableStateOf(playerInfo.isPlaying) }
    var isControlsVisible by rememberSaveable { mutableStateOf(true) }
    var isFullscreen by rememberSaveable { mutableStateOf(false) }
    var currentPosition by rememberSaveable { mutableFloatStateOf(0f) }
    var duration by rememberSaveable { mutableLongStateOf(0L) }
    var isMuted by rememberSaveable { mutableStateOf(playerInfo.isMuted) }
    var isUserInitiatedFullscreen by rememberSaveable { mutableStateOf(false) }
    var playbackSpeed by rememberSaveable { mutableFloatStateOf(playerInfo.playbackSpeed) }

    val coroutineScope = rememberCoroutineScope()
    var isRewindIndicatorVisible by remember { mutableStateOf(false) }
    var isForwardIndicatorVisible by remember { mutableStateOf(false) }
    var isSpeedMenuExpanded by remember { mutableStateOf(false) }

    // Handle device rotation
    var deviceOrientation by remember { androidx.compose.runtime.mutableIntStateOf(Configuration.ORIENTATION_PORTRAIT) }
    val configuration = LocalConfiguration.current

    // Restore playback state
    LaunchedEffect(Unit) {
        // Restore position
        if (playerInfo.playbackPosition > 0) {
            exoPlayer.seekTo(playerInfo.playbackPosition)
        }

        // Restore playback speed
        exoPlayer.setPlaybackSpeed(playbackSpeed)

        // Restore mute state
        exoPlayer.volume = if (isMuted) 0f else 1f

        // Restore playing state (with a slight delay to ensure UI is ready)
        delay(300)
        if (isPlaying) {
            exoPlayer.play()
        }
    }

    // Handle orientation changes
    LaunchedEffect(configuration) {
        val newOrientation = configuration.orientation
        deviceOrientation = newOrientation

        // Auto-enter fullscreen when device is rotated to landscape
        if (deviceOrientation == Configuration.ORIENTATION_LANDSCAPE && !isFullscreen) {
            isFullscreen = true
        }
        // Auto-exit fullscreen when device is rotated to portrait (if not manually set)
        else if (deviceOrientation == Configuration.ORIENTATION_PORTRAIT && isFullscreen && !isUserInitiatedFullscreen) {
            isFullscreen = false
        }
    }

    // Update position while playing
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            delay(500) // Update every half second
            val playerDuration = exoPlayer.duration
            if (playerDuration > 0) {
                duration = playerDuration
                val playerPosition = exoPlayer.currentPosition
                currentPosition = (playerPosition.toFloat() / playerDuration.toFloat()).coerceIn(0f, 1f)

                // Update the manager with current position
                VideoPlayerManager.updatePlayerState(
                    videoId = videoId,
                    playbackPosition = playerPosition
                )
            }
        }
    }

    // Auto-hide controls with better timing
    LaunchedEffect(isControlsVisible, isPlaying) {
        if (isControlsVisible && isPlaying) {
            delay(3000) // Hide controls after 3 seconds of inactivity when playing
            isControlsVisible = false
        }
    }

    // Handle fullscreen changes with modern APIs
    LaunchedEffect(isFullscreen) {
        activity?.let {
            if (isFullscreen) {
                // Enter fullscreen
                it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

                // Modern way to hide system UI
                WindowCompat.setDecorFitsSystemWindows(it.window, false)
                val controller = WindowInsetsControllerCompat(it.window, it.window.decorView)
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

                // Keep screen on during video playback
                it.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                // Exit fullscreen
                it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

                // Modern way to show system UI
                WindowCompat.setDecorFitsSystemWindows(it.window, true)
                val controller = WindowInsetsControllerCompat(it.window, it.window.decorView)
                controller.show(WindowInsetsCompat.Type.systemBars())

                // Remove keep screen on flag
                it.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
            onFullscreenToggle(isFullscreen)
        }
    }

    // Control ExoPlayer based on isPlaying state
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            exoPlayer.play()
            // Update MainActivity companion object
            MainActivity.isVideoPlaying = true

            // Pause other videos
            VideoPlayerManager.pauseOthers(videoId)

            // Update the manager
            VideoPlayerManager.updatePlayerState(videoId, isPlaying = true)
        } else {
            exoPlayer.pause()
            // Update MainActivity companion object
            MainActivity.isVideoPlaying = false

            // Update the manager
            VideoPlayerManager.updatePlayerState(videoId, isPlaying = false)
        }
    }

    // Control volume based on mute state
    LaunchedEffect(isMuted) {
        exoPlayer.volume = if (isMuted) 0f else 1f
        VideoPlayerManager.updatePlayerState(videoId, isMuted = isMuted)
    }

    // Update playback speed
    LaunchedEffect(playbackSpeed) {
        exoPlayer.setPlaybackSpeed(playbackSpeed)
        VideoPlayerManager.updatePlayerState(videoId, playbackSpeed = playbackSpeed)
    }

    // Observe lifecycle to handle app backgrounding/foregrounding
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    // Save state when app goes to background
                    if (isPlaying) {
                        exoPlayer.pause()
                        VideoPlayerManager.updatePlayerState(
                            videoId = videoId,
                            isPlaying = true, // Remember it was playing
                            playbackPosition = exoPlayer.currentPosition
                        )
                    } else {
                        VideoPlayerManager.updatePlayerState(
                            videoId = videoId,
                            playbackPosition = exoPlayer.currentPosition
                        )
                    }
                }
                Lifecycle.Event.ON_RESUME -> {
                    // Restore state when app comes to foreground
                    if (playerInfo.isPlaying) {
                        exoPlayer.seekTo(playerInfo.playbackPosition)
                        exoPlayer.play()
                        isPlaying = true
                    }
                }
                else -> { /* ignore other events */ }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Format time for display
    fun formatTime(timeMs: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(timeMs)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeMs) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeMs) % 60

        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    // Clean up when component is disposed
    DisposableEffect(Unit) {
        // Add a listener to update state
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) {
                    isPlaying = false
                    currentPosition = 1f
                    VideoPlayerManager.updatePlayerState(videoId, isPlaying = false)
                }
            }
        }

        exoPlayer.addListener(listener)

        onDispose {
            // Save position before disposing
            VideoPlayerManager.updatePlayerState(
                videoId = videoId,
                playbackPosition = exoPlayer.currentPosition,
                isPlaying = isPlaying,
                playbackSpeed = playbackSpeed,
                isMuted = isMuted
            )

            exoPlayer.removeListener(listener)

            // Note: We don't release the player here to maintain state
            // It will be managed by VideoPlayerManager

            // Ensure we return to portrait mode
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            activity?.let {
                WindowCompat.setDecorFitsSystemWindows(it.window, true)
                val controller = WindowInsetsControllerCompat(it.window, it.window.decorView)
                controller.show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        // Single tap toggles controls visibility
                        isControlsVisible = !isControlsVisible
                    },
                    onDoubleTap = { offset ->
                        // Double tap on left side seeks backward, right side seeks forward
                        val screenWidth = size.width
                        if (offset.x < screenWidth / 2) {
                            // Double tap on left side - seek backward 10 seconds
                            val newPosition = (exoPlayer.currentPosition - 10000).coerceAtLeast(0)
                            exoPlayer.seekTo(newPosition)
                            // Show a backward indicator
                            isRewindIndicatorVisible = true
                            coroutineScope.launch {
                                delay(500)
                                isRewindIndicatorVisible = false
                            }
                        } else {
                            // Double tap on right side - seek forward 10 seconds
                            val newPosition = (exoPlayer.currentPosition + 10000).coerceAtMost(exoPlayer.duration)
                            exoPlayer.seekTo(newPosition)
                            // Show a forward indicator
                            isForwardIndicatorVisible = true
                            coroutineScope.launch {
                                delay(500)
                                isForwardIndicatorVisible = false
                            }
                        }
                    }
                )
            }
    ) {
        // ExoPlayer View using androidx.media3
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false // We'll create our own controls

                    // Set the resize mode to fit the video content
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT

                    // Set a solid background color to ensure video is visible
                    setBackgroundColor(android.graphics.Color.BLACK)

                    // Make sure the surface is visible
                    keepScreenOn = true
                }
            },
            modifier = Modifier.fillMaxWidth(),
            update = { playerView ->
                // Update the player view when fullscreen changes
                playerView.player = exoPlayer

                // Always use RESIZE_MODE_FIT to prevent cropping content
                playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
            }
        )

        // Rewind indicator
        AnimatedVisibility(
            visible = isRewindIndicatorVisible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(16.dp)
                    .size(60.dp)
                    .background(Color.Black.copy(alpha = 0.6f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.FastRewind,
                        contentDescription = "Rewind 10 seconds",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "10s",
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        // Forward indicator
        AnimatedVisibility(
            visible = isForwardIndicatorVisible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(16.dp)
                    .size(60.dp)
                    .background(Color.Black.copy(alpha = 0.6f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.FastForward,
                        contentDescription = "Forward 10 seconds",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "10s",
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        // Custom controls overlay (only visible when isControlsVisible is true)
        if (isControlsVisible) {
            // Center play/pause button
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = { isPlaying = !isPlaying },
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color.Black.copy(alpha = 0.5f), shape = CircleShape)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            // Bottom controls bar
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(8.dp)
            ) {
                // Progress slider
                Slider(
                    value = currentPosition,
                    onValueChange = { newPosition ->
                        if (duration > 0) {
                            val seekPositionMs = (newPosition * duration).toLong()
                            exoPlayer.seekTo(seekPositionMs)
                            currentPosition = newPosition
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = androidx.compose.material3.SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Color.White,
                        inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                    )
                )

                // Time and controls row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Current time / total time
                    Text(
                        text = "${formatTime(exoPlayer.currentPosition)} / ${formatTime(duration)}",
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Volume toggle
                        IconButton(
                            onClick = { isMuted = !isMuted },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = if (isMuted) "Unmute" else "Mute",
                                tint = Color.White
                            )
                        }

                        // Playback speed control
                        Box {
                            IconButton(
                                onClick = { isSpeedMenuExpanded = true },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Speed,
                                    contentDescription = "Playback Speed",
                                    tint = Color.White
                                )
                            }

                            DropdownMenu(
                                expanded = isSpeedMenuExpanded,
                                onDismissRequest = { isSpeedMenuExpanded = false },
                                modifier = Modifier.background(Color.Black.copy(alpha = 0.9f))
                            ) {
                                listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { speed ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "${speed}x",
                                                color = if (playbackSpeed == speed) Color.White else Color.Gray
                                            )
                                        },
                                        onClick = {
                                            playbackSpeed = speed
                                            exoPlayer.setPlaybackSpeed(speed)
                                            isSpeedMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Fullscreen toggle
                        IconButton(
                            onClick = {
                                isUserInitiatedFullscreen = !isFullscreen
                                isFullscreen = !isFullscreen
                            },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                contentDescription = if (isFullscreen) "Exit Fullscreen" else "Enter Fullscreen",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
