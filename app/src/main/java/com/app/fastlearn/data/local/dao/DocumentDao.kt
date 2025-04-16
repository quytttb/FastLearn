package com.app.fastlearn.data.local.dao

import androidx.room.*
import com.app.fastlearn.data.local.entity.DocumentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {
    @Query("SELECT * FROM documents ORDER BY createdDate DESC")
    fun getAllDocuments(): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE docId = :docId")
    suspend fun getDocumentById(docId: String): DocumentEntity?

    @Query("SELECT * FROM documents WHERE category = :category ORDER BY createdDate DESC")
    fun getDocumentsByCategory(category: String): Flow<List<DocumentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: DocumentEntity)

    @Delete
    suspend fun deleteDocument(document: DocumentEntity)

    @Query("DELETE FROM documents WHERE docId = :docId")
    suspend fun deleteDocumentById(docId: String)

    @Query("SELECT * FROM documents WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%'")
    fun searchDocuments(query: String): Flow<List<DocumentEntity>>
}
