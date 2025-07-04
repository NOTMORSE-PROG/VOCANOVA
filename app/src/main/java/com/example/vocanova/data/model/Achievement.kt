package com.example.vocanova.data.model

import com.google.firebase.firestore.PropertyName

data class Achievement(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val iconName: String = "",
    val rewardAmount: Int = 30,
    @get:PropertyName("unlocked") @set:PropertyName("unlocked")
    var isUnlocked: Boolean = false,
    @get:PropertyName("claimed") @set:PropertyName("claimed")
    var isClaimed: Boolean = false,
    val unlockedAt: Long = 0,
    val quizId: String = ""
) {
    companion object {
        fun createQuizAchievements(): List<Achievement> {
            return listOf(
                Achievement(
                    id = "perfect_week1",
                    title = "Week 1 Master",
                    description = "Perfect score on Week 1: Understanding Basic Word Relationships",
                    iconName = "star",
                    quizId = "week1"
                ),
                Achievement(
                    id = "perfect_week2",
                    title = "Week 2 Master",
                    description = "Perfect score on Week 2: Expanding Vocabulary",
                    iconName = "star",
                    quizId = "week2"
                ),
                Achievement(
                    id = "perfect_week3",
                    title = "Week 3 Master",
                    description = "Perfect score on Week 3: Context Clues",
                    iconName = "star",
                    quizId = "week3"
                ),
                Achievement(
                    id = "perfect_week4",
                    title = "Week 4 Master",
                    description = "Perfect score on Week 4: Understanding Word Nuances",
                    iconName = "star",
                    quizId = "week4"
                ),
                Achievement(
                    id = "perfect_part2",
                    title = "Advanced Vocabulary Master",
                    description = "Perfect score on Part 2: Advanced Vocabulary",
                    iconName = "star",
                    quizId = "part2"
                )
            )
        }
    }
}
