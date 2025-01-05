package com.auto.artist.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TtsRequest(
    val model: String,
    val voice: String,
    val input: String
)


@Serializable
data class ChatCompletionRequest(
    val model: String,
    val messages: List<ChatMessage>
)

@Serializable
data class ChatMessage(
    val role: String,
    val content: String
)

@Serializable
data class ChatCompletionResponse(
    val choices: List<ChatChoice>
)

@Serializable
data class ChatChoice(
    val message: ChatMessage
)

@Serializable
data class ImageGenerationRequest(
    val model: String = "dall-e-3",
    val prompt: String,
    val n: Int = 1,
    val size: String = "1024x1792",
    val quality: String = "hd"
)

@Serializable
data class ImageGenerationResponse(
    val created: Long,
    val data: List<ImageData>
)

@Serializable
data class ImageData(
    @SerialName("revised_prompt") val revisedPrompt: String? = null,
    val url: String
)
