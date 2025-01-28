package com.auto.artist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.auto.artist.db.ImageEntity
import com.auto.artist.ui.AudioViewModel
import com.auto.artist.ui.ImageViewModel
import com.auto.artist.ui.Route
import com.auto.artist.ui.TopNavigationBar
import com.auto.artist.ui.UiState
import com.auto.artist.ui.back
import com.auto.artist.ui.imageRouteNext
import com.auto.artist.ui.screens.ColorScreen
import com.auto.artist.ui.screens.ConceptsScreen
import com.auto.artist.ui.screens.HomeScreen
import com.auto.artist.ui.screens.ImageScreen
import com.auto.artist.ui.screens.LoadingImageScreen
import com.auto.artist.ui.screens.MoodScreen
import com.auto.artist.ui.screens.StyleScreen
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel


@Composable
@Preview
fun App() {
    MaterialTheme {
        MainNavGraph()
    }
}


@Composable
fun MainNavGraph(
    imageViewModel: ImageViewModel = koinViewModel(),
    audioViewModel: AudioViewModel = koinViewModel(),
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.Start.route
    ) {
        composable(Route.Start.route) {
            println("ddd -> Start route")
            Scaffold { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    HomeScreen(
                        navController = navController,
                        viewModel = imageViewModel,
                        onImageClick = { imageEntity ->
                            println("ddd -> onImageClick prompt: ${imageEntity.prompt}")
                            imageViewModel.updateUiState(UiState.READY(imageEntity))
                            println("UiState updated to READY with image: $imageEntity")
                            navController.navigate(Route.Image.route)
                        }
                    )
                }
            }
        }

        composable(Route.Color.route) {
            WarpScreen(
                title = "Select up to 3 colors",
                navHostController = navController,
                currentPage = 1,
                totalPages = 3
            ) {
                ColorScreen(navController = navController, viewModel = imageViewModel)
            }
        }

        composable(Route.Mode.route) {
            WarpScreen(
                title = "Mode style",
                navHostController = navController,
                currentPage = 2,
                totalPages = 4
            ) {
                MoodScreen(navController = navController, viewModel = imageViewModel)
            }
        }
        composable(Route.Style.route) {
            WarpScreen(
                title = "Painting style",
                navHostController = navController,
                currentPage = 3,
                totalPages = 4
            ) {
                StyleScreen(navController = navController, viewModel = imageViewModel)
            }
        }
        composable(Route.Concepts.route) {
            WarpScreen(
                title = "Concepts",
                navHostController = navController,
                currentPage = 4,
                totalPages = 4
            ) {
                ConceptsScreen(navController = navController, viewModel = imageViewModel)
            }

        }
        composable(Route.Final.route) {
            println("LoadingImageScreen route")

            Scaffold { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    LoadingImageScreen(
                        navController = navController,
                        viewModel = imageViewModel,
                        onImageReady = { imageEntity ->
                            imageViewModel.updateUiState(UiState.READY(imageEntity))

                        }
                    )

                }
            }
        }

        composable(Route.Image.route) {
            val state = imageViewModel.uiState.collectAsState()

            when (state.value) {
                is UiState.LOADING -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is UiState.READY<*> -> {
                    val image = state.value.getReadyData<ImageEntity>()
                    Scaffold { paddingValues ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                        ) {
                            ImageScreen(
                                image = image!!,
                                navController = navController,
                                audioViewModel = audioViewModel
                            )
                        }
                    }
                }

                else -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Error")
                    }
                }
            }
        }


    }
}

// warped for all screens using Scaffold and TopNavigationBar
@Composable
fun WarpScreen(
    title: String,
    navHostController: NavController,
    currentPage: Int,
    totalPages: Int,
    content: @Composable () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize().statusBarsPadding(),
        topBar = {
            TopNavigationBar(
                title = title,
                onBack = navHostController::back,
                onSkip = navHostController::imageRouteNext,
                currentPage = currentPage,
                totalPages = totalPages
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            content()
        }
    }

}