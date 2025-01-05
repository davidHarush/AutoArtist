package com.auto.artist

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.serialization.json.Json
import platform.AVFAudio.AVAudioPlayer
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIDevice
import platform.Foundation.*
import platform.AVFoundation.*
import platform.Foundation.*
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import coil3.Bitmap



class IOSPlatform : Platform {
    override val name: String =
        UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion

    override val isAndroid: Boolean
        get() = false

    override val isIOS: Boolean
        get() = true

    override fun createHttpClient(): HttpClient {
        return HttpClient(Darwin) {
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
                // שימוש במנוע Darwin עבור iOS
                Darwin.create()
            }
        }
    }

    override fun openInBrowser(url: String) {
        val nsUrl = NSURL.URLWithString(url)
        if (nsUrl != null) {
            UIApplication.sharedApplication.openURL(nsUrl)
        }
    }



//    override fun saveBitmapLocally(bitmap: Bitmap, fileName: String): String {
////        val documentsPath = NSSearchPathForDirectoriesInDomains(
////            NSDocumentDirectory,
////            NSUserDomainMask,
////            true
////        ).first() as String
////        val filePath = "$documentsPath/$fileName"
////        val fileUrl = NSURL.fileURLWithPath(filePath)
////
////        val bitmapData = bitmap.toNSData()
////        bitmapData.writeToURL(fileUrl, true)
////        return filePath
//        return ""
//    }

//    fun Bitmap.toNSData(): NSData {
//        val byteArray = this.toByteArray() // Convert bitmap to byte array
//        return byteArray.usePinned { pinned ->
//            NSData.create(bytes = pinned.addressOf(0), length = byteArray.size.toULong())
//        }
//    }
}

actual fun getPlatform(): Platform = IOSPlatform()