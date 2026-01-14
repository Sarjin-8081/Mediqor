package com.example.mediqorog.model

import java.util.*

/**
 * Represents a single chat message in the conversation
 *
 * @property id Unique identifier for the message
 * @property text Content of the message
 * @property isUser True if message is from user, false if from bot
 * @property timestamp Unix timestamp when message was created
 * @property status Current delivery status of the message
 */
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val status: MessageStatus = MessageStatus.SENT
)

/**
 * Represents the delivery status of a chat message
 */
enum class MessageStatus {
    /** Message is currently being sent to the server */
    SENDING,

    /** Message has been successfully sent and received */
    SENT,

    /** Message failed to send due to an error */
    ERROR
}

/**
 * Sealed class representing the result of a chat API call
 */
sealed class ChatResponse {
    /**
     * Successful API response with bot message
     */
    data class Success(val message: String) : ChatResponse()

    /**
     * Failed API response with error details
     */
    data class Error(
        val message: String,
        val type: ErrorType = ErrorType.UNKNOWN
    ) : ChatResponse()

    /**
     * API call is in progress
     */
    data object Loading : ChatResponse()
}

/**
 * Types of errors that can occur during API communication
 */
enum class ErrorType {
    /** Network connectivity issues */
    NETWORK,

    /** API rate limit exceeded */
    RATE_LIMIT,

    /** Authentication/API key issues */
    AUTHENTICATION,

    /** Server returned an error */
    SERVER,

    /** Unknown or unhandled error */
    UNKNOWN
}