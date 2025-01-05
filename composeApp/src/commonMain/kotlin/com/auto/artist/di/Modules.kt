package com.auto.artist.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.auto.artist.db.DatabaseFactory
import com.auto.artist.db.ImageDatabase
import com.auto.artist.network.AiRepo
import com.auto.artist.network.HttpClientFactory
import com.auto.artist.network.MockRepo
import com.auto.artist.network.OpenAiRepo
import com.auto.artist.ui.AudioViewModel
import com.auto.artist.ui.ImageViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module


expect val platformModule: Module


val sharedModule = module {

    val mock = named("mock")
    val openai = named("openai")
    val isMock = false


    // HttpClient injection
    single { HttpClientFactory.create(get()) }

    single {
        CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }

    // Injection for OpenAiRepo as AiRepo
    single<AiRepo>(openai) { OpenAiRepo(get()) }
    single<AiRepo>(mock) { MockRepo(get()) }


    // Database Injection - Injection of ImageDatabase
//    single {
//        val databaseFactory: DatabaseFactory = get()
//        databaseFactory.create()
//    }

    single {
        get<DatabaseFactory>().create()
            .setDriver(BundledSQLiteDriver())
            .build()
    }

    // DAO Injection - Injection of DAO from the database
    single { get<ImageDatabase>().imageDao() }


    // Injection for ImageViewModel
    if (isMock) {
        viewModel { ImageViewModel(get(named("mock")), get()) }
        viewModel { AudioViewModel(get(named("mock"))) }
    } else {
        viewModel { ImageViewModel(get(named("openai")), get()) }
        viewModel { AudioViewModel(get(named("openai"))) }

    }
//    viewModel { ImageViewModel(get(named("mock")), get()) }
//    viewModel { AudioViewModel(get(named("mock"))) }
}
