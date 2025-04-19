package com.app.fastlearn.domain.usecase

import android.util.Log
import com.app.fastlearn.data.repository.DocumentRepository
import com.app.fastlearn.data.repository.FlashcardRepository
import com.app.fastlearn.domain.model.Document
import com.app.fastlearn.domain.model.Flashcard
import com.app.fastlearn.integration.apis.GeminiApi
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class CreateFlashcardsUseCase @Inject constructor(
    private val documentRepository: DocumentRepository,
    private val flashcardRepository: FlashcardRepository,
    private val geminiApi: GeminiApi
) {
    suspend fun createFlashcardsFromDocument(
        documentId: String,
        apiKey: String
    ): Result<List<Flashcard>> {
        return try {
            // Lấy tài liệu bằng ID
            val document = documentRepository.getDocumentById(documentId).firstOrNull()
                ?: return Result.failure(IllegalArgumentException("Document not found with ID: $documentId"))

            // Gọi API Gemini để tạo flashcards
            val flashcardsResult = geminiApi.generateFlashcards(
                content = document.content,
                category = document.category,
                docId = document.docId,
                apiKey = apiKey
            )

            // Lưu flashcards vào cơ sở dữ liệu
            flashcardsResult.forEach { flashcard ->
                flashcardRepository.insertFlashcard(flashcard)
            }

            Result.success(flashcardsResult)
        } catch (e: Exception) {
            Log.e("CreateFlashcardsUseCase", "Error creating flashcards", e)
            Result.failure(e)
        }
    }
}