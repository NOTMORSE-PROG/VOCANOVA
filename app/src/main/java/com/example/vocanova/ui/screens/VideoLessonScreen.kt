package com.example.vocanova.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.vocanova.R
import com.example.vocanova.ui.components.VideoPlayer
import com.example.vocanova.ui.theme.VocaBlue
import com.example.vocanova.ui.theme.VocaGreen
import com.example.vocanova.ui.theme.VocaOrange
import com.example.vocanova.ui.theme.VocaPurple

@SuppressLint("ContextCastToActivity")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoLessonScreen(navController: NavController, weekNumber: Int = 1) {
    val context = LocalContext.current
    val videoId = "week$weekNumber"

    // Get the raw resource ID for the video
    val videoResId = when (weekNumber) {
        1 -> R.raw.week1
        2 -> R.raw.week2
        3 -> R.raw.week3
        4 -> R.raw.week4
        else -> R.raw.week1
    }

    // Create video URI
    val videoUri = Uri.parse("android.resource://${context.packageName}/$videoResId")

    // Track fullscreen state
    var isFullscreen by remember { mutableStateOf(false) }

    // Handle fullscreen state
    val activity = LocalContext.current as? android.app.Activity

    // Reset orientation when leaving the screen
    DisposableEffect(Unit) {
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    val (title, themeColor) = when (weekNumber) {
        1 -> Pair("Word Relationships", VocaBlue)
        2 -> Pair("Expanding Vocabulary", VocaGreen)
        3 -> Pair("Context Clues", VocaOrange)
        4 -> Pair("Word Nuances", VocaPurple)
        else -> Pair("Word Relationships", VocaBlue)
    }

    // For Week 2, use green color
    val headerColor = themeColor
    val buttonColor = themeColor
    val backgroundColor = if (weekNumber == 2) Color(0xFF1A1A1A) else MaterialTheme.colorScheme.background
    val textColor = if (weekNumber == 2) Color.White else MaterialTheme.colorScheme.onBackground

    if (isFullscreen) {
        // Fullscreen video player
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            VideoPlayer(
                videoId = videoId,
                videoUri = videoUri,
                onFullscreenToggle = { fullscreen -> isFullscreen = fullscreen }
            )
        }
    } else {
        // Normal screen with video and content
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Week $weekNumber: $title", color = Color.White) },
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
                        containerColor = headerColor
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(backgroundColor)
                    .verticalScroll(rememberScrollState())
            ) {
                // Video Player
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                ) {
                    VideoPlayer(
                        videoId = videoId,
                        videoUri = videoUri,
                        onFullscreenToggle = { fullscreen -> isFullscreen = fullscreen }
                    )
                }

                // Content based on week number
                when (weekNumber) {
                    1 -> Week1Content(themeColor, textColor)
                    2 -> Week2ContentSpecial(VocaGreen)
                    3 -> Week3Content(themeColor, textColor)
                    4 -> Week4Content(themeColor, textColor)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = buttonColor
                        )
                    ) {
                        Text("Back to Lessons")
                    }

                    Spacer(modifier = Modifier.size(16.dp))

                    Button(
                        onClick = {
                            // Navigate to the corresponding quiz for this week
                            navController.navigate("quiz/lesson$weekNumber")
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = buttonColor
                        )
                    ) {
                        Text("Take Week $weekNumber Quiz")
                    }
                }
            }
        }
    }
}

