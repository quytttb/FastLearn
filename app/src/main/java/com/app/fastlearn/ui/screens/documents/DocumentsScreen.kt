package com.app.fastlearn.ui.screens.documents

import ExtendedFAB
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentPasteSearch
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.fastlearn.R
import com.app.fastlearn.domain.model.Document
import com.app.fastlearn.ui.components.DocumentGridItem
import com.app.fastlearn.ui.components.DocumentListItem
import com.app.fastlearn.ui.components.DocumentsTopBar
import com.app.fastlearn.ui.components.EmptyScreen
import com.app.fastlearn.ui.components.SearchTopBar
import com.app.fastlearn.ui.components.SelectTopBar

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SuspiciousIndentation")
@Composable
fun DocumentsScreen(
    modifier: Modifier = Modifier,
    viewModel: DocumentsViewModel = hiltViewModel(),
    onOpenCamera: () -> Unit,
    onImportFile: () -> Unit,
    onProfileClick: () -> Unit = {},
    onDocumentClick: (String) -> Unit
) {
    // UI states
    val documents by viewModel.documents.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isSelectionMode by viewModel.isSelectionMode.collectAsState()
    val selectedDocumentIds by viewModel.selectedDocuments.collectAsState()
    var isSearchActive by remember { mutableStateOf(false) }
    var isGridView by remember { mutableStateOf(true) }
    val focusRequester = remember { FocusRequester() }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            if (isSearchActive) {
                SearchTopBar(
                    query = searchQuery,
                    onQueryChange = { viewModel.setSearchQuery(it) },
                    onSearchClosed = {
                        isSearchActive = false
                        viewModel.clearSearch()
                    },
                    focusRequester = focusRequester,
                    onSearch = {}
                )
            } else if (isSelectionMode) {
                SelectTopBar(
                    selectedCount = selectedDocumentIds.size,
                    onBackClick = { viewModel.toggleSelectionMode() },
                    onDeleteClick = { viewModel.deleteSelectedDocuments() },
                    onCreateFlashcardsClick = {
                        viewModel.createFlashcardsFromSelected(
                            onSuccess = {
                                viewModel.toggleSelectionMode()
                            }
                        )
                    },
                    onSelectAllClick = { viewModel.selectAllDocuments() }
                )
            } else {
                DocumentsTopBar(
                    title = stringResource(R.string.documents_screen_title),
                    showBackButton = false,
                    onBackClick = {},
                    onProfileClick = onProfileClick,
                    isSearchActive = isSearchActive,
                    isGridView = isGridView,
                    onSearchClick = { isSearchActive = true },
                    onToggleViewClick = { isGridView = !isGridView },
                    onSelect = { viewModel.toggleSelectionMode() }
                )
            }

            // Empty state
            if (documents.isEmpty()) {
                EmptyScreen(modifier.fillMaxSize())
            }
            // Document list - Grid or List
            else if (!(isSearchActive && searchQuery.isEmpty())) {
                if (isGridView) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(documents) { document ->
                            val isSelected = selectedDocumentIds.contains(document.docId)
                            DocumentGridItem(
                                document = document,
                                isSelected = isSelected,
                                isSelectionMode = isSelectionMode,
                                onDocumentClick = { doc ->
                                    if (isSelectionMode) {
                                        viewModel.toggleDocumentSelection(doc.docId)
                                    } else {
                                        onDocumentClick(doc.docId)
                                    }
                                },
                                onLongClick = {
                                    if (!isSelectionMode) {
                                        viewModel.toggleSelectionMode()
                                        viewModel.toggleDocumentSelection(document.docId)
                                    }
                                }
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(documents) { document ->
                            val isSelected = selectedDocumentIds.contains(document.docId)
                            DocumentListItem(
                                document = document,
                                isSelected = isSelected,
                                isSelectionMode = isSelectionMode,
                                onDocumentClick = { doc ->
                                    if (isSelectionMode) {
                                        viewModel.toggleDocumentSelection(doc.docId)
                                    } else {
                                        onDocumentClick(doc.docId)
                                    }
                                },
                                onLongClick = {
                                    if (!isSelectionMode) {
                                        viewModel.toggleSelectionMode()
                                        viewModel.toggleDocumentSelection(document.docId)
                                    }
                                }
                            )
                        }
                    }
                }
            } else {
                // Search suggestion when in search mode with empty query
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ContentPasteSearch,
                        contentDescription = stringResource(R.string.search),
                        modifier = Modifier.size(48.dp),
                        tint = Color.Gray
                    )
                }
            }
        }

        // FAB to add documents - show only when not in selection mode
        if (!isSelectionMode) {
            ExtendedFAB(
                onCameraClick = onOpenCamera,
                onFileClick = onImportFile,
            )
        }
    }
}