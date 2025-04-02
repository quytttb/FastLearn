package com.app.fastlearn.ui.screens.capture

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import java.io.File

@Composable
fun CachedImagesScreen() {
    val context = LocalContext.current

    // Get list of image files from cache directory
    val cachedImageFiles = remember {
        context.cacheDir.listFiles { file ->
            file.canRead() && (file.name.endsWith(".jpg") || file.name.endsWith(".png"))
        }?.toList() ?: emptyList()
    }

    // State to track selected image for deletion
    var selectedImageToDelete by remember { mutableStateOf<File?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Cached Images (${cachedImageFiles.size})",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        if (cachedImageFiles.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No cached images found")
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(cachedImageFiles) { file ->
                    Card(
                        modifier = Modifier
                            .padding(4.dp)
                            .aspectRatio(1f),
                        onClick = {
                            // Optional: Add any action when image is clicked
                        }
                    ) {
                        Box {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(file)
                                    .build(),
                                contentDescription = "Cached Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            // Delete button
                            IconButton(
                                onClick = { selectedImageToDelete = file },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete Image",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Confirmation dialog for deletion
    selectedImageToDelete?.let { fileToDelete ->
        AlertDialog(
            onDismissRequest = { selectedImageToDelete = null },
            title = { Text("Delete Image") },
            text = { Text("Are you sure you want to delete this image?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Delete the file
                        fileToDelete.delete()
                        selectedImageToDelete = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { selectedImageToDelete = null }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
