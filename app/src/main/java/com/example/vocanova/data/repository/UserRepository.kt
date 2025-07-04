package com.example.vocanova.data.repository

import android.util.Log
import com.example.vocanova.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    firestore: FirebaseFirestore
) {
    private val tag = "UserRepository"
    private val usersCollection = firestore.collection("users")
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _userCurrency = MutableStateFlow(0)
    val userCurrency: StateFlow<Int> = _userCurrency

    private var userListener: ListenerRegistration? = null

    init {
        setupCurrentUserListener()
    }

    private fun setupCurrentUserListener() {
        val userId = getCurrentUserId()
        if (userId != null) {
            Log.d(tag, "Setting up listener for user: $userId")
            userListener?.remove()

            userListener = usersCollection.document(userId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(tag, "Error listening for user updates", error)
                        // Check if it's a permission error
                        if (error.message?.contains("PERMISSION_DENIED") == true) {
                            Log.e(tag, "Permission denied. User might not be properly authenticated.")
                            // Try to refresh the auth token
                            FirebaseAuth.getInstance().currentUser?.getIdToken(true)
                                ?.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Log.d(tag, "Auth token refreshed successfully")
                                        // Retry setting up the listener
                                        setupCurrentUserListener()
                                    } else {
                                        Log.e(tag, "Failed to refresh auth token", task.exception)
                                    }
                                }
                        }
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        try {
                            updateUserStateFromSnapshot(snapshot)
                        } catch (e: Exception) {
                            Log.e(tag, "Error parsing user data", e)
                        }
                    } else {
                        Log.w(tag, "User document does not exist or is null")
                    }
                }
        } else {
            Log.w(tag, "No authenticated user found")
        }
    }

    private fun updateUserStateFromSnapshot(snapshot: DocumentSnapshot) {
        try {
            val user = snapshot.toObject(User::class.java)
            if (user != null) {
                _currentUser.value = user

                // Get currency from the document
                val currencyValue = snapshot.getLong("currency")
                if (currencyValue != null) {
                    _userCurrency.value = currencyValue.toInt()
                    Log.d(tag, "Updated currency from Firestore: ${_userCurrency.value}")
                } else {
                    // If currency field doesn't exist yet, check for points field as fallback
                    val pointsValue = snapshot.getLong("points")
                    if (pointsValue != null) {
                        _userCurrency.value = pointsValue.toInt()
                        Log.d(tag, "Using points as currency: ${_userCurrency.value}")

                        // Migrate points to currency
                        val userId = getCurrentUserId()
                        if (userId != null) {
                            usersCollection.document(userId)
                                .update("currency", pointsValue)
                                .addOnSuccessListener {
                                    Log.d(tag, "Migrated points to currency")
                                }
                                .addOnFailureListener { e ->
                                    Log.e(tag, "Failed to migrate points to currency", e)
                                }
                        }
                    } else {
                        Log.w(tag, "Neither currency nor points field found in Firestore document")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Error converting snapshot to User", e)
        }
    }

    fun getCurrentUserId(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }

    suspend fun getUserProfile(userId: String): User? {
        return try {
            val userDoc = usersCollection.document(userId).get().await()
            if (userDoc.exists()) {
                val user = userDoc.toObject(User::class.java)
                if (user != null) {
                    _currentUser.value = user

                    // Get currency from the document
                    val currencyValue = userDoc.getLong("currency")
                    if (currencyValue != null) {
                        _userCurrency.value = currencyValue.toInt()
                        Log.d(tag, "Retrieved currency: ${_userCurrency.value}")
                    } else {
                        // If currency field doesn't exist yet, check for points field as fallback
                        val pointsValue = userDoc.getLong("points")
                        if (pointsValue != null) {
                            _userCurrency.value = pointsValue.toInt()
                            Log.d(tag, "Using points as currency: ${_userCurrency.value}")

                            // Migrate points to currency
                            usersCollection.document(userId)
                                .update("currency", pointsValue)
                                .addOnSuccessListener {
                                    Log.d(tag, "Migrated points to currency")
                                }
                                .addOnFailureListener { e ->
                                    Log.e(tag, "Failed to migrate points to currency", e)
                                }
                        }
                    }
                }
                user
            } else {
                Log.w(tag, "User document does not exist for ID: $userId")
                null
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting user profile", e)
            null
        }
    }

    suspend fun updateUserCurrency(userId: String, amount: Int): Boolean {
        return try {
            Log.d(tag, "Updating currency for user $userId by $amount")

            // Use a direct update with FieldValue.increment
            val userRef = usersCollection.document(userId)

            // First check if the document exists
            val docSnapshot = userRef.get().await()
            if (!docSnapshot.exists()) {
                Log.e(tag, "User document doesn't exist, can't update currency")
                return false
            }

            // Check if the document has a currency field
            val hasCurrency = docSnapshot.contains("currency")

            if (hasCurrency) {
                // Log the current value for debugging
                val currentCurrency = docSnapshot.getLong("currency")?.toInt() ?: 0
                Log.d(tag, "Current currency before update: $currentCurrency")

                // Perform the update
                userRef.update("currency", FieldValue.increment(amount.toLong())).await()

                // Verify the update worked by getting the document again
                val updatedDoc = userRef.get().await()
                val newCurrency = updatedDoc.getLong("currency")?.toInt() ?: 0
                Log.d(tag, "New currency after update: $newCurrency (expected: ${currentCurrency + amount})")

                // Update the local state
                _userCurrency.value = newCurrency

                true
            } else {
                // Check if there's a points field we can update instead
                val hasPoints = docSnapshot.contains("points")

                if (hasPoints) {
                    // Get current points
                    val currentPoints = docSnapshot.getLong("points")?.toInt() ?: 0
                    Log.d(tag, "Current points before update: $currentPoints")

                    // Update points and add currency field
                    userRef.update(
                        mapOf(
                            "points" to FieldValue.increment(amount.toLong()),
                            "currency" to (currentPoints + amount)
                        )
                    ).await()

                    // Verify the update
                    val updatedDoc = userRef.get().await()
                    val newCurrency = updatedDoc.getLong("currency")?.toInt() ?: 0
                    Log.d(tag, "Added currency field with value: $newCurrency")

                    // Update local state
                    _userCurrency.value = newCurrency

                    true
                } else {
                    // Neither currency nor points exists, create currency field
                    userRef.update("currency", amount).await()
                    _userCurrency.value = amount
                    Log.d(tag, "Created new currency field with value: $amount")
                    true
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Error updating currency", e)
            false
        }
    }

    // Add this method to make it easier to update currency from game screens
    suspend fun updateCurrency(amount: Int): Boolean {
        // Log the original amount for debugging
        Log.d(tag, "updateCurrency called with amount: $amount")

        val userId = getCurrentUserId() ?: return false

        // Check if we're already in a transaction to prevent double updates
        val transactionKey = "transaction_$userId"
        if (isInTransaction(transactionKey)) {
            Log.d(tag, "Skipping duplicate currency update for amount: $amount")
            return true
        }

        setInTransaction(transactionKey, true)
        try {
            return updateUserCurrency(userId, amount)
        } finally {
            setInTransaction(transactionKey, false)
        }
    }

    // Add these helper methods to track transactions
    private val activeTransactions = mutableMapOf<String, Boolean>()

    private fun isInTransaction(key: String): Boolean {
        return activeTransactions[key] == true
    }

    private fun setInTransaction(key: String, value: Boolean) {
        if (value) {
            activeTransactions[key] = true
        } else {
            activeTransactions.remove(key)
        }
    }

    suspend fun createUserProfile(userId: String, name: String, email: String): Boolean {
        return try {
            val newUser = User(
                id = userId,
                name = name,
                email = email,
                createdAt = System.currentTimeMillis(),
                currency = 0,
            )

            usersCollection.document(userId).set(newUser).await()
            _currentUser.value = newUser
            _userCurrency.value = newUser.currency

            Log.d(tag, "Created new user profile for $userId")
            true
        } catch (e: Exception) {
            Log.e(tag, "Error creating user profile", e)
            false
        }
    }

    suspend fun updateUserProfile(user: User): Boolean {
        return try {
            val userId = user.id
            if (userId.isBlank()) {
                Log.e(tag, "Cannot update user with blank ID")
                return false
            }

            usersCollection.document(userId).set(user).await()
            _currentUser.value = user
            _userCurrency.value = user.currency

            Log.d(tag, "Updated user profile for ${user.id}")
            true
        } catch (e: Exception) {
            Log.e(tag, "Error updating user profile", e)
            false
        }
    }
}
