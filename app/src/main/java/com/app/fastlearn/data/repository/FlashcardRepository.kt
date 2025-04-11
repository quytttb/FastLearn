package com.app.fastlearn.data.repository

import com.app.fastlearn.domain.model.Flashcard
import kotlinx.coroutines.flow.Flow

interface FlashcardRepository {
    fun getAllFlashcards(): Flow<List<Flashcard>>
    fun getFlashcardsByDocumentId(documentId: String): Flow<List<Flashcard>>
    fun getFlashcardCountByDocumentId(documentId: String): Flow<Int>
    suspend fun insertFlashcard(flashcard: Flashcard): Long
    suspend fun deleteFlashcard(flashcard: Flashcard)
    fun getFlashcardById(id: String): Flow<Flashcard?>
    suspend fun updateFlashcard(flashcard: Flashcard)
}