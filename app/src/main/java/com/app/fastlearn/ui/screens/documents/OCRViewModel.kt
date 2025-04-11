package com.app.fastlearn.ui.screens.documents

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.fastlearn.data.repository.DocumentRepository
import com.app.fastlearn.data.util.DataMapper
import com.app.fastlearn.domain.model.Document
import com.app.fastlearn.domain.model.RecognizedText
import com.app.fastlearn.domain.repository.RecognizedTextRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class OCRViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val recognizedTextRepository: RecognizedTextRepository,
    private val documentRepository: DocumentRepository, // Thêm dòng này
    private val dataMapper: DataMapper // Thêm dòng này
) : ViewModel() {

    private val recognizedTextId: String = savedStateHandle["recognizedTextId"] ?: ""

    private val _recognizedText = MutableStateFlow<RecognizedText?>(null)
    val recognizedText: StateFlow<RecognizedText?> = _recognizedText.asStateFlow()

    init {
        // Tải văn bản đã nhận diện từ kho dữ liệu
        loadRecognizedText()
    }

    private fun loadRecognizedText() {
        if (recognizedTextId.isNotEmpty()) {
            viewModelScope.launch {
                _recognizedText.value = recognizedTextRepository.getRecognizedTextById(recognizedTextId)
            }
        } else {
            Log.e("OCRViewModel", "recognizedTextId is empty")
        }
    }

    // Xóa văn bản đã nhận diện
    fun deleteRecognizedText() {
        _recognizedText.value = null
    }

    // Xác nhận ảnh và lưu tài liệu vào database
    fun confirmRecognizedText(title: String, text: String) {
        viewModelScope.launch {
            // Tạo đối tượng Document từ văn bản đã nhận diện
            val document = Document(
                docId = recognizedTextId,
                title = title, // Bạn có thể thay đổi tiêu đề tùy ý
                content = text,
                filePath = "", // Đường dẫn tệp, nếu có
                category = "", // Danh mục, nếu có
                createdDate = LocalDateTime.now()
            )

            // Lưu document trực tiếp vào repository
            // Không cần data mapper thành DocumentEntity trước khi lưu
            // vì DocumentRepository đã xử lý việc này
            documentRepository.insertDocument(document)
        }
    }
}
