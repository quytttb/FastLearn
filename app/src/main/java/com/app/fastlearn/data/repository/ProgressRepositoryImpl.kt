package com.app.fastlearn.data.repository

import com.app.fastlearn.data.local.dao.ProgressDao
import com.app.fastlearn.domain.model.Progress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDateTime
import javax.inject.Inject

class ProgressRepositoryImpl @Inject constructor(
    private val progressDao: ProgressDao
) : ProgressRepository {

    // Mock data cho testing
    private val mockProgressList = mutableListOf(
        Progress(
            progressId = "progress_1",
            flashId = "flash_1",
            studyDate = LocalDateTime.now().minusDays(5),
            status = "LEARNED",
            timeSpent = 120
        ),
        Progress(
            progressId = "progress_2",
            flashId = "flash_2",
            studyDate = LocalDateTime.now().minusDays(3),
            status = "REVIEWING",
            timeSpent = 85
        ),
        Progress(
            progressId = "progress_3",
            flashId = "flash_3",
            studyDate = LocalDateTime.now().minusDays(1),
            status = "NEW",
            timeSpent = 0
        )
    )

    override fun getProgressForFlashcard(flashcardId: Int): Flow<Progress?> {
        // Tìm progress theo flashId dựa vào flashcardId
        val flashId = "flash_$flashcardId"
        val progress = mockProgressList.find { it.flashId == flashId }

        return flowOf(progress)
    }

    override suspend fun insertProgress(progress: Progress) {
        // Giả lập việc thêm progress vào database
        mockProgressList.add(progress)
    }

    override suspend fun updateProgress(progress: Progress) {
        // Giả lập việc cập nhật progress
        val index = mockProgressList.indexOfFirst { it.progressId == progress.progressId }
        if (index != -1) {
            mockProgressList[index] = progress
        }
    }

    override suspend fun deleteProgress(progress: Progress) {
        // Giả lập việc xóa progress
        mockProgressList.removeIf { it.progressId == progress.progressId }
    }

    // Phương thức hỗ trợ thêm để lấy tất cả dữ liệu progress cho testing
    fun getAllProgress(): Flow<List<Progress>> {
        return flowOf(mockProgressList)
    }
}