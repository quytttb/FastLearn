package com.app.fastlearn.domain.service

import android.content.Context
import android.util.Log
import com.app.fastlearn.integration.services.MLKitTextRecognizer
import com.app.fastlearn.util.ImageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class OCRService(private val context: Context) {
    private val textRecognizer = MLKitTextRecognizer()

    // Hàm nhận diện văn bản từ ảnh lưu trong cache
    suspend fun recognizeTextFromImage(imageFile: File): String? {
        // Chuyển đổi File thành InputImage để ML Kit xử lý
        val inputImage = ImageUtils.fileToInputImage(imageFile)

        return withContext(Dispatchers.IO) {
            try {
                val result = textRecognizer.processTextRecognition(inputImage)
                Log.d("OCRService", "Recognition successful: $result")
                result
            } catch (e: Exception) {
                Log.e("OCRService", "Error recognizing text: ${e.message}")
                null
            }
        }
    }
}