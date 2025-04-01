package com.app.fastlearn.data.repository

import com.app.fastlearn.data.local.dao.FlashcardDao
import com.app.fastlearn.domain.model.Flashcard
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDateTime
import javax.inject.Inject

class FlashcardRepositoryImpl @Inject constructor(
    private val flashcardDao: FlashcardDao
) : FlashcardRepository {

    // Tạo danh sách mock data để sử dụng trong repository
    private val mockFlashcards = mutableListOf(
        Flashcard(
            flashId = "1",
            docId = "1",
            question = "What is Kotlin?",
            answer = "A cross-platform, statically typed, general-purpose programming language with type inference.",
            createdDate = LocalDateTime.now().minusDays(5)
        ),
        Flashcard(
            flashId = "2",
            docId = "1",
            question = "What is coroutine in Kotlin?",
            answer = "Lightweight threads that can be suspended and resumed later.",
            createdDate = LocalDateTime.now().minusDays(3)
        ),
        Flashcard(
            flashId = "3",
            docId = "1",
            question = "What is Flow in Kotlin?",
            answer = "A type that can emit multiple values sequentially, as opposed to suspend functions that return only a single value.",
            createdDate = LocalDateTime.now().minusDays(2)
        ),
        Flashcard(
            flashId = "4",
            docId = "2",
            question = "What are extension functions?",
            answer = "Extension functions allow you to add methods to classes without modifying their source code.",
            createdDate = LocalDateTime.now().minusDays(1)
        )
    )

    // Mock implementation for getting flashcards for a document
    override fun getFlashcardsForDocument(documentId: Int): Flow<List<Flashcard>> {
        val filteredCards = mockFlashcards.filter { it.docId == documentId.toString() }
        return flowOf(filteredCards)
    }

    // Mock implementation for inserting a flashcard
    override suspend fun insertFlashcard(flashcard: Flashcard): Long {
        val newId = if (mockFlashcards.isEmpty()) "1" else
            (mockFlashcards.maxByOrNull { it.flashId.toInt() }?.flashId?.toInt()?.plus(1) ?: 1).toString()

        val flashcardWithId = flashcard.copy(
            flashId = newId,
            createdDate = LocalDateTime.now()
        )

        mockFlashcards.add(flashcardWithId)
        return newId.toLong()
    }

    // Mock implementation for deleting a flashcard
    override suspend fun deleteFlashcard(flashcard: Flashcard) {
        mockFlashcards.removeIf { it.flashId == flashcard.flashId }
    }

    // Mock implementation for getting a flashcard by ID
    override fun getFlashcardById(id: Int): Flow<Flashcard?> {
        val flashcard = mockFlashcards.find { it.flashId == id.toString() }
        return flowOf(flashcard)
    }
}