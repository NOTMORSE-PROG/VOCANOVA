package com.example.vocanova.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.vocanova.R
import com.example.vocanova.ui.theme.VocaBlue
import com.example.vocanova.ui.viewmodels.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var isLoginButtonEnabled by remember { mutableStateOf(true) }
    var isAnimationVisible by remember { mutableStateOf(false) }
    var rememberMe by rememberSaveable { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val isLoading by authViewModel.isLoading.collectAsState()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()
    val successMessage by authViewModel.successMessage.collectAsState()

    // Email focus requester
    val emailFocusRequester = remember { FocusRequester() }

    // Start animation after a short delay
    LaunchedEffect(Unit) {
        delay(100)
        isAnimationVisible = true
    }

    // Navigate to home when logged in
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    LaunchedEffect(errorMessage) {
        if (errorMessage.isNotEmpty()) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(errorMessage)
                authViewModel.clearError()
                isLoginButtonEnabled = true
            }
        }
    }

    LaunchedEffect(successMessage) {
        if (successMessage.isNotEmpty()) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(successMessage)
                authViewModel.clearSuccess()
            }
        }
    }

    if (showForgotPasswordDialog) {
        ForgotPasswordDialog(
            onDismiss = { showForgotPasswordDialog = false },
            onSubmit = { resetEmail ->
                authViewModel.resetPassword(resetEmail)
                showForgotPasswordDialog = false
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AnimatedVisibility(
                    visible = isAnimationVisible,
                    enter = fadeIn(animationSpec = tween(1000)) +
                            slideInVertically(animationSpec = tween(1000)) { it / 2 }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.voca_nova_logo),
                        contentDescription = "Voca Nova Logo",
                        modifier = Modifier
                            .size(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                AnimatedVisibility(
                    visible = isAnimationVisible,
                    enter = fadeIn(animationSpec = tween(1000, delayMillis = 300))
                ) {
                    Text(
                        text = "Voca Nova",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = VocaBlue
                    )
                }

                AnimatedVisibility(
                    visible = isAnimationVisible,
                    enter = fadeIn(animationSpec = tween(1000, delayMillis = 500))
                ) {
                    Text(
                        text = "A new way of learning vocabulary",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                AnimatedVisibility(
                    visible = isAnimationVisible,
                    enter = fadeIn(animationSpec = tween(1000, delayMillis = 700))
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Login",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Email") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = null
                                    )
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Next
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .focusRequester(emailFocusRequester),
                                singleLine = true,
                                isError = errorMessage.contains("email", ignoreCase = true)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Password") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null
                                    )
                                },
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                                        )
                                    }
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                isError = errorMessage.contains("password", ignoreCase = true)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = { showForgotPasswordDialog = true }
                                ) {
                                    Text("Forgot Password?")
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                androidx.compose.material3.Checkbox(
                                    checked = rememberMe,
                                    onCheckedChange = { rememberMe = it }
                                )
                                Text("Remember me")
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    if (email.isNotEmpty() && password.isNotEmpty()) {
                                        isLoginButtonEnabled = false

                                        // Save the remember me preference
                                        val sharedPrefs = context.getSharedPreferences("auth_prefs", 0)
                                        with(sharedPrefs.edit()) {
                                            putBoolean("remember_me", rememberMe)
                                            apply()
                                        }

                                        authViewModel.login(email, password)
                                    } else {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Please fill in all fields")
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isLoading && isLoginButtonEnabled
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text("Login")
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                AnimatedVisibility(
                    visible = isAnimationVisible,
                    enter = fadeIn(animationSpec = tween(1000, delayMillis = 900))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Don't have an account?",
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )

                        TextButton(
                            onClick = { navController.navigate("signup") }
                        ) {
                            Text("Sign Up")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ForgotPasswordDialog(
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var isEmailValid by remember { mutableStateOf(true) }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reset Password") },
        text = {
            Column {
                Text("Enter your email address and we'll send you a link to reset your password.")

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        isEmailValid = it.isEmpty() || android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches()
                    },
                    label = { Text("Email") },
                    isError = !isEmailValid,
                    supportingText = {
                        if (!isEmailValid) {
                            Text("Please enter a valid email address")
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(email) },
                enabled = email.isNotEmpty() && isEmailValid
            ) {
                Text("Send Reset Link")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
