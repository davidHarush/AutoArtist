package com.auto.artist.ui.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.auto.artist.db.ImageEntity
import com.auto.artist.getPlatform
import com.auto.artist.ui.AudioResult
import com.auto.artist.ui.AudioViewModel
import com.auto.artist.ui.Route


@Composable
fun ImageScreen(
    navController: NavController,
    audioViewModel: AudioViewModel,
    image: ImageEntity
) {

    LaunchedEffect(image.url) {
        audioViewModel.initAudioForPrompt(image!!)
    }

    var showLoadingImage = remember { true }


    val painter = rememberAsyncImagePainter(
        model = image.url,
        onState = { state ->
            println("Image state: $state")
            showLoadingImage = when (state) {
                is AsyncImagePainter.State.Success -> {
                    false
                }

                is AsyncImagePainter.State.Loading -> {
                    true
                }

                else -> {
                    false
                }

            }
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // round image with loading indicator

            Box(
                modifier = Modifier
                    .fillMaxWidth().clip(RoundedCornerShape(20.dp))
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {

                if (showLoadingImage) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        strokeWidth = 2.dp
                    )
                }
                Image(
                    painter = painter,
                    contentDescription = "Generated Image",
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            if (getPlatform().isAndroid) {
                AudioControls(viewModel = audioViewModel, image = image)
            }

            Text(
                text = image.prompt,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = {
                    audioViewModel.stopAudio()
                    navController.navigate(Route.Start.route) {
                        popUpTo(Route.Start.route) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp)
                    .height(48.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Text("Exit", style = MaterialTheme.typography.bodyLarge)
            }

        }
    }
}

@Composable
fun AudioControls(viewModel: AudioViewModel, image: ImageEntity) {
    val audioState by viewModel.audioFlow.collectAsState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (audioState is AudioResult.READY) {


            Button(
                onClick = {
                    viewModel.stopAudio()
                },
            ) {
                Text("Stop")
            }

            Button(
                onClick = {
                    viewModel.playAudio(image)
                },
            ) {
                Text("Play")
            }
        }

        if (audioState is AudioResult.LOADING) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.secondary,
                strokeWidth = 2.dp
            )
        }
    }
}
