package com.auto.artist.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.auto.artist.ui.ConfirmationButton
import com.auto.artist.ui.ImageViewModel
import com.auto.artist.ui.imageRouteNext


data class ColorData(
    val colorName: String,
    val color: Color,
    var isSelected: Boolean,
)

@Composable
fun ColorScreen(
    navController: NavController, viewModel: ImageViewModel
) {
    val colorOptions = remember {
        mutableStateListOf(
            ColorData("Red", Color(0xFFFF0000), false),          // Red
            ColorData("Orange", Color(0xFFFFA500), false),       // Orange
            ColorData("Yellow", Color(0xFFFFFF00), false),       // Yellow
            ColorData("Light Green", Color(0xFFADFF2F), false),  // Light Green
            ColorData("Green", Color(0xFF008000), false),        // Green
            ColorData("Turquoise", Color(0xFF40E0D0), false),    // Turquoise
            ColorData("Blue", Color(0xFF0000FF), false),         // Blue
            ColorData("Purple", Color(0xFF800080), false),       // Purple
            ColorData("Pink", Color(0xFFFFC0CB), false),         // Pink
            ColorData("Brown", Color(0xFF8B4513), false),        // Brown
            ColorData("Light Gray", Color(0xFFD3D3D3), false),   // Light Gray
            ColorData("Dark Gray", Color(0xFFA9A9A9), false),    // Dark Gray
            ColorData("Black", Color(0xFF000000), false)         // Black
        )
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            ColorGrid(
                colorOptions = colorOptions,
                onColorSelected = { index ->
                    val selectedCount = colorOptions.count { it.isSelected }
                    if (colorOptions[index].isSelected) {
                        colorOptions[index] = colorOptions[index].copy(isSelected = false)
                    } else if (selectedCount < 3) {
                        colorOptions[index] = colorOptions[index].copy(isSelected = true)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
        ConfirmationButton(
            onConfirm = {
                viewModel.setSelectedColors(colorOptions.filter { it.isSelected })
                navController.imageRouteNext()
            },
            isEnabled = colorOptions.count { it.isSelected } > 0)
    }
}

@Composable
fun ColorGrid(colorOptions: List<ColorData>, onColorSelected: (Int) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(1.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(colorOptions) { colorData ->
            ColorBox(
                colorData = colorData,
                onClick = {
                    val index = colorOptions.indexOf(colorData)
                    onColorSelected(index)
                }
            )
        }
    }
}

@Composable
fun ColorBox(colorData: ColorData, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .height(60.dp)
            .border(
                width = 4.dp,
                color = if (colorData.isSelected) Color.Black else Color.Transparent,
                shape = RoundedCornerShape(10.dp)
            )
            .background(
                color = colorData.color,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = colorData.colorName,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            style = MaterialTheme.typography.bodyLarge,

            )
    }
}