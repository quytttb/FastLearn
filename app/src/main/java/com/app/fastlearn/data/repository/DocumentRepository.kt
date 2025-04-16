package com.app.fastlearn.data.repository

import com.app.fastlearn.domain.model.Document
import kotlinx.coroutines.flow.Flow

interface DocumentRepository {
    fun getAllDocuments(): Flow<List<Document>>
    suspend fun insertDocument(document: Document): Long
    suspend fun deleteDocument(document: Document)
    fun getDocumentById(id: String): Flow<Document?>
    suspend fun updateDocument(document: Document)
    fun searchDocuments(query: String): Flow<List<Document>>
}