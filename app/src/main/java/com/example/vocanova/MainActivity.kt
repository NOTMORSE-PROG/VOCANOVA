package com.example.vocanova

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.vocanova.ui.screens.AboutScreen
import com.example.vocanova.ui.screens.AchievementScreen
import com.example.vocanova.ui.screens.DailyWordScreen
import com.example.vocanova.ui.screens.FlyingWordsGame
import com.example.vocanova.ui.screens.GameOverScreenWithCurrency
import com.example.vocanova.ui.screens.GameSelectionScreen
import com.example.vocanova.ui.screens.GreenLightRedLightGame
import com.example.vocanova.ui.screens.HomeScreen
import com.example.vocanova.ui.screens.LessonContentScreen
import com.example.vocanova.ui.screens.LoginScreen
import com.example.vocanova.ui.screens.ProfileScreen
import com.example.vocanova.ui.screens.QuizScreen
import com.example.vocanova.ui.screens.QuizSelectionScreen
import com.example.vocanova.ui.screens.ReviewWordsScreen
import com.example.vocanova.ui.screens.ShopScreen
import com.example.vocanova.ui.screens.SignupScreen
import com.example.vocanova.ui.screens.SplashScreen
import com.example.vocanova.ui.screens.SwipeGame
import com.example.vocanova.ui.screens.VideoLessonScreen
import com.example.vocanova.ui.screens.LessonScreen
import com.example.vocanova.ui.theme.VocaNovaTheme
import com.example.vocanova.utils.VideoPlayerManager
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.vocanova.ui.viewmodels.AuthViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        var isVideoPlaying = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            VocaNovaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Clean up video players when activity is destroyed
                    CleanupOnDestroy()

                    val navController = rememberNavController()
                    val authViewModel: AuthViewModel = hiltViewModel()
                    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
                    NavHost(navController = navController, startDestination = "splash") {
                        composable("splash") {
                            SplashScreen(navController = navController, isLoggedIn = isLoggedIn)
                        }
                        composable("login") {
                            LoginScreen(navController = navController, authViewModel = authViewModel)
                        }
                        composable("signup") {
                            SignupScreen(navController = navController, authViewModel = authViewModel)
                        }
                        composable("home") {
                            HomeScreen(navController = navController)
                        }
                        composable("daily_word") {
                            DailyWordScreen(navController = navController)
                        }
                        composable("review_words") {
                            ReviewWordsScreen(navController = navController)
                        }
                        composable("quiz_selection") {
                            QuizSelectionScreen(navController = navController)
                        }
                        composable(
                            "quiz/{quizId}",
                            arguments = listOf(navArgument("quizId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val quizId = backStackEntry.arguments?.getString("quizId") ?: ""
                            QuizScreen(navController = navController, quizId = quizId)
                        }
                        composable("game_selection") {
                            GameSelectionScreen(navController = navController)
                        }
                        composable("flying_words_game") {
                            FlyingWordsGame(navController = navController)
                        }
                        composable("green_light_red_light_game") {
                            GreenLightRedLightGame(navController = navController)
                        }
                        composable("swipe_game") {
                            SwipeGame(navController = navController, words = emptyList())
                        }
                        composable(
                            "game_over/{score}/{gameType}",
                            arguments = listOf(
                                navArgument("score") { type = NavType.IntType },
                                navArgument("gameType") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val score = backStackEntry.arguments?.getInt("score") ?: 0
                            val gameType = backStackEntry.arguments?.getString("gameType") ?: ""
                            GameOverScreenWithCurrency(
                                score = score,
                                currencyEarned = score / 10, // Or use your own logic
                                onPlayAgain = { navController.popBackStack() },
                                onBackToGames = { navController.navigate("game_selection") }
                            )
                        }
                        composable("video_lessons") {
                            VideoLessonScreen(navController = navController)
                        }
                        composable(
                            "lesson/{lessonId}",
                            arguments = listOf(navArgument("lessonId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val lessonId = backStackEntry.arguments?.getInt("lessonId") ?: 1
                            LessonContentScreen(navController = navController, lessonId = lessonId)
                        }
                        composable("profile") {
                            ProfileScreen(navController = navController)
                        }
                        composable("about") {
                            AboutScreen(navController = navController)
                        }
                        composable("shop") {
                            ShopScreen(navController = navController)
                        }
                        composable("achievements") {
                            AchievementScreen(navController = navController)
                        }
                        composable("lessons") {
                            LessonScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release all video players when the app is destroyed
        VideoPlayerManager.releaseAll()
    }
}

@Composable
fun CleanupOnDestroy() {
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                VideoPlayerManager.releaseAll()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}
