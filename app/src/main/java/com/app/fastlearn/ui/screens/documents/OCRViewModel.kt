package com.app.fastlearn.ui.screens.documents

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.fastlearn.domain.model.RecognizedText
import com.app.fastlearn.domain.repository.RecognizedTextRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OCRViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val recognizedTextRepository: RecognizedTextRepository
) : ViewModel() {

    private val recognizedTextId: String = savedStateHandle["recognizedTextId"] ?: ""

    private val _recognizedText = MutableStateFlow<RecognizedText?>(null)
    val recognizedText: StateFlow<RecognizedText?> = _recognizedText.asStateFlow()

    // Tải văn bản đã nhận diện từ kho dữ liệu
    fun loadRecognizedText() {
        if (recognizedTextId.isNotEmpty()) {
            viewModelScope.launch {
                _recognizedText.value = recognizedTextRepository.getRecognizedTextById(recognizedTextId)
            }
        } else {
            Log.e("OCRViewModel", "recognizedTextId is empty")
        }
    }
}
