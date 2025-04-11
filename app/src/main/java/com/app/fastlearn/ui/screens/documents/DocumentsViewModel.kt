package com.app.fastlearn.ui.screens.documents

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.fastlearn.BuildConfig
import com.app.fastlearn.data.repository.DocumentRepository
import com.app.fastlearn.domain.model.Document
import com.app.fastlearn.domain.usecase.CreateFlashcardsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DocumentsViewModel @Inject constructor(
    private val documentRepository: DocumentRepository,
    private val createFlashcardsUseCase: CreateFlashcardsUseCase
) : ViewModel() {
    // Tạo danh sách tài liệu mẫu
    private val _allDocuments = MutableStateFlow<List<Document>>(emptyList())

    // Trạng thái truy vấn tài liệu
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // Lọc tài liệu theo truy vấn
    val documents: StateFlow<List<Document>> = combine(_allDocuments, _searchQuery) { docs, query ->
        if (query.isBlank()) {
            docs
        } else {
            docs.filter { doc ->
                doc.title.contains(query, ignoreCase = true) ||
                        doc.content.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        // Tải danh sách tài liệu
        loadDocuments()
    }

    private fun loadDocuments() {
        viewModelScope.launch {
            documentRepository.getAllDocuments().collect {
                _allDocuments.value = it
            }
        }
    }

    // Lấy tài liệu theo ID
    // In DocumentsViewModel.kt
    fun getDocumentById(documentId: String): Document? {
        return _allDocuments.value.find { it.docId == documentId }
    }

    // Cập nhật truy vấn tìm kiếm
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Xóa truy vấn tìm kiếm
    fun clearSearch() {
        _searchQuery.value = ""
    }

    // Tạo flashcards từ tài liệu
    fun createFlashcard(
        document: Document?,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        document?.let {
            viewModelScope.launch {
                try {
                    val result = createFlashcardsUseCase.createFlashcardsFromDocument(
                        documentId = document.docId,
                        apiKey = BuildConfig.apiKey
                    )

                    result.fold(
                        onSuccess = { flashcards ->
                            // Xử lý thành công, có thể cập nhật UI state
                            Log.d("ViewModel", "Created ${flashcards.size} flashcards")
                            onSuccess()
                        },
                        onFailure = { error ->
                            // Xử lý lỗi, có thể cập nhật UI state
                            Log.e("ViewModel", "Failed to create flashcards", error)
                            onError(Exception(error))
                        }
                    )
                } catch (e: Exception) {
                    Log.e("ViewModel", "Exception creating flashcards", e)
                    onError(e)
                }
            }
        }
    }
}