package com.example.mediqorog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediqorog.model.ChatMessage
import com.example.mediqorog.model.MessageStatus
import com.example.mediqorog.repository.ChatbotRepository
import com.example.mediqorog.repository.ChatbotRepoImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Chatbot screen
 *
 * Manages the chatbot UI state and coordinates between the UI and repository.
 * Handles all business logic including message sending, error handling, and state updates.
 *
 * Uses StateFlow for reactive UI updates following Android's recommended architecture.
 */
class ChatbotViewModel(
    private val repository: ChatbotRepository = ChatbotRepoImpl()
) : ViewModel() {

    // UI State - exposed as immutable StateFlow
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    // Chatbot visibility state
    private val _showChatbot = MutableStateFlow(false)
    val showChatbot: StateFlow<Boolean> = _showChatbot.asStateFlow()

    init {
        initializeChat()
    }

    // ==================== Public Methods ====================

    /**
     * Opens the chatbot interface
     */
    fun openChatbot() {
        _showChatbot.value = true
    }

    /**
     * Closes the chatbot interface
     */
    fun closeChatbot() {
        _showChatbot.value = false
    }

    /**
     * Updates the input text field
     *
     * @param text New text value
     */
    fun updateInputText(text: String) {
        _uiState.update { currentState ->
            currentState.copy(inputText = text)
        }
    }

    /**
     * Clears the input text field
     */
    fun clearInputText() {
        _uiState.update { currentState ->
            currentState.copy(inputText = "")
        }
    }

    /**
     * Sends a message to the chatbot
     *
     * @param message Optional message to send (uses inputText if not provided)
     */
    fun sendMessage(message: String? = null) {
        val messageText = (message ?: _uiState.value.inputText).trim()

        // Validation
        if (messageText.isBlank() || _uiState.value.isLoading) {
            return
        }

        // Add user message
        addUserMessage(messageText)

        // Get bot response
        fetchBotResponse(messageText)
    }

    /**
     * Sets a quick action query as input text
     *
     * @param query The quick action query
     */
    fun setQuickAction(query: String) {
        _uiState.update { currentState ->
            currentState.copy(inputText = query)
        }
    }

    /**
     * Clears the current error message
     */
    fun clearError() {
        _uiState.update { currentState ->
            currentState.copy(errorMessage = null)
        }
    }

    /**
     * Resets the chat to initial state (keeps welcome message)
     */
    fun resetChat() {
        _uiState.update { currentState ->
            currentState.copy(
                messages = currentState.messages.take(1), // Keep only welcome message
                inputText = "",
                isLoading = false,
                errorMessage = null,
                showQuickActions = true
            )
        }
    }

    /**
     * Completely clears all messages and reinitializes the chat
     */
    fun clearAllMessages() {
        _uiState.value = ChatUiState()
        initializeChat()
    }

    /**
     * Retries sending the last failed message
     */
    fun retryLastMessage() {
        val lastUserMessage = _uiState.value.messages
            .lastOrNull { it.isUser }
            ?.text

        if (lastUserMessage != null) {
            sendMessage(lastUserMessage)
        }
    }

    // ==================== Private Methods ====================

    /**
     * Initializes the chat with a welcome message
     */
    private fun initializeChat() {
        viewModelScope.launch {
            val welcomeMessage = ChatMessage(
                text = repository.getWelcomeMessage(),
                isUser = false,
                status = MessageStatus.SENT
            )

            _uiState.update { currentState ->
                currentState.copy(messages = listOf(welcomeMessage))
            }
        }
    }

    /**
     * Adds a user message to the chat and clears input
     */
    private fun addUserMessage(messageText: String) {
        val userMessage = ChatMessage(
            text = messageText,
            isUser = true,
            status = MessageStatus.SENT
        )

        _uiState.update { currentState ->
            currentState.copy(
                messages = currentState.messages + userMessage,
                inputText = "",
                isLoading = true,
                errorMessage = null,
                showQuickActions = false
            )
        }
    }

    /**
     * Fetches bot response from repository
     */
    private fun fetchBotResponse(messageText: String) {
        viewModelScope.launch {
            val result = repository.sendMessage(
                message = messageText,
                conversationHistory = _uiState.value.conversationHistory
            )

            result.fold(
                onSuccess = { response ->
                    handleSuccessResponse(response)
                },
                onFailure = { error ->
                    handleErrorResponse(error)
                }
            )
        }
    }

    /**
     * Handles successful API response
     */
    private fun handleSuccessResponse(response: String) {
        val botMessage = ChatMessage(
            text = response,
            isUser = false,
            status = MessageStatus.SENT
        )

        _uiState.update { currentState ->
            currentState.copy(
                messages = currentState.messages + botMessage,
                isLoading = false
            )
        }
    }

    /**
     * Handles API error response
     */
    private fun handleErrorResponse(error: Throwable) {
        val errorMessage = error.message ?: "An unexpected error occurred"

        val botErrorMessage = ChatMessage(
            text = "I apologize, but I'm having trouble connecting right now. $errorMessage\n\nPlease try again in a moment.",
            isUser = false,
            status = MessageStatus.ERROR
        )

        _uiState.update { currentState ->
            currentState.copy(
                messages = currentState.messages + botErrorMessage,
                isLoading = false,
                errorMessage = errorMessage
            )
        }
    }

    // ==================== Cleanup ====================

    /**
     * Called when ViewModel is cleared
     */
    override fun onCleared() {
        super.onCleared()
        // Clean up resources if needed
    }
}