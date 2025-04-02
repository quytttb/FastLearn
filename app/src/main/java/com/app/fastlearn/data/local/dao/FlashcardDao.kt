package com.app.fastlearn.data.local.dao

import androidx.room.*
import com.app.fastlearn.data.local.entity.FlashcardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FlashcardDao {
    @Query("SELECT * FROM flashcards")
    fun getAllFlashcards(): Flow<List<FlashcardEntity>>

    @Query("SELECT * FROM flashcards WHERE docId = :docId")
    fun getFlashcardsByDocId(docId: String): Flow<List<FlashcardEntity>>

    @Query("SELECT * FROM flashcards WHERE flashId = :flashId")
    suspend fun getFlashcardById(flashId: String): FlashcardEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcard(flashcard: FlashcardEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcards(flashcards: List<FlashcardEntity>)

    @Delete
    suspend fun deleteFlashcard(flashcard: FlashcardEntity)

    @Query("DELETE FROM flashcards WHERE docId = :docId")
    suspend fun deleteFlashcardsByDocId(docId: String)
}