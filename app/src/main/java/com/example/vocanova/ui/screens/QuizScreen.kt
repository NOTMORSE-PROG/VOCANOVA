package com.example.vocanova.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassDisabled
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.vocanova.R
import com.example.vocanova.data.model.PowerUpType
import com.example.vocanova.ui.theme.VocaGreen
import com.example.vocanova.ui.theme.VocaRed
import com.example.vocanova.ui.viewmodels.QuizViewModel
import com.example.vocanova.utils.AudioUtils
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    navController: NavController,
    quizId: String,
    viewModel: QuizViewModel = hiltViewModel()
) {
    val questions = viewModel.questions
    val quizTitle by viewModel.quizTitle.collectAsState()
    val userPowerUps by viewModel.userPowerUps.collectAsState()
    val isTimeFrozen by viewModel.isTimeFrozen.collectAsState()
    val isFiftyFiftyUsed by viewModel.isFiftyFiftyUsed.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val canGoBack by viewModel.canGoBack.collectAsState()
    val reverseTimeCount by viewModel.reverseTimeCount.collectAsState()
    val isReverseTimeAnimating by viewModel.isReverseTimeAnimating.collectAsState()

    var currentQuestionIndex by remember { androidx.compose.runtime.mutableIntStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var isAnswerSubmitted by remember { mutableStateOf(false) }
    var isAnswerCorrect by remember { mutableStateOf(false) }
    var timeRemaining by remember { mutableIntStateOf(30) }
    var isQuizCompleted by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf(0) }
    var showResult by remember { mutableStateOf(false) }
    var isReversingTime by remember { mutableStateOf(false) }
    var userAnswers by remember { mutableStateOf(mutableMapOf<Int, String>()) }

    val context = LocalContext.current
    val audioUtils = remember { AudioUtils.getInstance(context) }
    val coroutineScope = rememberCoroutineScope()

    // Load questions when the screen is first displayed
    LaunchedEffect(quizId) {
        viewModel.loadQuestions(quizId)
    }

    // Save question state when moving to next question
    LaunchedEffect(currentQuestionIndex) {
        if (currentQuestionIndex > 0 && !isReversingTime) {
            val previousIndex = currentQuestionIndex - 1
            val previousAnswer = userAnswers[previousIndex]
            viewModel.saveQuestionState(
                questionIndex = previousIndex,
                selectedAnswer = previousAnswer,
                timeRemaining = 30, // We reset time for each question
                score = score - (if (previousAnswer == questions[previousIndex].correctAnswer) 10 else 0)
            )
        }
        isReversingTime = false
    }

    // Timer effect
    LaunchedEffect(currentQuestionIndex, isTimeFrozen, isAnswerSubmitted) {
        if (!isAnswerSubmitted) {
            timeRemaining = 30 // Reset timer for each question

            while (timeRemaining > 0 && !isQuizCompleted && !isAnswerSubmitted) {
                if (!isTimeFrozen) {
                    delay(1000)
                    timeRemaining--
                } else {
                    delay(1000) // Still delay but don't decrease time
                }
            }

            // Time's up for this question
            if (timeRemaining <= 0 && !isQuizCompleted && !isAnswerSubmitted) {
                isAnswerSubmitted = true
                isAnswerCorrect = false

                // Play wrong sound
                audioUtils.playSoundWithVolume(R.raw.wrong_sound, 1.0f)

                delay(1500) // Show the result briefly

                // Move to next question or end quiz
                if (currentQuestionIndex < questions.size - 1) {
                    currentQuestionIndex++
                    selectedAnswer = null
                    isAnswerSubmitted = false
                } else {
                    isQuizCompleted = true
                    showResult = true
                }
            }
        }
    }

    // Handle error and success messages
    LaunchedEffect(errorMessage, successMessage) {
        if (errorMessage != null || successMessage != null) {
            delay(3000)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        quizTitle,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F3460)
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1A1A2E),
                            Color(0xFF16213E)
                        )
                    )
                )
        ) {
            if (questions.isEmpty()) {
                // Loading state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (showResult) {
                // Show quiz results using GameOverScreen
                val result = viewModel.calculateResult(quizId)

                GameOverScreenWithCurrency(
                    score = result.score,
                    currencyEarned = viewModel.calculateCurrencyEarned(result.score),
                    onPlayAgain = {
                        currentQuestionIndex = 0
                        selectedAnswer = null
                        isAnswerSubmitted = false
                        timeRemaining = 30
                        score = 0
                        isQuizCompleted = false
                        showResult = false
                        viewModel.loadQuestions(quizId)
                    },
                    onBackToGames = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                )
            } else {
                // Show current question
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Score indicator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Question ${currentQuestionIndex + 1} of ${questions.size}",
                            fontSize = 18.sp,
                            color = Color.White
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Score",
                                tint = Color.Yellow,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Score: $score",
                                fontSize = 18.sp,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Timer
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(
                            imageVector = if (isTimeFrozen) Icons.Default.HourglassDisabled else Icons.Default.HourglassTop,
                            contentDescription = "Timer",
                            tint = if (timeRemaining < 10) Color.Red else Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$timeRemaining s",
                            fontSize = 18.sp,
                            color = if (timeRemaining < 10) Color.Red else Color.White
                        )
                    }

                    // Timer progress bar
                    LinearProgressIndicator(
                    progress = { timeRemaining / 30f },
                    modifier = Modifier
                                                .fillMaxWidth()
                                                .height(8.dp)
                                                .clip(RoundedCornerShape(4.dp)),
                    color = when {
                                                isTimeFrozen -> Color.Blue
                                                timeRemaining > 10 -> VocaGreen
                                                else -> VocaRed
                                            },
                    trackColor = Color.DarkGray,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Power-ups
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        PowerUpButton(
                            icon = Icons.Default.HourglassDisabled,
                            name = "Freeze",
                            count = userPowerUps["freeze_time"] ?: 0,
                            color = Color(0xFF1E88E5),
                            onClick = {
                                viewModel.usePowerUp(PowerUpType.FREEZE_TIME)
                            }
                        )

                        PowerUpButton(
                            icon = Icons.Default.FilterAlt,
                            name = "50/50",
                            count = userPowerUps["fifty_fifty"] ?: 0,
                            color = Color(0xFF43A047),
                            onClick = {
                                viewModel.usePowerUp(PowerUpType.FIFTY_FIFTY)
                            }
                        )

                        // Enhanced Reverse Time Power-up
                        EnhancedReverseTimeButton(
                            count = userPowerUps["reverse_time"] ?: 0,
                            savedStatesCount = reverseTimeCount,
                            isAnimating = isReverseTimeAnimating,
                            onClick = {
                                viewModel.usePowerUp(PowerUpType.REVERSE_TIME)
                            }
                        )
                    }

                    // Add this after the PowerUpButton section
                    if (canGoBack && currentQuestionIndex > 0) {
                        Spacer(modifier = Modifier.height(16.dp))

                        // Enhanced Go Back Button
                        EnhancedGoBackButton(
                            onClick = {
                                if (currentQuestionIndex > 0) {
                                    // Get previous question state
                                    val previousState = viewModel.getPreviousQuestionState()
                                    if (previousState != null) {
                                        isReversingTime = true
                                        currentQuestionIndex = previousState.questionIndex
                                        selectedAnswer = previousState.selectedAnswer
                                        score = previousState.score
                                        isAnswerSubmitted = false

                                        // Play reverse sound
                                        audioUtils.playSoundWithVolume(R.raw.correct_sound, 0.7f)
                                    }
                                    viewModel.resetCanGoBack()
                                }
                            },
                            remainingSteps = reverseTimeCount
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Question text
                    val currentQuestion = questions[currentQuestionIndex]
                    Text(
                        text = currentQuestion.question,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Answer options
                    LazyColumn(
                        modifier = Modifier.weight(1f)
                    ) {
                        val options = currentQuestion.options
                        val correctAnswer = currentQuestion.correctAnswer

                        // If fifty-fifty is used, show only the correct answer and one wrong answer
                        val displayOptions = if (isFiftyFiftyUsed && !isAnswerSubmitted) {
                            val wrongOptions = options.filter { it != correctAnswer }.shuffled().take(1)
                            listOf(correctAnswer) + wrongOptions
                        } else {
                            options
                        }

                        items(displayOptions.size) { index ->
                            val option = displayOptions[index]
                            AnswerOption(
                                text = option,
                                isSelected = selectedAnswer == option,
                                isCorrect = isAnswerSubmitted && option == correctAnswer,
                                isIncorrect = isAnswerSubmitted && selectedAnswer == option && option != correctAnswer,
                                isEnabled = !isAnswerSubmitted,
                                onClick = {
                                    if (!isAnswerSubmitted) {
                                        selectedAnswer = option
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    // Submit or Next button
                    AnimatedVisibility(
                        visible = !isAnswerSubmitted,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Button(
                            onClick = {
                                if (selectedAnswer != null) {
                                    isAnswerSubmitted = true
                                    val isCorrect = selectedAnswer == questions[currentQuestionIndex].correctAnswer
                                    isAnswerCorrect = isCorrect

                                    // Play sound based on correctness
                                    if (isCorrect) {
                                        audioUtils.playSoundWithVolume(R.raw.correct_sound, 1.0f)
                                        score += 10
                                    } else {
                                        audioUtils.playSoundWithVolume(R.raw.wrong_sound, 1.0f)
                                    }

                                    viewModel.submitAnswer(currentQuestionIndex, selectedAnswer!!)
                                }
                            },
                            enabled = selectedAnswer != null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                disabledContainerColor = Color.Gray
                            )
                        ) {
                            Text("Submit Answer")
                        }
                    }

                    AnimatedVisibility(
                        visible = isAnswerSubmitted,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Column {
                            // Feedback text
                            Text(
                                text = if (isAnswerCorrect) "Correct!" else "Incorrect!",
                                color = if (isAnswerCorrect) VocaGreen else VocaRed,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Next button
                            Button(
                                onClick = {
                                    if (currentQuestionIndex < questions.size - 1) {
                                        currentQuestionIndex++
                                        selectedAnswer = null
                                        isAnswerSubmitted = false
                                    } else {
                                        isQuizCompleted = true
                                        showResult = true
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text(
                                    text = if (currentQuestionIndex < questions.size - 1)
                                        "Next Question"
                                    else
                                        "Finish Quiz"
                                )
                            }
                        }
                    }
                }

                // Show error or success messages
                if (errorMessage != null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Text(
                            text = errorMessage!!,
                            color = Color.Red,
                            modifier = Modifier
                                .padding(16.dp)
                                .background(
                                    Color.Black.copy(alpha = 0.7f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp)
                        )
                    }
                }

                if (successMessage != null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Text(
                            text = successMessage!!,
                            color = Color.Green,
                            modifier = Modifier
                                .padding(16.dp)
                                .background(
                                    Color.Black.copy(alpha = 0.7f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PowerUpButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    name: String,
    count: Int,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            color.copy(alpha = if (count > 0) 0.3f else 0.1f),
                            color.copy(alpha = if (count > 0) 0.1f else 0.05f)
                        )
                    )
                )
                .clickable(enabled = count > 0) { onClick() }
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = if (count > 0)
                            listOf(color, color.copy(alpha = 0.7f))
                        else
                            listOf(color.copy(alpha = 0.3f), color.copy(alpha = 0.1f))
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = name,
                tint = if (count > 0) color else color.copy(alpha = 0.3f),
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = name,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (count > 0)
                        color.copy(alpha = 0.2f)
                    else
                        Color.Gray.copy(alpha = 0.2f)
                )
                .padding(horizontal = 8.dp, vertical = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "x$count",
                color = if (count > 0) color else Color.Gray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun EnhancedReverseTimeButton(
    count: Int,
    savedStatesCount: Int,
    isAnimating: Boolean,
    onClick: () -> Unit
) {
    val color = Color(0xFFE53935)
    val infiniteTransition = rememberInfiniteTransition(label = "reverseAnimation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val scale by animateFloatAsState(
        targetValue = if (isAnimating) 1.2f else 1f,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            color.copy(alpha = if (count > 0) 0.3f else 0.1f),
                            color.copy(alpha = if (count > 0) 0.1f else 0.05f)
                        )
                    )
                )
                .clickable(enabled = count > 0) { onClick() }
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = if (count > 0)
                            listOf(color, color.copy(alpha = 0.7f))
                        else
                            listOf(color.copy(alpha = 0.3f), color.copy(alpha = 0.1f))
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = "Reverse Time",
                tint = if (count > 0) color else color.copy(alpha = 0.3f),
                modifier = Modifier
                    .size(28.dp)
                    .graphicsLayer {
                        if (isAnimating) {
                            rotationZ = rotation
                            scaleX = scale
                            scaleY = scale
                        }
                    }
            )

            // Show available states to go back
            if (savedStatesCount > 0 && count > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(18.dp)
                        .background(Color.White, CircleShape)
                        .border(1.dp, color, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$savedStatesCount",
                        color = color,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Reverse",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (count > 0)
                        color.copy(alpha = 0.2f)
                    else
                        Color.Gray.copy(alpha = 0.2f)
                )
                .padding(horizontal = 8.dp, vertical = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "x$count",
                color = if (count > 0) color else Color.Gray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun EnhancedGoBackButton(
    onClick: () -> Unit,
    remainingSteps: Int
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulseAnimation")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "buttonPulse"
    )

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFE53935)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = "Go Back",
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .rotate(30f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (remainingSteps > 1)
                    "Go Back ($remainingSteps steps available)"
                else
                    "Go Back to Previous Question",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun AnswerOption(
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean,
    isIncorrect: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isCorrect -> Brush.linearGradient(
            listOf(VocaGreen.copy(alpha = 0.3f), VocaGreen.copy(alpha = 0.1f))
        )
        isIncorrect -> Brush.linearGradient(
            listOf(VocaRed.copy(alpha = 0.3f), VocaRed.copy(alpha = 0.1f))
        )
        isSelected -> Brush.linearGradient(
            listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
        )
        else -> Brush.linearGradient(
            listOf(Color.DarkGray.copy(alpha = 0.5f), Color.DarkGray.copy(alpha = 0.3f))
        )
    }

    val borderColor = when {
        isCorrect -> VocaGreen
        isIncorrect -> VocaRed
        isSelected -> MaterialTheme.colorScheme.primary
        else -> Color.Gray.copy(alpha = 0.5f)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(enabled = isEnabled) { onClick() }
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = if (isSelected || isCorrect || isIncorrect) FontWeight.SemiBold else FontWeight.Normal,
                modifier = Modifier.weight(1f)
            )

            if (isCorrect) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(VocaGreen.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Correct",
                        tint = VocaGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else if (isIncorrect) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(VocaRed.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Incorrect",
                        tint = VocaRed,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
