package com.app.fastlearn.util

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CameraHelper(private val context: Context) {
    private var imageUri: Uri? = null

    // Tạo URI cho ảnh đã chụp
    fun createImageUri(): Uri? {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "IMG_$timestamp.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        }

        imageUri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )

        return imageUri
    }

    // Mở camera với launcher được cung cấp từ bên ngoài
    fun openCamera(takePictureLauncher: ActivityResultLauncher<Uri>) {
        val uri = createImageUri()
        uri?.let {
            takePictureLauncher.launch(it)
        }
    }

    // Lấy URI của ảnh hiện tại
    fun getCurrentImageUri(): Uri? {
        return imageUri
    }
}