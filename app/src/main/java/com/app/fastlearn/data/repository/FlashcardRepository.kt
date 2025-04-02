package com.app.fastlearn.data.repository

import com.app.fastlearn.domain.model.Flashcard
import kotlinx.coroutines.flow.Flow

interface FlashcardRepository {
    fun getFlashcardsForDocument(documentId: Int): Flow<List<Flashcard>>
    suspend fun insertFlashcard(flashcard: Flashcard): Long
    suspend fun deleteFlashcard(flashcard: Flashcard)
    fun getFlashcardById(id: Int): Flow<Flashcard?>
}