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
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.vocanova.data.model.Achievement
import com.example.vocanova.ui.viewmodels.AchievementViewModel
import com.example.vocanova.ui.viewmodels.UserViewModel
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementScreen(
    navController: NavController,
    achievementViewModel: AchievementViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel()
) {
    val achievements by achievementViewModel.achievements.collectAsState()
    val isLoading by achievementViewModel.isLoading.collectAsState()
    val errorMessage by achievementViewModel.errorMessage.collectAsState()
    val successMessage by achievementViewModel.successMessage.collectAsState()
    val userCurrency by userViewModel.userCurrency.collectAsState()

    // Group achievements by status
    val groupedAchievements by remember(achievements) {
        derivedStateOf {
            achievements.groupBy {
                when {
                    it.isClaimed -> "Claimed"
                    it.isUnlocked -> "Unlocked"
                    else -> "Locked"
                }
            }
        }
    }

    // Clear messages after delay
    LaunchedEffect(errorMessage, successMessage) {
        if (errorMessage != null || successMessage != null) {
            delay(3000)
            achievementViewModel.clearMessages()
        }
    }

    // Refresh achievements when screen is shown
    LaunchedEffect(Unit) {
        achievementViewModel.loadAchievements()
    }

    // Animation for background elements
    val infiniteTransition = rememberInfiniteTransition(label = "backgroundAnimation")
    val bgAnimation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bgAnimation"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Achievements",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
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
                ),
                modifier = Modifier.shadow(8.dp)
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
                            Color(0xFFF8F9FA),
                            Color(0xFFE9ECEF)
                        )
                    )
                )
        ) {
            // Animated background elements
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Draw animated circles
                val circleCount = 8
                for (i in 0 until circleCount) {
                    val progress = (bgAnimation + i / circleCount.toFloat()) % 1f
                    val radius = 80.dp.toPx() + (i * 20.dp.toPx())
                    val x = canvasWidth / 2 + cos(progress * 2 * Math.PI) * radius
                    val y = canvasHeight / 2 + sin(progress * 2 * Math.PI) * radius

                    drawCircle(
                        color = Color(0xFF0F3460).copy(alpha = 0.03f),
                        radius = 40.dp.toPx() * (1 - progress * 0.5f),
                        center = Offset(x.toFloat(), y.toFloat())
                    )
                }

                // Draw decorative lines
                for (i in 0 until 10) {
                    val startX = i * canvasWidth / 10
                    drawLine(
                        color = Color(0xFF0F3460).copy(alpha = 0.02f),
                        start = Offset(startX, 0f),
                        end = Offset(startX, canvasHeight),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // Draw decorative wave at bottom
                val wavePath = Path()
                val waveHeight = 60.dp.toPx()
                val waveWidth = canvasWidth
                val waveCount = 3

                wavePath.moveTo(0f, canvasHeight * 0.95f)

                for (i in 0..waveCount) {
                    wavePath.cubicTo(
                        waveWidth * (i.toFloat() / waveCount) - waveWidth / (waveCount * 3),
                        canvasHeight * 0.95f - waveHeight,
                        waveWidth * (i.toFloat() / waveCount) + waveWidth / (waveCount * 3),
                        canvasHeight * 0.95f + waveHeight,
                        waveWidth * ((i + 1).toFloat() / waveCount),
                        canvasHeight * 0.95f
                    )
                }

                wavePath.lineTo(canvasWidth, canvasHeight)
                wavePath.lineTo(0f, canvasHeight)
                wavePath.close()

                drawPath(
                    path = wavePath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0F3460).copy(alpha = 0.05f),
                            Color(0xFF0F3460).copy(alpha = 0.1f)
                        )
                    )
                )
            }

            if (isLoading && achievements.isEmpty()) {
                // Loading state with animated loader
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF0F3460),
                            modifier = Modifier.size(48.dp),
                            strokeWidth = 4.dp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Loading achievements...",
                            color = Color(0xFF0F3460),
                            fontSize = 16.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Currency card with animated glow
                    item {
                        CurrencyCard(userCurrency)

                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // Achievement progress summary
                    item {
                        AchievementProgressSummary(achievements)

                        Spacer(modifier = Modifier.height(32.dp))
                    }

                    // Unlocked but not claimed achievements section
                    if (!groupedAchievements["Unlocked"].isNullOrEmpty()) {
                        item {
                            SectionHeader(
                                title = "Ready to Claim",
                                icon = Icons.Default.EmojiEvents,
                                color = Color(0xFF4CAF50)
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        itemsIndexed(groupedAchievements["Unlocked"] ?: emptyList()) { index, achievement ->
                            AchievementItem(
                                achievement = achievement,
                                onClaimReward = { achievementId ->
                                    achievementViewModel.claimAchievementReward(achievementId)
                                },
                                isHighlighted = true,
                                animationDelay = index * 100L
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    // Locked achievements section
                    if (!groupedAchievements["Locked"].isNullOrEmpty()) {
                        item {
                            SectionHeader(
                                title = "Available Achievements",
                                icon = Icons.Default.Star,
                                color = Color(0xFF2196F3)
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        itemsIndexed(groupedAchievements["Locked"] ?: emptyList()) { index, achievement ->
                            AchievementItem(
                                achievement = achievement,
                                onClaimReward = { achievementId ->
                                    achievementViewModel.claimAchievementReward(achievementId)
                                },
                                isHighlighted = false,
                                animationDelay = index * 100L
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    // Claimed achievements section
                    if (!groupedAchievements["Claimed"].isNullOrEmpty()) {
                        item {
                            SectionHeader(
                                title = "Completed Achievements",
                                icon = Icons.Default.CheckCircle,
                                color = Color(0xFF9E9E9E)
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        itemsIndexed(groupedAchievements["Claimed"] ?: emptyList()) { index, achievement ->
                            AchievementItem(
                                achievement = achievement,
                                onClaimReward = { achievementId ->
                                    achievementViewModel.claimAchievementReward(achievementId)
                                },
                                isHighlighted = false,
                                animationDelay = index * 100L
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    // Bottom padding
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                // Show error or success messages with animations
                AnimatedVisibility(
                    visible = errorMessage != null,
                    enter = fadeIn() + slideInVertically { it },
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Card(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFB71C1C).copy(alpha = 0.9f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                Text(
                                    text = errorMessage ?: "",
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }

                AnimatedVisibility(
                    visible = successMessage != null,
                    enter = fadeIn() + slideInVertically { it },
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Card(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF2E7D32).copy(alpha = 0.9f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                Text(
                                    text = successMessage ?: "",
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CurrencyCard(userCurrency: Int) {
    // Animated glow effect
    val infiniteTransition = rememberInfiniteTransition(label = "glowAnimation")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                shadowElevation = 8f
                shape = RoundedCornerShape(20.dp)
            },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    // Draw glow effect
                    drawCircle(
                        color = Color(0xFF4CAF50).copy(alpha = 0.1f * glowAlpha),
                        radius = size.width * 0.8f,
                        center = Offset(size.width / 2, size.height / 2)
                    )
                }
        ) {
            // Decorative elements
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                // Draw dollar symbols in background
                val symbolCount = 10
                for (i in 0 until symbolCount) {
                    val x = (i * size.width / symbolCount) + (20.dp.toPx() * (i % 3))
                    val y = (i % 3) * 20.dp.toPx() + 20.dp.toPx()

                    drawCircle(
                        color = Color(0xFF4CAF50).copy(alpha = 0.05f),
                        radius = 10.dp.toPx(),
                        center = Offset(x, y)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Currency icon with glow
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF4CAF50).copy(alpha = 0.2f * glowAlpha),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachMoney,
                        contentDescription = "Currency",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Currency amount
                Column {
                    Text(
                        text = "Your Balance",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$userCurrency",
                            color = Color.Black,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = " coins",
                            color = Color.Gray,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(start = 4.dp, top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AchievementProgressSummary(achievements: List<Achievement>) {
    val totalAchievements = achievements.size
    val unlockedCount = achievements.count { it.isUnlocked }
    val claimedCount = achievements.count { it.isClaimed }

    val progress = if (totalAchievements > 0) {
        claimedCount.toFloat() / totalAchievements
    } else {
        0f
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Your Progress",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF0F3460)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Progress stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ProgressStat(
                    value = unlockedCount,
                    total = totalAchievements,
                    label = "Unlocked",
                    color = Color(0xFF2196F3)
                )

                ProgressStat(
                    value = claimedCount,
                    total = totalAchievements,
                    label = "Claimed",
                    color = Color(0xFF4CAF50)
                )

                ProgressStat(
                    value = totalAchievements - unlockedCount,
                    total = totalAchievements,
                    label = "Locked",
                    color = Color(0xFF9E9E9E)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Overall progress bar
            Text(
                text = "Overall Completion: ${(progress * 100).toInt()}%",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Color(0xFF4CAF50),
                trackColor = Color(0xFFE0E0E0)
            )
        }
    }
}

@Composable
fun ProgressStat(
    value: Int,
    total: Int,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$value/$total",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = color
        )

        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun SectionHeader(
    title: String,
    icon: ImageVector,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(28.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F3460)
        )

        Spacer(modifier = Modifier.weight(1f))

        Divider(
            modifier = Modifier
                .weight(2f)
                .padding(start = 16.dp),
            color = Color.LightGray.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun AchievementItem(
    achievement: Achievement,
    onClaimReward: (String) -> Unit,
    isHighlighted: Boolean,
    animationDelay: Long = 0
) {
    var isExpanded by remember { mutableStateOf(false) }

    // Entry animation
    var hasAnimated by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(animationDelay)
        hasAnimated = true
    }

    val scale by animateFloatAsState(
        targetValue = if (hasAnimated) 1f else 0.8f,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "scaleAnim"
    )

    val alpha by animateFloatAsState(
        targetValue = if (hasAnimated) 1f else 0f,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "alphaAnim"
    )

    // Pulse animation for highlighted items
    val infiniteTransition = rememberInfiniteTransition(label = "pulseAnimation")
    val highlightAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "highlightAlpha"
    )

    val borderColor = when {
        achievement.isClaimed -> Color(0xFF9E9E9E)
        achievement.isUnlocked -> Color(0xFF4CAF50)
        else -> Color(0xFF2196F3)
    }

    val cardColor = when {
        achievement.isClaimed -> Color.White
        achievement.isUnlocked -> Color.White
        else -> Color.White
    }

    val statusIcon = when {
        achievement.isClaimed -> Icons.Default.CheckCircle
        achievement.isUnlocked -> Icons.Default.Star
        else -> Icons.Default.Lock
    }

    val statusColor = when {
        achievement.isClaimed -> Color(0xFF9E9E9E)
        achievement.isUnlocked -> Color(0xFF4CAF50)
        else -> Color(0xFF2196F3)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alpha)
            .scale(scale)
            .clickable { isExpanded = !isExpanded }
            .then(
                if (isHighlighted && achievement.isUnlocked && !achievement.isClaimed) {
                    Modifier.border(
                        width = 2.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF4CAF50).copy(alpha = highlightAlpha),
                                Color(0xFF8BC34A).copy(alpha = highlightAlpha * 0.7f)
                            )
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                } else {
                    Modifier
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isHighlighted) 4.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Achievement icon with status indicator
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    statusColor.copy(alpha = 0.15f),
                                    statusColor.copy(alpha = 0.05f)
                                )
                            )
                        )
                        .border(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    statusColor,
                                    statusColor.copy(alpha = 0.5f)
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = statusIcon,
                        contentDescription = "Achievement Status",
                        tint = statusColor,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Achievement details
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = achievement.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF0F3460)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = achievement.description,
                        color = Color.Gray,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Status text with color
                    Text(
                        text = when {
                            achievement.isClaimed -> "Completed • Reward Claimed"
                            achievement.isUnlocked -> "Completed • Reward Available"
                            else -> "Not completed yet"
                        },
                        color = statusColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Claim button with animation
                Button(
                    onClick = { onClaimReward(achievement.id) },
                    enabled = achievement.isUnlocked && !achievement.isClaimed,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (achievement.isUnlocked && !achievement.isClaimed) {
                            Color(0xFF4CAF50)
                        } else {
                            Color.Gray.copy(alpha = 0.3f)
                        },
                        disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .then(
                            if (achievement.isUnlocked && !achievement.isClaimed) {
                                Modifier.graphicsLayer {
                                    shadowElevation = 8f * highlightAlpha
                                }
                            } else {
                                Modifier
                            }
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachMoney,
                        contentDescription = null,
                        tint = Color.White
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "${achievement.rewardAmount}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Expanded details
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Divider(color = Color.LightGray.copy(alpha = 0.5f))

                    Spacer(modifier = Modifier.height(16.dp))

                    // Achievement requirements
                    Text(
                        text = "Requirements:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF0F3460)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Quiz requirement with icon
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFF2196F3),
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Get a perfect score on ${
                                when (achievement.quizId) {
                                    "week1" -> "Week 1: Understanding Basic Word Relationships"
                                    "week2" -> "Week 2: Expanding Vocabulary"
                                    "week3" -> "Week 3: Context Clues"
                                    "week4" -> "Week 4: Understanding Word Nuances"
                                    "part2" -> "Part 2: Advanced Vocabulary"
                                    else -> "the quiz"
                                }
                            }",
                            color = Color.DarkGray,
                            fontSize = 14.sp
                        )
                    }

                    if (!achievement.isUnlocked) {
                        Spacer(modifier = Modifier.height(16.dp))

                        // Hint for locked achievements
                        Surface(
                            color = Color(0xFF2196F3).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFF2196F3),
                                    modifier = Modifier.size(20.dp)
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(
                                    text = "Complete the quiz with a perfect score to unlock this achievement!",
                                    color = Color(0xFF2196F3),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    if (achievement.isUnlocked && !achievement.isClaimed) {
                        Spacer(modifier = Modifier.height(16.dp))

                        // Call-to-action for unclaimed achievements
                        Surface(
                            color = Color(0xFF4CAF50).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AttachMoney,
                                    contentDescription = null,
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(20.dp)
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(
                                    text = "You've unlocked this achievement! Claim your ${achievement.rewardAmount} coins reward now!",
                                    color = Color(0xFF4CAF50),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
