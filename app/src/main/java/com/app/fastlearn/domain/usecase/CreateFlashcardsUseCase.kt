package com.app.fastlearn.domain.usecase

import android.util.Log
import com.app.fastlearn.data.repository.DocumentRepository
import com.app.fastlearn.data.repository.FlashcardRepository
import com.app.fastlearn.domain.model.Document
import com.app.fastlearn.domain.model.Flashcard
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

class CreateFlashcardsUseCase @Inject constructor(
    private val documentRepository: DocumentRepository,
    private val flashcardRepository: FlashcardRepository
) {
    suspend fun createFlashcardsFromDocument(
        documentId: String,
        apiKey: String
    ): Result<List<Flashcard>> {
        return try {
            // Fetch the document by ID - Using proper type conversion
            // Assuming DocumentRepository has a method that accepts String ID
            val document = documentRepository.getDocumentById(documentId).firstOrNull()
                ?: return Result.failure(IllegalArgumentException("Document not found with ID: $documentId"))

            // Generate flashcards using AI
            val flashcardsResult = generateFlashcards(document, apiKey)

            // Insert generated flashcards into the repository
            flashcardsResult.forEach { flashcard ->
                flashcardRepository.insertFlashcard(flashcard)
            }

            Result.success(flashcardsResult)
        } catch (e: Exception) {
            Log.e("CreateFlashcardsUseCase", "Error creating flashcards", e)
            Result.failure(e)
        }
    }

    private suspend fun generateFlashcards(document: Document, apiKey: String): List<Flashcard> {
        return withContext(Dispatchers.IO) {
            val generativeModel = GenerativeModel(
                modelName = "gemini-2.0-flash",
                apiKey = apiKey,
                generationConfig = generationConfig {
                    temperature = 1.0f
                }
            )

            val prompt = """
            Generate at least 5 flashcards based on the following document content.
            Each flashcard should contain a question on the front and a concise answer on the back.
            Use the document category as the flashcard category.
            
            Document content: ${document.content}
            Document category: ${document.category}
            
            Respond with a JSON array of flashcards in this format:
            [
              {
                "front": "Question text here",
                "back": "Answer text here",
                "category": "Category here"
              },
              ...more flashcards
            ]
        """.trimIndent()

            try {
                val response = generativeModel.generateContent(prompt)
                val flashcardJsonText = response.text

                // Xử lý trường hợp text là null
                if (flashcardJsonText != null) {
                    parseFlashcardsFromJson(flashcardJsonText, document.docId)
                } else {
                    Log.e("CreateFlashcardsUseCase", "Null response from AI model")
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e("CreateFlashcardsUseCase", "Error generating flashcards", e)
                emptyList()
            }
        }
    }

    private fun parseFlashcardsFromJson(jsonText: String, docId: String): List<Flashcard> {
        return try {
            // Try to extract JSON array if the response includes extra text
            val jsonArrayString = extractJsonArray(jsonText) ?: jsonText

            val flashcards = mutableListOf<Flashcard>()
            val jsonArray = JSONArray(jsonArrayString)

            for (i in 0 until jsonArray.length()) {
                val flashcardObj = jsonArray.getJSONObject(i)
                val flashcard = Flashcard(
                    flashId = UUID.randomUUID().toString(),
                    docId = docId,
                    question = flashcardObj.getString("front"),
                    answer = flashcardObj.getString("back"),
                    createdDate = LocalDateTime.now()
                )
                flashcards.add(flashcard)
            }

            flashcards
        } catch (e: Exception) {
            Log.e("CreateFlashcardsUseCase", "Error parsing flashcard JSON", e)
            emptyList()
        }
    }

    // Helper function to extract JSON array from text that might contain additional content
    private fun extractJsonArray(text: String): String? {
        val startIndex = text.indexOf("[")
        val endIndex = text.lastIndexOf("]")

        return if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            text.substring(startIndex, endIndex + 1)
        } else {
            null
        }
    }
}