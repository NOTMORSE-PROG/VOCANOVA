package com.example.vocanova.ui.screens

import android.util.Log
import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.Timer
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.vocanova.R
import com.example.vocanova.data.repository.UserRepository
import com.example.vocanova.ui.theme.VocaBlue
import com.example.vocanova.ui.theme.VocaGreen
import com.example.vocanova.ui.theme.VocaOrange
import com.example.vocanova.ui.theme.VocaPurple
import com.example.vocanova.ui.theme.VocaRed
import com.example.vocanova.utils.AudioUtils
import com.example.vocanova.utils.GameUtils
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

data class FlyingWord(
    val id: Int,
    val word: String,
    val isSynonym: Boolean,
    val isAntonym: Boolean = false,
    val isDummy: Boolean = false,
    val startX: Float,
    val startY: Float,
    var speedX: Float,
    var speedY: Float,
    val rotation: Float,
    val color: Color,
    var isHit: Boolean = false,
    var isVisible: Boolean = true
)

// Function to generate random vibrant colors
fun randomVibrantColor(): Color {
    val baseColors = listOf(
        Color(0xFF4CAF50), // Green
        Color(0xFF2196F3), // Blue
        Color(0xFFFF9800), // Orange
        Color(0xFF9C27B0), // Purple
        Color(0xFFE91E63), // Pink
        Color(0xFF00BCD4), // Cyan
        Color(0xFF3F51B5)  // Indigo
    )

    val selectedColor = baseColors.random()

    // Add some randomness to the color while keeping it vibrant
    val red = (selectedColor.red + Random.nextFloat() * 0.2f - 0.1f).coerceIn(0f, 1f)
    val green = (selectedColor.green + Random.nextFloat() * 0.2f - 0.1f).coerceIn(0f, 1f)
    val blue = (selectedColor.blue + Random.nextFloat() * 0.2f - 0.1f).coerceIn(0f, 1f)

    return Color(red, green, blue)
}

