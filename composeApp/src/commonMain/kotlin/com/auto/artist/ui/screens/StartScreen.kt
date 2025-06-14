package com.auto.artist.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.auto.artist.db.ImageEntity
import com.auto.artist.ui.ImageViewModel
import com.auto.artist.ui.Route
import com.auto.artist.ui.screens.HistoryScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: ImageViewModel,
    onImageClick: (ImageEntity) -> Unit
) {

    val imagesState = viewModel.allImages.collectAsState()
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                imagesState.value?.isEmpty() == true -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }
                }

                viewModel.allImages.value?.isNotEmpty() == true -> {
                    Gallery(
                        navController = navController,
                        generatedImages = viewModel.allImages.value!!,
                        viewModel = viewModel,
                        onImageClick = onImageClick
                    )
                }

                imagesState.value == null -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(8.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Welcome to Auto Artist",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
}




@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Gallery(
    navController: NavController,
    generatedImages: List<ImageEntity>,
    viewModel: ImageViewModel,
    onImageClick: (ImageEntity) -> Unit
) {
    val (selectedImageId, setSelectedImageId) = remember { mutableStateOf(0) }

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        verticalItemSpacing = 2.dp,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.fillMaxSize().padding(2.dp)
    ) {
        items(generatedImages.size) { index ->
            val image = generatedImages[index]
            val aspect = if (index % 5 == 0)
                1.4f
            else if (index % 3 == 0)
                1.2f
            else 1f

            Card(
                modifier = Modifier
                    .padding(1.dp)
                    .combinedClickable(
                        onClick = { onImageClick(image) },
                        onLongClick = { setSelectedImageId(image.id) }
                    ),
                shape = RoundedCornerShape(12.dp),
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    AsyncImage(
                        model = image.url,
                        contentDescription = "Generated Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(aspect)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartTabsScreen(
    navController: NavController,
    viewModel: ImageViewModel,
    onImageClick: (ImageEntity) -> Unit,
) {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { CenterAlignedTopAppBar(title = { Text("Auto Artist") }) },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Gallery") },
                    label = { Text("Gallery") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        navController.navigate(Route.Color.route)
                    },
                    icon = { Icon(Icons.Default.Add, contentDescription = "New Image") },
                    label = { Text("New Image") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "History") },
                    label = { Text("History") }
                )
            }
        }
    ) { paddingValues ->
        when (selectedTab) {
            0 -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                HomeScreen(
                    navController = navController,
                    viewModel = viewModel,
                    onImageClick = onImageClick
                )
            }

            2 -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                HistoryScreen()
            }
        }
    }
}


