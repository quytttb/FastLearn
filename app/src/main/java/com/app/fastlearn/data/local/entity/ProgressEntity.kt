package com.app.fastlearn.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "study_progress",
    foreignKeys = [
        ForeignKey(
            entity = FlashcardEntity::class,
            parentColumns = ["flashId"],
            childColumns = ["flashId"],
            onDelete = ForeignKey.CASCADE // Xóa progress khi flashcard bị xóa
        )
    ],
    indices = [Index("flashId")]
)
data class ProgressEntity(
    @PrimaryKey
    val progressId: String,
    val flashId: String,
    val studyDate: LocalDateTime,
    val status: String,
    val timeSpent: Int
)