package com.app.fastlearn.util

import android.content.Context
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Lớp quản lý việc dọn dẹp các file ảnh tạm.
 * Có thể được đưa vào trong Application class để gọi khi ứng dụng khởi động.
 */
@Singleton
class ImageCleanupManager @Inject constructor(
    private val context: Context
) {
    /**
     * Xóa tất cả các file ảnh tạm cũ hơn một khoảng thời gian nhất định
     * @param maxAgeHours Tuổi tối đa của file tính bằng giờ
     */
    fun cleanupOldTempImages(maxAgeHours: Int = 24) {
        try {
            val cacheDir = context.cacheDir
            val currentTime = System.currentTimeMillis()
            val maxAgeMs = TimeUnit.HOURS.toMillis(maxAgeHours.toLong())

            val oldImageFiles = cacheDir.listFiles { file ->
                file.isFile &&
                        file.name.startsWith("IMG_") &&
                        file.name.endsWith(".jpg") &&
                        (currentTime - file.lastModified() > maxAgeMs)
            }

            oldImageFiles?.forEach { file ->
                file.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Xóa tất cả các file ảnh tạm
     */
    fun cleanupAllTempImages() {
        try {
            val cacheDir = context.cacheDir
            val imageFiles = cacheDir.listFiles { file ->
                file.isFile && file.name.startsWith("IMG_") && file.name.endsWith(".jpg")
            }

            imageFiles?.forEach { file ->
                file.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}