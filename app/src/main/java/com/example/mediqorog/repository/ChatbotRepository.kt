package com.example.mediqorog.repository

import com.example.mediqorog.model.ChatMessage

/**
 * Repository interface for chatbot data operations
 * This abstraction allows for easy testing and implementation swapping
 */
interface ChatbotRepository {

    /**
     * Sends a message to the chatbot API and returns the response
     *
     * @param message The user's message text
     * @param conversationHistory Previous messages for context
     * @return Result containing bot response or error
     */
    suspend fun sendMessage(
        message: String,
        conversationHistory: List<ChatMessage>
    ): Result<String>

    /**
     * Validates if the API is properly configured
     *
     * @return True if API key and configuration are valid
     */
    fun isConfigured(): Boolean

    /**
     * Gets the welcome message for initial chat state
     *
     * @return Welcome message text
     */
    fun getWelcomeMessage(): String
}