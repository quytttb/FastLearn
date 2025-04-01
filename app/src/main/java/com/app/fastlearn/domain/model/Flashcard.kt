package com.app.fastlearn.domain.model

import java.time.LocalDateTime

data class Flashcard(
    val flashId: String,
    val docId: String,
    val question: String,
    val answer: String,
    val createdDate: LocalDateTime
)