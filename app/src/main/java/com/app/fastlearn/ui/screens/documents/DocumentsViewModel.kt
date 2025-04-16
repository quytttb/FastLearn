package com.app.fastlearn.ui.screens.documents

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.fastlearn.BuildConfig
import com.app.fastlearn.data.repository.DocumentRepository
import com.app.fastlearn.domain.model.Document
import com.app.fastlearn.domain.usecase.CreateFlashcardsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class DocumentsViewModel @Inject constructor(
    private val documentRepository: DocumentRepository,
    private val createFlashcardsUseCase: CreateFlashcardsUseCase
) : ViewModel() {

    // Unified UI state for Documents screen
    data class DocumentsUiState(
        val allDocuments: List<Document> = emptyList(),
        val searchQuery: String = "",
        val searchResults: List<Document> = emptyList(),
        val isLoading: Boolean = false,
        val isError: Boolean = false,
        val errorMessage: String = "",
        val isSelectionMode: Boolean = false,
        val selectedDocuments: Set<String> = emptySet()
    )

    // Private MutableStateFlow to hold UI state
    private val _uiState = MutableStateFlow(DocumentsUiState())

    // Public immutable StateFlow for UI
    val uiState: StateFlow<DocumentsUiState> = _uiState.asStateFlow()

    // Search debounce settings
    private val searchDebounceTimeMillis = 300L
    private var searchJob: Job? = null

    init {
        // Load initial documents
        loadDocuments()

        // Setup search query flow
        viewModelScope.launch {
            _uiState
                .debounce(searchDebounceTimeMillis)
                .collect { state ->
                    if (state.searchQuery.isBlank()) {
                        // If query is blank, just show all documents
                        _uiState.update { it.copy(
                            searchResults = it.allDocuments,
                            isLoading = false
                        )}
                    } else if (state.isLoading) {
                        // Only perform search if loading state is true
                        performSearch(state.searchQuery)
                    }
                }
        }
    }

    private fun loadDocuments() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isError = false) }

            try {
                documentRepository.getAllDocuments().collect { documents ->
                    _uiState.update { state ->
                        state.copy(
                            allDocuments = documents,
                            searchResults = if (state.searchQuery.isBlank()) documents else state.searchResults,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    isError = true,
                    errorMessage = e.message ?: "Lỗi khi tải tài liệu"
                )}
                Log.e("DocumentsViewModel", "Error loading documents", e)
            }
        }
    }

    // Update search query and trigger search
    fun updateSearchQuery(query: String) {
        if (query != _uiState.value.searchQuery) {
            _uiState.update { it.copy(
                searchQuery = query,
                isLoading = true  // This will trigger the debounced search in the collector
            )}
        }
    }

    // Perform search operation
    private fun performSearch(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            try {
                if (query.isBlank()) {
                    _uiState.update { it.copy(
                        searchResults = it.allDocuments,
                        isLoading = false,
                        isError = false
                    )}
                    return@launch
                }

                documentRepository.searchDocuments(query).collect { results ->
                    _uiState.update { it.copy(
                        searchResults = results,
                        isLoading = false,
                        isError = false
                    )}
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    isError = true,
                    errorMessage = e.message ?: "Lỗi tìm kiếm"
                )}
                Log.e("DocumentsViewModel", "Error searching documents", e)
            }
        }
    }

    // Clear search
    fun clearSearch() {
        _uiState.update { it.copy(
            searchQuery = "",
            searchResults = it.allDocuments,
            isLoading = false,
            isError = false
        )}
    }

    // Toggle selection mode
    fun toggleSelectionMode() {
        _uiState.update { state ->
            state.copy(
                isSelectionMode = !state.isSelectionMode,
                selectedDocuments = if (state.isSelectionMode) emptySet() else state.selectedDocuments
            )
        }
    }

    // Toggle document selection
    fun toggleDocumentSelection(docId: String) {
        _uiState.update { state ->
            val currentSelection = state.selectedDocuments.toMutableSet()
            if (currentSelection.contains(docId)) {
                currentSelection.remove(docId)
            } else {
                currentSelection.add(docId)
            }
            state.copy(selectedDocuments = currentSelection)
        }
    }

    // Clear selection
    fun clearSelection() {
        _uiState.update { it.copy(selectedDocuments = emptySet()) }
    }

    // Select all documents
    fun selectAllDocuments() {
        _uiState.update { state ->
            state.copy(selectedDocuments = state.allDocuments.map { it.docId }.toSet())
        }
    }

    // Get selected documents as objects
    fun getSelectedDocumentsObjects(): List<Document> {
        val state = _uiState.value
        return state.allDocuments.filter { state.selectedDocuments.contains(it.docId) }
    }

    // Get document by ID
    fun getDocumentById(documentId: String): Document? {
        return _uiState.value.allDocuments.find { it.docId == documentId }
    }

    // Create flashcards from document
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

    // Create flashcards from selected documents
    fun createFlashcardsFromSelected(
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        if (_uiState.value.selectedDocuments.isEmpty()) return

        viewModelScope.launch {
            try {
                val selectedDocs = getSelectedDocumentsObjects()
                // For simplicity, we'll create flashcards for the first selected document
                selectedDocs.firstOrNull()?.let { document ->
                    createFlashcard(document, onSuccess, onError)
                }
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    // Delete document
    fun deleteDocument(document: Document) {
        viewModelScope.launch {
            documentRepository.deleteDocument(document)
            loadDocuments()
        }
    }

    // Delete selected documents
    fun deleteSelectedDocuments() {
        viewModelScope.launch {
            val docsToDelete = getSelectedDocumentsObjects()
            docsToDelete.forEach { documentRepository.deleteDocument(it) }
            _uiState.update { it.copy(
                selectedDocuments = emptySet(),
                isSelectionMode = false
            )}
            loadDocuments()
        }
    }

    // Delete all documents
    fun deleteAllDocuments() {
        viewModelScope.launch {
            _uiState.value.allDocuments.forEach { documentRepository.deleteDocument(it) }
            loadDocuments()
        }
    }
}