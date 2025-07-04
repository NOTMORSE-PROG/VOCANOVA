package com.example.vocanova.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.vocanova.ui.theme.VocaBlue
import com.example.vocanova.ui.theme.VocaGreen
import com.example.vocanova.ui.theme.VocaRed
import com.example.vocanova.ui.viewmodels.DailyWordViewModel
import com.example.vocanova.utils.AudioUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyWordScreen(
    navController: NavController,
    viewModel: DailyWordViewModel = hiltViewModel()
) {
    val word by viewModel.dailyWord.collectAsState(initial = null)
    var isSaved by remember { mutableStateOf(false) }
    val currentDate = remember {
        SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(Date())
    }

    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val audioUtils = remember { AudioUtils.getInstance(context) }

    // Clean up resources when leaving the screen
    DisposableEffect(Unit) {
        onDispose {
            // No background music to clean up here
        }
    }

    // Define background colors
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF2C3E50),
            Color(0xFF4CA1AF)
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Word of the Day",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
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
                    containerColor = Color(0xFF1E3A5F)
                ),
                modifier = Modifier.shadow(8.dp)
            )
        }
    ) { paddingValues ->
        // Background with decorative elements
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient)
        ) {
            // Decorative circles in background
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Draw decorative circles
                repeat(15) {
                    val x = Random.nextFloat() * canvasWidth
                    val y = Random.nextFloat() * canvasHeight
                    val radius = Random.nextFloat() * 60f + 20f

                    drawCircle(
                        color = Color.White.copy(alpha = 0.05f),
                        radius = radius,
                        center = Offset(x, y)
                    )
                }

                // Draw decorative lines
                repeat(8) {
                    val startX = Random.nextFloat() * canvasWidth
                    val startY = Random.nextFloat() * canvasHeight
                    val endX = startX + Random.nextFloat() * 200f - 100f
                    val endY = startY + Random.nextFloat() * 200f - 100f

                    drawLine(
                        color = Color.White.copy(alpha = 0.03f),
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = Random.nextFloat() * 3f + 1f
                    )
                }

                // Draw decorative wave at bottom
                val wavePath = Path()
                val waveHeight = 100f
                val waveWidth = canvasWidth / 4

                wavePath.moveTo(0f, canvasHeight)

                for (i in 0..4) {
                    wavePath.quadraticTo(
                        waveWidth * (i + 0.5f),
                        canvasHeight - waveHeight,
                        waveWidth * (i + 1),
                        canvasHeight
                    )
                }

                wavePath.lineTo(canvasWidth, canvasHeight)
                wavePath.lineTo(0f, canvasHeight)
                wavePath.close()

                drawPath(
                    path = wavePath,
                    color = Color.White.copy(alpha = 0.07f)
                )
            }

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                word?.let { currentWord ->
                    // Update isSaved based on the word's saved status
                    isSaved = currentWord.isSaved

                    // Date display with enhanced styling
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.15f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = "Today's date",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = currentDate,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Main word card with enhanced styling
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(24.dp),
                                spotColor = Color(0xFF1E3A5F)
                            ),
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Decorative elements
                            Canvas(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            ) {
                                // Top right corner decoration
                                drawCircle(
                                    color = VocaBlue.copy(alpha = 0.05f),
                                    radius = 100f,
                                    center = Offset(size.width - 50f, 50f)
                                )

                                // Bottom left corner decoration
                                drawCircle(
                                    color = VocaGreen.copy(alpha = 0.05f),
                                    radius = 80f,
                                    center = Offset(50f, size.height - 50f)
                                )

                                // Subtle pattern
                                repeat(5) {
                                    val x = Random.nextFloat() * size.width
                                    val y = Random.nextFloat() * size.height
                                    drawCircle(
                                        color = Color.Gray.copy(alpha = 0.02f),
                                        radius = Random.nextFloat() * 30f + 10f,
                                        center = Offset(x, y)
                                    )
                                }
                            }

                            // Content
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Word header with actions
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Word and part of speech
                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Decorative element
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(CircleShape)
                                                    .background(VocaBlue)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))

                                            Text(
                                                text = currentWord.text,
                                                style = MaterialTheme.typography.headlineMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1E3A5F)
                                            )
                                        }

                                        if (currentWord.partOfSpeech.isNotEmpty()) {
                                            Text(
                                                text = currentWord.partOfSpeech,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontStyle = FontStyle.Italic,
                                                color = Color.Gray,
                                                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                                            )
                                        }
                                    }

                                    // Action buttons
                                    Row {
                                        // Pronounce button with enhanced styling
                                        IconButton(
                                            onClick = {
                                                audioUtils.speakWord(currentWord.text)
                                            },
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    Brush.radialGradient(
                                                        colors = listOf(
                                                            VocaBlue.copy(alpha = 0.2f),
                                                            VocaBlue.copy(alpha = 0.1f)
                                                        )
                                                    )
                                                )
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                                contentDescription = "Pronounce",
                                                tint = VocaBlue
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        // Bookmark button with enhanced styling
                                        IconButton(
                                            onClick = {
                                                isSaved = !isSaved
                                                if (isSaved) {
                                                    CoroutineScope(Dispatchers.IO).launch {
                                                        viewModel.saveWord(currentWord.id)
                                                    }
                                                }
                                            },
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    Brush.radialGradient(
                                                        colors = listOf(
                                                            if (isSaved) VocaBlue.copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.1f),
                                                            if (isSaved) VocaBlue.copy(alpha = 0.1f) else Color.Gray.copy(alpha = 0.05f)
                                                        )
                                                    )
                                                )
                                        ) {
                                            Icon(
                                                imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                                contentDescription = "Save word",
                                                tint = if (isSaved) VocaBlue else Color.Gray
                                            )
                                        }
                                    }
                                }

                                // Pronunciation with enhanced styling
                                if (currentWord.pronunciation.isNotEmpty()) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFF5F7FA),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = currentWord.pronunciation,
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.padding(8.dp),
                                            textAlign = TextAlign.Center,
                                            color = Color.Gray
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                // Definition section with enhanced styling
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color(0xFFF8FAFC),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        Brush.radialGradient(
                                                            colors = listOf(
                                                                VocaBlue,
                                                                VocaBlue.copy(alpha = 0.7f)
                                                            )
                                                        )
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "D",
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(8.dp))

                                            Text(
                                                text = "Definition",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1E3A5F)
                                            )
                                        }

                                        Text(
                                            text = currentWord.meaning,
                                            style = MaterialTheme.typography.bodyLarge,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(start = 32.dp),
                                            color = Color(0xFF2C3E50)
                                        )

                                        if (currentWord.example.isNotEmpty()) {
                                            Spacer(modifier = Modifier.height(16.dp))

                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = Color(0xFFECF0F1),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(
                                                    text = "\"${currentWord.example}\"",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontStyle = FontStyle.Italic,
                                                    modifier = Modifier.padding(12.dp),
                                                    color = Color(0xFF34495E)
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                // Synonyms and Antonyms with enhanced styling
                                EnhancedWordSection(
                                    title = "Synonyms",
                                    words = currentWord.synonyms,
                                    color = VocaGreen,
                                    icon = Icons.Default.Star
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                EnhancedWordSection(
                                    title = "Antonyms",
                                    words = currentWord.antonyms,
                                    color = VocaRed,
                                    icon = Icons.Default.Star
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
fun EnhancedWordSection(title: String, words: List<String>, color: Color, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Section header with icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    color,
                                    color.copy(alpha = 0.7f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }

            // Words list with enhanced styling
            if (words.isEmpty()) {
                Text(
                    text = "No $title available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 36.dp, top = 8.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (words.size > 3) 150.dp else (words.size * 50).dp)
                ) {
                    items(words) { word ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Decorative bullet point
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(color)
                            )

                            Spacer(modifier = Modifier.size(12.dp))

                            // Word with card background
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = color.copy(alpha = 0.1f),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text(
                                    text = word,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    color = Color(0xFF2C3E50)
                                )
                            }
                        }

                        if (word != words.last()) {
                            HorizontalDivider(
                                modifier = Modifier.padding(start = 24.dp, end = 16.dp),
                                thickness = 0.5.dp,
                                color = Color.LightGray
                            )
                        }
                    }
                }
            }
        }
    }
}
