/*
package com.app.fastlearn.ui.screens.capture

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.app.simplecamera.ui.components.PermissionTemplate
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SimpleCameraApp(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var hasImage by remember { mutableStateOf(false) }

    // Kiểm tra quyền camera
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Tạo URI tạm thời cho file ảnh
    val createImageUri = remember {
        {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "IMG_$timestamp.jpg")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            }
            context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
        }
    }

    // Xử lý kết quả từ camera
    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            hasImage = true
        }
    }

    // Xử lý quyền camera
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (isGranted) {
            // Tự động mở camera sau khi được cấp quyền
            imageUri = createImageUri()
            imageUri?.let { uri ->
                takePictureLauncher.launch(uri)
            }
        }
    }

    // Hiển thị màn hình xin quyền nếu chưa có quyền
    if (!hasCameraPermission) {
        PermissionTemplate(
            modifier = modifier.fillMaxSize(),
            onRequestPermission = {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            },
            imageVector = Icons.Default.Camera,
            iconAccessibilityText = "Camera Permission",
            title = "Camera Access",
            bodyText = "Ứng dụng cần quyền truy cập vào camera để chụp ảnh",
            requestButtonText = "Cho phép truy cập"
        )
    } else {
        // Hiển thị UI chính sau khi đã được cấp quyền
        CameraContent(
            hasImage = hasImage,
            imageUri = imageUri,
            onOpenCamera = {
                imageUri = createImageUri()
                imageUri?.let { uri ->
                    takePictureLauncher.launch(uri)
                }
            }
        )
    }
}

@Composable
fun CameraContent(
    hasImage: Boolean,
    imageUri: Uri?,
    onOpenCamera: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (hasImage && imageUri != null) {
            Image(
                painter = rememberAsyncImagePainter(model = imageUri), // Fixed: using 'model' parameter
                contentDescription = "Ảnh đã chụp",
                modifier = Modifier
                    .size(300.dp)
                    .padding(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "URI: $imageUri",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        Button(
            onClick = onOpenCamera,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Mở máy ảnh",
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}*/
