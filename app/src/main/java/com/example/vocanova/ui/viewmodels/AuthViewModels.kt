package com.example.vocanova.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vocanova.data.repository.AuthRepository
import com.example.vocanova.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    private val _successMessage = MutableStateFlow("")
    val successMessage: StateFlow<String> = _successMessage

    init {
        checkAuthState()
    }

    fun checkAuthState() {
        _isLoggedIn.value = authRepository.isUserLoggedIn()
    }

    fun login(email: String, password: String, rememberMe: Boolean = false) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val success = authRepository.login(email, password)
                if (success) {
                    _isLoggedIn.value = true
                    _successMessage.value = "Login successful"

                    // Update last login timestamp
                    val userId = authRepository.getCurrentUserId()
                    if (userId != null) {
                        val user = userRepository.getUserProfile(userId)
                        if (user != null) {
                            userRepository.updateUserProfile(
                                user.copy(lastLoginAt = System.currentTimeMillis())
                            )
                        }
                    }
                } else {
                    _errorMessage.value = "Login failed. Please check your credentials."
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "An error occurred during login"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signup(name: String, email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val userId = authRepository.signup(email, password)
                if (userId != null) {
                    userRepository.createUserProfile(userId, name, email)
                    _isLoggedIn.value = true
                    _successMessage.value = "Account created successfully"
                } else {
                    _errorMessage.value = "Signup failed. Please try again."
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "An error occurred during signup"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetPassword(email: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val success = authRepository.resetPassword(email)
                if (success) {
                    _successMessage.value = "Password reset email sent. Please check your inbox."
                } else {
                    _errorMessage.value = "Failed to send password reset email."
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "An error occurred during password reset"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _isLoggedIn.value = false
            _successMessage.value = "Logged out successfully"
        }
    }

    fun getCurrentUserId(): String? {
        return authRepository.getCurrentUserId()
    }

    fun clearError() {
        _errorMessage.value = ""
    }

    fun clearSuccess() {
        _successMessage.value = ""
    }
}
