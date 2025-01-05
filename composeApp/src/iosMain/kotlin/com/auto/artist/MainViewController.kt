package com.auto.artist

import androidx.compose.ui.window.ComposeUIViewController
import com.auto.artist.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) { App() }