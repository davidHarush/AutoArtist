package com.auto.artist

import android.content.ContentValues
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.io.File
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import java.io.FileOutputStream

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"

    override val isAndroid: Boolean
        get() = true

    override val isIOS: Boolean
        get() = false

    override fun createHttpClient(): HttpClient {
        return HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 15000
                connectTimeoutMillis = 10000
                socketTimeoutMillis = 15000
            }
            engine {
                OkHttp.create()
            }
        }
    }

    override fun openInBrowser(url: String) {
        val context = ArtApp.getApplicationContext()
        try {
            Log.i("AndroidPlatform", "Opening Chrome Custom Tab")
            // log url
            Log.i("AndroidPlatform", "url: $url")
            Log.i("AndroidPlatform", "url: ${ Uri.parse(url)}")

            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            customTabsIntent.intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            customTabsIntent.launchUrl(context, Uri.parse(url))
        } catch (e: Exception) {
            println("Error opening Chrome Custom Tab: ${e.message}")
        }
    }

//     fun saveAudioFile(audioData: ByteArray, outputFilePath: String) {
//        val file = File(outputFilePath)
//        file.writeBytes(audioData)
//    }
//
//     fun getOutputFilePath(): String {
//        val context= ArtApp.getApplicationContext()
//        return "${context.filesDir.path}/output.mp3"
//    }
//
//
//    override fun playAudio(filePath: String) {
//        val mediaPlayer = MediaPlayer().apply {
//            setDataSource(filePath)
//            prepare()
//            start()
//        }
//
//        mediaPlayer.setOnCompletionListener {
//            it.release()
//        }
//    }


//   override fun saveBitmapLocally(bitmap: Bitmap, fileName: String): String {
//       val softwareBitmap = bitmap.toSoftwareBitmap()
//
//       val context = ArtApp.getApplicationContext()
//       val resolver = context.contentResolver
//       val contentValues = ContentValues().apply {
//           put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
//           put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
//           put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
//       }
//
//       resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)?.let { uri ->
//           resolver.openOutputStream(uri)?.use { outputStream ->
//               softwareBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
//           }
//           return uri.toString()
//       }
//       return ""
//   }

//    private fun Bitmap.toSoftwareBitmap(): Bitmap {
//        return if (this.config == Bitmap.Config.HARDWARE) {
//            this.copy(Bitmap.Config.ARGB_8888, false)
//        } else {
//            this
//        }
//    }




}

actual fun getPlatform(): Platform = AndroidPlatform()