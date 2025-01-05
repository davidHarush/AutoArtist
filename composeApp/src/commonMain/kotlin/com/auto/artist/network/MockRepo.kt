package com.auto.artist.network

import com.auto.artist.db.ImageDao
import com.auto.artist.db.ImageEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


class MockRepo(
    private val imageDao: ImageDao,
) : AiRepo {
    companion object {
        private var images: List<ImageEntity> = emptyList()
        private var selectedImage: ImageEntity? = null
    }

    init {
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch { init() }
    }


    private suspend fun init(): AiRepo {
        images = imageDao.getAllImages()
        selectedImage = images.randomOrNull()
        return this
    }

    override suspend fun askForOptimalPrompt(keywords: List<String>): Result<String, DataError> {
        val prompt = if (keywords.isEmpty()) {
            "No keywords provided."
        } else {
            "Mock prompt: ${keywords.joinToString(", ")}"
        }
        return Result.Success(prompt)
    }

    override suspend fun generateImage(prompt: String): Result<String, DataError> {

        return Result.Success(selectedImage!!.url)

    }

    override suspend fun generateSpeech(
        text: String,
        outputFilePath: String
    ): Result<Unit, DataError> {
        return Result.Success(Unit)
    }
}
