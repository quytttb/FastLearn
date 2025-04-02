package com.app.fastlearn.ui.screens.documents

import ExtendedFAB
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.fastlearn.R

@Composable
fun DocumentsScreen(
    viewModel: DocumentsViewModel = hiltViewModel(),
    onOpenCamera: () -> Unit,
    onImportFile: () -> Unit,
) {
    // Sử dụng danh sách tài liệu từ ViewModel
    val documents by viewModel.documents.collectAsState()

        Column {
            Text(stringResource(id = R.string.documents_screen_title))
            LazyColumn {
                items(documents) { document ->
                    Text(
                        text = document.title,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        ExtendedFAB(
            onCameraClick = onOpenCamera,
            onFileClick = onImportFile
        )

}

