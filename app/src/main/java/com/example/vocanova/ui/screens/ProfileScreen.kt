package com.example.vocanova.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.vocanova.data.model.Achievement
import com.example.vocanova.ui.theme.VocaBlue
import com.example.vocanova.ui.theme.VocaGreen
import com.example.vocanova.ui.theme.VocaOrange
import com.example.vocanova.ui.theme.VocaPurple
import com.example.vocanova.ui.viewmodels.AchievementViewModel
import com.example.vocanova.ui.viewmodels.ProfileViewModel
import kotlinx.coroutines.delay
import kotlin.math.sin
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel(),
    achievementViewModel: AchievementViewModel = hiltViewModel()
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val currency by viewModel.currency.collectAsState()
    val achievements by achievementViewModel.achievements.collectAsState()
    val unlockedAchievements = achievements.filter { it.isUnlocked }

    val scrollState = rememberScrollState()
    var isLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadUserProfile()
        achievementViewModel.loadAchievements()
        delay(300)
        isLoaded = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Profile",
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
                    containerColor = MaterialTheme.colorScheme.primary
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
            BackgroundDecorations()

            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedVisibility(
                    visible = isLoaded,
                    enter = fadeIn(animationSpec = tween(500)) +
                            slideInVertically(
                                animationSpec = tween(500),
                                initialOffsetY = { it / 2 }
                            )
                ) {
                    // Profile Header Card
                    ProfileHeaderCard(
                        name = userProfile?.name ?: "Loading...",
                        currency = currency
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                AnimatedVisibility(
                    visible = isLoaded,
                    enter = fadeIn(animationSpec = tween(700)) +
                            slideInVertically(
                                animationSpec = tween(700),
                                initialOffsetY = { it / 2 }
                            )
                ) {
                    // User Info Card
                    UserInfoCard(
                        email = userProfile?.email ?: "Loading...",
                        currency = currency
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                AnimatedVisibility(
                    visible = isLoaded,
                    enter = fadeIn(animationSpec = tween(900)) +
                            slideInVertically(
                                animationSpec = tween(900),
                                initialOffsetY = { it / 2 }
                            )
                ) {
                    // Achievements Card
                    AchievementsCard(
                        achievements = unlockedAchievements,
                        totalAchievements = achievements.size,
                        navController = navController
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                AnimatedVisibility(
                    visible = isLoaded,
                    enter = fadeIn(animationSpec = tween(1100)) +
                            slideInVertically(
                                animationSpec = tween(1100),
                                initialOffsetY = { it / 2 }
                            )
                ) {
                    // Account Details Card
                    AccountDetailsCard()
                }

                // Bottom spacing
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun BackgroundDecorations() {
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
                        VocaBlue.copy(alpha = 0.1f),
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
                        VocaPurple.copy(alpha = 0.08f),
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
fun ProfileHeaderCard(name: String, currency: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            VocaBlue.copy(alpha = 0.2f),
                            VocaBlue.copy(alpha = 0.05f)
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
                    moveTo(0f, canvasHeight * 0.7f)

                    for (i in 0..canvasWidth.toInt() step 20) {
                        val x = i.toFloat()
                        val y = canvasHeight * 0.7f + sin(x * 0.03f) * 15
                        lineTo(x, y)
                    }

                    lineTo(canvasWidth, canvasHeight)
                    lineTo(0f, canvasHeight)
                    close()
                }

                drawPath(
                    path = path,
                    color = VocaBlue.copy(alpha = 0.1f)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile avatar with glow effect
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .drawBehind {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        VocaBlue.copy(alpha = 0.3f),
                                        VocaBlue.copy(alpha = 0.1f),
                                        Color.Transparent
                                    )
                                ),
                                radius = size.width * 0.6f
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(VocaBlue.copy(alpha = 0.1f))
                            .border(
                                width = 2.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        VocaBlue.copy(alpha = 0.7f),
                                        VocaBlue.copy(alpha = 0.3f)
                                    )
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = VocaBlue,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // User name with animated scale
                val scale by animateFloatAsState(
                    targetValue = 1f,
                    animationSpec = tween(500, easing = FastOutSlowInEasing),
                    label = "nameScale"
                )

                Text(
                    text = name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = VocaBlue,
                    modifier = Modifier.scale(scale)
                )

                // Currency display with icon
                Row(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(VocaOrange.copy(alpha = 0.1f))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Currency",
                        tint = VocaOrange,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "$currency coins",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = VocaOrange
                    )
                }
            }
        }
    }
}

@Composable
fun UserInfoCard(email: String, currency: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = VocaBlue,
                    modifier = Modifier.size(28.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "User Information",
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
                    )
            )

            // User Email
            InfoRow(
                icon = Icons.Default.Email,
                label = "Email",
                value = email,
                iconTint = VocaGreen
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Currency
            InfoRow(
                icon = Icons.Default.MonetizationOn,
                label = "Current Coins",
                value = currency.toString(),
                iconTint = VocaOrange
            )
        }
    }
}

