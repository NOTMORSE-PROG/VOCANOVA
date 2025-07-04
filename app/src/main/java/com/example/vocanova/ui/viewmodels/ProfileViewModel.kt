package com.example.vocanova.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vocanova.data.model.User
import com.example.vocanova.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> = _userProfile

    private val _currency = MutableStateFlow(0)
    val currency: StateFlow<Int> = _currency

    private val _quizScores = MutableStateFlow<Map<String, Int>>(emptyMap())
    val quizScores: StateFlow<Map<String, Int>> = _quizScores

    init {
        viewModelScope.launch {
            loadUserProfile()
        }
    }

    /**
     * Load user profile data
     */
    fun loadUserProfile() {
        viewModelScope.launch {
            val userId = userRepository.getCurrentUserId()
            if (userId != null) {
                val user = userRepository.getUserProfile(userId)
                _userProfile.value = user
                _currency.value = user?.currency ?: 0
            }
        }
    }

    /**
     * Load quiz scores
     */
    fun loadQuizScores() {
        viewModelScope.launch {
            val userId = userRepository.getCurrentUserId()
            if (userId != null) {
                // In a real app, you would fetch this from Firestore
                // For now, we'll use dummy data
                val scores = mapOf(
                    "week1" to 100,  // Perfect score
                    "week2" to 80,
                    "week3" to 70
                )
                _quizScores.value = scores
            }
        }
    }

    /**
     * Update quiz score and check for achievements
     */
    fun updateQuizScore(quizId: String, score: Int) {
        viewModelScope.launch {
            val userId = userRepository.getCurrentUserId()
            if (userId != null) {
                // Update quiz scores
                val currentScores = _quizScores.value.toMutableMap()
                currentScores[quizId] = score
                _quizScores.value = currentScores

                // Refresh data
                loadUserProfile()
            }
        }
    }
}
