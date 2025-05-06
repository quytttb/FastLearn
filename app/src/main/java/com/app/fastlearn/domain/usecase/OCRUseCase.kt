package com.app.fastlearn.domain.usecase

import com.app.fastlearn.domain.service.OCRService
import java.io.File
import javax.inject.Inject

class OCRUseCase @Inject constructor(
    private val ocrService: OCRService
) {
    /**
     * Thực hiện nhận dạng văn bản từ ảnh
     *
     * @param file File ảnh cần nhận dạng
     * @return Văn bản được nhận dạng hoặc null nếu quá trình nhận dạng thất bại
     */
    suspend fun executeOCR(file: File): String? {
        // Thực hiện OCR để nhận dạng văn bản và trả về kết quả trực tiếp
        return ocrService.recognizeTextFromImage(file)
    }
}