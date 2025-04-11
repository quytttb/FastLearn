package com.app.fastlearn.data.repository

import com.app.fastlearn.data.local.dao.FlashcardDao
import com.app.fastlearn.data.local.dao.ProgressDao
import com.app.fastlearn.data.util.DataMapper
import com.app.fastlearn.domain.model.Progress
import com.app.fastlearn.util.ProgressStatus
import kotlinx.coroutines.flow.*
import java.time.LocalDateTime
import javax.inject.Inject

class ProgressRepositoryImpl @Inject constructor(
    private val progressDao: ProgressDao,
    private val flashcardDao: FlashcardDao,
    private val dataMapper: DataMapper
) : ProgressRepository {

    override fun getProgressByFlashcardId(flashcardId: String): Flow<List<Progress>> {
        return progressDao.getProgressByFlashId(flashcardId).map { entities ->
            entities.map { dataMapper.mapProgressEntityToDomain(it) }
        }
    }

    override fun getProgressCountByFlashcardId(flashcardId: String): Flow<Int> {
        return progressDao.getProgressCountByFlashId(flashcardId)
    }

    override suspend fun insertProgress(progress: Progress) {
        progressDao.insertProgress(dataMapper.mapProgressDomainToEntity(progress))
    }

    override suspend fun updateProgress(progress: Progress) {
        progressDao.updateProgress(dataMapper.mapProgressDomainToEntity(progress))
    }

    override suspend fun deleteProgress(progress: Progress) {
        val entity = dataMapper.mapProgressDomainToEntity(progress)
        progressDao.getProgressById(entity.progressId)?.let {
            progressDao.updateProgress(it.copy(status = ProgressStatus.DELETED.value))
        }
    }

    override fun getProgressById(id: String): Flow<Progress?> {
        return flow {
            val entity = progressDao.getProgressById(id)
            emit(entity?.let { dataMapper.mapProgressEntityToDomain(it) })
        }
    }

    override fun getProgressByDocId(docId: String): Flow<List<Progress>> {
        return progressDao.getProgressByDocId(docId).map { entities ->
            entities.map { dataMapper.mapProgressEntityToDomain(it) }
        }
    }

    override fun getCorrectAnswerCountByDocId(docId: String): Flow<Int> {
        return progressDao.getCorrectAnswerCountByDocId(docId, ProgressStatus.CORRECT.value)
    }

    override fun getStudyStats(docId: String): Flow<Map<String, Any?>> {
        return combine(
            progressDao.getProgressByDocId(docId),
            flashcardDao.getFlashcardsByDocId(docId)
        ) { progressEntities, flashcardEntities ->
            val progress = progressEntities.map { dataMapper.mapProgressEntityToDomain(it) }
            val totalCards = flashcardEntities.size
            val progressByFlashcard = progress.groupBy { it.flashId }

            // Calculate correct answers
            val correctAnswers = progress.count { it.status == ProgressStatus.CORRECT.value }

            // Calculate accuracy rate
            val accuracyRate = if (progress.isNotEmpty()) {
                correctAnswers.toFloat() / progress.size
            } else 0f

            // Calculate average time per card
            val averageTime = if (progress.isNotEmpty()) {
                progress.sumOf { it.timeSpent }.toFloat() / progress.size
            } else 0f

            // Find most difficult cards (most incorrect answers)
            val difficultCards = progressByFlashcard
                .mapValues { (_, progressList) ->
                    val totalAttempts = progressList.size
                    val incorrectAttempts = progressList.count { it.status == ProgressStatus.INCORRECT.value }
                    if (totalAttempts > 0) incorrectAttempts.toFloat() / totalAttempts else 0f
                }
                .entries
                .sortedByDescending { it.value }
                .take(5)
                .map { it.key }

            // Get last study date
            val lastStudyDate = progress.maxOfOrNull { it.studyDate }

            mapOf<String, Any?>(
                "totalCards" to totalCards,
                "correctAnswers" to correctAnswers,
                "accuracyRate" to accuracyRate,
                "averageTimePerCard" to averageTime,
                "mostDifficultCards" to difficultCards,
                "lastStudyDate" to lastStudyDate
            )
        }
    }

    fun getAllProgress(): Flow<List<Progress>> {
        return progressDao.getProgressByFlashId("").map { progressList ->
            progressList.map { dataMapper.mapProgressEntityToDomain(it) }
        }
    }
}