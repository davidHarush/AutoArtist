package com.auto.artist


interface MediaPlayer {
    fun saveAudioFile(audioData: ByteArray, outputFilePath: String)
    fun getOutputFilePath(id: String): String
    fun isFileExist(id: String): Boolean
    fun playAudio(filePath: String)
    fun stop()
}

expect fun getMediaPlayer(): MediaPlayer