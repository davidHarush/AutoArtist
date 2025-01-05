package com.auto.artist.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SelectableCardGrid(
    items: List<String>,
    selectedItem: String?,
    onItemSelected: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items.size) { index ->
            Card(
                modifier = Modifier
                    .fillMaxWidth().clickable {
                        onItemSelected(items[index])
                    }
                    .padding(4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedItem == items[index]) Color.Blue else Color.Gray
                ),
                shape = MaterialTheme.shapes.medium,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = items[index],
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                }
            }
        }
    }
}

//
//
//@Composable
//fun SelectableCardGrid(
//    items: List<String>,
//    selectedItem: String?,
//    onItemSelected: (String) -> Unit
//) {
//    LazyVerticalGrid(
//        columns = GridCells.Fixed(2),
//        modifier = Modifier
//            .fillMaxSize(1f),
//        verticalArrangement = Arrangement.spacedBy(8.dp),
//        horizontalArrangement = Arrangement.spacedBy(8.dp)
//    ) {
//        items(items.size) { index ->
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .clickable {
//                        onItemSelected(items[index])
//                    }
//                    .padding(4.dp),
//                colors = CardDefaults.cardColors(
//                    containerColor = if (selectedItem == items[index]) Color.Blue else Color.Gray
//                ),
//                shape = MaterialTheme.shapes.medium,
//            ) {
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(16.dp),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text(
//                        text = items[index],
//                        style = MaterialTheme.typography.bodyLarge,
//                        color = Color.White
//                    )
//                }
//            }
//        }
//    }
//}
//
//
