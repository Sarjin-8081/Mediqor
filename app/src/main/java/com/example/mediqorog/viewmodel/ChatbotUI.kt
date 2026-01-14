package com.example.mediqorog.viewmodel

import com.example.mediqorog.model.ChatMessage

/**
 * Immutable UI state for the chatbot screen
 * Represents the complete state of the chat interface at any given time
 *
 * @property messages Complete list of chat messages in the conversation
 * @property inputText Current text in the message input field
 * @property isLoading Whether the bot is currently processing a request
 * @property errorMessage Error message to display, null if no error
 * @property showQuickActions Whether to show quick action suggestion chips
 */
data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showQuickActions: Boolean = true
) {
    /**
     * Determines if the send button should be enabled
     */
    val canSendMessage: Boolean
        get() = inputText.isNotBlank() && !isLoading

    /**
     * Determines if we should show quick actions
     * Show only when there's just the welcome message and not loading
     */
    val shouldShowQuickActions: Boolean
        get() = messages.size == 1 && !isLoading && showQuickActions

    /**
     * Gets the conversation history (excludes current user message if needed)
     */
    val conversationHistory: List<ChatMessage>
        get() = messages
}