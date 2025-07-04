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
import androidx.compose.material.icons.filled.ArrowForward
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.vocanova.ui.theme.VocaBlue
import com.example.vocanova.ui.theme.VocaGreen
import com.example.vocanova.ui.theme.VocaPurple

data class Lesson(
    val id: Int,
    val title: String,
    val shortTitle: String,
    val description: String,
    val quizId: String,
    val keyPoints: List<String>,
    val examples: List<Pair<String, String>> = emptyList(),
    val opposites: List<Pair<String, String>> = emptyList(),
    val weakWords: List<Triple<String, String, List<String>>>? = null,
    val benefits: List<String>? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonScreen(navController: NavController) {
    val lessons = listOf(
        Lesson(
            id = 1,
            title = "Understanding Basic Word Relationships",
            shortTitle = "Word Relationships",
            description = "Learn the basics of synonyms and antonyms",
            quizId = "week1",
            keyPoints = listOf()
        ),
        Lesson(
            id = 2,
            title = "Expanding Vocabulary: Using Simple Synonym Replacements",
            shortTitle = "Expanding Vocabulary",
            description = "Learn about common weak words and strong word choices",
            quizId = "week2",
            keyPoints = listOf()
        ),
        Lesson(
            id = 3,
            title = "Context Clues in Antonyms and Synonyms",
            shortTitle = "Context Clues",
            description = "Learn about context clues and how to apply them",
            quizId = "week3",
            keyPoints = listOf()
        ),
        Lesson(
            id = 4,
            title = "Understanding Word Nuances",
            shortTitle = "Word Nuances",
            description = "Learn about subtle differences in meaning between similar words",
            quizId = "week4",
            keyPoints = listOf()
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
                        "Lessons",
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
                                    listOf(VocaBlue, VocaPurple)
                                ),
                                RoundedCornerShape(4.dp)
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Continue Learning",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(lessons) { lesson ->
                        LessonCard(
                            lesson = lesson,
                            onClick = {
                                navController.navigate("lesson/${lesson.id}")
                            },
                            onTakeQuiz = {
                                // Navigate to the quiz for this lesson using the quizId
                                navController.navigate("quiz/${lesson.quizId}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LessonCard(
    lesson: Lesson,
    onClick: () -> Unit,
    onTakeQuiz: () -> Unit
) {
    val gradientColors = when (lesson.id) {
        1 -> listOf(VocaBlue.copy(alpha = 0.8f), VocaBlue.copy(alpha = 0.4f))
        2 -> listOf(VocaGreen.copy(alpha = 0.8f), VocaGreen.copy(alpha = 0.4f))
        3 -> listOf(Color(0xFFFF8C00).copy(alpha = 0.8f), Color(0xFFFF8C00).copy(alpha = 0.4f)) // Orange theme for Week 3
        else -> listOf(VocaPurple.copy(alpha = 0.8f), VocaPurple.copy(alpha = 0.4f))
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
                    Brush.linearGradient(gradientColors)
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
                            imageVector = Icons.Default.PlayArrow,
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
                            text = "Week ${lesson.id}: ${lesson.shortTitle}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = lesson.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onTakeQuiz,
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Take Quiz",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
