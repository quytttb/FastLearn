package com.app.fastlearn.ui.screens.flashcards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.fastlearn.data.repository.FlashcardRepository
import com.app.fastlearn.domain.model.Flashcard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class FlashcardsViewModel @Inject constructor(
    private val flashcardRepository: FlashcardRepository
) : ViewModel() {
    private val _flashcards = MutableStateFlow<List<Flashcard>>(emptyList())
    val flashcards: StateFlow<List<Flashcard>> = _flashcards

    init {
        loadFlashcards()
    }

    private fun loadFlashcards() {
        viewModelScope.launch {
            flashcardRepository.getFlashcardsForDocument(1).collect {
                _flashcards.value = it
            }
        }
    }

    fun addFlashcard(flashId: String, docId: String, question: String, answer: String, createdDate:  LocalDateTime) {
        viewModelScope.launch {
            val newFlashcard = Flashcard(flashId, docId, question, answer, createdDate)
            flashcardRepository.insertFlashcard(newFlashcard)
            loadFlashcards()
        }
    }
}