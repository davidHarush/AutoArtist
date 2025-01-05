package com.auto.artist

import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFAudio.AVAudioPlayer
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL

actual fun getMediaPlayer(): MediaPlayer {
    return NativeMediaPlayer()
}

class NativeMediaPlayer : MediaPlayer {
    override fun saveAudioFile(audioData: ByteArray, outputFilePath: String) {

    }

    override fun getOutputFilePath(id: String): String {
        return ""
    }

    override fun isFileExist(id: String): Boolean {
        return false
    }

    override fun playAudio(filePath: String) {

    }

    override fun stop() {

    }
}
