package com.example.vocanova.data.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val currency: Int = 0,
    val completedLessons: List<String> = emptyList(),
    val purchasedItems: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = 0L
)
