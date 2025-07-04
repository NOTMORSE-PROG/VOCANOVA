package com.example.vocanova.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vocanova.data.model.User
import com.example.vocanova.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val tag = "UserViewModel"

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    // Use the StateFlow from UserRepository for currency
    val userCurrency: StateFlow<Int> = userRepository.userCurrency


    init {
        refreshUserData()

        // Collect user updates from repository
        viewModelScope.launch {
            userRepository.currentUser.collectLatest { user ->
                _user.value = user
                Log.d(tag, "User updated: ${user?.name}, Currency: ${user?.currency}")
            }
        }

    }

    fun refreshUserData() {
        viewModelScope.launch {
            val userId = userRepository.getCurrentUserId()
            if (userId != null) {
                val user = userRepository.getUserProfile(userId)
                _user.value = user
                Log.d(tag, "User data refreshed: ${user?.name}, Currency: ${user?.currency}")
            }
        }
    }

}
