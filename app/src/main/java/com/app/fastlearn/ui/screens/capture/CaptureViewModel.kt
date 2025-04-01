package com.app.fastlearn.ui.screens.capture

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.fastlearn.util.Permission
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

@HiltViewModel
class CaptureViewModel @Inject constructor() : ViewModel() {

    private val _hasCameraPermission = MutableStateFlow(false)
    val hasCameraPermission: StateFlow<Boolean> = _hasCameraPermission.asStateFlow()

    private val _capturedImageFile = MutableStateFlow<File?>(null)
    val capturedImageFile: StateFlow<File?> = _capturedImageFile.asStateFlow()

    private val _cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private val _imageCapture: ImageCapture = ImageCapture.Builder().build()
    private val _cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    // Check and request camera permission
    fun checkAndRequestCameraPermission(context: Context, activity: Activity, requestCode: Int) {
        val hasPermission = Permission.hasCameraPermission(context)
        _hasCameraPermission.value = hasPermission
        if (!hasPermission) {
            Permission.requestCameraPermission(activity, requestCode)
        }
    }

    // Set up camera preview
    fun setupCameraPreview(
        context: Context,
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner,
        onSetupComplete: (Boolean) -> Unit
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            try {
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.surfaceProvider = previewView.surfaceProvider
                    }

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    _cameraSelector,
                    preview,
                    _imageCapture
                )

                onSetupComplete(true)
            } catch (exc: Exception) {
                Log.e("CameraX", "Binding use case failed", exc)
                onSetupComplete(false)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    // Take photo
    fun takePhoto(
        context: Context,
        onImageCaptured: (File) -> Unit,
        onError: (String) -> Unit
    ) {
        val name = SimpleDateFormat("yyyyMMddHHmmss", Locale.US)
            .format(System.currentTimeMillis())
        val outputDirectory = context.cacheDir
        val photoFile = File(outputDirectory, "$name.jpg")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        _imageCapture.takePicture(
            outputOptions,
            _cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    val errorMsg = "Photo capture failed: ${exc.message}"
                    Log.e("CameraX", errorMsg, exc)
                    onError(errorMsg)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    viewModelScope.launch {
                        Log.d("CameraX", "Photo capture succeeded: ${photoFile.name}")
                        processImageCaptured(photoFile, onImageCaptured)
                    }
                }
            }
        )
    }

    // Process the captured image
    private fun processImageCaptured(file: File, onImageCaptured: (File) -> Unit) {
        _capturedImageFile.value = file
        onImageCaptured(file)
    }

    // Clean up resources
    override fun onCleared() {
        super.onCleared()
        _cameraExecutor.shutdown()
    }
}