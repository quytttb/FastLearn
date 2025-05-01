package com.app.fastlearn.ui.screens.capture

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.fastlearn.domain.usecase.OCRUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class ImagePreviewViewModel @Inject constructor(
    private val ocrUseCase: OCRUseCase
) : ViewModel() {
    private val _capturedImageUri = MutableStateFlow<Uri?>(null)
    val capturedImageUri: StateFlow<Uri?> = _capturedImageUri.asStateFlow()

    // Thiết lập URI ảnh đã chụp
    fun setImageUri(uri: Uri) {
        _capturedImageUri.value = uri
    }

    // Xóa ảnh
    fun discardImage() {
        _capturedImageUri.value = null
    }

    // Xác nhận ảnh và thực hiện OCR
    fun confirmImage(context: Context, onTextRecognized: (String) -> Unit) {
        _capturedImageUri.value?.let { uri ->
            viewModelScope.launch(Dispatchers.IO) {
                // Chuyển đổi URI thành File
                val file = uriToFile(context, uri)

                file?.let {
                    // Sử dụng OCRUseCase để xử lý OCR
                    val id = ocrUseCase.executeOCR(it)
                    id?.let { textId ->
                        launch(Dispatchers.Main) {
                            onTextRecognized(textId)
                        }
                    }
                }
            }
        }
    }

    // Chuyển đổi URI thành File
    private fun uriToFile(context: Context, uri: Uri): File? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            inputStream?.use { stream ->
                val bitmap = BitmapFactory.decodeStream(stream)
                val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
                FileOutputStream(file).use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                }
                file
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}