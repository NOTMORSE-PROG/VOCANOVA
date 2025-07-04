package com.example.vocanova.data.repository

import android.util.Log
import com.example.vocanova.data.model.Achievement
import com.example.vocanova.data.model.QuizResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AchievementRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository
) {
    private val tag = "AchievementRepository"

    private val _achievements = MutableStateFlow<List<Achievement>>(emptyList())
    val achievements: StateFlow<List<Achievement>> = _achievements

    // Initialize achievements for a user
    suspend fun initializeAchievements() {
        try {
            val userId = auth.currentUser?.uid ?: return

            // First check if user has achievements collection
            val userAchievementsRef = firestore.collection("users").document(userId)
                .collection("achievements")

            val existingAchievements = userAchievementsRef.get().await()

            if (existingAchievements.isEmpty) {
                // Create default achievements
                val defaultAchievements = Achievement.createQuizAchievements()

                // Add each achievement to Firestore
                for (achievement in defaultAchievements) {
                    userAchievementsRef.document(achievement.id).set(achievement).await()
                }

                Log.d(tag, "Initialized achievements for user $userId")
                _achievements.value = defaultAchievements
            } else {
                // Load existing achievements
                val achievementsList = existingAchievements.documents.mapNotNull {
                    it.toObject(Achievement::class.java)
                }
                _achievements.value = achievementsList
                Log.d(tag, "Loaded ${achievementsList.size} existing achievements for user $userId")
            }
        } catch (e: Exception) {
            Log.e(tag, "Error initializing achievements (Ask Gemini)", e)
        }
    }

    // Load achievements for a user
    suspend fun loadAchievements() {
        try {
            val userId = auth.currentUser?.uid ?: return

            val userAchievementsRef = firestore.collection("users").document(userId)
                .collection("achievements")

            val achievementsSnapshot = userAchievementsRef.get().await()
            val achievementsList = achievementsSnapshot.documents.mapNotNull {
                it.toObject(Achievement::class.java)
            }

            _achievements.value = achievementsList
            Log.d(tag, "Loaded ${achievementsList.size} achievements for user $userId")
        } catch (e: Exception) {
            Log.e(tag, "Error loading achievements (Ask Gemini)", e)
        }
    }

    // Check if a quiz result unlocks an achievement
    suspend fun checkQuizAchievement(quizResult: QuizResult) {
        try {
            val userId = auth.currentUser?.uid ?: return

            // Perfect score means score is 100
            val isPerfectScore = quizResult.score == 100

            if (isPerfectScore) {
                val achievementId = "perfect_${quizResult.quizId}"

                // Get the achievement document
                val achievementRef = firestore.collection("users").document(userId)
                    .collection("achievements").document(achievementId)

                val achievementDoc = achievementRef.get().await()

                if (achievementDoc.exists()) {
                    val achievement = achievementDoc.toObject(Achievement::class.java)

                    if (achievement != null && !achievement.isUnlocked) {
                        // Update the achievement to unlocked
                        val updatedAchievement = achievement.copy(
                            isUnlocked = true,
                            unlockedAt = System.currentTimeMillis()
                        )

                        achievementRef.set(updatedAchievement).await()

                        Log.d(tag, "Unlocked achievement $achievementId for user $userId")

                        // Refresh achievements list
                        loadAchievements()
                    }
                }
            }

            // Save the quiz result
            saveQuizResult(quizResult)

        } catch (e: Exception) {
            Log.e(tag, "Error checking quiz achievement", e)
        }
    }

    // Save quiz result to Firestore
    private suspend fun saveQuizResult(quizResult: QuizResult) {
        try {
            val userId = auth.currentUser?.uid ?: return

            val quizResultsRef = firestore.collection("users").document(userId)
                .collection("quizResults")

            // Add timestamp to the result
            val resultWithTimestamp = quizResult.copy()

            // Use quiz ID and timestamp as document ID to avoid duplicates
            val docId = "${quizResult.quizId}_${System.currentTimeMillis()}"

            quizResultsRef.document(docId).set(resultWithTimestamp).await()

            Log.d(tag, "Saved quiz result for ${quizResult.quizId} with score ${quizResult.score}")
        } catch (e: Exception) {
            Log.e(tag, "Error saving quiz result", e)
        }
    }

    // Claim achievement reward
    suspend fun claimAchievementReward(achievementId: String): Boolean {
        try {
            val userId = auth.currentUser?.uid ?: return false

            // Get the achievement
            val achievementRef = firestore.collection("users").document(userId)
                .collection("achievements").document(achievementId)

            val achievementDoc = achievementRef.get().await()

            if (achievementDoc.exists()) {
                val achievement = achievementDoc.toObject(Achievement::class.java)

                if (achievement != null && achievement.isUnlocked && !achievement.isClaimed) {
                    // Update the achievement to claimed
                    val updatedAchievement = achievement.copy(isClaimed = true)

                    // Transaction to update both achievement and user currency
                    firestore.runTransaction { transaction ->
                        // Update achievement
                        transaction.set(achievementRef, updatedAchievement)

                        // Get user document reference
                        val userRef = firestore.collection("users").document(userId)

                        // Update user currency
                        transaction.update(userRef, "currency", com.google.firebase.firestore.FieldValue.increment(achievement.rewardAmount.toLong()))
                    }.await()

                    Log.d(tag, "Claimed achievement $achievementId reward of ${achievement.rewardAmount} for user $userId")

                    // Refresh achievements list
                    loadAchievements()

                    return true
                }
            }

            return false
        } catch (e: Exception) {
            Log.e(tag, "Error claiming achievement reward", e)
            return false
        }
    }
}
