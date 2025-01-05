package com.auto.artist.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.auto.artist.ui.ConfirmationButton
import com.auto.artist.ui.ImageViewModel
import com.auto.artist.ui.SelectableCardGrid
import com.auto.artist.ui.imageRouteNext

@Composable
fun MoodScreen(
    navController: NavController,
    viewModel: ImageViewModel
) {
    val moods = listOf(
        "Cheerful",
        "Calm",
        "Romantic",
        "Sad",
        "Angry",
        "Mysterious",
        "Energetic",
        "Playful"
    )

    var selectedMood by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SelectableCardGrid(
            items = moods,
            selectedItem = selectedMood,
            onItemSelected = { selectedMood = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        ConfirmationButton(
            onConfirm = {
                selectedMood?.let { mood ->
                    viewModel.setSelectedMood(mood)
                    navController.imageRouteNext()
                }
            },
            isEnabled = selectedMood != null
        )
    }
}