// Update the FlyingGameHeader function for a more creative design
@Composable
fun FlyingGameHeader(score: Int, lives: Int, timeLeft: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        // Decorative background elements
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        ) {
            // Draw decorative circles
            drawCircle(
                color = VocaPurple.copy(alpha = 0.1f),
                radius = 60.dp.toPx(),
                center = Offset(size.width * 0.1f, size.height * 0.5f)
            )
            drawCircle(
                color = VocaOrange.copy(alpha = 0.1f),
                radius = 40.dp.toPx(),
                center = Offset(size.width * 0.85f, size.height * 0.3f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            // Top row with score and lives
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Score with creative design
                Card(
                    modifier = Modifier
                        .width(150.dp)
                        .height(70.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = VocaPurple.copy(alpha = 0.9f)
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
                        .width(150.dp)
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
                                .offset(x = 20.dp, y = (-15).dp)
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

            Spacer(modifier = Modifier.height(12.dp))

            // Timer with creative design
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (timeLeft < 10) VocaRed.copy(alpha = 0.9f) else VocaOrange.copy(alpha = 0.9f)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Decorative elements
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        // Draw decorative circles
                        drawCircle(
                            color = Color.White.copy(alpha = 0.1f),
                            radius = 30.dp.toPx(),
                            center = Offset(size.width * 0.1f, size.height * 0.5f)
                        )
                        drawCircle(
                            color = Color.White.copy(alpha = 0.1f),
                            radius = 20.dp.toPx(),
                            center = Offset(size.width * 0.9f, size.height * 0.5f)
                        )
                    }

                    // Timer progress background
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                    )

                    // Timer progress fill
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(timeLeft / 60f)
                            .fillMaxHeight()
                            .padding(8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.5f))
                    )

                    // Timer text and icon
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "$timeLeft seconds remaining",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FlyingGameStartScreen(onStartGame: () -> Unit) {
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

    // Animated stars in background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D0221),
                        Color(0xFF190933),
                        Color(0xFF301B54),
                        Color(0xFF3A1E6C)
                    )
                )
            )
    ) {
        // Animated stars background
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw stars
            repeat(150) {
                val x = Random.nextFloat() * size.width
                val y = Random.nextFloat() * size.height
                val radius = (Random.nextFloat() * 2 + 0.5).dp.toPx()
                val alpha = Random.nextFloat() * 0.7f + 0.3f

                drawCircle(
                    color = Color.White.copy(alpha = alpha),
                    radius = radius,
                    center = Offset(x, y)
                )

                // Add glow to some stars
                if (Random.nextFloat() > 0.8f) {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.2f),
                        radius = radius * 3,
                        center = Offset(x, y)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Flying Words Challenge",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.scale(titleScale),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Tap the synonyms as they fly across the screen!",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Animated example words
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                // Example flying words with random colors
                ExampleWordAnimation(
                    word = "quick",
                    color = VocaGreen,
                    startOffsetX = 0.2f
                )

                ExampleWordAnimation(
                    word = "slow",
                    color = VocaRed,
                    startOffsetX = 0.7f
                )

                ExampleWordAnimation(
                    word = "rapid",
                    color = VocaBlue,
                    startOffsetX = 0.5f
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Instructions card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF301B54)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "How to Play:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "1. A target word will appear at the top\n" +
                                "2. Tap the synonyms as they float upward\n" +
                                "3. Avoid tapping antonyms or dummy words or you'll lose a life\n" +
                                "4. Score points for each correct synonym\n" +
                                "5. Keep tapping until time runs out",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onStartGame,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(56.dp)
                    .scale(buttonScale),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VocaPurple
                )
            ) {
                Text(
                    "Start Game",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// List of dummy words that are neither synonyms nor antonyms
val dummyWords = listOf(
    "apple", "banana", "orange", "grape", "melon", "peach", "plum", "cherry", "lemon", "lime",
    "table", "chair", "desk", "sofa", "lamp", "mirror", "window", "door", "floor", "ceiling",
    "river", "ocean", "lake", "mountain", "forest", "desert", "valley", "hill", "beach", "island",
    "coffee", "tea", "juice", "water", "milk", "soda", "wine", "beer", "cocktail", "smoothie",
    "book", "paper", "pencil", "pen", "marker", "eraser", "notebook", "journal", "magazine", "newspaper",
    "shirt", "pants", "jacket", "coat", "dress", "skirt", "hat", "gloves", "socks", "shoes",
    "phone", "computer", "tablet", "laptop", "camera", "speaker", "headphones", "keyboard", "mouse", "monitor",
    "pizza", "burger", "pasta", "salad", "soup", "sandwich", "cake", "cookie", "bread", "cheese",
    "dog", "cat", "bird", "fish", "rabbit", "hamster", "turtle", "snake", "lizard", "frog",
    "car", "bus", "train", "plane", "bike", "boat", "ship", "truck", "motorcycle", "helicopter"
)

@Composable
fun ExampleWordAnimation(
    word: String,
    color: Color,
    startOffsetX: Float
) {
    val infiniteTransition = rememberInfiniteTransition(label = "infinite")

    // Keep X position stable
    val offsetX by infiniteTransition.animateFloat(
        initialValue = startOffsetX * 300,
        targetValue = startOffsetX * 300, // No horizontal movement
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offsetX"
    )

    // Animate Y from bottom to top
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 200f,
        targetValue = -50f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing), // Slower animation
            repeatMode = RepeatMode.Restart
        ),
        label = "offsetY"
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .scale(scale)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = color
            )
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        color.copy(alpha = 0.3f),
                        color.copy(alpha = 0.1f)
                    )
                )
            )
            .border(
                width = 2.dp,
                color = color.copy(alpha = 0.7f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = word,
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
    }
}

// List of vibrant colors for word borders
val wordBorderColors = listOf(
    Color(0xFF4CAF50), // Green
    Color(0xFF2196F3), // Blue
    Color(0xFFFF9800), // Orange
    Color(0xFF9C27B0), // Purple
    Color(0xFFE91E63), // Pink
    Color(0xFF00BCD4), // Cyan
    Color(0xFF3F51B5), // Indigo
    Color(0xFFFF5722), // Deep Orange
    Color(0xFF607D8B), // Blue Grey
    Color(0xFF795548), // Brown
    Color(0xFF009688), // Teal
    Color(0xFFFFEB3B)  // Yellow
)

