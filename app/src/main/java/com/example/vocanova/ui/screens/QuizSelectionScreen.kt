package com.example.vocanova.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.vocanova.ui.theme.VocaBlue
import com.example.vocanova.ui.theme.VocaGreen
import com.example.vocanova.ui.theme.VocaOrange
import com.example.vocanova.ui.theme.VocaPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizSelectionScreen(navController: NavController) {
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
                        "Quiz Selection",
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
                .background(backgroundGradient)
        ) {
            // Decorative dot pattern
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(dotPattern)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(24.dp)
                                .background(
                                    Brush.verticalGradient(
                                        listOf(VocaBlue, VocaPurple)
                                    ),
                                    RoundedCornerShape(4.dp)
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Select a Quiz",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                item {
                    QuizCard(
                        title = "Week 1: Understanding Basic Word Relationships",
                        description = "Learn about synonyms and antonyms and their differences",
                        color = VocaBlue,
                        onClick = { navController.navigate("quiz/week1") }
                    )
                }

                item {
                    QuizCard(
                        title = "Week 2: Expanding Vocabulary",
                        description = "Practice using simple synonym replacements",
                        color = VocaGreen,
                        onClick = { navController.navigate("quiz/week2") }
                    )
                }

                item {
                    QuizCard(
                        title = "Week 3: Context Clues",
                        description = "Learn to identify antonyms and synonyms from context",
                        color = VocaOrange,
                        onClick = { navController.navigate("quiz/week3") }
                    )
                }

                item {
                    QuizCard(
                        title = "Week 4: Understanding Word Nuances",
                        description = "Explore subtle differences in word meanings",
                        color = VocaPurple,
                        onClick = { navController.navigate("quiz/week4") }
                    )
                }

                item {
                    QuizCard(
                        title = "Part 2: Advanced Vocabulary",
                        description = "Test your knowledge with advanced vocabulary questions",
                        color = Color(0xFF5E60CE),
                        onClick = { navController.navigate("quiz/part2") }
                    )
                }
            }
        }
    }
}

@Composable
fun QuizCard(
    title: String,
    description: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = color
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            color.copy(alpha = 0.8f),
                            color.copy(alpha = 0.4f)
                        )
                    )
                )
        ) {
            // Decorative circles
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 30.dp, y = (-30).dp)
                    .background(
                        Color.White.copy(alpha = 0.1f),
                        CircleShape
                    )
            )

            Box(
                modifier = Modifier
                    .size(60.dp)
                    .align(Alignment.BottomStart)
                    .offset(x = (-20).dp, y = 20.dp)
                    .background(
                        Color.White.copy(alpha = 0.1f),
                        CircleShape
                    )
            )

            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.2f),
                                        Color.White.copy(alpha = 0.1f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Quiz,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = description,
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Start Quiz",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Start Quiz",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
