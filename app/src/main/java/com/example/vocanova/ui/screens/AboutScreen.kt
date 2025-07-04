package com.example.vocanova.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.vocanova.R
import com.example.vocanova.ui.theme.VocaBlue
import com.example.vocanova.ui.theme.VocaGreen
import com.example.vocanova.ui.theme.VocaOrange
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavController) {
    var isLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        isLoaded = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "About Voca Nova",
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        // Background with decorative elements
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
                            Color(0xFFF5F7FA)
                        )
                    )
                )
        ) {
            // Decorative background elements
            AboutBackgroundDecorations()

            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // App Logo and Name
                AnimatedVisibility(
                    visible = isLoaded,
                    enter = fadeIn(animationSpec = tween(500)) +
                            slideInVertically(
                                animationSpec = tween(500),
                                initialOffsetY = { it / 2 }
                            )
                ) {
                    AppLogoSection()
                }

                Spacer(modifier = Modifier.height(16.dp))

                // About Card
                AnimatedVisibility(
                    visible = isLoaded,
                    enter = fadeIn(animationSpec = tween(700)) +
                            slideInVertically(
                                animationSpec = tween(700),
                                initialOffsetY = { it / 2 }
                            )
                ) {
                    AboutInfoCard()
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Features Card
                AnimatedVisibility(
                    visible = isLoaded,
                    enter = fadeIn(animationSpec = tween(900)) +
                            slideInVertically(
                                animationSpec = tween(900),
                                initialOffsetY = { it / 2 }
                            )
                ) {
                    FeaturesCard()
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Educational Focus Card
                AnimatedVisibility(
                    visible = isLoaded,
                    enter = fadeIn(animationSpec = tween(1100)) +
                            slideInVertically(
                                animationSpec = tween(1100),
                                initialOffsetY = { it / 2 }
                            )
                ) {
                    EducationalFocusCard()
                }

                // Version Info
                AnimatedVisibility(
                    visible = isLoaded,
                    enter = fadeIn(animationSpec = tween(1300))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Version 1.0.0",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AboutBackgroundDecorations() {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val maxWidth = constraints.maxWidth.toFloat()
        val maxHeight = constraints.maxHeight.toFloat()

        // Animated circles
        val infiniteTransition = rememberInfiniteTransition(label = "bgTransition")
        val scale1 by infiniteTransition.animateFloat(
            initialValue = 0.9f,
            targetValue = 1.1f,
            animationSpec = infiniteRepeatable(
                animation = tween(5000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "scale1"
        )

        val scale2 by infiniteTransition.animateFloat(
            initialValue = 1.1f,
            targetValue = 0.9f,
            animationSpec = infiniteRepeatable(
                animation = tween(7000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "scale2"
        )

        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(40000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotation"
        )

        // Background gradient circles
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    this.rotationZ = rotation
                }
        ) {
            // First circle
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        VocaGreen.copy(alpha = 0.1f),
                        Color.Transparent
                    ),
                    center = Offset(maxWidth * 0.2f, maxHeight * 0.2f),
                    radius = maxWidth * 0.6f * scale1
                ),
                center = Offset(maxWidth * 0.2f, maxHeight * 0.2f),
                radius = maxWidth * 0.6f * scale1
            )

            // Second circle
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        VocaBlue.copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    center = Offset(maxWidth * 0.8f, maxHeight * 0.7f),
                    radius = maxWidth * 0.5f * scale2
                ),
                center = Offset(maxWidth * 0.8f, maxHeight * 0.7f),
                radius = maxWidth * 0.5f * scale2
            )
        }

        // Decorative dots
        for (i in 0 until 20) {
            val x = remember { Random.nextFloat() * maxWidth }
            val y = remember { Random.nextFloat() * maxHeight }
            val size = remember { Random.nextFloat() * 5 + 2 }
            val alpha = remember { Random.nextFloat() * 0.15f + 0.05f }

            val offsetY by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 20f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000 + (i * 500), easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dot$i"
            )

            Box(
                modifier = Modifier
                    .size(size.dp)
                    .offset(x = x.dp, y = (y + offsetY).dp)
                    .alpha(alpha)
                    .background(
                        color = when (i % 3) {
                            0 -> VocaBlue
                            1 -> VocaGreen
                            else -> VocaOrange
                        },
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
fun AppLogoSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo with glow effect
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    VocaBlue.copy(alpha = 0.3f),
                                    VocaBlue.copy(alpha = 0.1f),
                                    Color.Transparent
                                )
                            ),
                            radius = size.width * 0.7f
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.voca_nova_logo),
                    contentDescription = "Voca Nova Logo",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .border(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    VocaBlue.copy(alpha = 0.7f),
                                    VocaBlue.copy(alpha = 0.3f)
                                )
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // App name with animated scale
            val infiniteTransition = rememberInfiniteTransition(label = "nameTransition")
            val scale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.05f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "nameScale"
            )

            Text(
                text = "Voca Nova",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = VocaBlue,
                modifier = Modifier.scale(scale)
            )

            // Tagline with gradient
            Text(
                text = "A new way of learning vocabulary",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun AboutInfoCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            VocaBlue.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    )
                )
        ) {
            // Decorative elements
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Draw decorative waves
                val path = Path().apply {
                    moveTo(0f, canvasHeight * 0.8f)

                    for (i in 0..canvasWidth.toInt() step 20) {
                        val x = i.toFloat()
                        val y = canvasHeight * 0.8f + sin(x * 0.03f) * 10
                        lineTo(x, y)
                    }

                    lineTo(canvasWidth, canvasHeight)
                    lineTo(0f, canvasHeight)
                    close()
                }

                drawPath(
                    path = path,
                    color = VocaBlue.copy(alpha = 0.05f)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(VocaBlue.copy(alpha = 0.1f))
                            .border(
                                width = 1.dp,
                                color = VocaBlue.copy(alpha = 0.3f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = VocaBlue,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "About",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = VocaBlue
                    )
                }

                HorizontalDivider(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    VocaBlue.copy(alpha = 0.3f),
                                    VocaBlue.copy(alpha = 0.1f),
                                    VocaBlue.copy(alpha = 0.3f)
                                )
                            )
                        ),
                )

                Text(
                    text = "Voca Nova is an educational application developed to enhance the vocabulary skills of Grade 7 students, with a particular focus on the use of synonyms and antonyms. The name of the app is derived from the Latin terms \"Voca,\" meaning word or vocabulary, and \"Nova,\" meaning new, which signifies \"A new way of learning vocabulary.\" Featuring an intuitive interface and interactive elements, the app aims to provide an engaging and effective means for students to expand their vocabulary knowledge.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun FeaturesCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            VocaOrange.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    )
                )
        ) {
            // Decorative elements
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Draw decorative waves
                val path = Path().apply {
                    moveTo(canvasWidth, canvasHeight * 0.2f)

                    for (i in canvasWidth.toInt() downTo 0 step 20) {
                        val x = i.toFloat()
                        val y = canvasHeight * 0.2f + cos(x * 0.03f) * 10
                        lineTo(x, y)
                    }

                    lineTo(0f, 0f)
                    lineTo(canvasWidth, 0f)
                    close()
                }

                drawPath(
                    path = path,
                    color = VocaOrange.copy(alpha = 0.05f)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(VocaOrange.copy(alpha = 0.1f))
                            .border(
                                width = 1.dp,
                                color = VocaOrange.copy(alpha = 0.3f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = VocaOrange,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "Key Features",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = VocaOrange
                    )
                }

                HorizontalDivider(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    VocaOrange.copy(alpha = 0.3f),
                                    VocaOrange.copy(alpha = 0.1f),
                                    VocaOrange.copy(alpha = 0.3f)
                                )
                            )
                        )
                )

                FeatureItem(
                    title = "Interactive Learning Games",
                    description = "Fun and engaging games to help students learn vocabulary in an enjoyable way."
                )

                FeatureItem(
                    title = "Daily Word Challenges",
                    description = "New words every day to continuously expand vocabulary knowledge."
                )

                FeatureItem(
                    title = "Quizzes and Assessments",
                    description = "Test knowledge and track progress with regular quizzes."
                )

                FeatureItem(
                    title = "Video Lessons",
                    description = "Visual learning through engaging video content."
                )

                FeatureItem(
                    title = "Achievement System",
                    description = "Earn rewards by completing various learning activities and challenges."
                )
            }
        }
    }
}

