package com.example.mediqorog.view.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mediqorog.model.ChatMessage
import com.example.mediqorog.viewmodel.ChatbotViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Main Chatbot Screen
 *
 * Displays the chat interface with messages, input field, and quick actions.
 * Uses MVVM architecture with ChatbotViewModel for state management.
 *
 * @param onBackClick Callback when back button is pressed
 * @param viewModel ViewModel instance (injected or default)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatbotScreen(
    onBackClick: () -> Unit = {},
    viewModel: ChatbotViewModel = viewModel()
) {
    // Collect UI state
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            delay(100)
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    // Auto-dismiss error after 4 seconds
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            delay(4000)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            ChatbotTopBar(
                isLoading = uiState.isLoading,
                onBackClick = onBackClick,
                onClearChat = { viewModel.resetChat() }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFF8FAFB),
                                Color(0xFFEDF2F7)
                            )
                        )
                    )
            ) {
                // Messages List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    state = listState,
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = uiState.messages,
                        key = { it.id }
                    ) { message ->
                        MessageBubble(message)
                    }

                    if (uiState.isLoading) {
                        item { TypingIndicator() }
                    }
                }

                // Quick Action Chips
                AnimatedVisibility(
                    visible = uiState.shouldShowQuickActions,
                    enter = slideInVertically() + fadeIn(),
                    exit = slideOutVertically() + fadeOut()
                ) {
                    QuickActionChips(
                        onChipClick = { query ->
                            viewModel.setQuickAction(query)
                        }
                    )
                }

                // Message Input Field
                MessageInputField(
                    value = uiState.inputText,
                    onValueChange = { viewModel.updateInputText(it) },
                    onSendClick = { viewModel.sendMessage() },
                    onClearClick = { viewModel.clearInputText() },
                    enabled = !uiState.isLoading,
                    canSend = uiState.canSendMessage
                )
            }

            // Error Snackbar
            uiState.errorMessage?.let { error ->
                ErrorSnackbar(
                    error = error,
                    onDismiss = { viewModel.clearError() },
                    onRetry = { viewModel.retryLastMessage() },
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.BottomCenter)
                )
            }
        }
    }
}

/**
 * Top bar component with title and actions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatbotTopBar(
    isLoading: Boolean,
    onBackClick: () -> Unit,
    onClearChat: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "🏥",
                        fontSize = 24.sp,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        "MediBot Assistant",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }

                // Status indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (isLoading) {
                        LoadingDots(size = 6.dp, color = Color.White)
                        Text(
                            "Thinking...",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color(0xFF4CAF50), CircleShape)
                        )
                        Text(
                            "Online • AI Powered by Groq",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        },
        actions = {
            IconButton(onClick = onClearChat) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "Clear Chat",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF0B8FAC)
        )
    )
}

/**
 * Individual message bubble
 */
@Composable
private fun MessageBubble(message: ChatMessage) {
    val dateFormat = remember { SimpleDateFormat("h:mm a", Locale.getDefault()) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        // Bot avatar
        if (!message.isUser) {
            BotAvatar()
        }

        Column(
            horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start
        ) {
            // Message content
            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (message.isUser) 16.dp else 4.dp,
                    bottomEnd = if (message.isUser) 4.dp else 16.dp
                ),
                color = if (message.isUser) Color(0xFF0B8FAC) else Color.White,
                shadowElevation = if (message.isUser) 2.dp else 3.dp,
                modifier = Modifier.widthIn(max = 300.dp)
            ) {
                Text(
                    text = message.text,
                    color = if (message.isUser) Color.White else Color(0xFF2D3748),
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }

            // Timestamp
            Text(
                text = dateFormat.format(Date(message.timestamp)),
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier
                    .padding(top = 4.dp, start = 8.dp, end = 8.dp)
                    .alpha(0.7f)
            )
        }

        // User avatar
        if (message.isUser) {
            UserAvatar()
        }
    }
}

/**
 * Bot avatar icon
 */
@Composable
private fun BotAvatar() {
    Surface(
        modifier = Modifier
            .size(32.dp)
            .padding(end = 8.dp),
        shape = CircleShape,
        color = Color(0xFF0B8FAC)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = "🤖",
                fontSize = 18.sp
            )
        }
    }
}

/**
 * User avatar icon
 */
@Composable
private fun UserAvatar() {
    Surface(
        modifier = Modifier
            .size(32.dp)
            .padding(start = 8.dp),
        shape = CircleShape,
        color = Color(0xFF4299E1)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                Icons.Default.Person,
                contentDescription = "User",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Typing indicator animation
 */
@Composable
private fun TypingIndicator() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        BotAvatar()

        Spacer(modifier = Modifier.width(8.dp))

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LoadingDots()
            }
        }
    }
}

/**
 * Animated loading dots
 */
@Composable
private fun LoadingDots(
    size: androidx.compose.ui.unit.Dp = 8.dp,
    color: Color = Color(0xFF0B8FAC)
) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(3) { index ->
            val infiniteTransition = rememberInfiniteTransition(label = "loading_dots")
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, delayMillis = index * 150),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dot_scale_$index"
            )

            Box(
                modifier = Modifier
                    .size(size)
                    .scale(scale)
                    .background(color, CircleShape)
            )
        }
    }
}

/**
 * Quick action suggestion chips
 */
@Composable
private fun QuickActionChips(onChipClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            "💡 Try asking:",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF4A5568)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "🤒 I have a fever",
                "💊 Find medicine",
                "📦 Track my order"
            ).forEach { suggestion ->
                SuggestionChip(
                    onClick = { onChipClick(suggestion) },
                    label = { Text(suggestion, fontSize = 13.sp) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = Color.White,
                        labelColor = Color(0xFF2D3748)
                    )
                )
            }
        }
    }
}

/**
 * Message input field with send button
 */
@Composable
private fun MessageInputField(
    value: String,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onClearClick: () -> Unit,
    enabled: Boolean = true,
    canSend: Boolean = false
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        "Ask me anything about your health...",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF7FAFC),
                    unfocusedContainerColor = Color(0xFFF7FAFC),
                    focusedBorderColor = Color(0xFF0B8FAC),
                    unfocusedBorderColor = Color(0xFFE2E8F0)
                ),
                shape = RoundedCornerShape(24.dp),
                enabled = enabled,
                maxLines = 4,
                trailingIcon = if (value.isNotBlank()) {
                    {
                        IconButton(
                            onClick = onClearClick,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                } else null
            )

            FloatingActionButton(
                onClick = onSendClick,
                modifier = Modifier.size(48.dp),
                containerColor = if (canSend)
                    Color(0xFF0B8FAC) else Color(0xFFCBD5E0),
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 2.dp,
                    pressedElevation = 4.dp
                )
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "Send Message",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Error snackbar with retry option
 */
@Composable
private fun ErrorSnackbar(
    error: String,
    onDismiss: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Snackbar(
        modifier = modifier,
        containerColor = Color(0xFFE53E3E),
        contentColor = Color.White,
        action = {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(onClick = onRetry) {
                    Text("Retry", color = Color.White)
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = Color.White
                    )
                }
            }
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Default.Warning, contentDescription = null)
            Text(error, fontSize = 14.sp)
        }
    }
}

/**
 * Floating action button for main screen
 * Opens the chatbot when clicked
 */
@Composable
fun FloatingChatbotButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = Color(0xFF0B8FAC),
        contentColor = Color.White,
        shape = CircleShape
    ) {
        Text(
            text = "💬",
            fontSize = 24.sp
        )
    }
}