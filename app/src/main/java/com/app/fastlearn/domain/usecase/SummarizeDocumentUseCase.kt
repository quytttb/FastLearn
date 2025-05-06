package com.app.fastlearn.domain.usecase

import android.util.Log
import com.app.fastlearn.data.repository.DocumentRepository
import com.app.fastlearn.integration.apis.GeminiApi
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class SummarizeDocumentUseCase @Inject constructor(
    private val documentRepository: DocumentRepository,
    private val geminiApi: GeminiApi
) {
    companion object {
        const val SUMMARY_SEPARATOR = "◆"  // Kí tự phân tách giữa tiêu đề và tóm tắt
    }

    suspend fun summarizeDocument(
        documentId: String,
        apiKey: String
    ): Result<String> {
        return try {
            // Get document by ID
            val document = documentRepository.getDocumentById(documentId).firstOrNull()
                ?: return Result.failure(IllegalArgumentException("Document not found with ID: $documentId"))

            // Check if document already has a summary
            if (document.title.contains(SUMMARY_SEPARATOR)) {
                // Extract existing summary
                val summary = document.title.split(SUMMARY_SEPARATOR).getOrNull(1)?.trim()
                    ?: return Result.failure(IllegalArgumentException("Invalid summary format"))
                return Result.success(summary)
            }

            // Generate summary using Gemini API
            val summary = geminiApi.generateSummary(
                content = document.content,
                apiKey = apiKey
            )

            // Update document title with the summary
            val updatedTitle = "${document.title} $SUMMARY_SEPARATOR $summary"
            documentRepository.updateDocument(document.copy(title = updatedTitle))

            Result.success(summary)
        } catch (e: Exception) {
            Log.e("SummarizeDocumentUseCase", "Error summarizing document", e)
            Result.failure(e)
        }
    }

    fun extractSummary(title: String): String? {
        return if (title.contains(SUMMARY_SEPARATOR)) {
            title.split(SUMMARY_SEPARATOR).getOrNull(1)?.trim()
        } else {
            null
        }
    }

    fun hasExistingSummary(title: String): Boolean {
        return title.contains(SUMMARY_SEPARATOR)
    }
}