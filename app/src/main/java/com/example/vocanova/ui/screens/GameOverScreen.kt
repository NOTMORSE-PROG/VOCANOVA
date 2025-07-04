package com.example.vocanova.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vocanova.data.repository.UserRepository
import com.example.vocanova.ui.theme.VocaGreen
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun GameOverScreenWithCurrency(
    score: Int,
    currencyEarned: Int,
    onPlayAgain: () -> Unit,
    onBackToGames: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Award currency when the game over screen is shown
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val userRepository = UserRepository(FirebaseFirestore.getInstance())
                val userId = userRepository.getCurrentUserId()
                if (userId != null) {
                    val success = userRepository.updateUserCurrency(userId, currencyEarned)
                    if (success) {
                        Log.d("GameOverScreen", "Successfully awarded $currencyEarned currency")
                        Toast.makeText(context, "Earned $currencyEarned coins!", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e("GameOverScreen", "Failed to award currency")
                    }
                }
            } catch (e: Exception) {
                Log.e("GameOverScreen", "Error awarding currency", e)
            }
        }
    }

    var showScore by remember { mutableStateOf(false) }
    var showCurrency by remember { mutableStateOf(false) }
    var showButtons by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(500)
        showScore = true
        delay(1000)
        showCurrency = true
        delay(1000)
        showButtons = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Game Over",
                style = MaterialTheme.typography.headlineLarge.copy(
                    shadow = Shadow(
                        color = Color.Black,
                        offset = Offset(2f, 2f),
                        blurRadius = 4f
                    )
                ),
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedVisibility(
                visible = showScore,
                enter = fadeIn(animationSpec = tween(1000)) +
                        slideInVertically(
                            animationSpec = tween(1000),
                            initialOffsetY = { it / 2 }
                        )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF3D4E81).copy(alpha = 0.8f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Your Score",
                        style = MaterialTheme.typography.titleMedium.copy(
                            shadow = Shadow(
                                color = Color.Black,
                                offset = Offset(1f, 1f),
                                blurRadius = 2f
                            )
                        ),
                        color = Color.White.copy(alpha = 0.9f)
                    )

                    Text(
                        text = "$score",
                        style = MaterialTheme.typography.displayMedium.copy(
                            shadow = Shadow(
                                color = Color.Black,
                                offset = Offset(2f, 2f),
                                blurRadius = 4f
                            )
                        ),
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedVisibility(
                visible = showCurrency,
                enter = fadeIn(animationSpec = tween(1000)) +
                        slideInVertically(
                            animationSpec = tween(1000),
                            initialOffsetY = { it / 2 }
                        )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(VocaGreen.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AttachMoney,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Currency Earned",
                        style = MaterialTheme.typography.titleMedium.copy(
                            shadow = Shadow(
                                color = Color.Black,
                                offset = Offset(1f, 1f),
                                blurRadius = 2f
                            )
                        ),
                        color = Color.White.copy(alpha = 0.9f)
                    )

                    Text(
                        text = "+$currencyEarned",
                        style = MaterialTheme.typography.displayMedium.copy(
                            shadow = Shadow(
                                color = Color.Black,
                                offset = Offset(2f, 2f),
                                blurRadius = 4f
                            )
                        ),
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            AnimatedVisibility(visible = showButtons) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onPlayAgain,
                        modifier = Modifier.fillMaxWidth(0.8f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3D4E81)
                        )
                    ) {
                        Text(
                            text = "Play Again",
                            fontSize = 18.sp,
                            color = Color.White,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onBackToGames,
                        modifier = Modifier.fillMaxWidth(0.8f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF424242)
                        )
                    ) {
                        Text(
                            text = "Back to Games",
                            fontSize = 18.sp,
                            color = Color.White,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}
