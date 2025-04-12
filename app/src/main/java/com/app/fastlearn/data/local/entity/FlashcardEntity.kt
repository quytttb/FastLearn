package com.app.fastlearn.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "flashcards",
    foreignKeys = [
        ForeignKey(
            entity = DocumentEntity::class,
            parentColumns = ["docId"],
            childColumns = ["docId"],
            onDelete = ForeignKey.CASCADE // Xóa flashcard khi tài liệu bị xóa
        )
    ],
    indices = [Index("docId")]
)
data class FlashcardEntity(
    @PrimaryKey
    val flashId: String,
    val docId: String,
    val question: String,
    val answer: String,
    val createdDate: LocalDateTime
)