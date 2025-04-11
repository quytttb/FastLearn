package com.app.fastlearn.ui.screens.flashcards

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.fastlearn.data.repository.FlashcardRepository
import com.app.fastlearn.domain.model.Flashcard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.rem

@HiltViewModel
class FlashcardsViewModel @Inject constructor(
    private val flashcardRepository: FlashcardRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _flashcards = MutableStateFlow<List<Flashcard>>(emptyList())
    val flashcards: StateFlow<List<Flashcard>> = _flashcards

    // Trạng thái card hiện tại
    private val _currentCardIndex = MutableStateFlow(0)
    val currentCardIndex = _currentCardIndex.asStateFlow()

    // Trạng thái lật thẻ
    private val _isFlipped = MutableStateFlow(false)
    val isFlipped = _isFlipped.asStateFlow()

    // Lật thẻ
    fun toggleFlip() {
        _isFlipped.value = !_isFlipped.value
    }

    // Chuyển đến thẻ tiếp theo
    fun moveToNextCard() {
        viewModelScope.launch {
            if (_flashcards.value.isNotEmpty()) {
                _currentCardIndex.value = (_currentCardIndex.value + 1) % _flashcards.value.size
                _isFlipped.value = false
            }
        }
    }

    // Xáo trộn thẻ
    fun shuffleFlashcards() {
        viewModelScope.launch {
            _flashcards.value = _flashcards.value.shuffled()
            _currentCardIndex.value = 0
            _isFlipped.value = false
        }
    }

    init {
//        if (documentId.isNotEmpty()) {
//            loadFlashcards(documentId)
//        }
        loadAllFlashcards()
    }

    // Nạp thẻ theo ID tài liệu
    fun loadFlashcards(docId: String) {
        viewModelScope.launch {
            flashcardRepository.getFlashcardsByDocumentId(docId).collect {
                _flashcards.value = it
            }
        }
    }

    // Nạp tất cả thẻ
    private fun loadAllFlashcards() {
        viewModelScope.launch {
            flashcardRepository.getAllFlashcards().collect {
                _flashcards.value = it
            }
        }
    }

    // Lấy số lượng thẻ theo ID tài liệu
    fun getFlashcardCountForDocument(docId: String, onResult: (Int) -> Unit) {
        viewModelScope.launch {
            flashcardRepository.getFlashcardCountByDocumentId(docId).collect { count ->
                onResult(count)
            }
        }
    }

    // Thêm thẻ mới
    fun addFlashcard(
        flashId: String,
        docId: String,
        question: String,
        answer: String,
        createdDate: LocalDateTime
    ) {
        viewModelScope.launch {
            val newFlashcard = Flashcard(flashId, docId, question, answer, createdDate)
            flashcardRepository.insertFlashcard(newFlashcard)
            loadFlashcards(docId)
        }
    }
}