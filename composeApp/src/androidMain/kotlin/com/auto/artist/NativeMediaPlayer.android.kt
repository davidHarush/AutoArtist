package com.auto.artist

import android.content.Context
import java.io.File

actual fun getMediaPlayer(): MediaPlayer {
    return NativeMediaPlayer
}

object NativeMediaPlayer : MediaPlayer {

    private var mediaPlayer = android.media.MediaPlayer()

    private fun fileName(context: Context, id: String): String
    = "${context.filesDir.path}/${id}+AudioFile.mp3"



    override fun saveAudioFile(audioData: ByteArray, outputFilePath: String) {
        val file = File(outputFilePath)
        file.writeBytes(audioData)
    }

    override fun getOutputFilePath(id: String): String {
        val context= ArtApp.getApplicationContext()
        return fileName(context,id)
    }

    override fun isFileExist(id: String): Boolean {

        val context= ArtApp.getApplicationContext()
        val file = File(fileName(context,id))
        return file.exists()
    }


    override fun playAudio(filePath: String) {
        stop()
        mediaPlayer = android.media.MediaPlayer().apply {
            setDataSource(filePath)
            prepare()
            start()
        }

        mediaPlayer.setOnCompletionListener {
            it.release()
            mediaPlayer =  android.media.MediaPlayer()
        }
    }

    override fun stop() {
        mediaPlayer.apply {
            stop()
            release()
        }
        mediaPlayer = android.media.MediaPlayer()
    }


}