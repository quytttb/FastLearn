package com.app.fastlearn.util

import android.graphics.BitmapFactory
import com.google.mlkit.vision.common.InputImage
import java.io.File

object ImageUtils {
    // Chuyển đổi file ảnh thành InputImage để sử dụng OCR với ML Kit
    fun fileToInputImage(imageFile: File): InputImage {
        val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
        return InputImage.fromBitmap(bitmap, 0)
    }
}