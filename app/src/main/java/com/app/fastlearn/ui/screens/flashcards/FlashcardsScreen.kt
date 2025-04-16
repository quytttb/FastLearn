package com.app.fastlearn.ui.screens.flashcards

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.fastlearn.R
import com.app.fastlearn.ui.components.SearchTopBar
import com.app.fastlearn.ui.components.SelectTopBar
import com.app.fastlearn.ui.screens.documents.DocumentsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardsScreen(
    modifier: Modifier = Modifier,
    flashcardsViewModel: FlashcardsViewModel = hiltViewModel(),
    documentsViewModel: DocumentsViewModel = hiltViewModel(),
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf(
        stringResource(R.string.tab_flashcards),
        stringResource(R.string.tab_flashcards_list)
    )
    val uiState by documentsViewModel.uiState.collectAsState()
    var isSearchActive by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar - Show different top bars depending on tab and state
            if (selectedTabIndex == 0) {
                // Show regular TopAppBar for Flashcards tab
                TopAppBar(
                    title = { Text(text = stringResource(R.string.flashcards_screen_title)) },
                    actions = {
                        IconButton(onClick = { flashcardsViewModel.shuffleFlashcards() }) {
                            Icon(
                                imageVector = Icons.Default.Shuffle,
                                contentDescription = stringResource(R.string.shuffle_flashcards)
                            )
                        }
                    }
                )
            } else {
                // Show SearchTopBar or SelectTopBar for Flashcards List tab
                if (uiState.isSelectionMode) {
                    SelectTopBar(
                        selectedCount = uiState.selectedDocuments.size,
                        onBackClick = { documentsViewModel.toggleSelectionMode() },
                        onDeleteClick = { documentsViewModel.deleteSelectedDocuments() },
                        onCreateFlashcardsClick = {
                            documentsViewModel.createFlashcardsFromSelected(
                                onSuccess = {
                                    documentsViewModel.toggleSelectionMode()
                                }
                            )
                        },
                        onSelectAllClick = { documentsViewModel.selectAllDocuments() }
                    )
                } else {
                    // Filter documents based on what has flashcards
                    val documentsToDisplay = if (uiState.searchQuery.isBlank()) {
                        uiState.allDocuments
                    } else {
                        uiState.searchResults
                    }

                    SearchTopBar(
                        query = uiState.searchQuery,
                        onQueryChange = { documentsViewModel.updateSearchQuery(it) },
                        onSearch = { query ->
                            if (query.isNotEmpty()) {
                                isSearchActive = true
                            }
                        },
                        documents = documentsToDisplay,
                        onBackClick = {
                            isSearchActive = false
                            documentsViewModel.clearSearch()
                        },
                        onDocumentClick = { doc ->
                            flashcardsViewModel.loadFlashcards(doc.docId)
                            selectedTabIndex = 0  // Switch to flashcards tab
                            isSearchActive = false
                        },
                        expanded = isSearchActive,
                        onExpandedChange = { isSearchActive = it },
                        onSelect = { documentsViewModel.toggleSelectionMode() },
                        onProfileClick = { },
                        isGridView = false,
                        onToggleViewClick = { }
                    )
                }
            }

            // Tab Row
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(text = title) }
                    )
                }
            }

            // Tab Content
            when (selectedTabIndex) {
                0 -> FlashcardsTab(flashcardsViewModel)
                1 -> FlashcardsListTab(
                    documentsViewModel = documentsViewModel,
                    flashcardsViewModel = flashcardsViewModel,
                    onDocumentSelected = {
                        // Switch to flashcards tab when a document is selected
                        selectedTabIndex = 0
                    }
                )
            }
        }
    }
}