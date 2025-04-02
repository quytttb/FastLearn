package com.app.fastlearn.domain.model

// Class này đại diện cho một đoạn văn bản đã được nhận diện từ hình ảnh
// Nó dùng để lưu trữ văn bản trung gian cho việc điều hướng
data class RecognizedText(
    val id: String,
    val text: String
)