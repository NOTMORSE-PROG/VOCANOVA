package com.example.vocanova.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vocanova.data.model.PowerUp
import com.example.vocanova.data.repository.ShopRepository
import com.example.vocanova.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class   ShopViewModel @Inject constructor(
    private val shopRepository: ShopRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val tag = "ShopViewModel"

    private val _powerUps = MutableStateFlow<List<PowerUp>>(emptyList())
    val powerUps: StateFlow<List<PowerUp>> = _powerUps

    private val _userPowerUps = MutableStateFlow<Map<String, Int>>(emptyMap())
    val userPowerUps: StateFlow<Map<String, Int>> = _userPowerUps

    private val _userCurrency = MutableStateFlow(0)
    val userCurrency: StateFlow<Int> = _userCurrency

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    init {
        loadPowerUps()
        loadUserPowerUps()

        // Observe currency changes from repository
        viewModelScope.launch {
            userRepository.userCurrency.collectLatest { currency ->
                _userCurrency.value = currency
                Log.d(tag, "Currency updated from repository: $currency")
            }
        }

        // Initial load
        loadUserCurrency()
    }

    private fun loadPowerUps() {
        _powerUps.value = shopRepository.getAllPowerUps()
    }

    private fun loadUserPowerUps() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _userPowerUps.value = shopRepository.getUserPowerUps()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load your inventory: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadUserCurrency() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = userRepository.getCurrentUserId()
                if (userId != null) {
                    val user = userRepository.getUserProfile(userId)
                    if (user != null) {
                        _userCurrency.value = user.currency
                        Log.d(tag, "Loaded user currency: ${user.currency}")
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load your currency: ${e.message}"
                Log.e(tag, "Failed to load currency", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun purchasePowerUp(powerUpId: String, quantity: Int = 1) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val powerUpItem = _powerUps.value.find { it.id == powerUpId }
                if (powerUpItem == null) {
                    _errorMessage.value = "Power-up not found"
                    return@launch
                }

                val totalPrice = powerUpItem.price * quantity
                if (_userCurrency.value < totalPrice) {
                    _errorMessage.value = "Not enough currency"
                    return@launch
                }

                // First deduct currency
                val userId = userRepository.getCurrentUserId() ?: return@launch
                val currencyUpdateSuccess = userRepository.updateUserCurrency(userId, -totalPrice)

                if (!currencyUpdateSuccess) {
                    _errorMessage.value = "Failed to update currency"
                    return@launch
                }

                // Then add power-up
                val success = shopRepository.purchasePowerUp(powerUpId, quantity)
                if (success) {
                    _successMessage.value = "Successfully purchased ${quantity}x ${powerUpItem.name}"
                    // Refresh data
                    loadUserPowerUps()
                    loadUserCurrency()
                } else {
                    // If power-up purchase fails, refund the currency
                    userRepository.updateUserCurrency(userId, totalPrice)
                    _errorMessage.value = "Failed to purchase power-up"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                Log.e(tag, "Error purchasing power-up", e)
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
