package com.example.vocanova.data.repository

import android.util.Log
import com.example.vocanova.data.model.PowerUp
import com.example.vocanova.data.model.PowerUpType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShopRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val tag = "ShopRepository"

    private val powerUps = listOf(
        PowerUp(
            id = "freeze_time",
            name = "Freeze Time",
            description = "Freezes the timer for 10 seconds",
            price = 50,
            type = PowerUpType.FREEZE_TIME
        ),
        PowerUp(
            id = "fifty_fifty",
            name = "50/50",
            description = "Removes two wrong answers",
            price = 30,
            type = PowerUpType.FIFTY_FIFTY
        ),
        PowerUp(
            id = "reverse_time",
            name = "Reverse Time",
            description = "Go back to the previous question",
            price = 70,
            type = PowerUpType.REVERSE_TIME
        )
    )

    fun getAllPowerUps(): List<PowerUp> {
        return powerUps
    }

    suspend fun getUserPowerUps(): Map<String, Int> {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return emptyMap()

        return try {
            val userPowerUpsDoc = firestore.collection("user_powerups").document(userId).get().await()

            if (!userPowerUpsDoc.exists()) {
                // Initialize with empty counts if document doesn't exist
                val initialPowerUps = powerUps.associate { it.id to 0 }
                firestore.collection("user_powerups").document(userId).set(initialPowerUps).await()
                return initialPowerUps
            }

            val result = mutableMapOf<String, Int>()
            for (powerUp in powerUps) {
                val count = userPowerUpsDoc.getLong(powerUp.id)?.toInt() ?: 0
                result[powerUp.id] = count
            }

            result
        } catch (e: Exception) {
            Log.e(tag, "Error getting user power-ups", e)
            emptyMap()
        }
    }

    suspend fun purchasePowerUp(powerUpId: String, quantity: Int = 1): Boolean {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return false

        return try {
            // Update the power-up count
            firestore.collection("user_powerups").document(userId)
                .update(powerUpId, FieldValue.increment(quantity.toLong()))
                .await()

            true
        } catch (e: Exception) {
            Log.e(tag, "Error purchasing power-up", e)
            false
        }
    }

    suspend fun usePowerUp(powerUpId: String): Boolean {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return false

        return try {
            // Get current count
            val userPowerUpsDoc = firestore.collection("user_powerups").document(userId).get().await()
            val currentCount = userPowerUpsDoc.getLong(powerUpId)?.toInt() ?: 0

            if (currentCount <= 0) {
                return false
            }

            // Decrement the power-up count
            firestore.collection("user_powerups").document(userId)
                .update(powerUpId, FieldValue.increment(-1))
                .await()

            true
        } catch (e: Exception) {
            Log.e(tag, "Error using power-up", e)
            false
        }
    }

    suspend fun refundPowerUp(powerUpId: String): Boolean {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return false

        return try {
            firestore.collection("user_powerups").document(userId)
                .update(powerUpId, FieldValue.increment(1))
                .await()
            true
        } catch (e: Exception) {
            Log.e(tag, "Error refunding power-up", e)
            false
        }
    }
}
