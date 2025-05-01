package com.app.fastlearn.domain.usecase

import com.app.fastlearn.domain.model.RecognizedText
import com.app.fastlearn.domain.repository.RecognizedTextRepository
import com.app.fastlearn.domain.service.OCRService
import java.io.File
import javax.inject.Inject

class OCRUseCase @Inject constructor(
    private val ocrService: OCRService,
    private val recognizedTextRepository: RecognizedTextRepository
) {
    /**
     * Thực hiện nhận dạng văn bản từ ảnh và lưu trữ kết quả
     *
     * @param file File ảnh cần nhận dạng
     * @return ID của văn bản đã nhận dạng hoặc null nếu quá trình nhận dạng thất bại
     */
    suspend fun executeOCR(file: File): String? {
        // Thực hiện OCR để nhận dạng văn bản
        val recognizedText = ocrService.recognizeTextFromImage(file)

        // Nếu nhận dạng thành công, lưu văn bản vào repository
        return recognizedText?.let {
            recognizedTextRepository.saveRecognizedText(
                RecognizedText(
                    id = file.nameWithoutExtension,
                    text = it
                )
            )
        }
    }
}