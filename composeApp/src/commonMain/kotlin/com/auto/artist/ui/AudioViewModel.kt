package com.auto.artist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auto.artist.db.ImageEntity
import com.auto.artist.getMediaPlayer
import com.auto.artist.network.AiRepo
import com.auto.artist.network.DataError
import com.auto.artist.network.onError
import com.auto.artist.network.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class AudioResult {
    data object EMPTY : AudioResult()
    data object LOADING : AudioResult()
    data object READY : AudioResult()
    data object ERROR : AudioResult()
}

class AudioViewModel(
    private val repo: AiRepo,
) : ViewModel() {

    private var _audio = MutableStateFlow<AudioResult>(AudioResult.READY)
    val audioFlow = _audio


    fun initAudioForPrompt(image: ImageEntity) {
        _audio.update { AudioResult.LOADING }

        val fileId = image.timestamp.toString()
        val outputFilePath = getMediaPlayer().getOutputFilePath(fileId)

        if (getMediaPlayer().isFileExist(fileId)) {
            _audio.update { AudioResult.READY }
            return
        }

        viewModelScope.launch {
            val result = repo.generateSpeech(image.prompt, outputFilePath)
            result.onSuccess {
                _audio.update { AudioResult.READY }
            }.onError { error ->
                _audio.update { AudioResult.ERROR }
                handleSpeechError(error)
            }
        }
    }

    fun playAudio(image: ImageEntity) {
        val fileId = image.timestamp.toString()

        viewModelScope.launch {
            val outputFilePath = getMediaPlayer().getOutputFilePath(fileId)
            getMediaPlayer().playAudio(outputFilePath)
        }
    }

    fun stopAudio() {
        getMediaPlayer().stop()
    }

    private fun handleSpeechError(result: DataError) {
        when (result) {
            DataError.Remote.NO_INTERNET -> println("No internet connection")
            DataError.Remote.REQUEST_TIMEOUT -> println("Request timed out")
            else -> println("Unknown error occurred")
        }
    }

}