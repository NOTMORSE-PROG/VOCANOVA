package com.example.vocanova.data.model

data class PowerUp(
    val id: String,
    val name: String,
    val description: String,
    val price: Int,
    val type: PowerUpType
)

enum class PowerUpType {
    FREEZE_TIME,
    FIFTY_FIFTY,
    REVERSE_TIME
}
