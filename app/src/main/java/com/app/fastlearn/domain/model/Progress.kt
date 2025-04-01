package com.app.fastlearn.domain.model

import java.time.LocalDateTime

data class Progress(
    val progressId: String,
    val flashId: String,
    val studyDate: LocalDateTime,
    val status: String,
    val timeSpent: Int
)