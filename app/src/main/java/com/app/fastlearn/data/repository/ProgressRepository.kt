package com.app.fastlearn.data.repository

import com.app.fastlearn.domain.model.Progress
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface ProgressRepository {
    fun getProgressByFlashcardId(flashcardId: String): Flow<List<Progress>>
    fun getProgressCountByFlashcardId(flashcardId: String): Flow<Int>
    suspend fun insertProgress(progress: Progress)
    suspend fun updateProgress(progress: Progress)
    suspend fun deleteProgress(progress: Progress)
    fun getProgressById(id: String): Flow<Progress?>
    fun getProgressByDocId(docId: String): Flow<List<Progress>>
    fun getCorrectAnswerCountByDocId(docId: String): Flow<Int>
    fun getStudyStats(docId: String): Flow<Map<String, Any?>>
}