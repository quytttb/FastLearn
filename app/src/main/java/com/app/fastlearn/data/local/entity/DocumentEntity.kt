package com.app.fastlearn.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey
    val docId: String,
    val title: String,
    val content: String,
    val filePath: String,
    val category: String,
    val createdDate: LocalDateTime
)