@Composable
fun Week1Content(themeColor: Color, textColor: Color) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = """
                Our topic is about Understanding Basic Word Relationships, The meaning and difference between
                synonyms and antonyms. We will talk about the Definition and function of Synonyms and Antonyms and
                the Importance of Word Choice.
            """.trimIndent(),
            style = MaterialTheme.typography.bodyMedium,
            color = textColor
        )

        ContentCard(
            title = "SYNONYMS",
            content = """
                • These are words that have the same or very similar meanings.
                • Synonyms are words with identical or nearly identical meanings.
                • The purpose of synonyms is to improve our word choice and to prevent the redundancy of the word
                in a sentence or paragraph.
            """.trimIndent(),
            examples = """
                • Happy: Cheerful, joyful, content, pleased
                • Small: Petite, tiny, little, miniature
            """.trimIndent(),
            color = themeColor,
            textColor = textColor
        )

        ContentCard(
            title = "ANTONYMS",
            content = """
                • These are words with opposite meanings.
                • Antonyms are words with opposite or contradictory meanings.
                • The purpose of antonyms is to make our words effective. It helps to highlight the differences
                between words.
            """.trimIndent(),
            examples = """
                • Happy: Sad, unhappy, miserable, depressed
                • Small: big, large, huge, enormous
            """.trimIndent(),
            color = themeColor,
            textColor = textColor
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "What are the functions of Synonyms and Antonyms?",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = textColor
        )

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "SYNONYMS - ",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = themeColor
            )

            Text(
                text = "Allows speakers and writers to avoid repetition and express themselves with variety and precision.",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "ANTONYMS - ",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = themeColor
            )

            Text(
                text = "Helps show contrast, highlight differences, and deepen understanding of a certain text.",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = themeColor.copy(alpha = 0.1f)
            )
        ) {
            Text(
                text = "By learning synonyms and antonyms, we can speak and write more clearly, and understand what we read more easily.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(16.dp),
                color = textColor
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Importance of Word Choice",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = textColor
        )

        Text(
            text = "These are the importance of word choice:",
            style = MaterialTheme.typography.bodyMedium,
            color = textColor
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "1. It helps us define meaning clearly.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = textColor
                )

                Text(
                    text = """
                        • With synonyms and antonyms, it will help us choose the best words by showing how some words
                        are similar and others are opposite.
                        • Additionally, they teach us that even if two words seem alike, they can have different meanings or
                        be used in different situations.
                    """.trimIndent(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor
                )

                Text(
                    text = "2. It strengthens our vocabulary control.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = textColor
                )

                Text(
                    text = """
                        • When you have a strong vocabulary, it's easier to understand what you read, explain your ideas
                        clearly, and do better on tests.
                        • In simple terms, having a strong vocabulary means you know and understand more words. This will
                        certainly help you read better, speak and write more clearly, and express your thoughts in school
                        activities, projects, and everyday conversations.
                    """.trimIndent(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor
                )

                Text(
                    text = "3. Synonyms and antonyms reveal subtle differences in meaning.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = textColor
                )

                Text(
                    text = """
                        • Noticing how meaning shifts between similar or opposite terms.
                        • To put it another way, synonyms and antonyms show us how words can have slightly different or
                        opposite meanings. By noticing these differences, you will get a chance to think more deeply about
                        words, which helps you expand your vocabulary and use language more effectively.
                    """.trimIndent(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor
                )
            }
        }
    }
}

