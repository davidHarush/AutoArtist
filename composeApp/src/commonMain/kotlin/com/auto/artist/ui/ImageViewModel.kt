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


    private val _generatedImageUrl = MutableStateFlow<ImageEntity?>(null)
    val generatedImageUrl = _generatedImageUrl.asStateFlow()


    fun launchedEffectKey(): Int = selectedColors.size + selectedStyle.length + selectedTheme.length


    private val _generatedPrompt = MutableStateFlow("")
    val generatedPrompt = _generatedPrompt.asStateFlow()


    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _emptyDataError = MutableStateFlow(false)
    val emptyDataError = _emptyDataError.asStateFlow()

    // Load existing image links (optional)
    private val _existingImageLinksState = MutableStateFlow<UiState>(UiState.LOADING)
    val existingImageLinksState: StateFlow<UiState> = _existingImageLinksState.asStateFlow()


    init {
        loadExistingImageLinks()
    }

    /**
     * Load existing image links from the database
     */
    private fun loadExistingImageLinks() {
        _existingImageLinksState.value = UiState.LOADING
        viewModelScope.launch(Dispatchers.IO) {
            val links = imageLinkDao.getAllImages()

            withContext(Dispatchers.Main) {
                _existingImageLinksState.value = if (links.isEmpty()) {
                    UiState.EMPTY
                } else {
                    UiState.READY(links)
                }
                links.forEach {
                    initAudioForPrompt(it)
                }
            }
        }
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

        // log
        println("dddd-> Creating image...")

        viewModelScope.launch {
            _isLoading.value = true
            _generatedImageUrl.value = null
            _generatedPrompt.value = ""

            // Prepare keywords from selected colors and style
            val keywords = getKeyWords()
            println("Keywords: $keywords")
            if (keywords.isEmpty()) {
                _emptyDataError.value = true
                return@launch
            } else {
                _emptyDataError.value = false
            }

            // Generate a prompt using the keywords
            val promptResult = repo.askForOptimalPrompt(keywords)

            promptResult.onSuccess { prompt ->
                _generatedPrompt.value = prompt

                // log the prompt
                println("-->  Prompt: $prompt")

                // Use the prompt to generate an image
                val imageResult = repo.generateImage(prompt)

                imageResult.onSuccess { imageUrl ->
                    _isLoading.value = false
                    saveImageLink(imageUrl, prompt) // Save the generated image link to the database
                }.onError { error ->
                    handleError(error)
                }
            }.onError { error ->
                handleError(error)
            }

            _isLoading.value = false
        }
    }


    private fun handleError(error: DataError) {
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
            _generatedImageUrl.value = imageLink
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

