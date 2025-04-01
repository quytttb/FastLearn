package com.app.fastlearn.ui.screens.capture

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ImagePreviewViewModel @Inject constructor() : ViewModel() {
    // StateFlow để theo dõi file ảnh
    private val _imageFile = MutableStateFlow<File?>(null)
    val imageFile: StateFlow<File?> = _imageFile.asStateFlow()

    // StateFlow để theo dõi cờ xóa ảnh
    private val _shouldDeleteImage = MutableStateFlow(false)
    val shouldDeleteImage: StateFlow<Boolean> = _shouldDeleteImage.asStateFlow()

    // Tải file ảnh vào ViewModel
    fun setImageFile(file: File) {
        _imageFile.value = file
    }

    // Xóa ảnh
    fun discardImage() {
        _imageFile.value?.delete()
        _imageFile.value = null
    }

    // Xác nhận ảnh
    fun confirmImage() {
        // TODO: Thêm logic để lưu ảnh vào bộ nhớ hoặc thực hiện hành động khác
    }
}