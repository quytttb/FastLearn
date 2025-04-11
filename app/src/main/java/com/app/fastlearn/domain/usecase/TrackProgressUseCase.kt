package com.app.fastlearn.domain.usecase

import com.app.fastlearn.data.repository.ProgressRepository
import com.app.fastlearn.domain.model.Progress
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/*class TrackProgressUseCase @Inject constructor(
    private val progressRepository: ProgressRepository
) {
    fun getProgressForFlashcard(flashcardId: Int): Flow<Progress?> {
        return progressRepository.getProgressForFlashcard(flashcardId)
    }

    suspend fun insertProgress(progress: Progress) {
        progressRepository.insertProgress(progress)
    }

    suspend fun updateProgress(progress: Progress) {
        progressRepository.updateProgress(progress)
    }
}*/
