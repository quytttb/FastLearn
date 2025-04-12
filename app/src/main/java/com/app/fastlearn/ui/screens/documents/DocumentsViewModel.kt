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

    // Trạng thái chế độ chọn tài liệu
    private val _isSelectionMode = MutableStateFlow(false)
    val isSelectionMode: StateFlow<Boolean> = _isSelectionMode

    // Trạng thái tài liệu đã chọn
    private val _selectedDocuments = MutableStateFlow<Set<String>>(emptySet())
    val selectedDocuments: StateFlow<Set<String>> = _selectedDocuments

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

    // Chuyển đổi chế độ chọn tài liệu
    fun toggleSelectionMode() {
        _isSelectionMode.value = !_isSelectionMode.value
        if (!_isSelectionMode.value) {
            clearSelection()
        }
    }

    // Chọn hoặc bỏ chọn tài liệu
    fun toggleDocumentSelection(docId: String) {
        val currentSelection = _selectedDocuments.value.toMutableSet()
        if (currentSelection.contains(docId)) {
            currentSelection.remove(docId)
        } else {
            currentSelection.add(docId)
        }
        _selectedDocuments.value = currentSelection
    }

    // Chọn tài liệu
    fun clearSelection() {
        _selectedDocuments.value = emptySet()
    }

    // Chọn tất cả tài liệu
    fun selectAllDocuments() {
        _selectedDocuments.value = _allDocuments.value.map { it.docId }.toSet()
    }

    // Bỏ chọn tất cả tài liệu
    fun getSelectedDocumentsObjects(): List<Document> {
        return _allDocuments.value.filter { _selectedDocuments.value.contains(it.docId) }
    }

    // Lấy tài liệu theo ID
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
                            onSuccess()
                        },
                        onFailure = { error ->
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

    // Tạo flashcards từ tài liệu đã chọn
    fun createFlashcardsFromSelected(
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        if (_selectedDocuments.value.isEmpty()) return

        viewModelScope.launch {
            try {
                val selectedDocs = getSelectedDocumentsObjects()
                // For simplicity, we'll create flashcards for the first selected document
                // In a real app, you might want to handle multiple documents differently
                selectedDocs.firstOrNull()?.let { document ->
                    createFlashcard(document, onSuccess, onError)
                }
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    // Xóa tài liệu
    fun deleteDocument(document: Document) {
        viewModelScope.launch {
            documentRepository.deleteDocument(document)
            loadDocuments()
        }
    }

    // Xóa các tài liệu đã chọn
    fun deleteSelectedDocuments() {
        viewModelScope.launch {
            val docsToDelete = getSelectedDocumentsObjects()
            docsToDelete.forEach { documentRepository.deleteDocument(it) }
            clearSelection()
            toggleSelectionMode()
            loadDocuments()
        }
    }

    // Xóa tất cả tài liệu
    fun deleteAllDocuments() {
        viewModelScope.launch {
            _allDocuments.value.forEach { documentRepository.deleteDocument(it) }
            loadDocuments()
        }
    }
}