package com.app.fastlearn

import android.app.Application
import com.app.fastlearn.util.ImageCleanupManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class Application : Application() {

    @Inject
    lateinit var imageCleanupManager: ImageCleanupManager

    override fun onCreate() {
        super.onCreate()

        // Dọn dẹp các file ảnh tạm cũ khi ứng dụng khởi động
        imageCleanupManager.cleanupOldTempImages()
    }
}