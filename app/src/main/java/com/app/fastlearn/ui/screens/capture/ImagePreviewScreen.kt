package com.app.fastlearn.ui.screens.capture

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.fastlearn.R
import coil3.compose.AsyncImage
import androidx.core.net.toUri

@Composable
fun ImagePreviewScreen(
    modifier: Modifier = Modifier,
    imageUri: String,
    onConfirmNavigate: (String) -> Unit,
    onDiscardNavigate: () -> Unit,
    viewModel: ImagePreviewViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // Thiết lập URI ảnh khi màn hình được tải
    LaunchedEffect(imageUri) {
        viewModel.setImageUri(imageUri.toUri())
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Hiển thị ảnh trong Surface với định dạng giống CaptureScreen
        val capturedImageUri = viewModel.capturedImageUri.collectAsState().value
        capturedImageUri?.let {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3f/4f) // Tỷ lệ 3:4 (phổ biến cho ảnh dọc)
                    .clip(RoundedCornerShape(16.dp))
                    .align(Alignment.Center),
                shadowElevation = 4.dp
            ) {
                AsyncImage(
                    model = it,
                    contentDescription = stringResource(id = R.string.captured_image_description),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Hàng với các nút xóa, ok ở dưới cùng
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Nút X
            FloatingActionButton(
                onClick = {
                    viewModel.discardImage()
                    onDiscardNavigate()
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = stringResource(id = R.string.discard)
                )
            }

            // Nút xác nhận
            FloatingActionButton(
                onClick = {
                    viewModel.confirmImage(context) { recognizedText ->
                        onConfirmNavigate(recognizedText)
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = stringResource(id = R.string.confirm)
                )
            }
        }
    }
}