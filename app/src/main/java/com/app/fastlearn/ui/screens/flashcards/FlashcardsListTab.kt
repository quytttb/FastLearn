package com.app.fastlearn.ui.screens.flashcards

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.app.fastlearn.R
import com.app.fastlearn.ui.components.DocumentListItem
import com.app.fastlearn.ui.components.EmptyScreen
import com.app.fastlearn.ui.screens.documents.DocumentsViewModel

@Composable
fun FlashcardsListTab(
    documentsViewModel: DocumentsViewModel,
    flashcardsViewModel: FlashcardsViewModel,
    onDocumentSelected: (String) -> Unit
) {
    val documents by documentsViewModel.uiState.collectAsState()
    val documentsToDisplay = documents.allDocuments
    val flashcardCounts = remember { mutableStateMapOf<String, Int>() }

    // Load flashcard counts when documents change
    LaunchedEffect(documentsToDisplay) {
        documentsToDisplay.forEach { document ->
            flashcardsViewModel.getFlashcardCountForDocument(document.docId) { count ->
                flashcardCounts[document.docId] = count
            }
        }
    }

    if (documents.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (documentsToDisplay.isEmpty()) {
        EmptyScreen(
            modifier = Modifier.fillMaxSize(),
            vectorImage = Icons.Outlined.CreditCard,
            title = stringResource(R.string.empty_title),
            message = stringResource(R.string.no_documents_for_flashcards)
        )
    } else {
        LazyColumn(
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(documentsToDisplay) { document ->
                // Chỉ hiển thị tài liệu có flashcard
                val flashcardCount = flashcardCounts[document.docId] ?: 0
                if (flashcardCount > 0) {
                    DocumentListItem(
                        document = document,
                        isSelected = false,
                        isSelectionMode = false,
                        flashcardCount = flashcardCount,
                        onDocumentClick = {
                            flashcardsViewModel.loadFlashcards(document.docId)
                            onDocumentSelected(document.docId)
                        }
                    )
                }
            }
        }
    }
}