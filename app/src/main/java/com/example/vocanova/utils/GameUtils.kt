package com.example.vocanova.utils

import android.util.Log

/**
 * Utility class for game-related functions
 */
object GameUtils {
    private const val TAG = "GameUtils"

    fun calculateCurrencyEarned(score: Int): Int {
        // Base conversion: 1 currency for every 10 points
        return score / 10
    }
}
