package com.app.fastlearn.ui.screens.capture

import android.Manifest
import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController

@Composable
fun CaptureScreen(
    onImageCaptured: (String) -> Unit,
    viewModel: CaptureViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val activity = context as Activity

    val hasCamPermission by viewModel.hasCameraPermission.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            viewModel.checkAndRequestCameraPermission(context, activity, 0)
        }
    )

    LaunchedEffect(key1 = true) {
        viewModel.checkAndRequestCameraPermission(context, activity, 0)
        if (!hasCamPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    if (hasCamPermission) {
        CameraPreview(
            context = context,
            lifecycleOwner = lifecycleOwner,
            viewModel = viewModel,
            onImageCaptured = onImageCaptured
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
        ) {
            Text(
                text = "Camera permission denied",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CameraPreview(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    viewModel: CaptureViewModel,
    onImageCaptured: (String) -> Unit
) {
    val previewView = remember { PreviewView(context) }

    // Setup camera preview when the composable is first composed
    LaunchedEffect(previewView) {
        viewModel.setupCameraPreview(
            context = context,
            previewView = previewView,
            lifecycleOwner = lifecycleOwner,
            onSetupComplete = { success ->
                // Handle camera setup success/failure if needed
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView({ previewView }, modifier = Modifier)
        Button(
            onClick = {
                viewModel.takePhoto(
                    context = context,
                    onImageCaptured = { file ->
                        // Gọi callback từ NavGraph
                        onImageCaptured(file.name)
                    },
                    onError = { errorMsg ->
                        // Xử lý lỗi chụp ảnh
                        Log.e("CameraX", errorMsg)
                    }
                )
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        ) {
            Icon(Icons.Default.CameraAlt, contentDescription = "Take Photo")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Take Photo")
        }
    }
}