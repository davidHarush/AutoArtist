package com.auto.artist.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.auto.artist.db.ImageEntity
import com.auto.artist.ui.ImageViewModel
import com.auto.artist.ui.Route


@Composable
fun LoadingImageScreen(
    navController: NavController,
    viewModel: ImageViewModel,
    onImageReady: (ImageEntity) -> Unit
) {
    val imageEntityState by viewModel.generatedImageUrl.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val emptyDataError by viewModel.emptyDataError.collectAsState()

    LaunchedEffect(viewModel.launchedEffectKey()) {
        viewModel.createImage()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when {
            emptyDataError -> {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Missing data. Unable to create an image.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            navController.navigate(Route.Start.route) {
                                popUpTo(Route.Start.route) {
                                    inclusive = true
                                }
                            }
                        }
                    ) {
                        Text("Return to Home")
                    }
                }
            }

            isLoading -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Creating your image, please wait...",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            imageEntityState != null -> {
                onImageReady(imageEntityState!!)
                navController.navigate(Route.Image.route) {
                    popUpTo(Route.Image.route) {
                        inclusive = true
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "An unexpected error occurred.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            navController.navigate(Route.Start.route) {
                                popUpTo(Route.Start.route) {
                                    inclusive = true
                                }
                            }
                        }
                    ) {
                        Text("Return to Home")
                    }
                }
            }
        }
    }
}



