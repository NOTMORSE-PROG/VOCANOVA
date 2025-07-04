package com.example.vocanova.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth
) {

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    suspend fun login(email: String, password: String): Boolean {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user != null
        } catch (_: FirebaseAuthInvalidUserException) {
            // User does not exist or has been disabled
            throw Exception("Account doesn't exist or has been disabled")
        } catch (_: FirebaseAuthInvalidCredentialsException) {
            // Invalid credentials (wrong password)
            throw Exception("Invalid email or password")
        } catch (exception: Exception) {
            throw Exception("Login failed: ${exception.message}")
        }
    }

    suspend fun signup(email: String, password: String): String? {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.uid
        } catch (_: FirebaseAuthWeakPasswordException) {
            // Password is too weak
            throw Exception("Password is too weak. Please use at least 6 characters")
        } catch (_: FirebaseAuthInvalidCredentialsException) {
            // Invalid email format
            throw Exception("Invalid email format")
        } catch (_: FirebaseAuthUserCollisionException) {
            // Email already in use
            throw Exception("Email is already in use")
        } catch (exception: Exception) {
            throw Exception("Signup failed: ${exception.message}")
        }
    }

    suspend fun resetPassword(email: String): Boolean {
        return try {
            auth.sendPasswordResetEmail(email).await()
            true
        } catch (_: FirebaseAuthInvalidUserException) {
            throw Exception("No account found with this email")
        } catch (exception: Exception) {
            throw Exception("Password reset failed: ${exception.message}")
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
}
