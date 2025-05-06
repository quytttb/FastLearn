package com.app.fastlearn.ui.screens.documents

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.fastlearn.R
import com.app.fastlearn.ui.components.DocumentGridItem
import com.app.fastlearn.ui.components.DocumentListItem
import com.app.fastlearn.ui.components.EmptyScreen
import com.app.fastlearn.ui.components.SearchTopBar
import com.app.fastlearn.ui.components.SelectTopBar
import com.app.fastlearn.ui.screens.flashcards.FlashcardsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SuspiciousIndentation")
@Composable
fun DocumentsScreen(
    modifier: Modifier = Modifier,
    documentsViewModel: DocumentsViewModel = hiltViewModel(),
    flashcardsViewModel: FlashcardsViewModel = hiltViewModel(),
    onProfileClick: () -> Unit,
    onDocumentClick: (String) -> Unit
) {

    // Lấy trạng thái của ViewModel
    val uiState by documentsViewModel.uiState.collectAsState()
    var isSearchActive by remember { mutableStateOf(false) }
    var isGridView by remember { mutableStateOf(true) }

    // Lưu trữ số lượng flashcard cho từng tài liệu
    val flashcardCounts = remember { mutableStateMapOf<String, Int>() }

    // Tải số lượng flashcard khi tài liệu thay đổi
    LaunchedEffect(uiState.allDocuments) {
        uiState.allDocuments.forEach { document ->
            flashcardsViewModel.getFlashcardCountForDocument(document.docId) { count ->
                flashcardCounts[document.docId] = count
            }
        }
    }

    // Kiểm trạng thái tìm kiếm
    var hasSearched by remember { mutableStateOf(false) }

    // Kiểm tra xem có đang hiển thị kết quả tìm kiếm hay không
    val isShowingSearchResults = (isSearchActive || hasSearched) && uiState.searchQuery.isNotEmpty()

    // Hiển thị các tài liệu dựa trên trạng thái tìm kiếm
    val documentsToDisplay =
        if (uiState.searchQuery.isBlank()) uiState.allDocuments else uiState.searchResults

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
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
                SearchTopBar(
                    query = uiState.searchQuery,
                    onQueryChange = { documentsViewModel.updateSearchQuery(it) },
                    onSearch = { query ->
                        if (query.isNotEmpty()) {
                            isSearchActive = true
                            hasSearched = true
                        }
                    },
                    documents = documentsToDisplay,
                    onBackClick = {
                        isSearchActive = false
                        hasSearched = false
                        documentsViewModel.clearSearch()
                    },
                    onDocumentClick = { doc ->
                        onDocumentClick(doc.docId)
                        isSearchActive = false
                    },
                    expanded = isSearchActive,
                    onExpandedChange = { isSearchActive = it },
                    onSelect = { documentsViewModel.toggleSelectionMode() },
                    onProfileClick = onProfileClick,
                    isGridView = isGridView,
                    onToggleViewClick = { isGridView = !isGridView }
                )
            }

            //
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                // Hiển thị nhãn kết quả khi đang tìm kiếm
                if (isShowingSearchResults) {
                    Text(
                        text = stringResource(
                            id = R.string.search_results_for,
                            uiState.searchQuery
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                // Hiển thị thông báo lỗi nếu có
                if (uiState.isError) {
                    Text(
                        text = uiState.errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                // Xử lý trường hợp không có tài liệu nào
                if (documentsToDisplay.isEmpty()) {
                    if (isShowingSearchResults) {
                        // Khi không có kết quả tìm kiếm
                        EmptyScreen(
                            modifier.fillMaxSize(),
                            vectorImage = Icons.Default.Search,
                            title = stringResource(id = R.string.no_search_results),
                            message = stringResource(id = R.string.try_different_search)
                        )
                    } else {
                        // Khi không có tài liệu nào
                        EmptyScreen(
                            modifier.fillMaxSize(),
                            vectorImage = Icons.AutoMirrored.Filled.Note,
                            title = stringResource(R.string.empty_title),
                            message = stringResource(R.string.empty_content),
                            actionLabel = stringResource(R.string.add_document),
                        )
                    }
                } else {
                    // Hiển thị danh sách tài liệu theo kiểu lưới hoặc danh sách
                    if (isGridView) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(documentsToDisplay) { document ->
                                val isSelected = uiState.selectedDocuments.contains(document.docId)
                                DocumentGridItem(
                                    document = document,
                                    isSelected = isSelected,
                                    isSelectionMode = uiState.isSelectionMode,
                                    flashcardCount = flashcardCounts[document.docId] ?: 0,
                                    onDocumentClick = { doc ->
                                        if (uiState.isSelectionMode) {
                                            documentsViewModel.toggleDocumentSelection(doc.docId)
                                        } else {
                                            onDocumentClick(doc.docId)
                                        }
                                    },
                                    onLongClick = {
                                        if (!uiState.isSelectionMode) {
                                            documentsViewModel.toggleSelectionMode()
                                            documentsViewModel.toggleDocumentSelection(document.docId)
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
                            items(documentsToDisplay) { document ->
                                val isSelected = uiState.selectedDocuments.contains(document.docId)
                                DocumentListItem(
                                    document = document,
                                    isSelected = isSelected,
                                    isSelectionMode = uiState.isSelectionMode,
                                    flashcardCount = flashcardCounts[document.docId] ?: 0,
                                    onDocumentClick = { doc ->
                                        if (uiState.isSelectionMode) {
                                            documentsViewModel.toggleDocumentSelection(doc.docId)
                                        } else {
                                            onDocumentClick(doc.docId)
                                        }
                                    },
                                    onLongClick = {
                                        if (!uiState.isSelectionMode) {
                                            documentsViewModel.toggleSelectionMode()
                                            documentsViewModel.toggleDocumentSelection(document.docId)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}