package com.app.fastlearn.ui.screens.documents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.fastlearn.data.repository.DocumentRepository
import com.app.fastlearn.domain.model.Document
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class DocumentsViewModel @Inject constructor(
    private val documentRepository: DocumentRepository
) : ViewModel() {
    // Tạo danh sách tài liệu mẫu
    private val _documents = MutableStateFlow<List<Document>>(emptyList())
    val documents: StateFlow<List<Document>> = _documents


    init {
        // Tải danh sách tài liệu
        loadDocuments()
    }

    private fun loadDocuments() {
        viewModelScope.launch {
            documentRepository.getAllDocuments().collect {
                _documents.value = it
            }
        }
    }

    // Chức năng thêm tài liệu mới
    fun addDocument(docId: String, title: String, content: String, filePath: String, category: String, createdDate: LocalDateTime) {
        viewModelScope.launch {
            val newDocument = Document(docId, title, content, filePath, category, createdDate)
            documentRepository.insertDocument(newDocument) // Insert to DB
            // Sau khi thêm tài liệu mới, tải lại danh sách tài liệu
            loadDocuments()
        }
    }
}