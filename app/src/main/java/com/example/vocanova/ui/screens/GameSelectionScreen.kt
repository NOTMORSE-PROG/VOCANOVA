package com.example.vocanova.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.SwipeRight
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.vocanova.ui.theme.VocaBlue
import com.example.vocanova.ui.theme.VocaGreen
import com.example.vocanova.ui.theme.VocaOrange
import com.example.vocanova.ui.theme.VocaPurple

data class Game(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val route: String,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameSelectionScreen(navController: NavController) {
    val games = listOf(
        Game(
            id = "green_light",
            title = "Green Light, Red Light",
            description = "Choose the correct synonym or antonym before time runs out!",
            icon = Icons.Default.DirectionsRun,
            route = "green_light_red_light_game",
            color = VocaGreen
        ),
        Game(
            id = "swipe",
            title = "Swipe Right or Left",
            description = "Swipe right for synonyms, left for antonyms!",
            icon = Icons.Default.SwipeRight,
            route = "swipe_game",
            color = VocaOrange
        ),
        Game(
            id = "flying_words",
            title = "Flying Words Challenge",
            description = "Slash the correct words as they fly across the screen!",
            icon = Icons.Default.Flight,
            route = "flying_words_game",
            color = VocaPurple
        )
    )

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1A1A2E),
            Color(0xFF16213E)
        )
    )

    val dotPattern = Brush.radialGradient(
        colors = listOf(Color.White.copy(alpha = 0.05f), Color.Transparent),
        center = Offset.Zero,
        radius = 20f,
        tileMode = TileMode.Repeated
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Mini Games",
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
                    containerColor = Color(0xFF0F3460)
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient)
        ) {
            // Decorative dot pattern
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(dotPattern)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(24.dp)
                            .background(
                                Brush.verticalGradient(
                                    listOf(VocaOrange, VocaPurple)
                                ),
                                RoundedCornerShape(4.dp)
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Choose a Game",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(games) { game ->
                        GameCard(game = game) {
                            navController.navigate(game.route)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GameCard(game: Game, onClick: () -> Unit) {
    val gradientColors = when (game.id) {
        "green_light" -> listOf(VocaGreen, VocaGreen.copy(alpha = 0.6f))
        "swipe" -> listOf(VocaOrange, VocaOrange.copy(alpha = 0.6f))
        else -> listOf(VocaPurple, VocaPurple.copy(alpha = 0.6f))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = gradientColors.first()
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = gradientColors.map { it.copy(alpha = 0.8f) }
                    )
                )
        ) {
            // Decorative circles
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 20.dp, y = (-20).dp)
                    .background(
                        Color.White.copy(alpha = 0.1f),
                        CircleShape
                    )
            )

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.BottomStart)
                    .offset(x = (-10).dp, y = 10.dp)
                    .background(
                        Color.White.copy(alpha = 0.1f),
                        CircleShape
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.3f),
                                    Color.White.copy(alpha = 0.1f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = game.icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = game.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = game.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
