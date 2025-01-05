package com.auto.artist

import io.ktor.client.HttpClient

interface Platform {
    val name: String
    val isAndroid: Boolean
    val isIOS: Boolean
    fun createHttpClient(): HttpClient
    fun openInBrowser(url: String)
}

expect fun getPlatform(): Platform

