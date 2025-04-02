package com.app.fastlearn.data.repository

import com.app.fastlearn.data.local.dao.DocumentDao
import com.app.fastlearn.domain.model.Document
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject

class DocumentRepositoryImpl @Inject constructor(
    private val documentDao: DocumentDao
) : DocumentRepository {

    // Tạo danh sách mock data để sử dụng trong repository
    private val mockDocuments = listOf(
        Document(
            docId = "1",
            title = "Học tiếng Anh cơ bản",
            content = "Các từ vựng và ngữ pháp cơ bản cho người mới học tiếng Anh",
            filePath = "files/english_basic.pdf",
            category = "English",
            createdDate = LocalDateTime.now().minusDays(5)
        ),
        Document(
            docId = "2",
            title = "Lập trình Android với Kotlin",
            content = "Hướng dẫn cơ bản về lập trình ứng dụng Android sử dụng Kotlin",
            filePath = "files/android_kotlin.pdf",
            category = "Programming",
            createdDate = LocalDateTime.now().minusDays(3)
        ),
        Document(
            docId = "3",
            title = "Bài tập Toán lớp 10",
            content = "Tuyển tập các bài tập Toán lớp 10 và đáp án",
            filePath = "files/math_exercises.pdf",
            category = "Mathematics",
            createdDate = LocalDateTime.now().minusDays(1)
        ),
        Document(
            docId = "4",
            title = "Từ vựng TOEIC",
            content = "Danh sách từ vựng thông dụng cho kỳ thi TOEIC",
            filePath = "files/toeic_vocab.pdf",
            category = "English",
            createdDate = LocalDateTime.now()
        )
    )

    override fun getAllDocuments(): Flow<List<Document>> {
        // Trả về mock data thay vì gọi từ DAO
        return flowOf(mockDocuments)
    }

    override suspend fun insertDocument(document: Document): Long {
        // Giả lập việc thêm document và trả về id
        return document.docId.toLongOrNull() ?: 0L
    }

    override suspend fun deleteDocument(document: Document) {
        // Không làm gì cả trong mock implementation
    }

    override fun getDocumentById(id: Int): Flow<Document?> {
        // Tìm document có id tương ứng trong mock data
        val document = mockDocuments.find { it.docId == id.toString() }
        return flowOf(document)
    }
}