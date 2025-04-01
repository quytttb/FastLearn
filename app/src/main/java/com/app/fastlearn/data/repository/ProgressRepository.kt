package com.app.fastlearn.data.repository

import com.app.fastlearn.domain.model.Progress
import kotlinx.coroutines.flow.Flow

interface ProgressRepository {
    fun getProgressForFlashcard(flashcardId: Int): Flow<Progress?>
    suspend fun insertProgress(progress: Progress)
    suspend fun updateProgress(progress: Progress)
    suspend fun deleteProgress(progress: Progress)
}