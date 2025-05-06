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
    suspend fun generateFlashcards(
        content: String,
        docId: String,
        apiKey: String
    ): List<Flashcard> {
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
            
            Document content: $content
            
            Respond with a JSON array of flashcards in this format:
            [
              {
                "front": "Question text here",
                "back": "Answer text here",
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

    // Hàm gọi API Gemini để tạo tóm tắt văn bản
    suspend fun generateSummary(content: String, apiKey: String): String {
        return withContext(Dispatchers.IO) {
            // Khởi tạo mô hình GenerativeModel với tên mô hình và API key
            val generativeModel = GenerativeModel(
                modelName = "gemini-2.0-flash",
                apiKey = apiKey,
                generationConfig = generationConfig {
                    temperature = 0.7f  // Giá trị temperature thấp hơn để tạo ra tóm tắt ngắn gọn hơn
                }
            )

            // Tạo prompt cho mô hình
            val prompt = """
            Generate a concise summary (30-50 words) of the following document content.
            Make the summary informative and capture the key points.
            
            Document content: $content
            
            Respond with just the summary text, no additional explanation or formatting.
            """.trimIndent()

            // Gọi mô hình để tạo nội dung tóm tắt
            try {
                val response = generativeModel.generateContent(prompt)
                val summaryText = response.text?.trim()

                if (summaryText != null) {
                    summaryText
                } else {
                    Log.e("GeminiApi", "Null response from AI model")
                    "Failed to generate summary"
                }
            } catch (e: Exception) {
                Log.e("GeminiApi", "Error generating summary", e)
                "Failed to generate summary: ${e.message}"
            }
        }
    }

}