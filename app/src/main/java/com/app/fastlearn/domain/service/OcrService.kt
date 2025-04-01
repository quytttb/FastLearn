/*
package com.app.fastlearn.domain.service

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class OcrService(private val context: Context) {

    suspend fun processImage(bitmap: Bitmap): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            val image = InputImage.fromBitmap(bitmap, 0)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            val result = recognizer.process(image)
                .await()
            Result.success(result.text)
        } catch (e: Exception) {
            Log.e("OcrService", "Error processing image", e)
            Result.failure(e)
        }
    }

    private suspend fun com.google.android.gms.tasks.Task<com.google.mlkit.vision.text.Text>.await(): com.google.mlkit.vision.text.Text {
        return withContext(Dispatchers.IO) {
            kotlin.coroutines.suspendCancellableCoroutine { cont ->
                addOnSuccessListener { result ->
                    cont.resume(result, null)
                }
                addOnFailureListener { exception ->
                    cont.resumeWithException(exception)
                }
            }
        }
    }
}*/