@Composable
fun Week2ContentSpecial(greenColor: Color) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        // First card with explanation of weak words
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2A2A2A)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = """
                        Weak words are often simple, vague, or overused, which makes writing less clear and less
                        engaging.
                        
                        They don't give the reader a strong image or a precise understanding of what is being said. By using
                        stronger, more specific words, writers can make their ideas clearer, more vivid, and more powerful.
                        Replacing weak words improves the quality and impact of writing.
                    """.trimIndent(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }
        }

        // Title for examples
        Text(
            text = "Example of Weak Words and the stronger alternative of the words",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        // Card with examples of weak words and their alternatives
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF3A3A3A)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Good example
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Good is weak because it's too general and doesn't show what specifically makes something positive.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )

                    Row(
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "• The stronger alternative words of Good are ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }

                    Column(
                        modifier = Modifier.padding(start = 16.dp)
                    ) {
                        Text(
                            text = "excellent,",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = greenColor
                        )
                        Text(
                            text = "delightful,",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = greenColor
                        )
                        Text(
                            text = "impressive",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = greenColor
                        )
                    }
                }

                // Bad example
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Bad is weak because it lacks detail and doesn't explain what is wrong or negative about something.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )

                    Row(
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "• The stronger alternative words of Bad are ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }

                    Column(
                        modifier = Modifier.padding(start = 16.dp)
                    ) {
                        Text(
                            text = "terrible,",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = greenColor
                        )
                        Text(
                            text = "unpleasant,",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = greenColor
                        )
                        Text(
                            text = "harmful",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = greenColor
                        )
                    }
                }

                // Happy example
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Happy is weak because it doesn't describe the type or depth of the emotion being felt.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )

                    Row(
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "• The stronger alternative words of Happy are ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }

                    Column(
                        modifier = Modifier.padding(start = 16.dp)
                    ) {
                        Text(
                            text = "joyful,",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = greenColor
                        )
                        Text(
                            text = "thrilled,",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = greenColor
                        )
                        Text(
                            text = "delighted",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = greenColor
                        )
                    }
                }
            }
        }

        Divider(color = greenColor.copy(alpha = 0.3f))

        // Additional content
        Text(
            text = "How to Choose Strong Words:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2A2A2A)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "1. Use alternative words – Replace weak words with stronger, more specific ones.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )

                Text(
                    text = "2. Think about the emotion or intensity – Choose words that better show the feeling or strength behind the message.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )

                Text(
                    text = "3. Consider the context – Pick words that fit the tone, whether it's formal or casual.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )

                Text(
                    text = "4. Practice by rewriting – Improve by changing simple sentences to include clearer, more powerful words.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }
        }

        Text(
            text = "Examples of Strengthened Sentences:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF3A3A3A)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Text(
                        text = "Weak sentence: She is a good dancer.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                    Text(
                        text = "• Good is a vague word that doesn't describe the level of skill or uniqueness of her dancing.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "Stronger sentence: She is an impressive dancer.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = greenColor
                    )
                    Text(
                        text = "• Impressive is more specific and conveys that her dancing stands out and leaves a strong impact on the audience. It highlights her talent more vividly.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                Column {
                    Text(
                        text = "Weak sentence: The weather was bad during the trip.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                    Text(
                        text = "• Bad is too general and doesn't convey the extent or severity of the weather.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "Stronger sentence: The weather was terrible during the trip.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = greenColor
                    )
                    Text(
                        text = "• Terrible is a stronger word that emphasizes how unpleasant or extreme the weather was, giving the reader a clearer sense of the experience.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                Column {
                    Text(
                        text = "Weak sentence: They were happy after winning the match.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                    Text(
                        text = "• This sentence uses the word happy, which is too general. It doesn't show how strong their emotion was or what kind of happiness they felt.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "Stronger sentence: They were thrilled after winning the match.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = greenColor
                    )
                    Text(
                        text = "• In this version, thrilled gives a clearer picture. It shows that they were extremely excited and joyful, making the sentence more vivid and expressive.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun Week3Content(themeColor: Color, textColor: Color) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = """
                Our past lesson was about Expanding Vocabulary: Using Simple Synonym Replacements; Common weak
                words and Strong word choice.
            """.trimIndent(),
            style = MaterialTheme.typography.bodyMedium,
            color = textColor
        )

        ContentCard(
            title = "What are Common Weak Words?",
            content = """
                These are some words that are overused or vague words that do not give the reader a clear picture. In simple
                terms, weak words are used too much and don't give a clear idea.
                
                For example, the word "good", it is a common weak word because it is used a lot, but it doesn't tell us
                exactly how good something is. Instead of saying "good", we can use the stronger words; excellent,
                delightful, and impressive. Remember that we need to use these words based on the sentence.
            """.trimIndent(),
            examples = "",
            color = themeColor,
            textColor = textColor
        )

        ContentCard(
            title = "What are Stronger Words?",
            content = """
                These are the words that are specific, descriptive, and convey precise meaning or emotion. In simple terms,
                strong words are clear, give more details, and show exact feelings or ideas.
                
                For example, in the sentence, "She is a good dancer." The word "good" is overused and it is an example of
                weak words so to make it specific or strong word, the sentence must be, "She is an impressive dancer." The
                word "impressive" is specific, that's why it is a strong word.
            """.trimIndent(),
            examples = "",
            color = themeColor,
            textColor = textColor
        )

        Divider(color = themeColor.copy(alpha = 0.3f))

        Text(
            text = "Context Clues in Antonyms and Synonyms",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = themeColor
        )

        ContentCard(
            title = "What are Context Clues?",
            content = """
                Context clues are hints within a text that help readers understand the meaning of unfamiliar words or phrases.
                These clues, often found in the same sentence or nearby sentences, provide information that allows readers
                to infer the meaning of a word without needing to look it up in a dictionary.
                
                Example of Context are Synonym, Antonym, Definition, Explanation, and Inference
                We use Synonym and Antonym as Context clues to help readers understand the meaning of unfamiliar
                words by providing similar or opposite meanings in the text. Also it allows readers to infer the meaning of
                the unknown word without looking up a dictionary, which can interrupt their reading flow.
            """.trimIndent(),
            examples = """
                Example of Synonym clue in Sentence: The big bagpack of Jared has a huge tumbler.
                Since 'big' and 'huge' have the same meaning in the sentence, it serves as an example of a Synonym Clue.
                
                Example of Antonym clue in Sentence: Kyle's house is valuable unlike Glenn's house is worthless.
                The words 'valuable' and 'worthless' mean the opposite of each other, so this sentence shows an example of
                an Antonym Clue.
            """.trimIndent(),
            color = themeColor,
            textColor = textColor
        )

        Text(
            text = "What are the importance of context clues?",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = textColor
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ImportancePoint(
                    title = "1. Enhanced Reading Comprehension:",
                    description = """
                        • By understanding the meaning of unfamiliar words, readers can grasp the overall meaning of the text more effectively.
                        • When you find clues in the story to help you guess what a new word means, it becomes easier to understand the whole story and also you don't get stuck just because you see a word you don't know.
                    """.trimIndent(),
                    textColor = textColor
                )

                ImportancePoint(
                    title = "2. Vocabulary Building:",
                    description = """
                        • Actively using context clues helps expand vocabulary and increases the ability to understand a wider range of words.
                        • When you practice in finding the meaning of new words, you start to learn and remember them and each time you figure out a new word, you may add it in your word bank.
                    """.trimIndent(),
                    textColor = textColor
                )

                ImportancePoint(
                    title = "3. Increased Reading Fluency:",
                    description = """
                        • Readers can maintain the flow of reading without constantly stopping to look up words, leading to more fluent and enjoyable reading experiences.
                        • When you can guess the meaning of new words without having to stop and check a dictionary, you can keep reading without breaking the flow.
                    """.trimIndent(),
                    textColor = textColor
                )
            }
        }

        Text(
            text = "Applying Context Clues in Synonym and Antonym",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = themeColor
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = themeColor.copy(alpha = 0.1f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "How to Apply the Context Clue in Synonym and Antonym?",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = themeColor
                )

                Column {
                    Text(
                        text = "For Synonym Context Clues:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )

                    Text(
                        text = """
                            We need to look for a word nearby that has a similar meaning. When you see an unfamiliar word, check if the sentence explains it with a simpler, similar word.
                            
                            In simple terms, the sentence gives you an easier word that helps you understand the new one. The Signal words that show a synonym are words like "also," "similar to," "like," or "means." These words tell you that the sentence is giving you a word with the same or almost the same meaning.
                        """.trimIndent(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor
                    )
                }

                Column {
                    Text(
                        text = "For Antonym Context Clues:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )

                    Text(
                        text = """
                            We need to look for a word that means the opposite. We need to notice if the sentence shows a contrast because the opposite meaning will help you figure out the unknown word.
                            
                            In simple terms, a sentence shows the opposite meaning to help you figure out a word. The Signal words that show an antonym are words like "but," "however," "unlike," "instead," or "although." These words tell you that the sentence is giving you an opposite idea to help explain the new word.
                        """.trimIndent(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor
                    )
                }
            }
        }
    }
}

@Composable
fun Week4Content(themeColor: Color, textColor: Color) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = """
                Our past lesson is all about context clues, specifically in antonyms and synonyms, its definition, and
                importance.
            """.trimIndent(),
            style = MaterialTheme.typography.bodyMedium,
            color = textColor
        )

        ContentCard(
            title = "Definition and importance of context clues",
            content = """
                What are context clues?
                Context clues are the hints within a text that help readers understand the meaning of unfamiliar words or
                phrases.
            """.trimIndent(),
            examples = "",
            color = themeColor,
            textColor = textColor
        )

        Text(
            text = "What is the importance of context clues?",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = textColor
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ImportancePoint(
                    title = "1. Enhanced Reading Comprehension:",
                    description = "By understanding the meaning of unfamiliar words, readers can grasp the overall meaning of the text more effectively.",
                    textColor = textColor
                )

                ImportancePoint(
                    title = "2. Vocabulary Building:",
                    description = "Actively using context clues helps expand vocabulary and increases the ability to understand a wider range of words.",
                    textColor = textColor
                )

                ImportancePoint(
                    title = "3. Increased Reading Fluency:",
                    description = "Readers can maintain the flow of reading without constantly stopping to look up words, leading to more fluent and enjoyable reading experiences.",
                    textColor = textColor
                )
            }
        }

        ContentCard(
            title = "Examples of Context Clues",
            content = "",
            examples = """
                Example of a synonym clue in a sentence:
                Dom was tired or exhausted after three hours of training.
                Tired and exhausted have the same meaning in the sentence, so it is considered a synonym clue
                
                Example of antonym clue in sentence:
                Jay's bicycle is valuable, unlike Noah's bicycle, which is worthless.
                The words valuable and worthless have opposite meanings, so this sentence is considered an antonym clue.
            """.trimIndent(),
            color = themeColor,
            textColor = textColor
        )

        Divider(color = themeColor.copy(alpha = 0.3f))

        Text(
            text = "Understanding Word Nuances",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = themeColor
        )

        ContentCard(
            title = "What does nuances mean?",
            content = """
                Nuances are the subtle differences in meaning between words, phrases, and ideas. These shades of meaning
                might seem small, but they can greatly affect how we understand what is being said. In simple terms,
                nuances refer to the slight differences in the meaning of words that seem similar but have different uses.
            """.trimIndent(),
            examples = "",
            color = themeColor,
            textColor = textColor
        )

        Text(
            text = "What is the importance of understanding word nuances?",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = textColor
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ImportancePoint(
                    title = "1. Improved Communication:",
                    description = "Understanding nuances allows individuals to convey messages more accurately and understand others more deeply. Meaning, understanding word nuances helps people to effectively share their thoughts with others.",
                    textColor = textColor
                )

                ImportancePoint(
                    title = "2. Develops of Emotional Intelligence:",
                    description = "Nuances help in understanding the emotional context of a conversation, and this awareness can lead to a more empathetic interaction. It simply means that knowing the small difference in words can help individuals improve their emotional intelligence because it helps them have a better understanding of others.",
                    textColor = textColor
                )
            }
        }

        ContentCard(
            title = "Example of Word Nuances",
            content = "",
            examples = """
                1. Looked and Glared
                Jay looked at Sanjo before the race.
                Jay glared at Sanjo before the race.
                Both looked and glared involve Jay using his eyes to focus on Sanjo, but the word looked is neutral and
                merely indicates that Jay directed his attention at him. However, the word glared introduces emotion and
                intensity and implies that Jay was angry or serious. This shows how word choices can alter the tone or
                meaning of a sentence.
                
                2. Warm and Boiling
                The noodle soup is warm.
                The noodle soup is boiling.
                Although both the words warm and boiling describe temperature, they show different degrees of heat. Warm
                refers to mild heat, while boiling conveys extreme or intense temperature. This reflects how words that seem
                similar can actually convey different meanings based on context.
            """.trimIndent(),
            color = themeColor,
            textColor = textColor
        )
    }
}

@Composable
fun ContentCard(
    title: String,
    content: String,
    examples: String,
    color: Color,
    textColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )

            if (content.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor
                )
            }

            if (examples.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "EXAMPLES:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = color
                )

                Text(
                    text = examples,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor
                )
            }
        }
    }
}

@Composable
fun ImportancePoint(
    title: String,
    description: String,
    textColor: Color
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = textColor
        )

        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = textColor
        )
    }
}
