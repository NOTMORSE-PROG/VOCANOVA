package com.example.vocanova.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vocanova.data.model.PowerUpType
import com.example.vocanova.data.model.QuizQuestion
import com.example.vocanova.data.model.QuizResult
import com.example.vocanova.data.repository.AchievementRepository
import com.example.vocanova.data.repository.QuizRepository
import com.example.vocanova.data.repository.ShopRepository
import com.example.vocanova.data.repository.UserRepository
import com.example.vocanova.utils.GameUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    private val userRepository: UserRepository,
    private val shopRepository: ShopRepository,
    private val achievementRepository: AchievementRepository
) : ViewModel() {
    private val tag = "QuizViewModel"

    private val _questions = mutableStateListOf<QuizQuestion>()
    val questions: SnapshotStateList<QuizQuestion> = _questions

    private val _userPowerUps = MutableStateFlow<Map<String, Int>>(emptyMap())
    val userPowerUps: StateFlow<Map<String, Int>> = _userPowerUps

    private val _isTimeFrozen = MutableStateFlow(false)
    val isTimeFrozen: StateFlow<Boolean> = _isTimeFrozen

    private val _isFiftyFiftyUsed = MutableStateFlow(false)
    val isFiftyFiftyUsed: StateFlow<Boolean> = _isFiftyFiftyUsed

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    // Store user answers for each question
    private val userAnswers = mutableMapOf<Int, String>()

    // Store previous question states for the reverse time power-up
    private val _previousQuestionStates = mutableListOf<QuestionState>()

    // Add a state for quiz title
    private val _quizTitle = MutableStateFlow("")
    val quizTitle: StateFlow<String> = _quizTitle

    // Enhanced reverse time functionality
    private val _canGoBack = MutableStateFlow(false)
    val canGoBack: StateFlow<Boolean> = _canGoBack

    // Track how many questions we can go back
    private val _reverseTimeCount = MutableStateFlow(0)
    val reverseTimeCount: StateFlow<Int> = _reverseTimeCount

    // Track if reverse time animation is playing
    private val _isReverseTimeAnimating = MutableStateFlow(false)
    val isReverseTimeAnimating: StateFlow<Boolean> = _isReverseTimeAnimating

    init {
        loadUserPowerUps()
    }

    // Update the loadQuestions function to use quizId
    fun loadQuestions(quizId: String) {
        _questions.clear()
        userAnswers.clear()
        _previousQuestionStates.clear()
        _isFiftyFiftyUsed.value = false
        _reverseTimeCount.value = 0

        // Set the quiz title based on the quizId
        _quizTitle.value = when (quizId) {
            "week1" -> "Week 1: Understanding Basic Word Relationships"
            "week2" -> "Week 2: Expanding Vocabulary"
            "week3" -> "Week 3: Context Clues"
            "week4" -> "Week 4: Understanding Word Nuances"
            "part2" -> "Part 2: Advanced Vocabulary"
            else -> "Vocabulary Quiz"
        }

        _questions.addAll(quizRepository.getQuestionsForLesson(quizId))
    }

    private fun loadUserPowerUps() {
        viewModelScope.launch {
            try {
                _userPowerUps.value = shopRepository.getUserPowerUps()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load power-ups: ${e.message}"
            }
        }
    }

    // Save the current question state before moving to the next question
    fun saveQuestionState(questionIndex: Int, selectedAnswer: String?, timeRemaining: Int, score: Int) {
        _previousQuestionStates.add(
            QuestionState(
                questionIndex = questionIndex,
                selectedAnswer = selectedAnswer,
                timeRemaining = timeRemaining,
                score = score
            )
        )
        Log.d(tag, "Saved question state: $questionIndex, answer: $selectedAnswer, time: $timeRemaining, score: $score")
    }

    // Get the previous question state
    fun getPreviousQuestionState(): QuestionState? {
        if (_previousQuestionStates.isEmpty()) return null
        val state = _previousQuestionStates.removeLastOrNull()
        Log.d(tag, "Retrieved previous question state: ${state?.questionIndex}, answer: ${state?.selectedAnswer}")

        // Update the reverse time count
        _reverseTimeCount.value = _previousQuestionStates.size

        // If we've used all our saved states, reset canGoBack
        if (_previousQuestionStates.isEmpty()) {
            _canGoBack.value = false
        }

        return state
    }

    fun submitAnswer(questionIndex: Int, answer: String) {
        userAnswers[questionIndex] = answer
    }

    // Update the calculateResult function to use quizId
    fun calculateResult(quizId: String): QuizResult {
        var correctAnswers = 0

        userAnswers.forEach { (index, answer) ->
            if (index < _questions.size && answer == _questions[index].correctAnswer) {
                correctAnswers++
            }
        }

        val score = correctAnswers * 10
        val totalQuestions = _questions.size

        val result = QuizResult(
            totalQuestions = _questions.size,
            correctAnswers = correctAnswers,
            score = score,
            quizId = quizId
        )

        // Award currency based on score
        viewModelScope.launch {
            val userId = userRepository.getCurrentUserId()
            if (userId != null) {
                val currencyEarned = calculateCurrencyEarned(score)
                Log.d(tag, "Awarding $currencyEarned currency for score $score")

                // Update currency in Firestore
                try {
                    userRepository.updateUserCurrency(userId, currencyEarned)
                    _successMessage.value = "You earned $currencyEarned coins!"
                    Log.d(tag, "Successfully updated currency: +$currencyEarned")

                    // Check for achievements
                    achievementRepository.checkQuizAchievement(result)

                } catch (e: Exception) {
                    Log.e(tag, "Failed to update currency", e)
                    _errorMessage.value = "Failed to update currency"
                }
            }
        }

        return result
    }

    // Add a function to calculate currency earned
    fun calculateCurrencyEarned(score: Int): Int {
        return GameUtils.calculateCurrencyEarned(score)
    }

    fun usePowerUp(type: PowerUpType): Boolean {
        val powerUpId = when (type) {
            PowerUpType.FREEZE_TIME -> "freeze_time"
            PowerUpType.FIFTY_FIFTY -> "fifty_fifty"
            PowerUpType.REVERSE_TIME -> "reverse_time"
        }

        val currentCount = _userPowerUps.value[powerUpId] ?: 0
        if (currentCount <= 0) {
            _errorMessage.value = "You don't have this power-up"
            return false
        }

        viewModelScope.launch {
            val success = shopRepository.usePowerUp(powerUpId)
            if (success) {
                // Update local state
                val updatedPowerUps = _userPowerUps.value.toMutableMap()
                updatedPowerUps[powerUpId] = currentCount - 1
                _userPowerUps.value = updatedPowerUps

                when (type) {
                    PowerUpType.FREEZE_TIME -> {
                        _isTimeFrozen.value = true
                        _successMessage.value = "Time frozen for 10 seconds!"
                        // Reset after 10 seconds
                        kotlinx.coroutines.delay(10000)
                        _isTimeFrozen.value = false
                    }
                    PowerUpType.FIFTY_FIFTY -> {
                        _isFiftyFiftyUsed.value = true
                        _successMessage.value = "Two wrong answers removed!"
                    }
                    PowerUpType.REVERSE_TIME -> {
                        // Enhanced reverse time functionality
                        if (_previousQuestionStates.isNotEmpty()) {
                            _canGoBack.value = true
                            _reverseTimeCount.value = _previousQuestionStates.size
                            _successMessage.value = "Go back to previous ${if (_previousQuestionStates.size > 1) "${_previousQuestionStates.size} questions" else "question"}!"

                            // Play reverse time animation
                            _isReverseTimeAnimating.value = true
                            kotlinx.coroutines.delay(1500)
                            _isReverseTimeAnimating.value = false
                        } else {
                            // Refund the power-up if there are no previous questions
                            val refundedPowerUps = _userPowerUps.value.toMutableMap()
                            refundedPowerUps[powerUpId] = currentCount
                            _userPowerUps.value = refundedPowerUps
                            _errorMessage.value = "No previous questions to go back to!"

                            // Notify the repository about the refund
                            shopRepository.refundPowerUp(powerUpId)
                        }
                    }
                }
            } else {
                _errorMessage.value = "Failed to use power-up"
            }
        }

        return true
    }

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }

    fun resetCanGoBack() {
        _canGoBack.value = _previousQuestionStates.isNotEmpty()
    }

    // Data class to store question state for reverse time power-up
    data class QuestionState(
        val questionIndex: Int,
        val selectedAnswer: String?,
        val timeRemaining: Int,
        val score: Int
    )
}
