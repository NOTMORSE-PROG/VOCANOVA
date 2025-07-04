package com.example.vocanova.ui.screens

import android.R.attr.radius
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.vocanova.ui.viewmodels.DailyWordViewModel
import com.example.vocanova.ui.viewmodels.UserViewModel
import kotlin.math.cos
import kotlin.math.sin
import androidx.core.graphics.withSave

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    dailyWordViewModel: DailyWordViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel()
) {
    val dailyWord by dailyWordViewModel.dailyWord.collectAsState()
    val userCurrency by userViewModel.userCurrency.collectAsState()
    var isWordSaved by remember { mutableStateOf(false) }

    // Load daily word and user data when the screen is shown
    LaunchedEffect(Unit) {
        dailyWordViewModel.refreshDailyWord()
        userViewModel.refreshUserData()
    }

    // Update saved state when daily word changes
    LaunchedEffect(dailyWord) {
        dailyWord?.let { word ->
            isWordSaved = word.isSaved
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("VocaNova", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
                    // Currency display
                    Row(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AttachMoney,
                            contentDescription = "Currency",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$userCurrency",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Profile icon
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { navController.navigate("profile") },
                        tint = Color.White
                    )
                }
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
                            Color(0xFFE1F5FE), // Light blue sky color
                            Color(0xFFB3E5FC)  // Slightly darker blue at bottom
                        )
                    )
                )
        ) {
            // Cloud and educational elements background
            val primaryLineColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Draw clouds
                drawClouds(canvasWidth, canvasHeight)

                // Draw floating alphabet letters
                drawAlphabetElements(canvasWidth, canvasHeight)
            }

            // Use LazyVerticalGrid for the entire content to avoid nested scrollables
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Daily Word Card (spans full width)
                item(span = { GridItemSpan(2) }) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(8.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Decorative background elements
                            Canvas(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                            ) {
                                val canvasWidth = size.width
                                val canvasHeight = size.height

                                // Draw decorative circles
                                val circleColor = Color(0x0A6200EA)
                                for (i in 0..5) {
                                    val x = canvasWidth * (0.2f + i * 0.15f)
                                    val y = canvasHeight * (0.3f + (i % 3) * 0.2f)
                                    val radius = (10 + (i % 3) * 15).dp.toPx()
                                    drawCircle(
                                        color = circleColor,
                                        radius = radius,
                                        center = Offset(x, y)
                                    )
                                }

                                // Draw decorative lines
                                val lineColor = Color(0x0A6200EA)
                                val lineWidth = 1.dp.toPx()

                                for (i in 0..5) {
                                    val y = canvasHeight * (0.2f + i * 0.15f)
                                    drawLine(
                                        color = lineColor,
                                        start = Offset(0f, y),
                                        end = Offset(canvasWidth * 0.3f, y),
                                        strokeWidth = lineWidth
                                    )
                                }

                                for (i in 0..5) {
                                    val x = canvasWidth * (0.7f + i * 0.05f)
                                    drawLine(
                                        color = lineColor,
                                        start = Offset(x, 0f),
                                        end = Offset(x, canvasHeight * 0.5f),
                                        strokeWidth = lineWidth
                                    )
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Title with decorative element
                                    Box(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = "Word of the Day",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )

                                        Canvas(
                                            modifier = Modifier
                                                .padding(top = 28.dp)
                                                .width(100.dp)
                                                .height(8.dp)
                                        ) {
                                            drawLine(
                                                color = primaryLineColor,
                                                start = Offset(0f, 0f),
                                                end = Offset(size.width, 0f),
                                                strokeWidth = 8.dp.toPx(),
                                                cap = StrokeCap.Round
                                            )
                                        }
                                    }

                                    // Decorative star
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.radialGradient(
                                                    colors = listOf(
                                                        if (isWordSaved) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                                        else Color.Gray.copy(alpha = 0.1f),
                                                        if (isWordSaved) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                                        else Color.Gray.copy(alpha = 0.05f)
                                                    )
                                                )
                                            )
                                            .clickable {
                                                dailyWord?.let { word ->
                                                    if (isWordSaved) {
                                                        // Remove from saved words
                                                        dailyWordViewModel.removeWord(word.id)
                                                    } else {
                                                        // Save the word
                                                        dailyWordViewModel.saveWord(word.id)
                                                    }
                                                    isWordSaved = !isWordSaved
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (isWordSaved) Icons.Filled.Star else Icons.Outlined.Star,
                                            contentDescription = if (isWordSaved) "Remove from saved words" else "Save word",
                                            tint = if (isWordSaved) MaterialTheme.colorScheme.primary else Color.Gray,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // No isLoading, just check if dailyWord is null
                                if (dailyWord == null) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(80.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Loading...",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.Gray
                                        )
                                    }
                                } else {
                                    Column {
                                        Text(
                                            text = dailyWord?.text ?: "",
                                            style = MaterialTheme.typography.headlineMedium,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Text(
                                            text = dailyWord?.partOfSpeech ?: "",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.Gray
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            text = dailyWord?.meaning?.take(100)?.plus("...") ?: "",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = { navController.navigate("daily_word") },
                                    modifier = Modifier.align(Alignment.End),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Learn More")
                                }
                            }
                        }
                    }
                }

                // Menu title (spans full width)
                item(span = { GridItemSpan(2) }) {
                    Text(
                        text = "Menu",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }

                // Menu items
                val menuItems = listOf(
                    MenuItem(
                        title = "Lessons",
                        icon = Icons.Default.School,
                        color = Color(0xFF1976D2),
                        route = "lessons"
                    ),
                    MenuItem(
                        title = "Quizzes",
                        icon = Icons.Default.Quiz,
                        color = Color(0xFF388E3C),
                        route = "quiz_selection"
                    ),
                    MenuItem(
                        title = "Games",
                        icon = Icons.Default.Gamepad,
                        color = Color(0xFFE64A19),
                        route = "game_selection"
                    ),
                    MenuItem(
                        title = "Saved Words",
                        icon = Icons.Default.Visibility,
                        color = Color(0xFF7B1FA2),
                        route = "review_words"
                    ),
                    MenuItem(
                        title = "Shop",
                        icon = Icons.Default.ShoppingCart,
                        color = Color(0xFFD81B60),
                        route = "shop"
                    ),
                    MenuItem(
                        title = "Achievements",
                        icon = Icons.Default.Celebration,
                        color = Color(0xFF8E24AA),
                        route = "achievements"
                    ),
                    MenuItem(
                        title = "About",
                        icon = Icons.Default.Info,
                        color = Color(0xFF0097A7),
                        route = "about"
                    )
                )

                items(menuItems) { menuItem ->
                    MenuItemCard(menuItem = menuItem, onClick = {
                        navController.navigate(menuItem.route)
                    })
                }
            }
        }
    }
}

