package com.example.vocanova.ui.screens

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.vocanova.R
import com.example.vocanova.ui.theme.VocaGreen
import com.example.vocanova.ui.theme.VocaRed
import com.example.vocanova.utils.AudioUtils
import com.example.vocanova.utils.GameUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class GameWord(
    val word: String,
    val isCorrect: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GreenLightRedLightGame(navController: NavController) {
    var gameStarted by remember { mutableStateOf(false) }
    var gameOver by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }
    var lives by remember { mutableIntStateOf(3) }
    var currentRound by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(10) }
    var isGreenLight by remember { mutableStateOf(true) }
    var targetWord by remember { mutableStateOf("") }
    var wordType by remember { mutableStateOf("Synonym") }
    var options by remember { mutableStateOf<List<GameWord>>(emptyList()) }
    var selectedWord by remember { mutableStateOf<GameWord?>(null) }
    var showFeedback by remember { mutableStateOf(false) }
    var isTimerRunning by remember { mutableStateOf(false) }
    var shouldPlaySquidMusic by remember { mutableStateOf(true) }
    var currencyEarned by remember { mutableIntStateOf(0) }
    var showInstructions by remember { mutableStateOf(true) }

    val scrollState = rememberScrollState()

    val context = LocalContext.current
    val audioUtils = remember { AudioUtils.getInstance(context) }
    var isMuted by remember { mutableStateOf(audioUtils.isGloballyMuted()) }
    val coroutineScope = rememberCoroutineScope()
    val localView = LocalView.current

    // Animation for the light indicator
    val infiniteTransition = rememberInfiniteTransition(label = "lightPulse")
    val lightPulse by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Start background music when the game starts
    LaunchedEffect(gameStarted) {
        if (gameStarted && !gameOver) {
            // Play squid game music without looping
            if (shouldPlaySquidMusic) {
                audioUtils.playBackgroundMusic(R.raw.squid_game, shouldLoop = false)
                shouldPlaySquidMusic = false
            }
        }
    }

    // Clean up resources when leaving the screen
    DisposableEffect(Unit) {
        onDispose {
            audioUtils.stopBackgroundMusic()
        }
    }

    val timerProgress by animateFloatAsState(
        targetValue = timeLeft / 10f,
        animationSpec = tween(1000, easing = LinearEasing),
        label = "timer"
    )

    // New word pairs from the PDF
    val wordPairs = listOf(
        // Format: target word to list of options (correct answer first, then incorrect ones)
        "Anger" to listOf(
            GameWord("calm", true),  // Correct antonym
            GameWord("mad", false),
            GameWord("fury", false)
        ),
        "Brave" to listOf(
            GameWord("fearless", true),  // Correct synonym
            GameWord("fearful", false),
            GameWord("cowardly", false)
        ),
        "Poor" to listOf(
            GameWord("wealthy", true),  // Correct antonym
            GameWord("indigent", false),
            GameWord("impoverished", false)
        ),
        "Tall" to listOf(
            GameWord("short", true),  // Correct antonym
            GameWord("high", false),
            GameWord("long", false)
        ),
        "Smart" to listOf(
            GameWord("clever", true),  // Correct synonym
            GameWord("dumb", false),
            GameWord("slow", false)
        ),
        "Strong" to listOf(
            GameWord("weak", true),  // Correct antonym
            GameWord("firm", false),
            GameWord("tough", false)
        ),
        "Wet" to listOf(
            GameWord("dry", true),  // Correct antonym
            GameWord("damp", false),
            GameWord("rainy", false)
        ),
        "Nice" to listOf(
            GameWord("kind", true),  // Correct synonym
            GameWord("mean", false),
            GameWord("bad", false)
        ),
        "Hot" to listOf(
            GameWord("cold", true),  // Correct antonym
            GameWord("fire", false),
            GameWord("warm", false)
        ),
        "Magnificent" to listOf(
            GameWord("majestic", true),  // Correct synonym
            GameWord("plain", false),
            GameWord("unremarkable", false)
        ),
        "Encounter" to listOf(
            GameWord("avoid", true),  // Correct antonym
            GameWord("meet", false),
            GameWord("evade", false)
        ),
        "Abandon" to listOf(
            GameWord("leave", true),  // Correct synonym
            GameWord("keep", false),
            GameWord("stay", false)
        ),
        "Loyal" to listOf(
            GameWord("unfaithful", true),  // Correct antonym
            GameWord("devoted", false),
            GameWord("dedicated", false)
        ),
        "Serene" to listOf(
            GameWord("peaceful", true),  // Correct synonym
            GameWord("rowdy", false),
            GameWord("chaotic", false)
        ),
        "Uncanny" to listOf(
            GameWord("eerie", true),  // Correct synonym
            GameWord("ordinary", false),
            GameWord("simple", false)
        ),
        "Frigid" to listOf(
            GameWord("scorching", true),  // Correct antonym
            GameWord("chilly", false),
            GameWord("freezing", false)
        ),
        "Hostile" to listOf(
            GameWord("friendly", true),  // Correct antonym
            GameWord("aggressive", false),
            GameWord("rude", false)
        ),
        "Complicated" to listOf(
            GameWord("complex", true),  // Correct synonym
            GameWord("simple", false),
            GameWord("clear", false)
        ),
        "Ancient" to listOf(
            GameWord("old", true),  // Correct synonym
            GameWord("modern", false),
            GameWord("future", false)
        ),
        "Rare" to listOf(
            GameWord("common", true),  // Correct antonym
            GameWord("special", false),
            GameWord("unique", false)
        )
    )

    // Map to track which word is a synonym or antonym
    val wordTypeMap = mapOf(
        "Anger" to "Antonym",
        "Brave" to "Synonym",
        "Poor" to "Antonym",
        "Tall" to "Antonym",
        "Smart" to "Synonym",
        "Strong" to "Antonym",
        "Wet" to "Antonym",
        "Nice" to "Synonym",
        "Hot" to "Antonym",
        "Magnificent" to "Synonym",
        "Encounter" to "Antonym",
        "Abandon" to "Synonym",
        "Loyal" to "Antonym",
        "Serene" to "Synonym",
        "Uncanny" to "Synonym",
        "Frigid" to "Antonym",
        "Hostile" to "Antonym",
        "Complicated" to "Synonym",
        "Ancient" to "Synonym",
        "Rare" to "Antonym"
    )

    fun startNewRound() {
        if (currentRound >= 10 || lives <= 0) {
            gameOver = true
            // Calculate currency earned when game is over
            currencyEarned = GameUtils.calculateCurrencyEarned(score)
            return
        }

        // Play squid game music at the start of each round
        if (currentRound > 0) {
            audioUtils.playBackgroundMusic(R.raw.squid_game, shouldLoop = false)
        }

        // Get a random word pair that hasn't been used yet if possible
        val availablePairs = wordPairs.shuffled()
        val randomPair = availablePairs[currentRound % availablePairs.size]

        targetWord = randomPair.first
        wordType = wordTypeMap[targetWord] ?: "Synonym"

        // Get all options for this word and shuffle them
        val allOptions = randomPair.second.shuffled()

        // Make sure we have exactly one correct answer
        val correctOption = allOptions.first { it.isCorrect }
        val incorrectOptions = allOptions.filter { !it.isCorrect }.take(2)

        // Combine and shuffle for final options
        options = (listOf(correctOption) + incorrectOptions).shuffled()

        // Reset for new round
        timeLeft = 10
        isGreenLight = true
        selectedWord = null
        showFeedback = false
        isTimerRunning = true
        currentRound++

        // Play green light sound with higher volume
        audioUtils.playSoundWithVolume(R.raw.doll_green_light, 1.0f)

        // Provide haptic feedback for round start
        localView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
    }

    LaunchedEffect(gameStarted) {
        if (gameStarted && !gameOver) {
            startNewRound()
        }
    }

    LaunchedEffect(timeLeft, isGreenLight, showFeedback, isTimerRunning) {
        if (gameStarted && !gameOver && isTimerRunning && !showFeedback) {
            if (timeLeft > 0) {
                delay(1000)
                timeLeft--

                // Provide subtle haptic feedback for timer tick when time is running low
                if (timeLeft <= 2) {
                    localView.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                }
            } else {
                isGreenLight = false
                showFeedback = true
                isTimerRunning = false

                // Play red light sound with higher volume
                audioUtils.playSoundWithVolume(R.raw.doll_red_light, 1.0f)

                // Strong haptic feedback for red light
                localView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)

                // If no word was selected, it counts as a mistake
                if (selectedWord == null) {
                    lives--
                }

                delay(1500)
                startNewRound()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Green Light, Red Light",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            audioUtils.toggleGlobalMute()
                            isMuted = audioUtils.isGloballyMuted()
                            if (!isMuted && gameStarted && !gameOver) {
                                audioUtils.playBackgroundMusic(R.raw.squid_game, shouldLoop = false)
                            } else if (isMuted) {
                                audioUtils.stopBackgroundMusic()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (!isMuted) Icons.Default.MusicNote else Icons.Default.MusicOff,
                            contentDescription = if (!isMuted) "Mute Music" else "Unmute Music",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1A1A2E), // Deep blue background
                            Color(0xFF16213E), // Midnight blue
                            Color(0xFF0F3460)  // Dark blue
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // Add a starry night effect to the background
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Draw stars with different sizes
                repeat(100) {
                    val x = (Math.random() * canvasWidth).toFloat()
                    val y = (Math.random() * canvasHeight).toFloat()
                    val radius = (1 + Math.random() * 2).toFloat().dp.toPx()
                    val alpha = (0.3f + Math.random() * 0.7f).toFloat()

                    drawCircle(
                        color = Color.White.copy(alpha = alpha),
                        radius = radius,
                        center = Offset(x, y)
                    )
                }
            }

            // Keep the rest of the content the same, but update the light indicator
            if (!gameStarted) {
                GameStartScreen(
                    onStartGame = {
                        gameStarted = true
                        gameOver = false
                        score = 0
                        lives = 3
                        currentRound = 0
                        shouldPlaySquidMusic = true
                        showInstructions = false
                    },
                    onShowInstructions = {
                        showInstructions = !showInstructions
                    },
                    showInstructions = showInstructions
                )
            } else if (gameOver) {
                GameOverScreenWithCurrency(
                    score = score,
                    currencyEarned = currencyEarned,
                    onPlayAgain = {
                        gameStarted = true
                        gameOver = false
                        score = 0
                        lives = 3
                        currentRound = 0
                        shouldPlaySquidMusic = true
                        startNewRound()
                    },
                    onBackToGames = {
                        navController.navigateUp()
                    }
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GameHeader(
                        score = score,
                        lives = lives,
                        currentRound = currentRound
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Enhanced light indicator with creative design
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .padding(horizontal = 16.dp)
                    ) {
                        // Background with gradient
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = if (isGreenLight) {
                                            listOf(
                                                VocaGreen.copy(alpha = 0.8f),
                                                VocaGreen.copy(alpha = 0.6f),
                                                VocaGreen.copy(alpha = 0.4f)
                                            )
                                        } else {
                                            listOf(
                                                VocaRed.copy(alpha = 0.8f),
                                                VocaRed.copy(alpha = 0.6f),
                                                VocaRed.copy(alpha = 0.4f)
                                            )
                                        }
                                    )
                                )
                                .border(
                                    width = 2.dp,
                                    brush = Brush.linearGradient(
                                        colors = if (isGreenLight) {
                                            listOf(
                                                VocaGreen,
                                                VocaGreen.copy(alpha = 0.7f),
                                                VocaGreen.copy(alpha = 0.5f)
                                            )
                                        } else {
                                            listOf(
                                                VocaRed,
                                                VocaRed.copy(alpha = 0.7f),
                                                VocaRed.copy(alpha = 0.5f)
                                            )
                                        }
                                    ),
                                    shape = RoundedCornerShape(20.dp)
                                )
                        ) {
                            // Decorative circles
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                // Draw multiple circles for a light effect
                                val centerX = size.width / 2
                                val centerY = size.height / 2

                                for (i in 1..3) {
                                    val radius = (i * 30).dp.toPx()
                                    drawCircle(
                                        color = Color.White.copy(alpha = (0.3f - i * 0.08f).coerceAtLeast(0.05f)),
                                        radius = radius,
                                        center = Offset(centerX, centerY)
                                    )
                                }
                            }

                            // Content
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = if (isGreenLight) "GREEN LIGHT!" else "RED LIGHT!",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    fontSize = 28.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.8f)
                                        .height(12.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color.White.copy(alpha = 0.3f))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(timerProgress)
                                            .fillMaxHeight()
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color.White.copy(alpha = 0.8f))
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "$timeLeft seconds",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Enhanced target word card with creative design
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Transparent
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF2A2D3E),
                                            Color(0xFF272B3F),
                                            Color(0xFF232634)
                                        )
                                    )
                                )
                        ) {
                            // Decorative elements
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                // Draw decorative circles
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.05f),
                                    radius = 60.dp.toPx(),
                                    center = Offset(size.width * 0.15f, size.height * 0.5f)
                                )
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.05f),
                                    radius = 40.dp.toPx(),
                                    center = Offset(size.width * 0.85f, size.height * 0.5f)
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Find the $wordType for:",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White.copy(alpha = 0.7f)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            brush = Brush.linearGradient(
                                                colors = listOf(
                                                    Color(0xFF3D4E81).copy(alpha = 0.7f),
                                                    Color(0xFF3D4E81).copy(alpha = 0.5f)
                                                )
                                            )
                                        )
                                        .border(
                                            width = 2.dp,
                                            brush = Brush.linearGradient(
                                                colors = listOf(
                                                    Color(0xFF5D8BF4).copy(alpha = 0.8f),
                                                    Color(0xFF5D8BF4).copy(alpha = 0.5f)
                                                )
                                            ),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .padding(horizontal = 24.dp, vertical = 12.dp)
                                ) {
                                    Text(
                                        text = targetWord,
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Debug text to show number of options
                    Text(
                        text = "Choose from ${options.size} options:",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Word options with improved styling
                    options.forEach { option ->
                        WordOption(
                            word = option,
                            isSelected = selectedWord == option,
                            showFeedback = showFeedback,
                            isEnabled = isGreenLight && !showFeedback,
                            onClick = {
                                if (isGreenLight && !showFeedback) {
                                    selectedWord = option
                                    showFeedback = true
                                    isTimerRunning = false
                                    isGreenLight = false

                                    // Provide haptic feedback
                                    localView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)

                                    // Stop the squid game music when a word is selected
                                    audioUtils.stopBackgroundMusic()

                                    // Play red light sound when selecting an answer with higher volume
                                    audioUtils.playSoundWithVolume(R.raw.doll_red_light, 1.0f)

                                    // Use coroutineScope to launch a coroutine for delayed sound
                                    coroutineScope.launch {
                                        delay(500)
                                        if (option.isCorrect) {
                                            score += 10
                                            audioUtils.playSoundWithVolume(R.raw.correct_sound, 1.0f)
                                            // Provide success haptic feedback
                                            localView.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                                        } else {
                                            lives--
                                            audioUtils.playSoundWithVolume(R.raw.wrong_sound, 1.0f)
                                            // Provide error haptic feedback
                                            localView.performHapticFeedback(HapticFeedbackConstants.REJECT)
                                        }
                                    }
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    AnimatedVisibility(
                        visible = showFeedback,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            selectedWord?.let { selected ->
                                if (selected.isCorrect) {
                                    Text(
                                        text = "Correct!",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = VocaGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                } else {
                                    Text(
                                        text = "Wrong!",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = VocaRed,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } ?: run {
                                Text(
                                    text = "Time's up!",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = VocaRed,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { startNewRound() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF5D8BF4)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .height(50.dp)
                                    .clip(RoundedCornerShape(25.dp))
                                    .shadow(
                                        elevation = 4.dp,
                                        shape = RoundedCornerShape(25.dp)
                                    )
                            ) {
                                Text("Next Round", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Add extra space at the bottom for scrolling
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

// Update the GameHeader function for a more creative design
@Composable
fun GameHeader(score: Int, lives: Int, currentRound: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        // Decorative background elements
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            // Draw decorative circles
            drawCircle(
                color = Color.White.copy(alpha = 0.05f),
                radius = 60.dp.toPx(),
                center = Offset(size.width * 0.1f, size.height * 0.5f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.05f),
                radius = 40.dp.toPx(),
                center = Offset(size.width * 0.85f, size.height * 0.3f)
            )
        }

        // Main content
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Round indicator with creative design
            Card(
                modifier = Modifier
                    .width(100.dp)
                    .height(70.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF3D4E81)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Decorative elements
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .offset(x = (-20).dp, y = (-15).dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "ROUND",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "$currentRound/10",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }

            // Score with creative design
            Card(
                modifier = Modifier
                    .width(100.dp)
                    .height(70.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF5D8BF4)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Decorative elements
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .offset(x = 20.dp, y = (-15).dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "SCORE",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "$score",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }

            // Lives with heart icons and creative design
            Card(
                modifier = Modifier
                    .width(100.dp)
                    .height(70.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = VocaRed.copy(alpha = 0.9f)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Decorative elements
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .offset(x = (-20).dp, y = 15.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "LIVES",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            repeat(lives) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(22.dp)
                                        .padding(horizontal = 2.dp)
                                )
                            }
                            repeat(3 - lives) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.3f),
                                    modifier = Modifier
                                        .size(22.dp)
                                        .padding(horizontal = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Update the WordOption function for a more creative design
@Composable
fun WordOption(
    word: GameWord,
    isSelected: Boolean,
    showFeedback: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        showFeedback && word.isCorrect -> VocaGreen.copy(alpha = 0.15f)
        showFeedback && isSelected && !word.isCorrect -> VocaRed.copy(alpha = 0.15f)
        isSelected -> Color(0xFF5D8BF4).copy(alpha = 0.15f)
        else -> Color(0xFF2A2D3E)
    }

    val borderColor = when {
        showFeedback && word.isCorrect -> VocaGreen
        showFeedback && isSelected && !word.isCorrect -> VocaRed
        isSelected -> Color(0xFF5D8BF4)
        else -> Color.White.copy(alpha = 0.1f)
    }

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = tween(150),
        label = "scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .scale(scale)
            .shadow(
                elevation = if (isSelected || (showFeedback && word.isCorrect)) 8.dp else 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = when {
                    showFeedback && word.isCorrect -> VocaGreen
                    showFeedback && isSelected && !word.isCorrect -> VocaRed
                    isSelected -> Color(0xFF5D8BF4)
                    else -> Color.Gray
                }
            )
            .clickable(enabled = isEnabled) { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = when {
                            showFeedback && word.isCorrect -> listOf(VocaGreen, VocaGreen.copy(alpha = 0.7f))
                            showFeedback && isSelected && !word.isCorrect -> listOf(VocaRed, VocaRed.copy(alpha = 0.7f))
                            isSelected -> listOf(Color(0xFF5D8BF4), Color(0xFF5D8BF4).copy(alpha = 0.7f))
                            else -> listOf(Color.White.copy(alpha = 0.1f), Color.White.copy(alpha = 0.05f))
                        }
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            // Decorative elements
            Canvas(modifier = Modifier.fillMaxSize()) {
                if (showFeedback || isSelected) {
                    // Draw decorative circles
                    drawCircle(
                        color = when {
                            showFeedback && word.isCorrect -> VocaGreen.copy(alpha = 0.1f)
                            showFeedback && isSelected && !word.isCorrect -> VocaRed.copy(alpha = 0.1f)
                            isSelected -> Color(0xFF5D8BF4).copy(alpha = 0.1f)
                            else -> Color.Transparent
                        },
                        radius = 40.dp.toPx(),
                        center = Offset(size.width * 0.15f, size.height * 0.5f)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = word.word,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = if (isSelected || showFeedback) FontWeight.Bold else FontWeight.Medium,
                    color = when {
                        showFeedback && word.isCorrect -> VocaGreen
                        showFeedback && isSelected && !word.isCorrect -> VocaRed
                        isSelected -> Color(0xFF5D8BF4)
                        else -> Color.White
                    }
                )

                if (showFeedback) {
                    if (word.isCorrect) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            VocaGreen.copy(alpha = 0.2f),
                                            VocaGreen.copy(alpha = 0.1f)
                                        )
                                    )
                                )
                                .border(
                                    width = 2.dp,
                                    color = VocaGreen,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = VocaGreen,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    } else if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            VocaRed.copy(alpha = 0.2f),
                                            VocaRed.copy(alpha = 0.1f)
                                        )
                                    )
                                )
                                .border(
                                    width = 2.dp,
                                    color = VocaRed,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = VocaRed,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Enhanced GameStartScreen with improved design
@Composable
fun GameStartScreen(
    onStartGame: () -> Unit,
    onShowInstructions: () -> Unit,
    showInstructions: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "titleAnimation")
    val titleScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "titleScale"
    )

    val buttonScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "buttonScale"
    )

    // Animation for traffic light
    val lightPulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lightPulse"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Background decorative elements
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw decorative lines
            val width = size.width
            val height = size.height

            // Draw curved lines
            for (i in 0..5) {
                val path = Path()
                val startX = width * 0.1f
                val startY = height * (0.3f + i * 0.1f)
                path.moveTo(startX, startY)

                val controlX1 = width * 0.4f
                val controlY1 = height * (0.25f + i * 0.1f)
                val controlX2 = width * 0.6f
                val controlY2 = height * (0.35f + i * 0.1f)
                val endX = width * 0.9f
                val endY = height * (0.3f + i * 0.1f)

                path.cubicTo(controlX1, controlY1, controlX2, controlY2, endX, endY)

                drawPath(
                    path = path,
                    color = Color.White.copy(alpha = 0.05f),
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Traffic light animation
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF2A2D3E))
                    .border(
                        width = 2.dp,
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Traffic light design
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxSize().padding(12.dp)
                ) {
                    // Red light
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        VocaRed.copy(alpha = 0.7f + 0.3f * (1 - lightPulse)),
                                        VocaRed.copy(alpha = 0.3f + 0.2f * (1 - lightPulse))
                                    )
                                )
                            )
                            .border(
                                width = 1.dp,
                                color = VocaRed.copy(alpha = 0.8f),
                                shape = CircleShape
                            )
                    )

                    // Yellow light
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFFFFC107).copy(alpha = 0.3f),
                                        Color(0xFFFFC107).copy(alpha = 0.1f)
                                    )
                                )
                            )
                            .border(
                                width = 1.dp,
                                color = Color(0xFFFFC107).copy(alpha = 0.5f),
                                shape = CircleShape
                            )
                    )

                    // Green light
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        VocaGreen.copy(alpha = 0.7f + 0.3f * lightPulse),
                                        VocaGreen.copy(alpha = 0.3f + 0.2f * lightPulse)
                                    )
                                )
                            )
                            .border(
                                width = 1.dp,
                                color = VocaGreen.copy(alpha = 0.8f),
                                shape = CircleShape
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Green Light, Red Light",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.scale(titleScale),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Test your vocabulary skills with this fast-paced word game!",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Instructions toggle button
            Button(
                onClick = onShowInstructions,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3D4E81)
                ),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(24.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = if (showInstructions) "Hide Instructions" else "Show Instructions",
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }

            AnimatedVisibility(visible = showInstructions) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF2A2D3E)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "How to Play:",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5D8BF4)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "1. When the light is GREEN, select a word\n" +
                                    "2. Find the correct synonym or antonym for the target word\n" +
                                    "3. When the light turns RED, time's up!\n" +
                                    "4. Score points for correct answers\n" +
                                    "5. You have 3 lives - use them wisely!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onStartGame,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(56.dp)
                    .scale(buttonScale),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5D8BF4)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    "Start Game",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
