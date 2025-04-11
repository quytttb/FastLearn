package com.app.fastlearn.ui.screens.study

import android.util.Log
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.fastlearn.R
import com.app.fastlearn.domain.model.Document
import com.app.fastlearn.ui.components.EmptyScreen
import com.app.fastlearn.ui.screens.documents.DocumentsViewModel
import com.app.fastlearn.ui.screens.flashcards.FlashcardsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyListScreen(
    modifier: Modifier = Modifier,
    documentsViewModel: DocumentsViewModel = hiltViewModel(),
    flashcardsViewModel: FlashcardsViewModel = hiltViewModel(),
    onDocumentSelected: (String) -> Unit,
    onNavigateToDocuments: () -> Unit
) {
    val documents by documentsViewModel.documents.collectAsState()

    // Map to store flashcard counts for each document
    val flashcardCounts = remember { mutableStateMapOf<String, Int>() }

    // Load flashcard counts for all documents when the screen is displayed
    LaunchedEffect(documents) {
        documents.forEach { document ->
            flashcardsViewModel.getFlashcardCountForDocument(document.docId) { count ->
                flashcardCounts[document.docId] = count
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.study_screen_title)) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToDocuments) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.add_document)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (documents.isEmpty()) {
                EmptyScreen()
            } else {
/*                Text(
                    text = stringResource(id = R.string.select_document_to_study),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )*/

                LazyColumn {
                    items(documents.filter { document ->
                        // Only show documents that have flashcards
                        flashcardCounts[document.docId] ?: 0 > 0
                    }) { document ->
                        DocumentItem(
                            document = document,
                            flashcardCount = flashcardCounts[document.docId] ?: 0,
                            onClick = {
                                onDocumentSelected(document.docId)
                            }
                        )
                    }
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