private fun DrawScope.drawClouds(canvasWidth: Float, canvasHeight: Float) {
    val cloudColor = Color.White
    val cloudShadow = Color.White.copy(alpha = 0.7f)

    // Cloud 1
    drawCloud(
        centerX = canvasWidth * 0.2f,
        centerY = canvasHeight * 0.15f,
        scale = 0.8f,
        color = cloudColor
    )

    // Cloud 2
    drawCloud(
        centerX = canvasWidth * 0.7f,
        centerY = canvasHeight * 0.1f,
        scale = 1.2f,
        color = cloudColor
    )

    // Cloud 3
    drawCloud(
        centerX = canvasWidth * 0.5f,
        centerY = canvasHeight * 0.25f,
        scale = 0.9f,
        color = cloudShadow
    )

    // Cloud 4
    drawCloud(
        centerX = canvasWidth * 0.85f,
        centerY = canvasHeight * 0.2f,
        scale = 0.7f,
        color = cloudShadow
    )
}

private fun DrawScope.drawCloud(centerX: Float, centerY: Float, scale: Float, color: Color) {
    val baseRadius = 30.dp.toPx() * scale

    // Main cloud body
    drawCircle(
        color = color,
        radius = baseRadius,
        center = Offset(centerX, centerY)
    )

    // Cloud puffs
    drawCircle(
        color = color,
        radius = baseRadius * 0.8f,
        center = Offset(centerX - baseRadius * 0.7f, centerY)
    )

    drawCircle(
        color = color,
        radius = baseRadius * 0.7f,
        center = Offset(centerX + baseRadius * 0.7f, centerY)
    )

    drawCircle(
        color = color,
        radius = baseRadius * 0.6f,
        center = Offset(centerX - baseRadius * 0.3f, centerY - baseRadius * 0.5f)
    )

    drawCircle(
        color = color,
        radius = baseRadius * 0.6f,
        center = Offset(centerX + baseRadius * 0.4f, centerY - baseRadius * 0.4f)
    )
}

