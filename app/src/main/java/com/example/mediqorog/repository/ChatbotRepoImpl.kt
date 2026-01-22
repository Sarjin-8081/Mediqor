package com.example.mediqorog.repository

import com.example.mediqorog.BuildConfig
import com.example.mediqorog.model.ChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.SocketTimeoutException

/**
 * Implementation of ChatbotRepository using Groq API
 * Handles all network communication and error handling for the chatbot
 */
class ChatbotRepoImpl : ChatbotRepository {

    companion object {
        // Read API key from local.properties
        private val API_KEY: String by lazy {
            try {
                val properties = java.util.Properties()
                val inputStream = ChatbotRepoImpl::class.java.classLoader
                    ?.getResourceAsStream("local.properties")

                if (inputStream != null) {
                    properties.load(inputStream)
                    properties.getProperty("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", "")
                } else {
                    // Fallback - try to read from file system
                    val file = java.io.File("local.properties")
                    if (file.exists()) {
                        properties.load(java.io.FileInputStream(file))
                        properties.getProperty("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", "")
                    } else {
                        ""
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("ChatbotRepo", "Failed to load API key", e)
                ""
            }
        }


        private const val API_URL = "https://api.groq.com/openai/v1/chat/completions"
        private const val MODEL = "llama-3.3-70b-versatile"

        // Request Configuration
        private const val MAX_CONTEXT_MESSAGES = 15
        private const val CONNECT_TIMEOUT = 30000
        private const val READ_TIMEOUT = 30000
        private const val TEMPERATURE = 0.7
        private const val MAX_TOKENS = 1200
        private const val TOP_P = 0.9

        // HTTP Response Codes
        private const val HTTP_RATE_LIMIT = 429
        private const val HTTP_UNAUTHORIZED = 401
    }

    /**
     * System prompt that defines the bot's personality and capabilities
     */
    private val systemPrompt = """You are MediBot, an empathetic and professional AI health assistant for the MediQor healthcare platform.

🎯 YOUR CORE CAPABILITIES:

1. **SYMPTOM ANALYSIS** 
   - Ask clarifying questions about duration, severity, and related symptoms
   - Provide possible causes and self-care suggestions
   - Recommend when to seek medical attention

2. **MEDICATION GUIDANCE**
   - Provide information about medicines, dosages, and side effects
   - Suggest over-the-counter alternatives when appropriate
   - Explain proper usage and storage

3. **APP NAVIGATION**
   - Guide users through MediQor features
   - Help with ordering medicines and booking appointments
   - Explain prescription upload process

4. **HEALTH & WELLNESS**
   - Offer preventive health tips
   - Suggest lifestyle modifications
   - Provide general wellness advice

5. **ORDER ASSISTANCE**
   - Help track orders
   - Assist with prescription requirements
   - Explain delivery options

📋 COMMUNICATION STYLE:
- Be warm, empathetic, and professional
- Use clear, simple language (avoid excessive medical jargon)
- Structure responses with bullet points for clarity
- Keep responses concise but comprehensive (2-4 paragraphs max)
- Use emojis sparingly for visual appeal (1-2 per response)

⚠️ CRITICAL SAFETY PROTOCOLS:

**EMERGENCY SYMPTOMS** (require immediate medical attention):
- Severe chest pain or pressure
- Difficulty breathing or shortness of breath
- Sudden severe headache or vision changes
- Signs of stroke (facial drooping, arm weakness, speech difficulty)
- Severe allergic reactions
- Uncontrolled bleeding
- Loss of consciousness
- Severe abdominal pain

For emergencies, ALWAYS respond:
"🚨 **EMERGENCY ALERT**: Your symptoms require immediate medical attention. Please:
1. Call emergency services (911/local emergency number) immediately
2. Do NOT wait or try home remedies
3. If alone, contact someone nearby for help

This is a medical emergency and I cannot provide adequate assistance through chat."

**IMPORTANT DISCLAIMERS:**
- Always include: "⚕️ *Note: I provide general information only, not medical diagnosis or treatment. Always consult a healthcare professional for personalized medical advice.*"
- For persistent symptoms (>3 days): Recommend seeing a doctor
- For medication questions: Suggest consulting a pharmacist or doctor
- Never suggest stopping prescribed medications
- Don't provide specific dosages without professional consultation

🎯 RESPONSE STRUCTURE:
1. Acknowledge the user's concern with empathy
2. Provide relevant information or ask clarifying questions
3. Offer actionable next steps
4. Include appropriate disclaimers
5. Ask if they need additional help

Remember: You're a helpful guide, not a replacement for professional medical care."""

    override suspend fun sendMessage(
        message: String,
        conversationHistory: List<ChatMessage>
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            // Create HTTP connection
            val connection = createConnection()

            // Build request payload
            val requestBody = buildRequestBody(message, conversationHistory)

            // Send request
            sendRequest(connection, requestBody)

            // Handle response
            val response = handleResponse(connection)

            Result.success(response)

        } catch (e: Exception) {
            Result.failure(mapException(e))
        }
    }

    override fun isConfigured(): Boolean {
        return API_KEY.isNotBlank() && API_URL.isNotBlank()
    }

    override fun getWelcomeMessage(): String {
        return """👋 **Welcome to MediBot!**

I'm your AI health assistant, here to help you with:

🩺 **Symptom Analysis** - Understand your health concerns
💊 **Medicine Information** - Get details about medications  
📱 **App Guidance** - Navigate MediQor features easily
❤️ **Wellness Tips** - Improve your overall health
🛒 **Order Help** - Assistance with prescriptions & delivery

How may I assist you today?

⚕️ *Remember: I provide general information only. For medical emergencies or serious concerns, please consult a healthcare professional.*"""
    }

    /**
     * Creates and configures an HTTP connection to the API
     */
    private fun createConnection(): HttpURLConnection {
        return (URL(API_URL).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("Authorization", "Bearer $API_KEY")
            doOutput = true
            connectTimeout = CONNECT_TIMEOUT
            readTimeout = READ_TIMEOUT
        }
    }

    /**
     * Builds the JSON request body with conversation context
     */
    private fun buildRequestBody(
        message: String,
        conversationHistory: List<ChatMessage>
    ): JSONObject {
        val messages = JSONArray().apply {
            // Add system prompt
            put(JSONObject().apply {
                put("role", "system")
                put("content", systemPrompt)
            })

            // Add conversation history (last N messages for context)
            conversationHistory.takeLast(MAX_CONTEXT_MESSAGES).forEach { msg ->
                put(JSONObject().apply {
                    put("role", if (msg.isUser) "user" else "assistant")
                    put("content", msg.text)
                })
            }

            // Add current user message
            put(JSONObject().apply {
                put("role", "user")
                put("content", message)
            })
        }

        return JSONObject().apply {
            put("model", MODEL)
            put("messages", messages)
            put("temperature", TEMPERATURE)
            put("max_tokens", MAX_TOKENS)
            put("top_p", TOP_P)
            put("stream", false)
        }
    }

    /**
     * Sends the JSON request to the API
     */
    private fun sendRequest(connection: HttpURLConnection, requestBody: JSONObject) {
        connection.outputStream.use { outputStream ->
            outputStream.write(requestBody.toString().toByteArray())
        }
    }

    /**
     * Handles the API response and extracts the message content
     *
     * @throws ApiException for non-200 responses
     */
    private fun handleResponse(connection: HttpURLConnection): String {
        return when (val responseCode = connection.responseCode) {
            HttpURLConnection.HTTP_OK -> {
                val responseText = connection.inputStream
                    .bufferedReader()
                    .use { it.readText() }

                extractMessageFromResponse(responseText)
            }
            HTTP_RATE_LIMIT -> {
                throw RateLimitException("Rate limit exceeded. Please wait before trying again.")
            }
            HTTP_UNAUTHORIZED -> {
                throw AuthenticationException("API authentication failed. Please check configuration.")
            }
            else -> {
                val errorText = connection.errorStream
                    ?.bufferedReader()
                    ?.use { it.readText() }
                    ?: "Unknown error occurred"

                throw ApiException(
                    code = responseCode,
                    message = "API Error ($responseCode): $errorText"
                )
            }
        }
    }

    /**
     * Extracts the bot's message from the JSON response
     */
    private fun extractMessageFromResponse(responseText: String): String {
        val jsonResponse = JSONObject(responseText)

        return jsonResponse
            .getJSONArray("choices")
            .getJSONObject(0)
            .getJSONObject("message")
            .getString("content")
            .trim()
    }

    /**
     * Maps exceptions to user-friendly error messages
     */
    private fun mapException(exception: Exception): Exception {
        return when (exception) {
            is RateLimitException -> {
                Exception("⏳ Rate limit reached. Please wait a moment and try again.")
            }
            is AuthenticationException -> {
                Exception("🔐 Authentication error. Please contact support.")
            }
            is ApiException -> {
                Exception("⚠️ Server error (${exception.code}). Please try again later.")
            }
            is SocketTimeoutException -> {
                Exception("⌛ Request timed out. Please check your connection and try again.")
            }
            is IOException -> {
                Exception("🌐 Network error. Please check your internet connection.")
            }
            else -> {
                Exception("❌ ${exception.message ?: "An unexpected error occurred. Please try again."}")
            }
        }
    }


    // Custom exception classes for better error handling
    private class RateLimitException(message: String) : Exception(message)
    private class AuthenticationException(message: String) : Exception(message)
    private class ApiException(val code: Int, message: String) : Exception(message)
}
