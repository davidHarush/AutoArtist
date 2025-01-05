package com.auto.artist.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.auto.artist.db.ImageEntity
import com.auto.artist.ui.ImageViewModel
import com.auto.artist.ui.Route
import com.auto.artist.ui.UiState


@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: ImageViewModel,
    onImageClick: (ImageEntity) -> Unit
) {

    val generatedImagesState = viewModel.existingImageLinksState.collectAsState()

    if (generatedImagesState.value.isLoading()) {
        Box(
            modifier = Modifier.fillMaxSize()
                .background(Color.White)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

    }

    if (generatedImagesState.value.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Welcome to Auto Artist",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    navController.navigate(Route.Color.route)
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Create New Image")
            }

        }
    }
    if (generatedImagesState.value.isReady()) {
        val readyState = generatedImagesState.value as UiState.READY<List<ImageEntity>>
        val generatedImages = readyState.data

        showGallery(
            navController = navController,
            generatedImages = generatedImages,
            viewModel = viewModel,
            onImageClick = onImageClick
        )
    }


}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun showGallery(
    navController: NavController,
    generatedImages: List<ImageEntity>,
    viewModel: ImageViewModel,
    onImageClick: (ImageEntity) -> Unit
) {
    val (selectedImageId, setSelectedImageId) = remember { mutableStateOf(0) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                navController.navigate(Route.Color.route)
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Create New Image")
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(generatedImages.size) { index ->
                val image = generatedImages[index]

                Card(
                    modifier = Modifier
                        .padding(1.dp)
                        .combinedClickable(
                            onClick = {
                                onImageClick(image)
                                navController.navigate(Route.Image.route)
                            },
                            onLongClick = {
                                setSelectedImageId(image.id)
                            }
                        ),
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {


                        AsyncImage(
                            model = image.url,
                            contentDescription = "Generated Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .aspectRatio(1f)
                        )

                        if (selectedImageId == image.id) {
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .zIndex(3f)
                                    .background(Color.White.copy(alpha = 0.7f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.Red.copy(alpha = 0.7f),
                                    modifier = Modifier
                                        .size(48.dp)
                                        .align(Alignment.Center)
                                        .clickable {
                                            viewModel.removeImage(image)
                                            setSelectedImageId(0)
                                        }
                                )
                            }
                        }

                    }
                }
            }
        }
    }
}


