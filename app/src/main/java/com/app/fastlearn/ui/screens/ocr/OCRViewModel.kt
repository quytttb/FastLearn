package com.app.fastlearn.ui.screens.ocr

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.fastlearn.data.repository.CategoryRepository
import com.app.fastlearn.data.repository.DocumentRepository
import com.app.fastlearn.data.util.DataMapper
import com.app.fastlearn.domain.model.Category
import com.app.fastlearn.domain.model.Document
import com.app.fastlearn.domain.usecase.OCRUseCase
import com.app.fastlearn.util.CameraHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

sealed class OCRProcessingState {
    object Idle : OCRProcessingState()
    object Processing : OCRProcessingState()
    data class Success(val text: String) : OCRProcessingState()
    data class Error(val errorMessage: String) : OCRProcessingState()
    object EmptyText : OCRProcessingState() // Trường hợp ảnh không có ký tự nhận dạng
}

@HiltViewModel
class OCRViewModel @Inject constructor(
    private val ocrUseCase: OCRUseCase,
    private val documentRepository: DocumentRepository,
    private val categoryRepository: CategoryRepository,
    private val dataMapper: DataMapper
) : ViewModel() {

    private val _capturedImageUri = MutableStateFlow<Uri?>(null)
    val capturedImageUri: StateFlow<Uri?> = _capturedImageUri.asStateFlow()

    private val _processingState = MutableStateFlow<OCRProcessingState>(OCRProcessingState.Idle)
    val processingState: StateFlow<OCRProcessingState> = _processingState.asStateFlow()

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.getAllCategories().collect { categoryList ->
                _categories.value = categoryList
            }
        }
    }

    // Thiết lập URI ảnh đã chụp
    fun setImageUri(uri: Uri) {
        _capturedImageUri.value = uri
    }

    // Xóa ảnh và reset trạng thái
    fun discardImage() {
        _capturedImageUri.value?.path?.let {
            try {
                val file = File(it)
                if (file.exists()) {
                    file.delete()
                }
            } catch (e: Exception) {
                Log.e("OCRViewModel", "Error deleting discarded image: ${e.message}")
            }
        }

        _capturedImageUri.value = null
        _processingState.value = OCRProcessingState.Idle
    }

    // Xử lý ảnh và thực hiện OCR
    fun processImage(context: Context) {
        _capturedImageUri.value?.let { uri ->
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    _processingState.value = OCRProcessingState.Processing

                    // Tạo đối tượng CameraHelper để quản lý file ảnh
                    val cameraHelper = CameraHelper(context)

                    // Sử dụng file trực tiếp nếu có thể (trường hợp ảnh đã ở trong cache)
                    val file = File(uri.path ?: "")

                    if (file.exists()) {
                        // Sử dụng file trực tiếp
                        val text = ocrUseCase.executeOCR(file)

                        // Kiểm tra kết quả OCR
                        if (text.isNullOrBlank()) {
                            _processingState.value = OCRProcessingState.EmptyText
                        } else {
                            _processingState.value = OCRProcessingState.Success(text)
                        }
                    } else {
                        // Nếu không tìm thấy file trực tiếp, thử chuyển đổi URI thành file
                        val tempFile = uriToFile(context, uri)

                        tempFile?.let {
                            val text = ocrUseCase.executeOCR(it)

                            if (text.isNullOrBlank()) {
                                _processingState.value = OCRProcessingState.EmptyText
                            } else {
                                _processingState.value = OCRProcessingState.Success(text)
                            }
                        } ?: run {
                            _processingState.value = OCRProcessingState.Error("Không thể xử lý ảnh")
                        }
                    }
                } catch (e: Exception) {
                    Log.e("OCRViewModel", "Error processing image: ${e.message}")
                    _processingState.value =
                        OCRProcessingState.Error(e.message ?: "Lỗi không xác định")
                }
            }
        }
    }

    // Chọn danh mục
    fun selectCategory(category: Category) {
        _selectedCategory.value = category
    }

    // Xác nhận văn bản và lưu tài liệu vào database
    fun confirmRecognizedText(title: String, text: String): Boolean {
        if (title.isBlank()) {
            return false
        }

        if (_selectedCategory.value == null) {
            return false
        }

        viewModelScope.launch {
            val documentId = UUID.randomUUID().toString()

            // Tạo đối tượng Document từ văn bản đã nhận diện
            val document = Document(
                docId = documentId,
                title = title.takeIf { it.isNotEmpty() } ?: "Document-${System.currentTimeMillis()}",
                content = text,
                // Không lưu đường dẫn ảnh vì ảnh sẽ bị xóa
                filePath = "",
                categoryId = _selectedCategory.value?.categoryId ?: "",
                createdDate = LocalDateTime.now()
            )

            // Lưu document vào repository
            documentRepository.insertDocument(document)

            // Xóa file ảnh tạm sau khi lưu document
            _capturedImageUri.value?.path?.let {
                try {
                    val file = File(it)
                    if (file.exists()) {
                        file.delete()
                    }
                } catch (e: Exception) {
                    Log.e("OCRViewModel", "Error deleting temp image: ${e.message}")
                }
            }
        }
        return true
    }

    // Chuyển đổi URI thành File
    private fun uriToFile(context: Context, uri: Uri): File? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            inputStream?.use { stream ->
                val bitmap = BitmapFactory.decodeStream(stream)
                if (bitmap == null) {
                    Log.e("OCRViewModel", "Failed to decode bitmap from input stream")
                    return null
                }

                val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
                FileOutputStream(file).use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                }
                file
            }
        } catch (e: Exception) {
            Log.e("OCRViewModel", "Error converting URI to file: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}