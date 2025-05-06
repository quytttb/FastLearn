package com.app.fastlearn.util

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CameraHelper(private val context: Context) {
    private var imageFile: File? = null
    private var imageUri: Uri? = null

    // Tạo file tạm trong thư mục cache và trả về URI cho camera
    fun createImageUri(): Uri? {
        try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "IMG_$timestamp.jpg"

            // Tạo file trong thư mục cache
            imageFile = File(context.cacheDir, fileName)

            // Sử dụng FileProvider để tạo URI có thể chia sẻ với camera
            imageUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                imageFile!!
            )

            return imageUri
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
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

    // Lấy File của ảnh hiện tại
    fun getCurrentImageFile(): File? {
        return imageFile
    }

    // Xóa file ảnh tạm sau khi xử lý xong
    fun deleteCurrentImage(): Boolean {
        return imageFile?.let {
            if (it.exists()) {
                it.delete()
            } else {
                false
            }
        } ?: false
    }

    // Phương thức dọn dẹp tất cả ảnh tạm thời trong thư mục cache
    fun cleanupTempImages() {
        try {
            val cacheDir = context.cacheDir
            val files = cacheDir.listFiles { file ->
                file.isFile && file.name.startsWith("IMG_") && file.name.endsWith(".jpg")
            }

            files?.forEach { file ->
                file.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}