// Update the FlyingWordItem function for a more creative design
@Composable
fun FlyingWordItem(
    word: FlyingWord,
    screenWidth: Float,
    screenHeight: Float,
    textSizeMultiplier: Float = 1f,
    isDyslexiaFontEnabled: Boolean = false,
    isHighContrastEnabled: Boolean = false,
    onWordTapped: (FlyingWord) -> Unit
) {
    // Use a random color from the wordBorderColors list instead of basing it on word type
    val borderColor = remember { wordBorderColors.random() }

    val offsetX = remember { Animatable(word.startX) }
    val offsetY = remember { Animatable(word.startY) }
    val scale = remember { Animatable(1f) }

    // Calculate word width based on text length (approximate)
    val wordWidth = (word.word.length * 15).coerceIn(60, 200)
    val wordHeight = 50 // Approximate height

    // Screen boundaries with padding
    val minX = 10f
    val maxX = screenWidth - wordWidth - 10f
    val minY = -100f // Allow words to go slightly above screen for smooth exit
    val maxY = screenHeight - wordHeight - 10f

    LaunchedEffect(word.id) {
        // Set initial position at the bottom of the screen
        offsetY.snapTo(word.startY)

        // Smooth entry animation
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(500, easing = LinearEasing)
        )

        // Vertical movement animation - only move upward with boundary checks
        launch {
            // Animate from bottom to top with varied speeds
            offsetY.animateTo(
                targetValue = minY, // Move to top boundary
                animationSpec = tween(
                    // Faster words take less time to reach the top
                    durationMillis = (8000 / word.speedY).toInt(),
                    easing = LinearEasing
                )
            )

            // When word reaches the top boundary, make it invisible
            if (word.isVisible && !word.isHit) {
                word.isVisible = false
            }
        }
    }

    Box(
        modifier = Modifier
            .offset {
                // Ensure the word stays within screen boundaries
                val boundedX = offsetX.value.coerceIn(minX, maxX)
                val boundedY = offsetY.value.coerceIn(minY, maxY)
                IntOffset(boundedX.roundToInt(), boundedY.roundToInt())
            }
            .scale(scale.value)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = borderColor
            )
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(
                width = 3.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onWordTapped(word) }
            .padding(horizontal = 18.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = word.word,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = (20 * textSizeMultiplier).sp,
            fontFamily = if (isDyslexiaFontEnabled) FontFamily.Monospace else FontFamily.Default,
            letterSpacing = if (isDyslexiaFontEnabled) 0.05.sp else 0.sp
        )
    }
}

