/*
package com.app.fastlearn.ui.screens.capture

import android.app.Activity
import android.content.Context
import android.util.Log
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
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject
import android.graphics.Bitmap
import java.io.FileOutputStream

@HiltViewModel
class CaptureViewModel @Inject constructor() : ViewModel() {

    private val _hasCameraPermission = MutableStateFlow(false)
    val hasCameraPermission: StateFlow<Boolean> = _hasCameraPermission.asStateFlow()

    private val _cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private val _imageCapture: ImageCapture = ImageCapture.Builder().build()
    private val _cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    // Kiểm tra và yêu cầu quyền camera
    fun checkAndRequestCameraPermission(context: Context, activity: Activity, requestCode: Int) {
        val hasPermission = Permission.hasCameraPermission(context)
        _hasCameraPermission.value = hasPermission
        if (!hasPermission) {
            Permission.requestCameraPermission(activity, requestCode)
        }
    }

    // Thiết lập màn hình camera
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

    // Xử lý hình ảnh đã chụp
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
                        onImageCaptured(photoFile)
                    }
                }
            }
        )
    }

    // Xóa file ảnh
    override fun onCleared() {
        super.onCleared()
        _cameraExecutor.shutdown()
    }

    // Lưu bitmap vào file
    fun saveBitmapToFile(context: Context, bitmap: Bitmap, onFileSaved: (File) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val name = SimpleDateFormat("yyyyMMddHHmmss", Locale.US)
                    .format(System.currentTimeMillis())
                val outputDirectory = context.cacheDir
                val photoFile = File(outputDirectory, "$name.jpg")

                FileOutputStream(photoFile).use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    outputStream.flush()
                }

                launch(Dispatchers.Main) {
                    onFileSaved(photoFile)
                }
            } catch (e: Exception) {
                Log.e("CaptureViewModel", "Error saving bitmap to file", e)
            }
        }
    }
}*/
