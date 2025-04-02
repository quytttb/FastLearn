package com.app.fastlearn.ui.screens.study

import androidx.lifecycle.ViewModel
import com.app.fastlearn.data.repository.ProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StudyViewModel @Inject constructor(
    private val progressRepository: ProgressRepository
) : ViewModel() {
    // TODO: Thêm logic cho ViewModel
}