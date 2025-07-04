package com.example.vocanova.ui.screens

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.List
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.example.vocanova.ui.components.VideoPlayer
import com.example.vocanova.ui.theme.VocaBlue
import com.example.vocanova.ui.theme.VocaGreen
import com.example.vocanova.ui.theme.VocaOrange
import com.example.vocanova.ui.theme.VocaPurple


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonContentScreen(navController: NavController, lessonId: Int) {
    val context = LocalContext.current
    val activity = context as? Activity

    // Track fullscreen state
    var isFullscreen by remember { mutableStateOf(false) }

    val lessons = remember {
        listOf(
            Lesson(
                id = 1,
                title = "Understanding Word Relationships",
                shortTitle = "Word Relationships",
                description = "Learn about synonyms and antonyms",
                quizId = "week1",
                keyPoints = listOf(
                    "Synonyms are words with similar meanings",
                    "Antonyms are words with opposite meanings",
                    "Word choice improves communication"
                ),
                examples = listOf(
                    Pair("Happy", "Cheerful, joyful, content"),
                    Pair("Small", "Petite, tiny, little")
                ),
                opposites = listOf(
                    Pair("Happy", "Sad, unhappy, miserable"),
                    Pair("Small", "Big, large, huge")
                )
            ),
            Lesson(
                id = 2,
                title = "Expanding Vocabulary",
                shortTitle = "Expanding Vocabulary",
                description = "Learn about weak words and strong alternatives",
                quizId = "week2",
                keyPoints = listOf(
                    "Weak words are vague and overused",
                    "Strong words are specific and descriptive",
                    "Better word choice improves writing"
                ),
                weakWords = listOf(
                    Triple("Good", "too general", listOf("excellent", "outstanding", "superb")),
                    Triple("Bad", "lacks detail", listOf("terrible", "awful", "dreadful")),
                    Triple("Happy", "not specific", listOf("thrilled", "delighted", "ecstatic"))
                ),
                examples = listOf(
                    Pair("She is a good dancer", "She is an impressive dancer"),
                    Pair("The weather was bad", "The weather was terrible")
                )
            ),
            Lesson(
                id = 3,
                title = "Context Clues",
                shortTitle = "Context Clues",
                description = "Learn how to use context to understand words",
                quizId = "week3",
                keyPoints = listOf(
                    "Context clues help understand unfamiliar words",
                    "Synonyms and antonyms provide context",
                    "Context improves reading comprehension"
                ),
                examples = listOf(
                    Pair("The big backpack has a huge tumbler", "big and huge are synonyms"),
                    Pair("The house is valuable, unlike the worthless shed", "valuable and worthless are antonyms")
                ),
                benefits = listOf(
                    "Improved reading comprehension",
                    "Expanded vocabulary",
                    "Better reading fluency"
                )
            ),
            Lesson(
                id = 4,
                title = "Word Nuances",
                shortTitle = "Word Nuances",
                description = "Learn about subtle differences in word meanings",
                quizId = "week4",
                keyPoints = listOf(
                    "Nuances are subtle differences in meaning",
                    "Similar words can have different connotations",
                    "Understanding nuances improves communication"
                ),
                examples = listOf(
                    Pair("Jay looked at Sanjo", "Neutral observation"),
                    Pair("Jay glared at Sanjo", "Suggests anger or intensity")
                ),
                benefits = listOf(
                    "More precise communication",
                    "Better emotional intelligence",
                    "Enhanced writing skills"
                )
            )
        )
    }

    val lesson = lessons.find { it.id == lessonId } ?: lessons[0]

    // Get the appropriate theme color based on lesson ID
    val themeColor = when (lesson.id) {
        1 -> VocaBlue
        2 -> VocaGreen
        3 -> VocaOrange
        4 -> VocaPurple
        else -> MaterialTheme.colorScheme.primary
    }

    // Create video URI based on lesson ID
    val videoFileName = "week${lesson.id}"
    val videoUri = remember {
        "android.resource://${context.packageName}/raw/$videoFileName".toUri()
    }

    // Handle fullscreen changes
    DisposableEffect(isFullscreen) {
        activity?.let {
            if (isFullscreen) {
                // Enter fullscreen
                it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            } else {
                // Exit fullscreen
                it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
        }

        onDispose {
            // Ensure we return to portrait mode when leaving
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    Scaffold(
        topBar = {
            if (!isFullscreen) {
                TopAppBar(
                    title = {
                        Text(
                            "Week ${lesson.id}: ${lesson.shortTitle}",
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
                        containerColor = themeColor
                    )
                )
            }
        }
    ) { paddingValues ->
        if (isFullscreen) {
            // Fullscreen video player
            Box(modifier = Modifier.fillMaxSize()) {
                VideoPlayer(
                    videoId = videoFileName,
                    videoUri = videoUri,
                    onFullscreenToggle = { isFullscreen = it }
                )
            }
        } else {
            // Regular content view with improved visual UI
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Video player section with fullscreen button
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    elevation = CardDefaults.elevatedCardElevation(
                        defaultElevation = 4.dp
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        VideoPlayer(
                            videoId = videoFileName,
                            videoUri = videoUri,
                            onFullscreenToggle = { isFullscreen = it }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title and description card
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = themeColor.copy(alpha = 0.1f)
                    ),
                    elevation = CardDefaults.elevatedCardElevation(
                        defaultElevation = 3.dp
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = lesson.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = themeColor,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = lesson.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Key Points Section
                ExpandableSection(
                    title = "Key Points",
                    icon = Icons.Outlined.Info,
                    themeColor = themeColor
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        lesson.keyPoints.forEachIndexed { index, point ->
                            Row(
                                verticalAlignment = Alignment.Top,
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = themeColor,
                                    modifier = Modifier.size(24.dp)
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(
                                    text = point,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            if (index < lesson.keyPoints.size - 1) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    color = themeColor.copy(alpha = 0.2f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Examples Section
                if (lesson.id == 2) {
                    // Special styling for Week 2 (Weak Words)
                    ExpandableSection(
                        title = "Weak vs. Strong Words",
                        icon = Icons.Outlined.Lightbulb,
                        themeColor = themeColor
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            lesson.weakWords?.forEach { (word, reason, alternatives) ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = themeColor.copy(alpha = 0.1f)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(themeColor.copy(alpha = 0.2f))
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = word,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = themeColor
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(8.dp))

                                            Text(
                                                text = "is weak because it's $reason",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Text(
                                            text = "Strong alternatives:",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontStyle = FontStyle.Italic,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            alternatives.forEach { alternative ->
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(16.dp))
                                                        .background(themeColor)
                                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                                ) {
                                                    Text(
                                                        text = alternative,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Example sentences for Week 2
                    ExpandableSection(
                        title = "Example Sentences",
                        icon = Icons.AutoMirrored.Outlined.List,
                        themeColor = themeColor
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            lesson.examples.forEach { (weak, strong) ->
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp)
                                ) {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = weak,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(12.dp)
                                        )
                                    }

                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowDown,
                                            contentDescription = null,
                                            tint = themeColor,
                                            modifier = Modifier
                                                .padding(vertical = 4.dp)
                                                .size(32.dp)
                                        )
                                    }

                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = themeColor.copy(alpha = 0.2f)
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = strong,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = themeColor,
                                            modifier = Modifier.padding(12.dp)
                                        )
                                    }
                                }

                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    color = themeColor.copy(alpha = 0.2f)
                                )
                            }
                        }
                    }
                } else if (lesson.id == 1) {
                    // Synonyms and Antonyms for Week 1
                    ExpandableSection(
                        title = "Synonyms (Similar Words)",
                        icon = Icons.Outlined.Lightbulb,
                        themeColor = themeColor
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            lesson.examples.forEach { (word, synonyms) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(themeColor)
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = word,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Text(
                                        text = synonyms,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                if (lesson.examples.indexOf(Pair(word, synonyms)) < lesson.examples.size - 1) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        color = themeColor.copy(alpha = 0.2f)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    ExpandableSection(
                        title = "Antonyms (Opposite Words)",
                        icon = Icons.AutoMirrored.Outlined.List,
                        themeColor = themeColor
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            lesson.opposites.forEach { (word, antonyms) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(themeColor)
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = word,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Text(
                                        text = antonyms,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                if (lesson.opposites.indexOf(Pair(word, antonyms)) < lesson.opposites.size - 1) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        color = themeColor.copy(alpha = 0.2f)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Examples for Weeks 3 and 4
                    ExpandableSection(
                        title = "Examples",
                        icon = Icons.Outlined.Lightbulb,
                        themeColor = themeColor
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            lesson.examples.forEach { (example, explanation) ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = themeColor.copy(alpha = 0.1f)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = "\"$example\"",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.Info,
                                                contentDescription = null,
                                                tint = themeColor,
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .padding(end = 4.dp)
                                            )

                                            Text(
                                                text = explanation,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontStyle = FontStyle.Italic,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Benefits Section for Weeks 3 and 4
                if (lesson.id >= 3) {
                    ExpandableSection(
                        title = "Benefits",
                        icon = Icons.Outlined.Info,
                        themeColor = themeColor
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            lesson.benefits?.forEachIndexed { index, benefit ->
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = themeColor,
                                        modifier = Modifier.size(24.dp)
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Text(
                                        text = benefit,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                if (index < lesson.benefits.size - 1) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        color = themeColor.copy(alpha = 0.2f)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Navigation buttons
                Button(
                    onClick = { navController.navigate("quiz/${lesson.quizId}") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = themeColor
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Take Quiz",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
fun ExpandableSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    themeColor: Color,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f
    )

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = themeColor,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = themeColor
                )
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = if (expanded) "Collapse" else "Expand",
                tint = themeColor,
                modifier = Modifier
                    .size(24.dp)
                    .rotate(rotationState)
            )
        }

        // Content
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            HorizontalDivider(color = themeColor.copy(alpha = 0.2f))
            content()
        }
    }
}
