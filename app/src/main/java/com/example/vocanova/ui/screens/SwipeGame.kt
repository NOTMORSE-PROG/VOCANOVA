package com.example.vocanova.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutQuad
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwipeLeft
import androidx.compose.material.icons.filled.SwipeRight
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.vocanova.R
import com.example.vocanova.data.model.Word
import com.example.vocanova.utils.AudioUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

// List of 30 words with synonyms and antonyms
val wordsList = listOf(
    Word(
        id = 1,
        text = "happy",
        partOfSpeech = "adjective",
        pronunciation = "ˈhæpi",
        meaning = "Feeling or showing pleasure or contentment.",
        example = "She felt happy when she received the gift.",
        synonyms = listOf("joyful", "cheerful", "delighted"),
        antonyms = listOf("sad", "unhappy", "miserable")
    ),
    Word(
        id = 2,
        text = "big",
        partOfSpeech = "adjective",
        pronunciation = "bɪg",
        meaning = "Of considerable size, extent, or intensity.",
        example = "They live in a big house.",
        synonyms = listOf("large", "huge", "enormous"),
        antonyms = listOf("small", "tiny", "little")
    ),
    Word(
        id = 3,
        text = "fast",
        partOfSpeech = "adjective",
        pronunciation = "fæst",
        meaning = "Moving or capable of moving at high speed.",
        example = "He is a fast runner.",
        synonyms = listOf("quick", "rapid", "swift"),
        antonyms = listOf("slow", "sluggish", "leisurely")
    ),
    Word(
        id = 4,
        text = "hot",
        partOfSpeech = "adjective",
        pronunciation = "hɒt",
        meaning = "Having a high degree of heat or a high temperature.",
        example = "It was a hot day.",
        synonyms = listOf("warm", "burning", "scorching"),
        antonyms = listOf("cold", "cool", "chilly")
    ),
    Word(
        id = 5,
        text = "good",
        partOfSpeech = "adjective",
        pronunciation = "gʊd",
        meaning = "To be desired or approved of.",
        example = "She is a good student.",
        synonyms = listOf("excellent", "fine", "wonderful"),
        antonyms = listOf("bad", "poor", "terrible")
    ),
    Word(
        id = 6,
        text = "beautiful",
        partOfSpeech = "adjective",
        pronunciation = "ˈbjuːtəfəl",
        meaning = "Pleasing the senses or mind aesthetically.",
        example = "The sunset was incredibly beautiful.",
        synonyms = listOf("pretty", "gorgeous", "attractive"),
        antonyms = listOf("ugly", "unattractive", "plain")
    ),
    Word(
        id = 7,
        text = "strong",
        partOfSpeech = "adjective",
        pronunciation = "strɔːŋ",
        meaning = "Having great physical power or strength.",
        example = "She is a strong and independent woman.",
        synonyms = listOf("powerful", "mighty", "sturdy"),
        antonyms = listOf("weak", "feeble", "fragile")
    ),
    Word(
        id = 8,
        text = "rich",
        partOfSpeech = "adjective",
        pronunciation = "rɪtʃ",
        meaning = "Having a great deal of money or assets.",
        example = "The businessman is very rich.",
        synonyms = listOf("wealthy", "affluent", "prosperous"),
        antonyms = listOf("poor", "broke", "destitute")
    ),
    Word(
        id = 9,
        text = "brave",
        partOfSpeech = "adjective",
        pronunciation = "breɪv",
        meaning = "Ready to face and endure danger or pain.",
        example = "The firefighter was very brave.",
        synonyms = listOf("courageous", "fearless", "bold"),
        antonyms = listOf("afraid", "fearful", "cowardly")
    ),
    Word(
        id = 10,
        text = "smart",
        partOfSpeech = "adjective",
        pronunciation = "smɑːt",
        meaning = "Having or showing a quick-witted intelligence.",
        example = "He is a smart student.",
        synonyms = listOf("intelligent", "clever", "bright"),
        antonyms = listOf("stupid", "dumb", "foolish")
    ),
    Word(
        id = 11,
        text = "loud",
        partOfSpeech = "adjective",
        pronunciation = "laʊd",
        meaning = "Producing or capable of producing much noise.",
        example = "The music was too loud.",
        synonyms = listOf("noisy", "deafening", "thunderous"),
        antonyms = listOf("quiet", "silent", "soft")
    ),
    Word(
        id = 12,
        text = "dark",
        partOfSpeech = "adjective",
        pronunciation = "dɑːk",
        meaning = "With little or no light.",
        example = "It was dark in the cave.",
        synonyms = listOf("dim", "gloomy", "shadowy"),
        antonyms = listOf("light", "bright", "illuminated")
    ),
    Word(
        id = 13,
        text = "hard",
        partOfSpeech = "adjective",
        pronunciation = "hɑːd",
        meaning = "Requiring a great deal of endurance or effort.",
        example = "This exam is very hard.",
        synonyms = listOf("difficult", "tough", "challenging"),
        antonyms = listOf("easy", "simple", "effortless")
    ),
    Word(
        id = 14,
        text = "old",
        partOfSpeech = "adjective",
        pronunciation = "əʊld",
        meaning = "Having lived or existed for many years.",
        example = "That house is very old.",
        synonyms = listOf("ancient", "aged", "elderly"),
        antonyms = listOf("new", "young", "fresh")
    ),
    Word(
        id = 15,
        text = "clean",
        partOfSpeech = "adjective",
        pronunciation = "kliːn",
        meaning = "Free from dirt, marks, or stains.",
        example = "The room is clean and tidy.",
        synonyms = listOf("spotless", "pristine", "immaculate"),
        antonyms = listOf("dirty", "filthy", "soiled")
    ),
    Word(
        id = 16,
        text = "expensive",
        partOfSpeech = "adjective",
        pronunciation = "ɪkˈspɛnsɪv",
        meaning = "Costing a lot of money.",
        example = "That watch is very expensive.",
        synonyms = listOf("costly", "pricey", "valuable"),
        antonyms = listOf("cheap", "inexpensive", "affordable")
    ),
    Word(
        id = 17,
        text = "thick",
        partOfSpeech = "adjective",
        pronunciation = "θɪk",
        meaning = "Having a large distance between opposite sides.",
        example = "The book is very thick.",
        synonyms = listOf("dense", "heavy", "substantial"),
        antonyms = listOf("thin", "slim", "slender")
    ),
    Word(
        id = 18,
        text = "deep",
        partOfSpeech = "adjective",
        pronunciation = "diːp",
        meaning = "Extending far down from the top or surface.",
        example = "The ocean is deep.",
        synonyms = listOf("profound", "bottomless", "abyssal"),
        antonyms = listOf("shallow", "superficial", "surface")
    ),
    Word(
        id = 19,
        text = "wide",
        partOfSpeech = "adjective",
        pronunciation = "waɪd",
        meaning = "Having a great extent from side to side.",
        example = "The road is wide enough for two cars.",
        synonyms = listOf("broad", "extensive", "vast"),
        antonyms = listOf("narrow", "slim", "thin")
    ),
    Word(
        id = 20,
        text = "tall",
        partOfSpeech = "adjective",
        pronunciation = "tɔːl",
        meaning = "Of great or more than average height.",
        example = "He is a tall basketball player.",
        synonyms = listOf("high", "towering", "lofty"),
        antonyms = listOf("short", "small", "low")
    ),
    Word(
        id = 21,
        text = "smooth",
        partOfSpeech = "adjective",
        pronunciation = "smuːð",
        meaning = "Having an even and regular surface.",
        example = "The table surface is smooth.",
        synonyms = listOf("sleek", "even", "flat"),
        antonyms = listOf("rough", "bumpy", "uneven")
    ),
    Word(
        id = 22,
        text = "heavy",
        partOfSpeech = "adjective",
        pronunciation = "ˈhɛvi",
        meaning = "Of great weight; difficult to lift or move.",
        example = "This box is too heavy to carry alone.",
        synonyms = listOf("weighty", "massive", "ponderous"),
        antonyms = listOf("light", "weightless", "feathery")
    ),
    Word(
        id = 23,
        text = "sharp",
        partOfSpeech = "adjective",
        pronunciation = "ʃɑːp",
        meaning = "Having an edge or point that is able to cut or pierce.",
        example = "Be careful, that knife is sharp.",
        synonyms = listOf("pointed", "keen", "acute"),
        antonyms = listOf("dull", "blunt", "rounded")
    ),
    Word(
        id = 24,
        text = "wet",
        partOfSpeech = "adjective",
        pronunciation = "wɛt",
        meaning = "Covered with, or saturated with liquid (usually water).",
        example = "Her clothes were wet from the rain.",
        synonyms = listOf("damp", "moist", "soaked"),
        antonyms = listOf("dry", "arid", "parched")
    ),
    Word(
        id = 25,
        text = "full",
        partOfSpeech = "adjective",
        pronunciation = "fʊl",
        meaning = "Containing or holding as much as possible.",
        example = "The glass is full of water.",
        synonyms = listOf("complete", "filled", "packed"),
        antonyms = listOf("empty", "vacant", "hollow")
    ),
    Word(
        id = 26,
        text = "early",
        partOfSpeech = "adjective",
        pronunciation = "ˈɜːli",
        meaning = "Happening or done before the usual or expected time.",
        example = "She arrived early to the meeting.",
        synonyms = listOf("prompt", "timely", "beforehand"),
        antonyms = listOf("late", "tardy", "delayed")
    ),
    Word(
        id = 27,
        text = "high",
        partOfSpeech = "adjective",
        pronunciation = "haɪ",
        meaning = "Of great vertical extent.",
        example = "That mountain is very high.",
        synonyms = listOf("elevated", "lofty", "tall"),
        antonyms = listOf("low", "short", "small")
    ),
    Word(
        id = 28,
        text = "open",
        partOfSpeech = "adjective",
        pronunciation = "ˈəʊpən",
        meaning = "Allowing access; not closed or blocked.",
        example = "The store is open all day.",
        synonyms = listOf("unlocked", "accessible", "available"),
        antonyms = listOf("closed", "shut", "sealed")
    ),
    Word(
        id = 29,
        text = "soft",
        partOfSpeech = "adjective",
        pronunciation = "sɒft",
        meaning = "Easy to press or deform; not hard or firm.",
        example = "This pillow is very soft.",
        synonyms = listOf("gentle", "tender", "plush"),
        antonyms = listOf("hard", "firm", "rigid")
    ),
    Word(
        id = 30,
        text = "wild",
        partOfSpeech = "adjective",
        pronunciation = "waɪld",
        meaning = "Living or growing in the natural environment; not domesticated.",
        example = "They saw wild animals in the jungle.",
        synonyms = listOf("untamed", "feral", "savage"),
        antonyms = listOf("tame", "domesticated", "docile")
    )
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTextApi::class, ExperimentalAnimationApi::class)
@Composable
fun SwipeGame(
    navController: NavController,
    words: List<Word>
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Audio utilities
    val audioUtils = remember { AudioUtils.getInstance(context) }

    // Use the generated words list if the provided list is empty
    val gameWords = if (words.isEmpty()) wordsList else words

    // Game state
    var gameStarted by remember { mutableStateOf(false) }
    var currentWordIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(60) }
    var showFeedback by remember { mutableStateOf(false) }
    var feedbackCorrect by remember { mutableStateOf(false) }
    var gameOver by remember { mutableStateOf(false) }
    var showGameOver by remember { mutableStateOf(false) }
    var finalScore by remember { mutableStateOf(0) }
    var currencyEarned by remember { mutableStateOf(0) }
    var lives by remember { mutableIntStateOf(3) }
    var isGameActive by remember { mutableStateOf(false) }

    // Card position and animation
    var cardOffsetX by remember { mutableFloatStateOf(0f) }
    var cardOffsetY by remember { mutableFloatStateOf(0f) }
    var cardRotation by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var swipeDirection by remember { mutableIntStateOf(0) } // -1 for left, 1 for right, 0 for none

    // Add mute state at the top of the composable
    var isMuted by remember { mutableStateOf(audioUtils.isGloballyMuted()) }

    // Start background music when game starts
    LaunchedEffect(gameStarted) {
        if (gameStarted) {
            audioUtils.playBackgroundMusic(R.raw.game_music_2, true)
        } else {
            audioUtils.stopBackgroundMusic()
        }
    }

    // Stop music when leaving the screen
    DisposableEffect(Unit) {
        onDispose {
            audioUtils.stopBackgroundMusic()
        }
    }

    // Animated stars in background
    val stars = remember {
        List(150) {
            Triple(
                Random.nextFloat(), // x position
                Random.nextFloat(), // y position
                (Random.nextFloat() * 0.8f + 0.2f) // size/brightness
            )
        }
    }

    // Animated nebula effect
    val nebulaOffset = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        nebulaOffset.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(20000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    // Floating particles animation
    val particles = remember {
        List(40) {
            val randomDelay = Random.nextInt(0, 3000)
            val randomDuration = Random.nextInt(3000, 8000)
            Triple(
                Random.nextFloat(), // x position
                Random.nextFloat(), // y position
                randomDelay to randomDuration // animation parameters
            )
        }
    }

    val particleAnimations = particles.map { (x, y, animParams) ->
        val anim = remember { Animatable(0f) }
        LaunchedEffect(gameStarted) {
            delay(animParams.first.toLong())
            anim.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = animParams.second,
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Restart
                )
            )
        }
        Triple(x, y, anim.value)
    }

    // Timer effect
    LaunchedEffect(gameStarted) {
        if (gameStarted) {
            while (timeLeft > 0 && !gameOver) {
                delay(1000)
                timeLeft--
            }
            if (timeLeft <= 0) {
                gameOver = true
            }
        }
    }

    // Pulse animation for the timer
    val pulseAnim = remember { Animatable(1f) }
    LaunchedEffect(timeLeft) {
        if (timeLeft <= 10 && gameStarted && !gameOver) {
            pulseAnim.animateTo(
                targetValue = 1.2f,
                animationSpec = tween(150)
            )
            pulseAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(150)
            )
        }
    }

    // Shimmer effect for text

    val transition = rememberInfiniteTransition()

    // Floating animation for cards
    val floatingAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Precompute all cards up front, using remember(gameWords)
    val cardList = remember(gameWords) {
        gameWords.map { word ->
            val hasSynonyms = word.synonyms.isNotEmpty()
            val hasAntonyms = word.antonyms.isNotEmpty()
            val isSynonym = when {
                hasSynonyms && hasAntonyms -> Random.nextBoolean()
                hasSynonyms -> true
                hasAntonyms -> false
                else -> true // default to synonym if none
            }
            val relatedWord = if (isSynonym) {
                word.synonyms.randomOrNull() ?: "Unknown"
            } else {
                word.antonyms.randomOrNull() ?: "Unknown"
            }
            Triple(word, isSynonym, relatedWord)
        }
    }

    // Replace the game over condition with:
    if (lives <= 0 || timeLeft <= 0) {
        showGameOver = true
        finalScore = score
        currencyEarned = score / 10 // Award 1 currency for every 10 points
    }

    // Main UI
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
            stars.forEach { (x, y, brightness) ->
                val xPos = x * size.width
                val yPos = y * size.height
                val radius = brightness * 2.dp.toPx()

                drawCircle(
                    color = Color.White.copy(alpha = brightness),
                    radius = radius,
                    center = Offset(xPos, yPos)
                )

                // Add glow effect to some stars
                if (brightness > 0.7f) {
                    drawCircle(
                        color = Color.White.copy(alpha = brightness * 0.4f),
                        radius = radius * 3,
                        center = Offset(xPos, yPos)
                    )
                }
            }

            // Draw nebula effect
            val nebulaColors = listOf(
                Color(0x10CE93D8),
                Color(0x20BA68C8),
                Color(0x15AB47BC),
                Color(0x108E24AA)
            )

            // Draw multiple nebula clouds
            for (i in 0 until 3) {
                val xOffset = size.width * 0.3f * sin(nebulaOffset.value * Math.PI * 2 + i) + size.width * 0.5f
                val yOffset = size.height * (0.3f + i * 0.2f)
                val radius = size.width * (0.3f + i * 0.1f)

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = nebulaColors,
                        center = Offset(xOffset.toFloat(), yOffset),
                        radius = radius
                    ),
                    radius = radius,
                    center = Offset(xOffset.toFloat(), yOffset)
                )
            }
        }

        // Animated floating particles
        Canvas(modifier = Modifier.fillMaxSize()) {
            particleAnimations.forEach { (x, y, animValue) ->
                val xPos = x * size.width
                val yPos = (y + animValue) % 1f * size.height
                val particleSize = (2..5).random().dp.toPx()
                val alpha = Random.nextFloat() * (0.6f - 0.2f) + 0.2f

                drawCircle(
                    color = Color(0xFFBA68C8).copy(alpha = alpha),
                    radius = particleSize,
                    center = Offset(xPos, yPos)
                )
            }
        }

        // Game content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Top bar with back button, score, and mute button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back button with glow effect
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .shadow(8.dp, CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF9C27B0),
                                    Color(0xFF7B1FA2)
                                )
                            ),
                            shape = CircleShape
                        )
                        .border(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFCE93D8),
                                    Color(0xFF7B1FA2)
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = {
                            audioUtils.stopBackgroundMusic()
                            navController.popBackStack()
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Mute/unmute button
                IconButton(
                    onClick = {
                        audioUtils.toggleGlobalMute()
                        isMuted = audioUtils.isGloballyMuted()
                        if (!isMuted && gameStarted && !gameOver) {
                            audioUtils.playBackgroundMusic(R.raw.game_music_2, true)
                        } else if (isMuted) {
                            audioUtils.stopBackgroundMusic()
                        }
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = if (isMuted) "Unmute" else "Mute",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Score display with glow effect
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .shadow(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF7B1FA2),
                                        Color(0xFF9C27B0)
                                    )
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .border(
                                width = 2.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFFCE93D8),
                                        Color(0xFF7B1FA2)
                                    )
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Score",
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Score: $score",
                                style = TextStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Timer display with cosmic design
            Box(
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 24.dp)
                    .size(80.dp * pulseAnim.value)
                    .shadow(16.dp, CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                if (timeLeft <= 10) Color(0xFFF44336) else Color(0xFF9C27B0),
                                if (timeLeft <= 10) Color(0xFFB71C1C) else Color(0xFF4A148C)
                            )
                        ),
                        shape = CircleShape
                    )
                    .border(
                        width = 3.dp,
                        brush = Brush.sweepGradient(
                            listOf(
                                Color(0xFFCE93D8),
                                Color(0xFF9C27B0),
                                Color(0xFF7B1FA2),
                                Color(0xFFCE93D8)
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {

                // Add a pulsing glow effect
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                if (timeLeft <= 10) Color(0x40F44336) else Color(0x409C27B0),
                                Color.Transparent
                            ),
                            radius = size.width * 0.8f
                        ),
                        radius = size.width * 0.8f,
                        center = Offset(size.width / 2, size.height / 2)
                    )
                }

                // Timer text
                Text(
                    text = "$timeLeft",
                    style = TextStyle(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                // Add a small clock icon
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = "Timer",
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 12.dp)
                        .size(16.dp)
                )
            }

            // Chosen Word below the timer
            if (gameStarted && !gameOver && currentWordIndex < cardList.size) {
                val (currentWord, isSynonym, _) = cardList[currentWordIndex]
                if (isSynonym) "Synonym" else "Antonym"
                Text(
                    text = "Chosen Word",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF4A148C),
                                    Color(0xFF7B1FA2)
                                )
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .border(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFCE93D8),
                                    Color(0xFF4A148C)
                                )
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentWord.text,
                        style = TextStyle(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            shadow = Shadow(
                                color = Color.Black,
                                offset = Offset(2f, 2f),
                                blurRadius = 6f
                            )
                        )
                    )
                }
            }

            if (!gameStarted) {
                // Game start screen with cosmic theme
                GameStartScreen(
                    onStartClick = {
                        gameStarted = true
                        // Play background music when game starts
                        audioUtils.playBackgroundMusic(R.raw.game_music_2, true)
                    }
                )
            } else if (!gameOver) {
                // Swipe indicators with cosmic design
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Antonym indicator (left)
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                            .shadow(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFFB71C1C),
                                            Color(0xFFD32F2F)
                                        )
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .border(
                                    width = 2.dp,
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFFEF9A9A),
                                            Color(0xFFB71C1C)
                                        )
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Antonym",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Antonym",
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }
                        }
                    }

                    // Synonym indicator (right)
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                            .shadow(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF1B5E20),
                                            Color(0xFF2E7D32)
                                        )
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .border(
                                    width = 2.dp,
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFFA5D6A7),
                                            Color(0xFF1B5E20)
                                        )
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Synonym",
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Synonym",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Word card with cosmic design
                if (currentWordIndex < cardList.size) {
                    val (_, isSynonym, relatedWord) = cardList[currentWordIndex]
                    val displayedRelation = if (isSynonym) "Synonym" else "Antonym"

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                            .padding(16.dp)
                    ) {
                        // Floating animation for the card
                        val floatOffset = sin(floatingAnim.value * Math.PI.toFloat()) * 8

                        // Swipe card with cosmic design
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset {
                                    IntOffset(
                                        cardOffsetX.roundToInt(),
                                        (cardOffsetY + floatOffset).roundToInt()
                                    )
                                }
                                .graphicsLayer(
                                    rotationZ = cardRotation,
                                    shadowElevation = 20f
                                )
                                .pointerInput(Unit) {
                                    detectDragGestures(
                                        onDragStart = {
                                            isDragging = true
                                        },
                                        onDragEnd = {
                                            val swipedRight = cardOffsetX > 150
                                            val swipedLeft = cardOffsetX < -150
                                            if (swipedRight || swipedLeft) {
                                                swipeDirection = if (swipedRight) 1 else -1
                                                scope.launch {
                                                    // Animate the card off screen
                                                    if (swipedRight) {
                                                        while (cardOffsetX < size.width * 1.5f) {
                                                            cardOffsetX += 40
                                                            cardRotation += 1
                                                            delay(10)
                                                        }
                                                    } else {
                                                        while (cardOffsetX > -size.width * 1.5f) {
                                                            cardOffsetX -= 40
                                                            cardRotation -= 1
                                                            delay(10)
                                                        }
                                                    }

                                                    // Inside the scope.launch block after the card animation off screen
                                                    // Replace the existing correctness logic with this:

                                                    // Get the current card data
                                                    val currentCard = cardList[currentWordIndex]
                                                    val currentIsSynonym = currentCard.second // Get isSynonym for current word

                                                    // Correctness logic: check if swipe direction matches the word relationship
                                                    val isCorrect = (swipedRight && currentIsSynonym) || (swipedLeft && !currentIsSynonym)

                                                    if (isCorrect) {
                                                        score += 10
                                                        feedbackCorrect = true
                                                        audioUtils.playSoundWithVolume(R.raw.correct_sound, 1.0f)
                                                    } else {
                                                        score = maxOf(0, score - 5)
                                                        feedbackCorrect = false
                                                        audioUtils.playSoundWithVolume(R.raw.wrong_sound, 1.0f)
                                                    }

                                                    showFeedback = true
                                                    delay(1000)
                                                    showFeedback = false

                                                    // Reset and move to next word
                                                    cardOffsetX = 0f
                                                    cardOffsetY = 0f
                                                    cardRotation = 0f
                                                    swipeDirection = 0

                                                    currentWordIndex++
                                                    if (currentWordIndex >= cardList.size) {
                                                        gameOver = true
                                                    }
                                                }
                                            } else {
                                                // Not swiped far enough, return to center
                                                scope.launch {
                                                    val startX = cardOffsetX
                                                    val startY = cardOffsetY
                                                    val startRotation = cardRotation
                                                    val steps = 20
                                                    for (i in 1..steps) {
                                                        val progress = i.toFloat() / steps
                                                        cardOffsetX = startX * (1 - progress)
                                                        cardOffsetY = startY * (1 - progress)
                                                        cardRotation = startRotation * (1 - progress)
                                                        delay(10)
                                                    }
                                                    cardOffsetX = 0f
                                                    cardOffsetY = 0f
                                                    cardRotation = 0f
                                                    swipeDirection = 0
                                                }
                                            }
                                        },
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            cardOffsetX += dragAmount.x
                                            cardOffsetY += dragAmount.y
                                            cardRotation = (cardOffsetX / 50).coerceIn(-10f, 10f)

                                            // Update swipe direction indicator
                                            swipeDirection = when {
                                                cardOffsetX > 50 -> 1
                                                cardOffsetX < -50 -> -1
                                                else -> 0
                                            }
                                        }
                                    )
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                when (swipeDirection) {
                                                    -1 -> Color(0xFF4A0D0D)
                                                    1 -> Color(0xFF0D3B0D)
                                                    else -> Color(0xFF2C1B4A)
                                                },
                                                Color(0xFF190933)
                                            )
                                        )
                                    )
                                    .border(
                                        width = 3.dp,
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                when (swipeDirection) {
                                                    -1 -> Color(0xFFEF9A9A)
                                                    1 -> Color(0xFFA5D6A7)
                                                    else -> Color(0xFFCE93D8)
                                                },
                                                when (swipeDirection) {
                                                    -1 -> Color(0xFFB71C1C)
                                                    1 -> Color(0xFF1B5E20)
                                                    else -> Color(0xFF4A148C)
                                                }
                                            )
                                        ),
                                        shape = RoundedCornerShape(24.dp)
                                    )
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                // Add a subtle glow effect
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawCircle(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                when (swipeDirection) {
                                                    -1 -> Color(0x30EF9A9A)
                                                    1 -> Color(0x30A5D6A7)
                                                    else -> Color(0x30CE93D8)
                                                },
                                                Color.Transparent
                                            )
                                        ),
                                        radius = size.width * 0.8f,
                                        center = Offset(size.width / 2, size.height / 2)
                                    )

                                    // Add some stars inside the card
                                    repeat(10) {
                                        val x = Random.nextFloat() * size.width
                                        val y = Random.nextFloat() * size.height
                                        val radius = Random.nextFloat() * 2.dp.toPx()

                                        drawCircle(
                                            color = Color.White.copy(alpha = Random.nextFloat() * 0.5f + 0.2f),
                                            radius = radius,
                                            center = Offset(x, y)
                                        )
                                    }
                                }

                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Swipe to match",
                                        style = TextStyle(
                                            fontSize = 16.sp,
                                            color = Color.White.copy(alpha = 0.7f),
                                            textAlign = TextAlign.Center
                                        ),
                                        modifier = Modifier.padding(bottom = 24.dp)
                                    )

                                    // Related word with cosmic design
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 16.dp)
                                            .background(
                                                brush = Brush.linearGradient(
                                                    colors = listOf(
                                                        Color(0xFF4A148C),
                                                        Color(0xFF7B1FA2)
                                                    )
                                                ),
                                                shape = RoundedCornerShape(16.dp)
                                            )
                                            .border(
                                                width = 2.dp,
                                                brush = Brush.linearGradient(
                                                    colors = listOf(
                                                        Color(0xFFCE93D8),
                                                        Color(0xFF4A148C)
                                                    )
                                                ),
                                                shape = RoundedCornerShape(16.dp)
                                            )
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        // Add a subtle glow effect
                                        Canvas(modifier = Modifier.fillMaxSize()) {
                                            drawCircle(
                                                brush = Brush.radialGradient(
                                                    colors = listOf(
                                                        Color(0x30CE93D8),
                                                        Color.Transparent
                                                    )
                                                ),
                                                radius = size.width * 0.5f,
                                                center = Offset(size.width / 2, size.height / 2)
                                            )
                                        }

                                        Text(
                                            text = relatedWord,
                                            style = TextStyle(
                                                fontSize = 32.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                textAlign = TextAlign.Center,
                                                shadow = Shadow(
                                                    color = Color.Black,
                                                    offset = Offset(2f, 2f),
                                                    blurRadius = 6f
                                                )
                                            )
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(32.dp))

                                    // Relation type with cosmic design
                                    Card(
                                        modifier = Modifier
                                            .padding(top = 8.dp)
                                            .shadow(8.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color.Transparent
                                        ),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    brush = Brush.linearGradient(
                                                        colors = listOf(
                                                            Color(0xFF4A148C),
                                                            Color(0xFF7B1FA2)
                                                        )
                                                    ),
                                                    shape = RoundedCornerShape(16.dp)
                                                )
                                                .border(
                                                    width = 2.dp,
                                                    brush = Brush.linearGradient(
                                                        colors = listOf(
                                                            Color(0xFFCE93D8),
                                                            Color(0xFF4A148C)
                                                        )
                                                    ),
                                                    shape = RoundedCornerShape(16.dp)
                                                )
                                        ) {
                                            Text(
                                                text = displayedRelation,
                                                style = TextStyle(
                                                    fontSize = 18.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                ),
                                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Swipe direction indicators with cosmic design
                        androidx.compose.animation.AnimatedVisibility(
                            visible = swipeDirection == -1,
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 8.dp),
                            enter = fadeIn() + scaleIn(),
                            exit = fadeOut() + scaleOut()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .shadow(12.dp, CircleShape)
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                Color(0xFFD32F2F),
                                                Color(0xFFB71C1C)
                                            )
                                        ),
                                        shape = CircleShape
                                    )
                                    .border(
                                        width = 2.dp,
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                Color(0xFFEF9A9A),
                                                Color(0xFFB71C1C)
                                            )
                                        ),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                // Add a glow effect
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawCircle(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                Color(0x40EF9A9A),
                                                Color.Transparent
                                            )
                                        ),
                                        radius = size.width * 0.8f,
                                        center = Offset(size.width / 2, size.height / 2)
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Antonym",
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }

                        androidx.compose.animation.AnimatedVisibility(
                            visible = swipeDirection == 1,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 8.dp),
                            enter = fadeIn() + scaleIn(),
                            exit = fadeOut() + scaleOut()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .shadow(12.dp, CircleShape)
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                Color(0xFF2E7D32),
                                                Color(0xFF1B5E20)
                                            )
                                        ),
                                        shape = CircleShape
                                    )
                                    .border(
                                        width = 2.dp,
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                Color(0xFFA5D6A7),
                                                Color(0xFF1B5E20)
                                            )
                                        ),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                // Add a glow effect
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawCircle(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                Color(0x40A5D6A7),
                                                Color.Transparent
                                            )
                                        ),
                                        radius = size.width * 0.8f,
                                        center = Offset(size.width / 2, size.height / 2)
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Synonym",
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }
                }

                // Feedback overlay with cosmic design
                AnimatedVisibility(
                    visible = showFeedback,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF0D0221).copy(alpha = 0.8f)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Add animated stars in the background
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            repeat(30) {
                                val x = Random.nextFloat() * size.width
                                val y = Random.nextFloat() * size.height
                                val radius = Random.nextFloat() * 3.dp.toPx() + 1.dp.toPx()

                                drawCircle(
                                    color = Color.White.copy(alpha = Random.nextFloat() * 0.7f + 0.3f),
                                    radius = radius,
                                    center = Offset(x, y)
                                )
                            }
                        }

                        // Feedback card with cosmic design
                        Card(
                            modifier = Modifier
                                .size(200.dp)
                                .shadow(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                if (feedbackCorrect) Color(0xFF2E7D32) else Color(0xFFD32F2F),
                                                if (feedbackCorrect) Color(0xFF1B5E20) else Color(0xFFB71C1C)
                                            )
                                        )
                                    )
                                    .border(
                                        width = 3.dp,
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                if (feedbackCorrect) Color(0xFFA5D6A7) else Color(0xFFEF9A9A),
                                                if (feedbackCorrect) Color(0xFF1B5E20) else Color(0xFFB71C1C)
                                            )
                                        ),
                                        shape = RoundedCornerShape(24.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                // Add a glow effect
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawCircle(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                if (feedbackCorrect) Color(0x40A5D6A7) else Color(0x40EF9A9A),
                                                Color.Transparent
                                            )
                                        ),
                                        radius = size.width * 0.8f,
                                        center = Offset(size.width / 2, size.height / 2)
                                    )
                                }

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = if (feedbackCorrect) Icons.Default.Check else Icons.Default.Close,
                                        contentDescription = if (feedbackCorrect) "Correct" else "Incorrect",
                                        modifier = Modifier.size(80.dp),
                                        tint = Color.White
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        text = if (feedbackCorrect) "Correct!" else "Wrong!",
                                        style = TextStyle(
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Game over screen with cosmic design
                GameOverScreenWithCurrency(
                    score = finalScore,
                    currencyEarned = currencyEarned,
                    onPlayAgain = {
                        showGameOver = false
                        lives = 3
                        score = 0
                        timeLeft = 60
                        currentWordIndex = 0
                        isGameActive = true
                    },
                    onBackToGames = {
                        navController.navigate("game_selection") {
                            popUpTo("game_selection") { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun GameStartScreen(onStartClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition()

    // Pulsing animation for the start button
    val pulseAnim = infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Floating animation for the logo
    val floatAnim = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Rotating animation for the stars
    val rotateAnim = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Game logo/icon with cosmic design
        Box(
            modifier = Modifier
                .size(180.dp)
                .padding(bottom = 24.dp)
                .offset(y = (sin(floatAnim.value * Math.PI.toFloat()) * 10).dp),
            contentAlignment = Alignment.Center
        ) {
            // Rotating stars background
            Canvas(
                modifier = Modifier
                    .size(180.dp)
                    .graphicsLayer {
                        rotationZ = rotateAnim.value
                    }
            ) {
                val centerX = size.width / 2
                val centerY = size.height / 2
                val radius = size.width / 2

                // Draw star points
                for (i in 0 until 12) {
                    val angle = Math.PI * 2 * i / 12
                    val x = centerX + (radius * 0.9f * cos(angle)).toFloat()
                    val y = centerY + (radius * 0.9f * sin(angle)).toFloat()

                    drawLine(
                        color = Color(0xFFCE93D8),
                        start = Offset(centerX, centerY),
                        end = Offset(x, y),
                        strokeWidth = 2.dp.toPx(),
                        alpha = 0.5f
                    )
                }
            }

            // Main logo circle
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .shadow(16.dp, CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF9C27B0),
                                Color(0xFF7B1FA2),
                                Color(0xFF4A148C)
                            )
                        ),
                        shape = CircleShape
                    )
                    .border(
                        width = 3.dp,
                        brush = Brush.sweepGradient(
                            listOf(
                                Color(0xFFCE93D8),
                                Color(0xFF9C27B0),
                                Color(0xFF7B1FA2),
                                Color(0xFFCE93D8)
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Add a glow effect
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0x60CE93D8),
                                Color.Transparent
                            )
                        ),
                        radius = size.width * 0.6f,
                        center = Offset(size.width / 2, size.height / 2)
                    )
                }

                // Swipe icon
                Icon(
                    imageVector = Icons.Default.SwipeLeft,
                    contentDescription = "Swipe Game",
                    tint = Color.White,
                    modifier = Modifier.size(80.dp)
                )
            }
        }

        // Game title with cosmic design
        Text(
            text = "Word Swipe",
            style = TextStyle(
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                shadow = Shadow(
                    color = Color(0xFF9C27B0),
                    offset = Offset(2f, 2f),
                    blurRadius = 4f
                )
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Game description
        Text(
            text = "Swipe right for synonyms, left for antonyms. Test your vocabulary skills!",
            style = TextStyle(
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Start button with cosmic design
        Button(
            onClick = onStartClick,
            modifier = Modifier
                .scale(pulseAnim.value)
                .fillMaxWidth()
                .height(60.dp)
                .shadow(16.dp, RoundedCornerShape(30.dp)),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF7B1FA2)
            ),
            shape = RoundedCornerShape(30.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Add a subtle glow effect
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0x30CE93D8),
                                Color.Transparent
                            )
                        ),
                        radius = size.width * 0.5f,
                        center = Offset(size.width / 2, size.height / 2)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Start Game",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // How to play section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
                .shadow(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF4A148C).copy(alpha = 0.8f),
                                Color(0xFF7B1FA2).copy(alpha = 0.8f)
                            )
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(
                        width = 2.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFCE93D8),
                                Color(0xFF4A148C)
                            )
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "How to Play",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "How to Play",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = Color.White.copy(alpha = 0.3f)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwipeRight,
                            contentDescription = "Swipe Right",
                            tint = Color(0xFFA5D6A7),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Swipe RIGHT for SYNONYMS",
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwipeLeft,
                            contentDescription = "Swipe Left",
                            tint = Color(0xFFEF9A9A),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Swipe LEFT for ANTONYMS",
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Timer",
                            tint = Color(0xFFFFD54F),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "You have 60 seconds to score as high as possible",
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }
    }
}

