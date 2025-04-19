package com.app.fastlearn.integration.services

import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class MLKitTextRecognizer {
    private val textRecognizer: TextRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    suspend fun processTextRecognition(inputImage: InputImage): String = suspendCancellableCoroutine { continuation ->
        textRecognizer.process(inputImage)
            .addOnSuccessListener { visionText ->
                continuation.resume(visionText.text)
            }
            .addOnFailureListener { e ->
                Log.e("MLKitTextRecognizer", "Text recognition failed: ${e.message}")
                continuation.resumeWithException(e)
            }
    }
}