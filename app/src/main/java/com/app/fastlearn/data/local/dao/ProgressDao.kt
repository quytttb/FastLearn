package com.app.fastlearn.data.local.dao

import androidx.room.*
import com.app.fastlearn.data.local.entity.ProgressEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface ProgressDao {
    @Query("SELECT * FROM study_progress WHERE flashId = :flashId ORDER BY studyDate DESC")
    fun getProgressByFlashId(flashId: String): Flow<List<ProgressEntity>>

    @Query("SELECT * FROM study_progress WHERE studyDate BETWEEN :startDate AND :endDate")
    fun getProgressByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<ProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: ProgressEntity)

    @Update
    suspend fun updateProgress(progress: ProgressEntity)

    @Query("SELECT * FROM study_progress WHERE progressId = :progressId")
    suspend fun getProgressById(progressId: String): ProgressEntity?

    @Query("SELECT * FROM study_progress WHERE flashId = :flashId ORDER BY studyDate DESC LIMIT 1")
    suspend fun getLatestProgressForFlashcard(flashId: String): ProgressEntity?

    @Query("SELECT COUNT(*) FROM study_progress WHERE flashId = :flashId")
    fun getProgressCountByFlashId(flashId: String): Flow<Int>

    @Query("SELECT * FROM study_progress WHERE flashId IN (SELECT flashId FROM flashcards WHERE docId = :docId) ORDER BY studyDate DESC")
    fun getProgressByDocId(docId: String): Flow<List<ProgressEntity>>

    @Query("SELECT COUNT(*) FROM study_progress WHERE flashId IN (SELECT flashId FROM flashcards WHERE docId = :docId) AND status = :status")
    fun getCorrectAnswerCountByDocId(docId: String, status: String): Flow<Int>
}