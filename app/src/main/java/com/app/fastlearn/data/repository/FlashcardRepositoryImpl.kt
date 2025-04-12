package com.app.fastlearn.data.repository

import com.app.fastlearn.data.local.dao.FlashcardDao
import com.app.fastlearn.data.util.DataMapper
import com.app.fastlearn.domain.model.Flashcard
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlashcardRepositoryImpl @Inject constructor(
    private val flashcardDao: FlashcardDao,
    private val dataMapper: DataMapper
) : FlashcardRepository {

    override fun getAllFlashcards(): Flow<List<Flashcard>> {
        return flashcardDao.getAllFlashcards().map { entities ->
            entities.map { dataMapper.mapFlashcardEntityToDomain(it) }
        }
    }

    override fun getFlashcardsByDocumentId(documentId: String): Flow<List<Flashcard>> {
        return flashcardDao.getFlashcardsByDocId(documentId).map { entities ->
            entities.map { dataMapper.mapFlashcardEntityToDomain(it) }
        }
    }

    override fun getFlashcardCountByDocumentId(documentId: String): Flow<Int> {
        return flashcardDao.getFlashcardCountByDocId(documentId)
    }

    override suspend fun insertFlashcard(flashcard: Flashcard): Long {
        val flashcardEntity = dataMapper.mapFlashcardDomainToEntity(flashcard)
        flashcardDao.insertFlashcard(flashcardEntity)
        return 0L // Return 0L as there is no auto-increment ID
    }

    override suspend fun deleteFlashcard(flashcard: Flashcard) {
        val flashcardEntity = dataMapper.mapFlashcardDomainToEntity(flashcard)
        flashcardDao.deleteFlashcard(flashcardEntity)
    }

    override fun getFlashcardById(id: String): Flow<Flashcard?> {
        return flow {
            val entity = flashcardDao.getFlashcardById(id)
            emit(entity?.let { dataMapper.mapFlashcardEntityToDomain(it) })
        }
    }

    override suspend fun updateFlashcard(flashcard: Flashcard) {
        val flashcardEntity = dataMapper.mapFlashcardDomainToEntity(flashcard)
        flashcardDao.insertFlashcard(flashcardEntity)
    }
}