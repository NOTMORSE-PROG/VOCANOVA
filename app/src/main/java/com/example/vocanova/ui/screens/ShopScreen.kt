package com.example.vocanova.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.TimerOff
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.twotone.Filter
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.vocanova.data.model.PowerUp
import com.example.vocanova.data.model.PowerUpType
import com.example.vocanova.ui.theme.VocaBlue
import com.example.vocanova.ui.theme.VocaGreen
import com.example.vocanova.ui.theme.VocaOrange
import com.example.vocanova.ui.theme.VocaPurple
import com.example.vocanova.ui.theme.VocaRed
import com.example.vocanova.ui.viewmodels.ShopViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen(
    navController: NavController,
    viewModel: ShopViewModel = hiltViewModel()
) {
    val powerUps by viewModel.powerUps.collectAsState()
    val userPowerUps by viewModel.userPowerUps.collectAsState()
    val userCurrency by viewModel.userCurrency.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Show error or success messages
    LaunchedEffect(errorMessage, successMessage) {
        errorMessage?.let {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(it)
                viewModel.clearMessages()
            }
        }

        successMessage?.let {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(it)
                viewModel.clearMessages()
            }
        }
    }

    // Refresh data when screen is shown
    LaunchedEffect(Unit) {
        viewModel.loadUserCurrency()
    }

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
                        "Shop",
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
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
                // Currency display
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(16.dp),
                            spotColor = VocaOrange.copy(alpha = 0.5f)
                        ),
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
                                        VocaOrange.copy(alpha = 0.8f),
                                        VocaOrange.copy(alpha = 0.4f)
                                    )
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
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
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
                                    imageVector = Icons.Default.AttachMoney,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = "$userCurrency",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )

                                Text(
                                    text = "Your Balance",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

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
                        text = "Power-Ups",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Purchase power-ups to help you in quizzes",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (isLoading && powerUps.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(powerUps) { powerUp ->
                            PowerUpItem(
                                powerUp = powerUp,
                                count = userPowerUps[powerUp.id] ?: 0,
                                userCurrency = userCurrency,
                                onPurchase = { quantity -> viewModel.purchasePowerUp(powerUp.id, quantity) }
                            )
                        }
                    }
                }
            }

            // Loading overlay
            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }
    }
}

@Composable
fun PowerUpItem(
    powerUp: PowerUp,
    count: Int,
    userCurrency: Int,
    onPurchase: (Int) -> Unit
) {
    val (backgroundColor, icon) = when (powerUp.type) {
        PowerUpType.FREEZE_TIME -> VocaBlue to Icons.Default.TimerOff
        PowerUpType.FIFTY_FIFTY -> VocaGreen to Icons.TwoTone.Filter
        PowerUpType.REVERSE_TIME -> VocaRed to Icons.Default.History
    }

    var isPurchasing by remember { mutableStateOf(false) }
    var quantity by remember { mutableStateOf(1) }
    val totalPrice = powerUp.price * quantity
    val canAfford = userCurrency >= totalPrice

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = backgroundColor.copy(alpha = 0.5f)
            ),
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
                            backgroundColor.copy(alpha = 0.3f),
                            backgroundColor.copy(alpha = 0.1f)
                        )
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
                        Color.White.copy(alpha = 0.05f),
                        CircleShape
                    )
            )

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.BottomStart)
                    .offset(x = (-10).dp, y = 10.dp)
                    .background(
                        Color.White.copy(alpha = 0.05f),
                        CircleShape
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    backgroundColor.copy(alpha = 0.3f),
                                    backgroundColor.copy(alpha = 0.1f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Info
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = powerUp.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = powerUp.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (count > 0)
                                        VocaGreen.copy(alpha = 0.2f)
                                    else
                                        Color.White.copy(alpha = 0.1f)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "You have: $count",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = if (count > 0) Color.White else Color.White.copy(alpha = 0.5f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Purchase section with quantity selector
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Price display
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (canAfford)
                                    VocaOrange.copy(alpha = 0.2f)
                                else
                                    Color.Gray.copy(alpha = 0.2f)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AttachMoney,
                                contentDescription = null,
                                tint = if (canAfford) Color.White else Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )

                            Text(
                                text = "$totalPrice",
                                style = MaterialTheme.typography.titleMedium,
                                color = if (canAfford) Color.White else Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Quantity selector
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.1f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        IconButton(
                            onClick = { if (quantity > 1) quantity-- },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Text(
                                text = "−",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        Text(
                            text = "$quantity",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        IconButton(
                            onClick = { quantity++ },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Text(
                                text = "+",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Buy button
                    Button(
                        onClick = {
                            if (!isPurchasing) {
                                isPurchasing = true
                                onPurchase(quantity)
                                // Reset after a delay to prevent double-clicks
                                kotlinx.coroutines.MainScope().launch {
                                    delay(1000)
                                    isPurchasing = false
                                }
                            }
                        },
                        enabled = canAfford && !isPurchasing,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (canAfford) backgroundColor else Color.Gray.copy(alpha = 0.5f),
                            disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Buy",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
