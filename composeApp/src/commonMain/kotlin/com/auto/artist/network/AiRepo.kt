package com.auto.artist.network

interface AiRepo {

    /**
     * Ask for optimal prompt based on the keywords
     */
    suspend fun askForOptimalPrompt(keywords: List<String>): Result<String, DataError>

    /**
     * Generate an image based on the prompt
     */
    suspend fun generateImage(prompt: String): Result<String, DataError>


    /**
     *
     */
    suspend fun generateSpeech(text: String, outputFilePath: String): Result<Unit, DataError>


}