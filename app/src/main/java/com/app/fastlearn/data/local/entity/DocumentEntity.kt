package com.app.fastlearn.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "documents",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["categoryId"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("categoryId")]
)
data class DocumentEntity(
    @PrimaryKey
    val docId: String,
    val title: String,
    val content: String,
    val filePath: String,
    val categoryId: String?, // Changed from category to categoryId
    val createdDate: LocalDateTime
)