@Composable
fun AchievementsCard(
    achievements: List<Achievement>,
    totalAchievements: Int,
    navController: NavController
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = VocaOrange,
                        modifier = Modifier.size(28.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Achievements",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = VocaOrange
                    )
                }

                // View All button
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .border(
                            width = 1.dp,
                            color = VocaOrange.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(50)
                        ),
                    color = VocaOrange.copy(alpha = 0.1f),
                    onClick = { navController.navigate("achievements") }
                ) {
                    Text(
                        text = "View All",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = VocaOrange,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
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

            // Achievement progress
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Progress",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = "${achievements.size}/$totalAchievements",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = VocaOrange
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Progress bar
                LinearProgressIndicator(
                    progress = { if (totalAchievements > 0) achievements.size.toFloat() / totalAchievements else 0f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = VocaOrange,
                    trackColor = VocaOrange.copy(alpha = 0.2f)
                )
            }

            // Achievement list
            if (achievements.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = Color.Gray.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "No achievements unlocked yet",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Complete activities to earn achievements",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                Text(
                    text = "Your Unlocked Achievements",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Achievement cards in horizontal scrollable row
                AchievementCarousel(
                    achievements = achievements,
                    navController = navController
                )

                // Show count of remaining achievements if there are more
                if (achievements.size < totalAchievements) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(VocaOrange.copy(alpha = 0.05f))
                                .clickable { navController.navigate("achievements") }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Unlock ${totalAchievements - achievements.size} more achievements",
                                style = MaterialTheme.typography.bodyMedium,
                                color = VocaOrange,
                                fontStyle = FontStyle.Italic
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = VocaOrange,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AchievementCarousel(
    achievements: List<Achievement>,
    navController: NavController
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(achievements) { achievement ->
            AchievementCard(achievement = achievement)
        }

        // "View All" card at the end
        item {
            Card(
                modifier = Modifier
                    .size(width = 140.dp, height = 180.dp)
                    .clickable { navController.navigate("achievements") },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = VocaOrange.copy(alpha = 0.05f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(
                            width = 1.dp,
                            color = VocaOrange.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = VocaOrange,
                            modifier = Modifier.size(32.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "View All",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = VocaOrange
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AchievementCard(achievement: Achievement) {
    Card(
        modifier = Modifier
            .size(width = 140.dp, height = 180.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            VocaOrange.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Achievement icon
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(VocaOrange.copy(alpha = 0.1f))
                        .border(
                            width = 1.dp,
                            color = VocaOrange.copy(alpha = 0.3f),
                            shape = CircleShape
                        )
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = VocaOrange,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Achievement title
                Text(
                    text = achievement.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    modifier = Modifier.height(48.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Achievement reward
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(VocaOrange.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = null,
                        tint = VocaOrange,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "${achievement.rewardAmount}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = VocaOrange
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Status
                Text(
                    text = if (achievement.isClaimed) "Claimed" else "Unlocked",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (achievement.isClaimed) VocaGreen else VocaOrange,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun AccountDetailsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = VocaPurple,
                    modifier = Modifier.size(28.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Account Details",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = VocaPurple
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
                                VocaPurple.copy(alpha = 0.3f),
                                VocaPurple.copy(alpha = 0.1f),
                                VocaPurple.copy(alpha = 0.3f)
                            )
                        )
                    )
            )

            // Account Created
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Account Created",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Text(
                    text = "May 2025",  // Updated to 2025 as requested
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }

            // App Version
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "App Version",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Text(
                    text = "1.0.0",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    iconTint: Color = MaterialTheme.colorScheme.primary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = 0.1f))
                .border(
                    width = 1.dp,
                    color = iconTint.copy(alpha = 0.3f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.size(16.dp))

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
