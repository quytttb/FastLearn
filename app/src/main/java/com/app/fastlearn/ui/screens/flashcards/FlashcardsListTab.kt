package com.app.fastlearn.ui.screens.flashcards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.fastlearn.domain.model.Document
import com.app.fastlearn.ui.screens.documents.DocumentsViewModel
import kotlin.text.set

@Composable
fun FlashcardsListTab(
    documentsViewModel: DocumentsViewModel,
    flashcardsViewModel: FlashcardsViewModel,
    onDocumentSelected: (String) -> Unit
) {
    val documents by documentsViewModel.documents.collectAsState()

    // Số lượng flashcard theo từng tài liệu
    val flashcardCounts = remember { mutableStateMapOf<String, Int>() }

    // Nạp flashcardCounts cho tất cả các tài liệu khi FlashcardsListTab hiển thị
    LaunchedEffect(Unit) {
        documents.forEach { document ->
            flashcardsViewModel.getFlashcardCountForDocument(document.docId) { count ->
                flashcardCounts[document.docId] = count
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (documents.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No documents available",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn {
                items(documents) { document ->
                    DocumentItem(
                        document = document,
                        flashcardCount = flashcardCounts[document.docId] ?: 0,
                        onClick = {
                            flashcardsViewModel.loadFlashcards(document.docId)
                            onDocumentSelected(document.docId)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DocumentItem(
    document: Document,
    flashcardCount: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = document.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Created: ${document.createdDate.toLocalDate()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Badge(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Text(
                    text = "$flashcardCount cards",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}