// New component for side barriers
@Composable
fun SideBarrier(
    modifier: Modifier = Modifier,
    isLeft: Boolean = true
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(60.dp)
    ) {
        // Remove the decorative elements since we want it invisible
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlyingWordsGame(navController: NavController) {
    var gameStarted by remember { mutableStateOf(false) }
    var gameOver by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }
    var lives by remember { mutableIntStateOf(3) }
    var timeLeft by remember { mutableIntStateOf(60) } // 60 seconds game
    var screenWidth by remember { mutableFloatStateOf(0f) }
    var screenHeight by remember { mutableFloatStateOf(0f) }
    var currencyEarned by remember { mutableIntStateOf(0) }
    var targetWord by remember { mutableStateOf("") }

    // Text size for accessibility
    var textSizeMultiplier by remember { mutableFloatStateOf(1f) }
    var wordSpeedMultiplier by remember { mutableFloatStateOf(1f) }
    var isDyslexiaFontEnabled by remember { mutableStateOf(false) }
    var isHighContrastEnabled by remember { mutableStateOf(false) }

    // Define barrier width (used for both visual barriers and spawn area calculation)
    val barrierWidth = 60.dp
    // Convert to pixels for calculations
    val localDensity = LocalDensity.current
    val barrierWidthPx = with(localDensity) { barrierWidth.toPx() }

    val coroutineScope = rememberCoroutineScope()
    val localView = LocalView.current

    val context = LocalContext.current
    val audioUtils = remember { AudioUtils.getInstance(context) }
    var isMuted by remember { mutableStateOf(audioUtils.isGloballyMuted()) }

    // Lists to store words
    val flyingWords = remember { mutableStateListOf<FlyingWord>() }
    val wordSynonyms = remember { mutableStateListOf<String>() }
    val wordAntonyms = remember { mutableStateListOf<String>() }
    val wordDummies = remember { mutableStateListOf<String>() }

    // Word generation counter to ensure unique IDs
    var wordIdCounter = remember { mutableIntStateOf(0) }

    // Flag to control word spawning
    var isSpawningWords by remember { mutableStateOf(false) }

    // Start background music when the game starts
    LaunchedEffect(gameStarted) {
        if (gameStarted && !gameOver && !isMuted) {
            audioUtils.playBackgroundMusic(R.raw.game_music_3)
        }
    }

    // Clean up resources when leaving the screen
    DisposableEffect(Unit) {
        onDispose {
            audioUtils.stopBackgroundMusic()
        }
    }

    // Target word that player needs to find synonyms for
    // Expanded word pairs with clear distinction between synonyms and antonyms
    val wordPairs = listOf(
        "happy" to (listOf("joyful", "cheerful", "glad", "delighted", "pleased", "content", "merry", "jolly", "ecstatic", "blissful") to
                listOf("sad", "angry", "upset", "miserable", "gloomy", "depressed", "unhappy", "sorrowful", "melancholy", "downcast")),

        "big" to (listOf("large", "huge", "enormous", "gigantic", "massive", "colossal", "immense", "vast", "substantial", "grand") to
                listOf("small", "tiny", "little", "miniature", "microscopic", "petite", "diminutive", "minute", "compact", "slight")),

        "fast" to (listOf("quick", "speedy", "rapid", "swift", "hasty", "brisk", "expeditious", "fleet", "nimble", "prompt") to
                listOf("slow", "sluggish", "lazy", "unhurried", "leisurely", "gradual", "plodding", "dawdling", "crawling", "tardy")),

        "cold" to (listOf("chilly", "cool", "freezing", "frosty", "icy", "frigid", "wintry", "arctic", "bitter", "nippy") to
                listOf("hot", "warm", "burning", "scorching", "boiling", "sweltering", "sizzling", "tropical", "heated", "fiery")),

        "beautiful" to (listOf("pretty", "gorgeous", "attractive", "stunning", "lovely", "handsome", "exquisite", "elegant", "charming", "radiant") to
                listOf("ugly", "plain", "hideous", "unattractive", "unsightly", "homely", "grotesque", "repulsive", "revolting", "disfigured")),

        "brave" to (listOf("courageous", "fearless", "heroic", "valiant", "bold", "daring", "intrepid", "gallant", "dauntless", "audacious") to
                listOf("cowardly", "fearful", "timid", "afraid", "scared", "frightened", "spineless", "gutless", "craven", "fainthearted")),

        "smart" to (listOf("intelligent", "clever", "bright", "brilliant", "wise", "astute", "sharp", "ingenious", "knowledgeable", "intellectual") to
                listOf("stupid", "dumb", "foolish", "ignorant", "unintelligent", "dense", "slow", "simple", "brainless", "obtuse")),

        "strong" to (listOf("powerful", "mighty", "muscular", "sturdy", "tough", "robust", "vigorous", "forceful", "potent", "brawny") to
                listOf("weak", "feeble", "fragile", "delicate", "frail", "flimsy", "powerless", "puny", "vulnerable", "infirm")),

        "rich" to (listOf("wealthy", "affluent", "prosperous", "opulent", "loaded", "moneyed", "well-off", "well-to-do", "flush", "comfortable") to
                listOf("poor", "broke", "needy", "impoverished", "destitute", "penniless", "indigent", "bankrupt", "insolvent", "poverty-stricken")),

        "loud" to (listOf("noisy", "booming", "thunderous", "deafening", "blaring", "clamorous", "ear-splitting", "resounding", "vociferous", "stentorian") to
                listOf("quiet", "soft", "silent", "hushed", "muted", "subdued", "faint", "gentle", "low", "inaudible")),

        "difficult" to (listOf("hard", "challenging", "tough", "complicated", "complex", "arduous", "demanding", "strenuous", "laborious", "formidable") to
                listOf("easy", "simple", "straightforward", "effortless", "uncomplicated", "painless", "trouble-free", "manageable", "elementary", "facile")),

        "ancient" to (listOf("old", "antique", "prehistoric", "primeval", "archaic", "aged", "venerable", "timeworn", "hoary", "antiquated") to
                listOf("new", "modern", "recent", "contemporary", "current", "fresh", "novel", "latest", "up-to-date", "newfangled")),

        "polite" to (listOf("courteous", "respectful", "well-mannered", "civil", "gracious", "considerate", "tactful", "diplomatic", "refined", "genteel") to
                listOf("rude", "impolite", "discourteous", "disrespectful", "ill-mannered", "uncivil", "boorish", "crude", "vulgar", "insolent"))
    )

    // Function to replenish word lists when they're running low
    fun replenishWordLists() {
        // Get a new set of words from the current target
        val currentPair = wordPairs.find { it.first == targetWord }
        if (currentPair != null) {
            // Add more synonyms by repeating the list if needed
            val synonyms = currentPair.second.first
            val repeatedSynonyms = (synonyms + synonyms).shuffled()

            val antonyms = currentPair.second.second.shuffled()
            val dummyWordsList = dummyWords.shuffled()

            wordSynonyms.addAll(repeatedSynonyms)
            wordAntonyms.addAll(antonyms)
            wordDummies.addAll(dummyWordsList)

            Log.d("FlyingWordsGame", "Replenished words - Synonyms: ${wordSynonyms.size}, Antonyms: ${wordAntonyms.size}, Dummies: ${wordDummies.size}")
        }
    }

    // Function to create a new flying word
    fun createNewWord(word: String, isSynonym: Boolean, isAntonym: Boolean, isDummy: Boolean): FlyingWord {
        val id = wordIdCounter.intValue
        wordIdCounter.intValue += 1

        val estimatedWordWidth = (word.length * 15f).coerceIn(60f, 200f)

        // MODIFIED: Account for barriers on both sides
        // Safe area is the screen width minus the barrier widths on both sides
        val safeAreaWidth = screenWidth - (barrierWidthPx * 2) - estimatedWordWidth

        // Generate a random position within the safe area
        val randomPosition = Random.nextFloat() * safeAreaWidth

        // Add the left barrier width to position the word in the safe area
        val safeStartX = barrierWidthPx + randomPosition

        // MODIFIED: Spawn words at the bottom of the screen
        // Use 50-60% of screen height to ensure words start from middle
        val minY = screenHeight * 0.50f
        val maxY = screenHeight * 0.60f
        val visibleY = Random.nextFloat() * (maxY - minY) + minY

        // Create more varied speeds - some words move much faster than others
        // Speed multiplier range from 0.5x to 2.5x the base speed
        val speedMultiplier = 0.5f + Random.nextFloat() * 2.0f
        val baseSpeed = 0.3f * wordSpeedMultiplier
        val finalSpeed = baseSpeed * speedMultiplier

        return FlyingWord(
            id = id,
            word = word,
            isSynonym = isSynonym,
            isAntonym = isAntonym,
            isDummy = isDummy,
            startX = safeStartX,
            startY = visibleY,
            speedX = 0f,
            speedY = finalSpeed,
            rotation = 0f,
            color = randomVibrantColor(),
            isHit = false,
            isVisible = true
        )
    }

    // Function to add a batch of words (6 at a time)
    fun addWordBatch() {
        if (isSpawningWords || gameOver) return

        isSpawningWords = true

        // Add 6 words with a mix of types
        var wordsAdded = 0

        // Always add at least 2 synonyms if available
        for (i in 0 until 2) {
            if (wordSynonyms.isNotEmpty() && wordsAdded < 6) {
                val word = wordSynonyms.removeAt(0)
                flyingWords.add(createNewWord(word, true, false, false))
                wordsAdded++
            }
        }

        // Add an antonym if available
        if (wordAntonyms.isNotEmpty() && wordsAdded < 6) {
            val word = wordAntonyms.removeAt(0)
            flyingWords.add(createNewWord(word, false, true, false))
            wordsAdded++
        }

        // Fill the rest with dummy words
        while (wordDummies.isNotEmpty() && wordsAdded < 6) {
            val word = wordDummies.removeAt(0)
            flyingWords.add(createNewWord(word, false, false, true))
            wordsAdded++
        }

        // If we're running out of words, replenish the lists
        if (wordSynonyms.size < 10 || wordAntonyms.size < 5 || wordDummies.size < 10) {
            replenishWordLists()
        }

        // Set a shorter delay before allowing the next batch
        coroutineScope.launch {
            delay(500) // 0.5 second delay between batches (to achieve ~6 words per 3 seconds)
            isSpawningWords = false
        }
    }

    // Function to initialize the game with words
    fun initializeGame() {
        // Clear any existing words
        flyingWords.clear()
        wordSynonyms.clear()
        wordAntonyms.clear()
        wordDummies.clear()

        // Reset word ID counter
        wordIdCounter.intValue = 0

        // Select a random word pair
        val randomPair = wordPairs.random()
        targetWord = randomPair.first

        // Get synonyms and antonyms
        val synonyms = randomPair.second.first.shuffled()
        val antonyms = randomPair.second.second.shuffled()
        val dummyWordsList = dummyWords.shuffled()

        // Store words for later use
        wordSynonyms.addAll(synonyms)
        wordAntonyms.addAll(antonyms)
        wordDummies.addAll(dummyWordsList)

        // Start with an initial batch of words
        isSpawningWords = false

        // FIXED: Force immediate word generation after game starts
        coroutineScope.launch {
            delay(500) // Short delay to ensure screen is ready
            addWordBatch()
        }
    }

    // Function to start the game
    fun startGame() {
        // Reset all game states
        gameStarted = false
        gameOver = false
        score = 0
        lives = 3
        timeLeft = 60
        currencyEarned = 0
        flyingWords.clear()
        wordSynonyms.clear()
        wordAntonyms.clear()
        wordDummies.clear()
        wordIdCounter.intValue = 0
        isSpawningWords = false

        // Small delay before starting the game to ensure clean state
        coroutineScope.launch {
            delay(100)
            gameStarted = true
            initializeGame()
        }
    }

    // Game timer and word management
    LaunchedEffect(gameStarted) {
        if (gameStarted && !gameOver) {
            // Timer countdown
            while (timeLeft > 0 && !gameOver) {
                delay(1000)
                timeLeft--

                // Provide haptic feedback when time is running low
                if (timeLeft <= 10 && timeLeft > 0) {
                    localView.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                }
            }

            if (timeLeft <= 0) {
                gameOver = true
                // Calculate currency earned when game is over
                currencyEarned = GameUtils.calculateCurrencyEarned(score)
            }
        }
    }

    // Word batch spawning system
    LaunchedEffect(gameStarted) {
        if (gameStarted && !gameOver) {
            while (!gameOver) {
                delay(100) // Check frequently

                // If we're not currently spawning words and there are fewer than 12 words on screen, add a batch
                if (!isSpawningWords && flyingWords.count { it.isVisible } < 12) {
                    addWordBatch()
                }

                // Log the number of visible words for debugging
                Log.d("FlyingWordsGame", "Visible words: ${flyingWords.count { it.isVisible }}")
            }
        }
    }

    // FIXED: Added a debug LaunchedEffect to monitor word generation
    LaunchedEffect(gameStarted) {
        if (gameStarted && !gameOver) {
            while (!gameOver) {
                delay(5000) // Check every 5 seconds
                Log.d("FlyingWordsGame", "Word lists - Synonyms: ${wordSynonyms.size}, Antonyms: ${wordAntonyms.size}, Dummies: ${wordDummies.size}")
                Log.d("FlyingWordsGame", "Flying words: ${flyingWords.size}, Visible: ${flyingWords.count { it.isVisible }}")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Flying Words",
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
                    // Music toggle button
                    IconButton(
                        onClick = {
                            audioUtils.toggleGlobalMute()
                            isMuted = audioUtils.isGloballyMuted()
                            if (!isMuted && gameStarted && !gameOver) {
                                audioUtils.playBackgroundMusic(R.raw.game_music_3)
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
                    containerColor = VocaPurple
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
                        colors = if (isHighContrastEnabled) {
                            listOf(
                                Color(0xFFF5F5DC), // Beige background for high contrast
                                Color(0xFFF5F5DC),
                                Color(0xFFF5F5DC)
                            )
                        } else {
                            listOf(
                                Color(0xFF0D0221), // Deep blue background
                                Color(0xFF190933), // Midnight blue
                                Color(0xFF301B54)  // Dark purple
                            )
                        }
                    )
                )
                .onGloballyPositioned {
                    screenWidth = it.size.width.toFloat()
                    screenHeight = it.size.height.toFloat()

                    // FIXED: Log screen dimensions for debugging
                    Log.d("FlyingWordsGame", "Screen dimensions: $screenWidth x $screenHeight")
                },
            contentAlignment = Alignment.Center
        ) {
            // Add a starry night effect to the background
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Draw stars with different sizes
                repeat(150) {
                    val x = (Math.random() * size.width).toFloat()
                    val y = (Math.random() * size.height).toFloat()
                    val radius = (1 + Math.random() * 2).toFloat().dp.toPx()
                    val alpha = (0.3f + Math.random() * 0.7f).toFloat()

                    drawCircle(
                        color = Color.White.copy(alpha = alpha),
                        radius = radius,
                        center = Offset(x, y)
                    )
                }

                // Add some nebula effects
                repeat(3) {
                    val x = (Math.random() * size.width).toFloat()
                    val y = (Math.random() * size.height).toFloat()
                    val radius = (100 + Math.random() * 200).toFloat().dp.toPx()

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0x10CE93D8),
                                Color(0x08BA68C8),
                                Color(0x05AB47BC),
                                Color.Transparent
                            ),
                            center = Offset(x, y),
                            radius = radius
                        ),
                        radius = radius,
                        center = Offset(x, y)
                    )
                }
            }

            if (!gameStarted) {
                FlyingGameStartScreen(onStartGame = { startGame() })
            } else if (gameOver) {
                GameOverScreenWithCurrency(
                    score = score,
                    currencyEarned = currencyEarned,
                    onPlayAgain = { startGame() },
                    onBackToGames = { navController.navigateUp() }
                )
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Game content
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        // Game header with score, lives, and timer
                        FlyingGameHeader(
                            score = score,
                            lives = lives,
                            timeLeft = timeLeft
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Show the chosen word at the top (improved UI)
                        Column(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(bottom = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Find synonyms for:",
                                style = MaterialTheme.typography.titleMedium,
                                color = VocaPurple,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFF7B1FA2)) // More vibrant purple
                                    .border(
                                        width = 2.dp,
                                        color = VocaPurple,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .padding(horizontal = 24.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = targetWord,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = Color.White, // White for contrast
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            // Display the number of remaining synonyms
                            val synonymsLeft = flyingWords.count { it.isSynonym && !it.isHit && it.isVisible }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Synonyms remaining: $synonymsLeft",
                                style = MaterialTheme.typography.bodyMedium,
                                color = VocaGreen,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Game area
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 16.dp)
                        ) {
                            // FIXED: Added a debug indicator for empty word list
                            if (flyingWords.isEmpty() && gameStarted && !gameOver) {
                                Text(
                                    text = "Loading words...",
                                    color = Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }

                            // Flying words
                            flyingWords.forEach { flyingWord ->
                                if (flyingWord.isVisible) {
                                    FlyingWordItem(
                                        word = flyingWord,
                                        screenWidth = screenWidth,
                                        screenHeight = screenHeight,
                                        textSizeMultiplier = textSizeMultiplier,
                                        isDyslexiaFontEnabled = isDyslexiaFontEnabled,
                                        isHighContrastEnabled = isHighContrastEnabled,
                                        onWordTapped = { word ->
                                            if (word.isSynonym) {
                                                // Correct word tapped
                                                score += 10
                                                flyingWord.isHit = true
                                                flyingWord.isVisible = false
                                                // Play correct sound with higher volume
                                                if (!isMuted) {
                                                    audioUtils.playSoundWithVolume(R.raw.correct_sound, 1.0f)
                                                }
                                                // Provide success haptic feedback
                                                localView.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                                            } else {
                                                // Wrong word tapped (antonym or dummy)
                                                lives--
                                                flyingWord.isVisible = false
                                                // Play wrong sound with higher volume
                                                if (!isMuted) {
                                                    audioUtils.playSoundWithVolume(R.raw.wrong_sound, 1.0f)
                                                }
                                                // Provide error haptic feedback
                                                localView.performHapticFeedback(HapticFeedbackConstants.REJECT)

                                                if (lives <= 0) {
                                                    gameOver = true
                                                    // Calculate currency earned when game is over
                                                    currencyEarned = GameUtils.calculateCurrencyEarned(score)

                                                    // Directly update currency in Firestore
                                                    coroutineScope.launch {
                                                        try {
                                                            val userRepository = UserRepository(FirebaseFirestore.getInstance())
                                                            val userId = userRepository.getCurrentUserId()
                                                            if (userId != null) {
                                                                val success = userRepository.updateUserCurrency(userId, currencyEarned)
                                                                if (success) {
                                                                    Log.d("FlyingWordsGame", "Successfully awarded $currencyEarned currency")
                                                                } else {
                                                                    Log.e("FlyingWordsGame", "Failed to award currency")
                                                                }
                                                            }
                                                        } catch (e: Exception) {
                                                            Log.e("FlyingWordsGame", "Error awarding currency", e)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Add side barriers (left and right)
                    SideBarrier(
                        modifier = Modifier.align(Alignment.CenterStart),
                        isLeft = true
                    )

                    SideBarrier(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        isLeft = false
                    )
                }
            }
        }
    }
}
