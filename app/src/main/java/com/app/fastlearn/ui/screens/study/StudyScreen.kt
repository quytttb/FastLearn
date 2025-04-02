package com.app.fastlearn.ui.screens.study

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.fastlearn.R

@Composable
fun StudyScreen() {
    val viewModel: StudyViewModel = hiltViewModel()
    Column {
        Text(stringResource(id = R.string.study_screen_title))
        // TODO: Implement study UI
    }
}