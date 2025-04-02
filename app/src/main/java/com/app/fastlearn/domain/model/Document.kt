package com.app.fastlearn.domain.model

import java.time.LocalDateTime

data class Document(
    val docId: String,
    val title: String,
    val content: String,
    val filePath: String,
    val category: String,
    val createdDate: LocalDateTime
)