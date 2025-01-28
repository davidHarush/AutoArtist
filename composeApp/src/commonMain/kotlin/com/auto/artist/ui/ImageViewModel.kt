package com.auto.artist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auto.artist.db.ImageDao
import com.auto.artist.db.ImageEntity
import com.auto.artist.getMediaPlayer
import com.auto.artist.network.AiRepo
import com.auto.artist.network.DataError
import com.auto.artist.network.onError
import com.auto.artist.network.onSuccess
import com.auto.artist.ui.screens.ColorData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock


class ImageViewModel(
    private val repo: AiRepo,
    private val imageLinkDao: ImageDao
) : ViewModel() {
    private var selectedColors = listOf<ColorData>()
    private var selectedStyle: String = ""
    private var selectedTheme: String = ""
    private var selectedMood: String = ""


    fun launchedEffectKey(): Int = selectedColors.size + selectedStyle.length + selectedTheme.length


    private var _uiState = MutableStateFlow<UiState>(UiState.EMPTY)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    var allImages: MutableStateFlow<List<ImageEntity>?> = MutableStateFlow(emptyList())


    init {
        loadExistingImageLinks()
    }

    private fun loadExistingImageLinks() {
        viewModelScope.launch(Dispatchers.IO) {
            val images = imageLinkDao.getAllImages()

            withContext(Dispatchers.Main) {

                if (images.isEmpty()) {
                    allImages.value = null
                } else {
                    allImages.value = images
                    images.forEachIndexed { index, it ->
                        println("Image-$index: $it")
                        initAudioForPrompt(it)
                    }
                }
            }
        }
    }


    fun updateUiState(uiState: UiState) {
        println("Updating UiState to: $uiState")
        _uiState.update { uiState }
    }


    private suspend fun initAudioForPrompt(image: ImageEntity) {

        val fileId = image.timestamp.toString()
        val outputFilePath = getMediaPlayer().getOutputFilePath(fileId)

        if (getMediaPlayer().isFileExist(fileId)) {
            return
        }
        repo.generateSpeech(image.prompt, outputFilePath)
    }

    fun setSelectedColors(selectedColors: List<ColorData>): ImageViewModel {
        this.selectedColors = selectedColors
        return this
    }

    fun setSelectedStyle(style: String): ImageViewModel {
        selectedStyle = style
        return this
    }


    fun setSelectedTheme(selectedTheme: String) {
        this.selectedTheme = selectedTheme

    }

    fun setSelectedMood(mood: String) {
        this.selectedMood = mood

    }


    fun createImage() {
        println("Creating image...")

        viewModelScope.launch {
            _uiState.value = UiState.LOADING

            // Prepare keywords from selected colors and style
            val keywords = getKeyWords()
            println("dddddd-> Keywords: $keywords")
            if (keywords.isEmpty()) {
                _uiState.value = UiState.EMPTY
                return@launch
            }

            // Generate a prompt using the keywords
            val promptResult = repo.askForOptimalPrompt(keywords)

            promptResult.onSuccess { prompt ->
                // log the prompt
                println("Prompt: $prompt")

                // Use the prompt to generate an image
                val imageResult = repo.generateImage(prompt)

                imageResult.onSuccess { imageUrl ->
                    println("onSuccess: $imageUrl")
                    saveImageLink(imageUrl, prompt) // Save the generated image link to the database
                }.onError { error ->
                    handleError(error)
                }
            }.onError { error ->
                handleError(error)
            }
        }
    }


    private fun handleError(error: DataError) {
        println("handleError: $error")

        when (error) {
            DataError.Remote.NO_INTERNET -> {
                // Handle no internet connection case
                println("Error: No internet connection.")
            }

            DataError.Remote.REQUEST_TIMEOUT -> {
                // Handle request timeout case
                println("Error: Request timed out.")
            }

            DataError.Remote.TOO_MANY_REQUESTS -> {
                // Handle rate limit reached
                println("Error: Too many requests.")
            }

            DataError.Remote.SERVER -> {
                // Handle server error
                println("Error: Server issue encountered.")
            }

            DataError.Remote.SERIALIZATION -> {
                // Handle serialization error
                println("Error: Serialization issue.")
            }

            DataError.Remote.UNKNOWN -> {
                // Handle unknown error
                println("Error: An unknown error occurred.")
            }

            else -> {
                // Handle other errors
                println("Error: An error occurred.")

            }
        }
    }


    private fun getKeyWords(): MutableList<String> {

        val keywords = mutableListOf<String>()
        keywords.addAll(selectedColors.map { it.colorName })

        if (selectedStyle.isNotEmpty()) {
            keywords.add(selectedStyle)
        }
        if (selectedTheme.isNotEmpty()) {
            keywords.add(selectedTheme)
        }
        if (selectedMood.isNotEmpty()) {
            keywords.add(selectedMood)
        }
        return keywords
    }


    /**
     * Saves an image link to the local database.
     */
    private fun saveImageLink(url: String, prompt: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val timestamp = Clock.System.now().toEpochMilliseconds()
            val imageLink = ImageEntity(url = url, prompt = prompt, timestamp = timestamp)
            imageLinkDao.insertImageLink(imageLink)
            _uiState.update { UiState.READY(imageLink) }
            loadExistingImageLinks()
        }
    }

    fun removeImage(image: ImageEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            imageLinkDao.deleteImage(image.id)
            loadExistingImageLinks()
        }
    }


}

