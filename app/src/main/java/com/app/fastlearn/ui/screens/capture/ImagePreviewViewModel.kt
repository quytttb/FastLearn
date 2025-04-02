package com.app.fastlearn.ui.screens.capture

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.fastlearn.domain.model.RecognizedText
import com.app.fastlearn.domain.repository.RecognizedTextRepository
import com.app.fastlearn.domain.service.OCRService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ImagePreviewViewModel @Inject constructor(
    private val ocrService: OCRService,
    private val recognizedTextRepository: RecognizedTextRepository
) : ViewModel() {
    private val _capturedImageFile = MutableStateFlow<File?>(null)
    val capturedImageFile: StateFlow<File?> = _capturedImageFile.asStateFlow()

    // Thiết lập file ảnh đã chụp
    fun setImageFile(file: File) {
        _capturedImageFile.value = file
    }

    // Xóa ảnh
    fun discardImage() {
        _capturedImageFile.value?.delete()
        _capturedImageFile.value = null
    }

    // Xác nhận ảnh và thực hiện OCR
    fun confirmImage(onTextRecognized: (String) -> Unit) {
        _capturedImageFile.value?.let { file ->
            viewModelScope.launch {
                // OCR và lưu trữ văn bản đã nhận diện
                val recognizedText = ocrService.recognizeTextFromImage(file)
                recognizedText?.let {
                    // Lưu trữ văn bản đã nhận diện vào kho dữ liệu trung gian
                    val id = recognizedTextRepository.saveRecognizedText(
                        RecognizedText(
                            id = file.nameWithoutExtension,
                            text = it
                        )
                    )
                    onTextRecognized(id)
                    // Đặt log để kiểm tra
//                    Log.d(
//                        "ImagePreviewViewModel",
//                        "Recognized text saved with ID: $id"
//                    )
                    // Xóa file ảnh sau khi đã nhận diện
                    file.delete()
                }
            }
        }
    }
}