private fun DrawScope.drawAlphabetElements(canvasWidth: Float, canvasHeight: Float) {
    val alphabetColors = listOf(
        Color(0xFFE57373), // Red
        Color(0xFF81C784), // Green
        Color(0xFF64B5F6), // Blue
        Color(0xFFFFD54F), // Yellow
        Color(0xFFBA68C8), // Purple
        Color(0xFF4FC3F7), // Light Blue
        Color(0xFFFFB74D)  // Orange
    )

    // Draw floating alphabet balloons
    drawAlphabetBalloon(
        letter = "A",
        x = canvasWidth * 0.3f,
        y = canvasHeight * 0.2f,
        color = alphabetColors[1],
        size = 40.dp.toPx()
    )

    drawAlphabetBalloon(
        letter = "B",
        x = canvasWidth * 0.7f,
        y = canvasHeight * 0.15f,
        color = alphabetColors[3],
        size = 45.dp.toPx()
    )

    drawAlphabetBalloon(
        letter = "V",
        x = canvasWidth * 0.5f,
        y = canvasHeight * 0.3f,
        color = alphabetColors[0],
        size = 35.dp.toPx()
    )

    // Draw scattered alphabet letters
    val letters = listOf("a", "b", "c", "d", "e", "f", "g", "n", "o", "v")
    val positions = listOf(
        Pair(0.1f, 0.8f),
        Pair(0.2f, 0.85f),
        Pair(0.3f, 0.75f),
        Pair(0.4f, 0.9f),
        Pair(0.5f, 0.8f),
        Pair(0.6f, 0.85f),
        Pair(0.7f, 0.75f),
        Pair(0.8f, 0.9f),
        Pair(0.9f, 0.8f),
        Pair(0.25f, 0.7f)
    )

    for (i in letters.indices) {
        drawScatteredLetter(
            letter = letters[i],
            x = canvasWidth * positions[i].first,
            y = canvasHeight * positions[i].second,
            color = alphabetColors[i % alphabetColors.size],
            size = (20 + (i % 3) * 5).dp.toPx(),
            rotation = (i * 30) % 360
        )
    }
}

private fun DrawScope.drawAlphabetBalloon(letter: String, x: Float, y: Float, color: Color, size: Float) {
    // Draw balloon
    drawCircle(
        color = color,
        radius = size / 2,
        center = Offset(x, y)
    )

    // Draw highlight on balloon
    drawCircle(
        color = Color.White.copy(alpha = 0.3f),
        radius = size / 6,
        center = Offset(x - size / 5, y - size / 5)
    )

    // Draw string
    val path = Path().apply {
        moveTo(x, y + size / 2)
        quadraticTo(x + size / 4, y + size, x, y + size * 1.5f)
    }

    drawPath(
        path = path,
        color = Color.Gray.copy(alpha = 0.6f),
        style = Stroke(width = 2.dp.toPx())
    )

    // Draw letter
    drawContext.canvas.nativeCanvas.apply {
        val paint = android.graphics.Paint().apply {
            textSize = size * 0.7f
            isFakeBoldText = true
            this.color = Color.White.toArgb()
            textAlign = android.graphics.Paint.Align.CENTER
        }
        drawText(
            letter,
            x,
            y + size / 5,
            paint
        )
    }
}

private fun DrawScope.drawScatteredLetter(letter: String, x: Float, y: Float, color: Color, size: Float, rotation: Int) {
    drawContext.canvas.nativeCanvas.apply {
        withSave {
            translate(x, y)
            rotate(rotation.toFloat())

            val paint = android.graphics.Paint().apply {
                textSize = size
                this.color = color.toArgb()
                textAlign = android.graphics.Paint.Align.CENTER
                isFakeBoldText = true
            }

            drawText(
                letter,
                0f,
                size / 3,
                paint
            )
        }
    }
}

@Composable
fun MenuItemCard(menuItem: MenuItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Decorative background elements
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Draw decorative circles
                val circleColor = menuItem.color.copy(alpha = 0.05f)
                for (i in 0..3) {
                    val radius = (20 + i * 15).dp.toPx()
                    drawCircle(
                        color = circleColor,
                        radius = radius,
                        center = Offset(canvasWidth * 0.5f, canvasHeight * 0.5f)
                    )
                }

                // Draw decorative lines
                val lineColor = menuItem.color.copy(alpha = 0.1f)
                val lineWidth = 1.dp.toPx()

                // Draw a few random lines
                for (i in 0..5) {
                    val angle1 = (i * 60) * (Math.PI / 180)
                    val angle2 = ((i * 60) + 30) * (Math.PI / 180)

                    val x1 = canvasWidth * 0.5f + (radius * 0.8f * cos(angle1)).toFloat()
                    val y1 = canvasHeight * 0.5f + (radius * 0.8f * sin(angle1)).toFloat()

                    val x2 = canvasWidth * 0.5f + (radius * 0.8f * cos(angle2)).toFloat()
                    val y2 = canvasHeight * 0.5f + (radius * 0.8f * sin(angle2)).toFloat()

                    drawLine(
                        color = lineColor,
                        start = Offset(x1, y1),
                        end = Offset(x2, y2),
                        strokeWidth = lineWidth
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Icon container with background
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    menuItem.color.copy(alpha = 0.2f),
                                    menuItem.color.copy(alpha = 0.05f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = menuItem.icon,
                        contentDescription = menuItem.title,
                        tint = menuItem.color,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = menuItem.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )
            }
        }
    }
}

data class MenuItem(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val route: String
)
