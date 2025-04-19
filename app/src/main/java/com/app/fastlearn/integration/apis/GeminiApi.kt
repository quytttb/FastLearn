package com.app.fastlearn.integration.apis

import android.util.Log
import com.app.fastlearn.domain.model.Flashcard
import com.app.fastlearn.util.JsonUtils
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GeminiApi @Inject constructor() {
    // Hàm gọi API Gemini để tạo flashcards
    suspend fun generateFlashcards(content: String, category: String, docId: String, apiKey: String): List<Flashcard> {
        return withContext(Dispatchers.IO) {
            // Khởi tạo mô hình GenerativeModel với tên mô hình và API key
            val generativeModel = GenerativeModel(
                modelName = "gemini-2.0-flash",
                apiKey = apiKey,
                generationConfig = generationConfig {
                    temperature = 1.0f
                }
            )

            // Tạo prompt cho mô hình
            val prompt = """
            Generate at least 5 flashcards based on the following document content.
            Each flashcard should contain a question on the front and a concise answer on the back.
            Use the document category as the flashcard category.
            
            Document content: $content
            Document category: $category
            
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

            // Gọi mô hình để tạo nội dung flashcards
            try {
                val response = generativeModel.generateContent(prompt)
                val flashcardJsonText = response.text

                // Xử lý trường hợp text là null
                if (flashcardJsonText != null) {
                    JsonUtils.parseFlashcardsFromJson(flashcardJsonText, docId)
                } else {
                    Log.e("GeminiApi", "Null response from AI model")
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e("GeminiApi", "Error generating flashcards", e)
                emptyList()
            }
        }
    }
}