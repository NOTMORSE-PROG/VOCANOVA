package com.example.vocanova.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vocanova.data.model.Achievement
import com.example.vocanova.data.repository.AchievementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AchievementViewModel @Inject constructor(
    private val achievementRepository: AchievementRepository
) : ViewModel() {
    private val tag = "AchievementViewModel"

    private val _achievements = MutableStateFlow<List<Achievement>>(emptyList())
    val achievements: StateFlow<List<Achievement>> = _achievements

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    init {
        loadAchievements()

        // Collect achievements updates
        viewModelScope.launch {
            achievementRepository.achievements.collectLatest { achievementsList ->
                _achievements.value = achievementsList
            }
        }
    }

    fun loadAchievements() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // First check if we need to initialize achievements
                achievementRepository.initializeAchievements()

                // Then load the achievements
                achievementRepository.loadAchievements()

                _isLoading.value = false
            } catch (e: Exception) {
                Log.e(tag, "Error loading achievements", e)
                _errorMessage.value = "Failed to load achievements: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun claimAchievementReward(achievementId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val success = achievementRepository.claimAchievementReward(achievementId)

                // Always reload achievements after a claim attempt
                achievementRepository.loadAchievements()

                if (success) {
                    _successMessage.value = "Reward claimed successfully!"
                } else {
                    _errorMessage.value = "Failed to claim reward"
                }
            } catch (e: Exception) {
                Log.e(tag, "Error claiming achievement reward", e)
                _errorMessage.value = "Failed to claim reward: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }
}
