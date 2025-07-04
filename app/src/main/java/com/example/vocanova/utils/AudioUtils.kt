package com.example.vocanova.utils

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

/**
 * Utility class to handle audio playback and text-to-speech functionality
 */
class AudioUtils private constructor(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var tts: TextToSpeech? = null
    private var ttsInitialized = false
    private var currentMusicResource = 0

    // Global mute state
    private var isGloballyMuted: Boolean = false
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("audio_prefs", Context.MODE_PRIVATE)

    companion object {
        @Volatile
        private var INSTANCE: AudioUtils? = null

        fun getInstance(context: Context): AudioUtils {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AudioUtils(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    init {
        initTextToSpeech(context)
        // Load mute state from SharedPreferences
        isGloballyMuted = sharedPreferences.getBoolean("is_globally_muted", false)
    }

    private fun initTextToSpeech(context: Context) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Language not supported")
                } else {
                    ttsInitialized = true
                }
            } else {
                Log.e("TTS", "Initialization failed")
            }
        }
    }

    fun speakWord(word: String) {
        if (isGloballyMuted) return
        if (ttsInitialized) {
            tts?.speak(word, TextToSpeech.QUEUE_FLUSH, null, "word_id")
        } else {
            Log.e("TTS", "TextToSpeech not initialized")
        }
    }

    fun playBackgroundMusic(resourceId: Int, shouldLoop: Boolean = true) {
        if (isGloballyMuted) {
            stopBackgroundMusic() // Ensure music is stopped if globally muted
            return
        }

        try {
            if (currentMusicResource == resourceId && mediaPlayer?.isPlaying == true) {
                return
            }
            currentMusicResource = resourceId
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(context, resourceId)?.apply {
                isLooping = shouldLoop
                setVolume(0.7f, 0.7f)
                start()
            }
        } catch (e: Exception) {
            Log.e("AudioUtils", "Error playing background music: ${e.message}")
        }
    }

    fun stopBackgroundMusic() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
            mediaPlayer = null
            currentMusicResource = 0
        } catch (e: Exception) {
            Log.e("AudioUtils", "Error stopping music: ${e.message}")
        }
    }

    fun playSoundWithVolume(resourceId: Int, volume: Float = 1.0f) {
        if (isGloballyMuted) return
        try {
            if (!resourceExists(resourceId)) {
                Log.e("AudioUtils", "Sound resource not found: $resourceId")
                return
            }

            MediaPlayer.create(context, resourceId)?.apply {
                setVolume(volume, volume)
                setOnCompletionListener { mp -> mp.release() }
                start()
            }
        } catch (e: Exception) {
            Log.e("AudioUtils", "Error playing sound: ${e.message}")
        }
    }
    
    // Global Mute Functions
    fun isGloballyMuted(): Boolean = isGloballyMuted

    fun toggleGlobalMute() {
        isGloballyMuted = !isGloballyMuted
        sharedPreferences.edit().putBoolean("is_globally_muted", isGloballyMuted).apply()
        if (isGloballyMuted) {
            stopBackgroundMusic()
            // Potentially stop TTS if it's speaking
            tts?.stop()
        } else {
            // If unmuting and there was a currentMusicResource, you might want to resume it
            // For simplicity, we'll let the game screens handle re-triggering music if needed.
        }
    }

    private fun resourceExists(resourceId: Int): Boolean {
        return try {
            context.resources.getResourceName(resourceId)
            true
        } catch (e: Exception) {
            // Log.e("AudioUtils", "Resource not found: $resourceId") // Can be noisy
            false
        }
    }

    fun onDestroy() {
        stopBackgroundMusic()
        tts?.stop()
        tts?.shutdown()
    }
}
