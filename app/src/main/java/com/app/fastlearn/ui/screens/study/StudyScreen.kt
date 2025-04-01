package com.app.fastlearn.ui.screens.study

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun StudyScreen() {
    val viewModel: StudyViewModel = hiltViewModel()
    Column {
        Text("Study Screen")
        // TODO: Implement study UI
    }
}