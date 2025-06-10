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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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


@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: ImageViewModel,
    onImageClick: (ImageEntity) -> Unit
) {
    val imagesState = viewModel.allImages.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Create", "Gallery", "History")

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                0 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
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

                1 -> {
                    if (imagesState.value?.isEmpty() == true) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                if (viewModel.allImages.value?.isNotEmpty() == true) {
                    showGallery(
                        navController = navController,
                        generatedImages = viewModel.allImages.value!!,
                        viewModel = viewModel,
                        onImageClick = onImageClick
                    )
                }

                if (imagesState.value == null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No images yet")
                    }
                    }
                }

                2 -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Coming soon...")
                    }
                }
            }
        }

        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }
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


