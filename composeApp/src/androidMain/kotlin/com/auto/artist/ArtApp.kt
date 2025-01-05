package com.auto.artist

import android.app.Application
import android.content.Context
import com.auto.artist.di.initKoin
import org.koin.android.ext.koin.androidContext

class ArtApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        initKoin {
            androidContext(this@ArtApp)
        }
    }

    companion object {
        private lateinit var instance: ArtApp

        fun getApplicationContext(): Context {
            return instance.applicationContext
        }

    }


}