@Composable
fun EducationalFocusCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            VocaGreen.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    )
                )
        ) {
            // Decorative elements
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Draw decorative pattern
                for (i in 0 until 5) {
                    val radius = 20f + (i * 15)
                    drawCircle(
                        color = VocaGreen.copy(alpha = 0.03f),
                        radius = radius,
                        center = Offset(canvasWidth * 0.85f, canvasHeight * 0.15f)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(VocaGreen.copy(alpha = 0.1f))
                            .border(
                                width = 1.dp,
                                color = VocaGreen.copy(alpha = 0.3f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = VocaGreen,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "Educational Focus",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = VocaGreen
                    )
                }

                HorizontalDivider(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    VocaGreen.copy(alpha = 0.3f),
                                    VocaGreen.copy(alpha = 0.1f),
                                    VocaGreen.copy(alpha = 0.3f)
                                )
                            )
                        )
                )

                Text(
                    text = "Voca Nova is specifically designed for Grade 7 students to help them master synonyms and antonyms through interactive learning. The app aligns with educational standards and provides a supplementary tool for classroom learning.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Educational goals
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(VocaGreen.copy(alpha = 0.05f))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Educational Goals",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = VocaGreen
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(VocaGreen, CircleShape)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Expand vocabulary through interactive learning",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(VocaGreen, CircleShape)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Master synonyms and antonyms for better expression",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(VocaGreen, CircleShape)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Build confidence in language skills",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FeatureItem(title: String, description: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(VocaOrange.copy(alpha = 0.1f))
                    .border(
                        width = 1.dp,
                        color = VocaOrange.copy(alpha = 0.3f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "•",
                    color = VocaOrange,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 44.dp, top = 4.dp)
        )
    }
}
