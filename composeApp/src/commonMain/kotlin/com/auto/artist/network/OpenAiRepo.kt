package com.auto.artist.network

import com.auto.artist.getMediaPlayer
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.io.IOException
import kotlinx.serialization.json.Json




class OpenAiRepo(
    private val httpClient: HttpClient
) : AiRepo {
    private val apiKey = "Your OpenAi API Key"


    /**
     * Generates an optimal prompt for creating an image from given keywords.
     * @param keywords List of keywords to be used in the prompt.
     * @return A generated prompt for creating an image.
     */
    private fun generateOptimalPrompt(keywords: List<String>): String {
        require(keywords.isNotEmpty()) { "The keywords list cannot be empty" }
        return buildString {
            append("Please generate an optimal prompt for creating an image that should include the following elements: ")
            append(keywords.joinToString(", "))
            append(". The prompt should be imaginative and encourage a creative composition. Avoid directly repeating the keywords without adding any context.")
        }
    }


    /**
     * Generates a chat completion using OpenAI GPT-3.5 based on the given keywords.
     */
    override suspend fun askForOptimalPrompt(keywords: List<String>): Result<String, DataError> {
        val request = generateOptimalPrompt(keywords)
        return try {
            println("[generateChatCompletion] Request: $request") // Logging the request
            val response: HttpResponse =
                httpClient.post("https://api.openai.com/v1/chat/completions") {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $apiKey")
                        append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    }
                    setBody(
                        ChatCompletionRequest(
                            model = "gpt-3.5-turbo",
                            messages = listOf(ChatMessage(role = "user", content = request))
                        )
                    )
                }

            if (response.status.isSuccess()) {
                val completionResponse: ChatCompletionResponse = response.body()
                val prompt = completionResponse.choices.firstOrNull()?.message?.content
                println("[generateChatCompletion] Response: $prompt") // Logging the response
                prompt?.let { Result.Success(it) } ?: Result.Error(DataError.Remote.UNKNOWN)
            } else {
                parseError(response)
            }
        } catch (e: IOException) {
            println("[generateChatCompletion] IOException: ${e.message}") // Logging IOException
            Result.Error(DataError.Remote.NO_INTERNET)
        } catch (e: TimeoutCancellationException) {
            println("[generateChatCompletion] TimeoutCancellationException: ${e.message}") // Logging Timeout
            Result.Error(DataError.Remote.REQUEST_TIMEOUT)
        } catch (e: Exception) {
            println("[generateChatCompletion] Exception: ${e.message}") // Logging unknown exceptions
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }

    /**
     * Generates an image using OpenAI DALL-E based on the given prompt.
     * @param prompt The prompt for image generation.
     * @return A result containing either the generated image URL or an error.
     */
    override suspend fun generateImage(prompt: String): Result<String, DataError> {
        val imageRequest = ImageGenerationRequest(prompt = prompt)

        return try {
            println("[generateImage] Request: $imageRequest") // Logging the request
            val response: HttpResponse =
                httpClient.post("https://api.openai.com/v1/images/generations") {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $apiKey")
                        append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    }
                    setBody(imageRequest)
                }

            if (response.status.isSuccess()) {
                val responseText = response.bodyAsText()
                println("[generateImage] Response Text: $responseText") // Logging the response text
                try {
                    val imageResponse = Json.decodeFromString<ImageGenerationResponse>(responseText)
                    val imageUrl = imageResponse.data.firstOrNull()?.url
                    println("[generateImage] Image URL: $imageUrl") // Logging the image URL
                    imageUrl?.let { Result.Success(it) } ?: Result.Error(DataError.Remote.UNKNOWN)
                } catch (e: Exception) {
                    println("[generateImage] Error parsing response: ${e.message}") // Logging parsing error
                    Result.Error(DataError.Remote.SERIALIZATION)
                }
            } else {
                println("[generateImage] Error Body: ${response.bodyAsText()}") // Logging error body
                parseError(response)
            }
        } catch (e: IOException) {
            println("[generateImage] IOException: ${e.message}") // Logging IOException
            Result.Error(DataError.Remote.NO_INTERNET)
        } catch (e: TimeoutCancellationException) {
            println("[generateImage] TimeoutCancellationException: ${e.message}") // Logging Timeout
            Result.Error(DataError.Remote.REQUEST_TIMEOUT)
        } catch (e: Exception) {
            println("[generateImage] Exception: ${e.message}") // Logging unknown exceptions
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }

    /**
     * Parses the error response from an HTTP request.
     * @param response The HTTP response containing an error.
     * @return A Result.Error representing the parsed error.
     */
    private fun parseError(response: HttpResponse): Result.Error<DataError.Remote> {
        val errorType = when (response.status.value) {
            in 500..599 -> DataError.Remote.SERVER
            in 400..499 -> DataError.Remote.TOO_MANY_REQUESTS
            else -> DataError.Remote.UNKNOWN
        }
        println("[parseError] Status Code: ${response.status.value}, Error Type: $errorType") // Logging error parsing
        return Result.Error(errorType)
    }


    override suspend fun generateSpeech(
        text: String,
        outputFilePath: String
    ): Result<Unit, DataError> {
        val request = TtsRequest(
            model = "tts-1",
            voice = "alloy",
            input = text
        )

        return try {
            println("[generateSpeech] Request: $request") // Logging the request
            val response: HttpResponse =
                httpClient.post("https://api.openai.com/v1/audio/speech") {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $apiKey")
                        append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    }
                    setBody(request)
                }

            if (response.status.isSuccess()) {
                val audioData: ByteArray = response.body()
                getMediaPlayer().saveAudioFile(audioData, outputFilePath)
                println("[generateSpeech] Audio file saved to: $outputFilePath")
                Result.Success(Unit)
            } else {
                println("[generateSpeech] Error Body: ${response.bodyAsText()}")
                parseError(response)
            }
        } catch (e: IOException) {
            println("[generateSpeech] IOException: ${e.message}") // Logging IOException
            Result.Error(DataError.Remote.NO_INTERNET)
        } catch (e: TimeoutCancellationException) {
            println("[generateSpeech] TimeoutCancellationException: ${e.message}") // Logging Timeout
            Result.Error(DataError.Remote.REQUEST_TIMEOUT)
        } catch (e: Exception) {
            println("[generateSpeech] Exception: ${e.message}") // Logging unknown exceptions
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }


}
