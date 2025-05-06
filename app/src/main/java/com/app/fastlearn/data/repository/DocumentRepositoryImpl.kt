package com.app.fastlearn.data.repository

import com.app.fastlearn.data.local.dao.DocumentDao
import com.app.fastlearn.data.util.DataMapper
import com.app.fastlearn.domain.model.Document
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DocumentRepositoryImpl @Inject constructor(
    private val documentDao: DocumentDao,
    private val dataMapper: DataMapper
) : DocumentRepository {

    override fun getAllDocuments(): Flow<List<Document>> {
        return documentDao.getAllDocuments().map { entities ->
            entities.map { dataMapper.mapDocumentEntityToDomain(it) }
        }
    }

    override suspend fun insertDocument(document: Document): Long {
        val documentEntity = dataMapper.mapDocumentDomainToEntity(document)
        documentDao.insertDocument(documentEntity)
        return 0L // Trả về 0L vì không có ID tự động tăng
    }

    override suspend fun deleteDocument(document: Document) {
        val documentEntity = dataMapper.mapDocumentDomainToEntity(document)
        documentDao.deleteDocument(documentEntity)
    }

    override fun getDocumentById(id: String): Flow<Document?> {
        return flow {
            val entity = documentDao.getDocumentById(id)
            emit(entity?.let { dataMapper.mapDocumentEntityToDomain(it) })
        }
    }

    override suspend fun updateDocument(document: Document) {
        val documentEntity = dataMapper.mapDocumentDomainToEntity(document)
        documentDao.insertDocument(documentEntity) // Dùng insert để cập nhật
    }

    override fun searchDocuments(query: String): Flow<List<Document>> {
        return documentDao.searchDocuments(query).map { entities ->
            entities.map { dataMapper.mapDocumentEntityToDomain(it) }
        }
    }

    override fun getDocumentsByCategory(categoryId: String): Flow<List<Document>> {
        return documentDao.getDocumentsByCategory(categoryId).map { entities ->
            entities.map { dataMapper.mapDocumentEntityToDomain(it) }
        }
    }
}