package com.app.fastlearn.data.util

import com.app.fastlearn.data.local.entity.CategoryEntity
import com.app.fastlearn.data.local.entity.DocumentEntity
import com.app.fastlearn.data.local.entity.FlashcardEntity
import com.app.fastlearn.data.local.entity.ProgressEntity
import com.app.fastlearn.domain.model.Category
import com.app.fastlearn.domain.model.Document
import com.app.fastlearn.domain.model.Flashcard
import com.app.fastlearn.domain.model.Progress
import javax.inject.Inject

class DataMapper @Inject constructor() {

    // Document mappings
    fun mapDocumentEntityToDomain(entity: DocumentEntity): Document {
        return Document(
            docId = entity.docId,
            title = entity.title,
            content = entity.content,
            filePath = entity.filePath,
            categoryId = entity.categoryId,
            createdDate = entity.createdDate
        )
    }

    fun mapDocumentDomainToEntity(domain: Document): DocumentEntity {
        return DocumentEntity(
            docId = domain.docId,
            title = domain.title,
            content = domain.content,
            filePath = domain.filePath,
            categoryId = domain.categoryId,
            createdDate = domain.createdDate
        )
    }

    // Category mappings
    fun mapCategoryEntityToDomain(entity: CategoryEntity): Category {
        return Category(
            categoryId = entity.categoryId,
            name = entity.name,
            description = entity.description
        )
    }

    fun mapCategoryDomainToEntity(domain: Category): CategoryEntity {
        return CategoryEntity(
            categoryId = domain.categoryId,
            name = domain.name,
            description = domain.description
        )
    }

    // Flashcard mappings
    fun mapFlashcardEntityToDomain(entity: FlashcardEntity): Flashcard {
        return Flashcard(
            flashId = entity.flashId,
            docId = entity.docId,
            question = entity.question,
            answer = entity.answer,
            createdDate = entity.createdDate
        )
    }

    fun mapFlashcardDomainToEntity(domain: Flashcard): FlashcardEntity {
        return FlashcardEntity(
            flashId = domain.flashId,
            docId = domain.docId,
            question = domain.question,
            answer = domain.answer,
            createdDate = domain.createdDate
        )
    }

    // Progress mappings
    fun mapProgressEntityToDomain(entity: ProgressEntity): Progress {
        return Progress(
            progressId = entity.progressId,
            flashId = entity.flashId,
            studyDate = entity.studyDate,
            status = entity.status,
            timeSpent = entity.timeSpent
        )
    }

    fun mapProgressDomainToEntity(domain: Progress): ProgressEntity {
        return ProgressEntity(
            progressId = domain.progressId,
            flashId = domain.flashId,
            studyDate = domain.studyDate,
            status = domain.status,
            timeSpent = domain.timeSpent
        )
    }
}