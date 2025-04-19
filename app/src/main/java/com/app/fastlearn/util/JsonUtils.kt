package com.app.fastlearn.util

import android.util.Log
import com.app.fastlearn.domain.model.Flashcard
import org.json.JSONArray
import java.time.LocalDateTime
import java.util.UUID

class JsonUtils {
    companion object {
        /**
         * Phân tích văn bản JSON vào danh sách các đối tượng flashcard
         */
        fun parseFlashcardsFromJson(jsonText: String, docId: String): List<Flashcard> {
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
                Log.e("JsonUtils", "Error parsing flashcard JSON", e)
                emptyList()
            }
        }

        /**
         * Trích xuất mảng JSON từ văn bản
         */
        fun extractJsonArray(text: String): String? {
            val startIndex = text.indexOf("[")
            val endIndex = text.lastIndexOf("]")

            return if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
                text.substring(startIndex, endIndex + 1)
            } else {
                null
            }
        }
    }
}