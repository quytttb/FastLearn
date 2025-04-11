package com.app.fastlearn.domain.service

import android.content.Context
import android.util.Log
import com.app.fastlearn.util.ImageUtils
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class OCRService(private val context: Context) {

    private val textRecognizer: TextRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    // Hàm nhận diện văn bản từ ảnh lưu trong cache
    suspend fun recognizeTextFromImage(imageFile: File): String? {
        // Chuyển đổi File thành InputImage để ML Kit xử lý
        val inputImage: InputImage = ImageUtils.fileToInputImage(imageFile)

        return withContext(Dispatchers.IO) {
            try {
                val result = processTextRecognition(inputImage)
                Log.d("OCRService", "Recognition successful: $result")
                result
            } catch (e: Exception) {
                Log.e("OCRService", "Error recognizing text: ${e.message}")
                null
            }
        }
    }

    // Hàm xử lý nhận diện văn bản
    private suspend fun processTextRecognition(inputImage: InputImage): String = suspendCancellableCoroutine { continuation ->
        textRecognizer.process(inputImage)
            .addOnSuccessListener { visionText ->
                continuation.resume(visionText.text) // Trả về văn bản đã nhận diện
            }
            .addOnFailureListener { e ->
                continuation.resumeWithException(e) // Trả về lỗi nếu có
            }
    }
}
