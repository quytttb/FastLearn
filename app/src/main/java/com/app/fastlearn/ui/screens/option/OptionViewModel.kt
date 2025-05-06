package com.app.fastlearn.ui.screens.option

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.fastlearn.BuildConfig
import com.app.fastlearn.data.repository.DocumentRepository
import com.app.fastlearn.data.repository.FlashcardRepository
import com.app.fastlearn.domain.model.Document
import com.app.fastlearn.domain.usecase.CreateFlashcardsUseCase
import com.app.fastlearn.domain.usecase.SummarizeDocumentUseCase
import com.app.fastlearn.ui.navigation.DestinationsArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OptionViewModel @Inject constructor(
    private val documentRepository: DocumentRepository,
    private val flashcardRepository: FlashcardRepository,
    private val summarizeDocumentUseCase: SummarizeDocumentUseCase,
    private val createFlashcardsUseCase: CreateFlashcardsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val documentId: String = checkNotNull(savedStateHandle[DestinationsArgs.DOCUMENT_ID])

    // State for the document
    private val _document = MutableStateFlow<Document?>(null)
    val document = _document.asStateFlow()

    // State for flashcards count
    private val _flashcardsCount = MutableStateFlow(0)
    val flashcardsCount: StateFlow<Int> = _flashcardsCount.asStateFlow()

    // State for document summary
    private val _documentSummary = MutableStateFlow<String?>(null)
    val documentSummary = _documentSummary.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    // Summary loading state
    private val _isSummarizing = MutableStateFlow(false)
    val isSummarizing = _isSummarizing.asStateFlow()

    // Flashcards creation loading state
    private val _isGeneratingFlashcards = MutableStateFlow(false)
    val isGeneratingFlashcards = _isGeneratingFlashcards.asStateFlow()

    // Content suitability state (> 50 words)
    private val _isContentSuitable = MutableStateFlow(false)
    val isContentSuitable = _isContentSuitable.asStateFlow()

    init {
        loadDocument()
        getFlashcardCount()
    }

    private fun loadDocument() {
        viewModelScope.launch {
            documentRepository.getDocumentById(documentId).collect { fetchedDocument ->
                _document.value = fetchedDocument
                _isLoading.value = false

                // Extract summary if exists
                fetchedDocument?.let { doc ->
                    val summary = summarizeDocumentUseCase.extractSummary(doc.title)
                    _documentSummary.value = summary

                    // Check if content is suitable for AI features
                    _isContentSuitable.value = isContentSuitableForAIProcessing(doc.content)
                }
            }
        }
    }

    private fun isContentSuitableForAIProcessing(content: String): Boolean {
        // Minimum content length - check if at least 50 words
        val wordCount = content.split(Regex("\\s+")).count { it.isNotBlank() }
        return wordCount >= 50
    }

    private fun getFlashcardCount() {
        viewModelScope.launch {
            flashcardRepository.getFlashcardCountByDocumentId(documentId).collect { count ->
                _flashcardsCount.value = count
            }
        }
    }

    fun generateSummary(
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isSummarizing.value = true
            try {
                val result = summarizeDocumentUseCase.summarizeDocument(
                    documentId = documentId,
                    apiKey = BuildConfig.apiKey
                )

                result.fold(
                    onSuccess = { summary ->
                        _documentSummary.value = summary
                        onSuccess()
                    },
                    onFailure = { error ->
                        onError(Exception(error))
                    }
                )
            } catch (e: Exception) {
                onError(e)
            } finally {
                _isSummarizing.value = false
                // Reload document to get updated title
                loadDocument()
            }
        }
    }

    fun generateFlashcards(
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isGeneratingFlashcards.value = true
            try {
                val result = createFlashcardsUseCase.createFlashcardsFromDocument(
                    documentId = documentId,
                    apiKey = BuildConfig.apiKey
                )

                result.fold(
                    onSuccess = { flashcards ->
                        // Update the count
                        getFlashcardCount()
                        onSuccess()
                    },
                    onFailure = { error ->
                        onError(Exception(error))
                    }
                )
            } catch (e: Exception) {
                onError(e)
            } finally {
                _isGeneratingFlashcards.value = false
            }
        }
    }
}