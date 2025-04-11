package com.app.fastlearn.ui.screens.documents

import ExtendedFAB
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ContentPasteSearch
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.fastlearn.ui.components.DocumentGridItem
import com.app.fastlearn.ui.components.DocumentListItem
import com.app.fastlearn.ui.components.SearchTopBar
import com.app.fastlearn.R
import com.app.fastlearn.ui.components.EmptyScreen
import com.app.fastlearn.ui.components.ListTopBar

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
    // Trạng thái UI
    val documents by viewModel.documents.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
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
                    onSearch = {
                        // Todo: Thêm logic tìm kiếm tại đây
                        // Ví dụ: gọi API tìm kiếm hoặc lọc danh sách tài liệu
                    })
            } else {
                ListTopBar(
                    title = stringResource(R.string.documents_screen_title),
                    showBackButton = false,
                    onBackClick = {},
                    onProfileClick = onProfileClick,
                    isSearchActive = isSearchActive,
                    isGridView = isGridView,
                    onSearchClick = { isSearchActive = true },
                    onToggleViewClick = { isGridView = !isGridView })
            }

            // Hiển thị danh sách trống nếu không có tài liệu
            if (documents.isEmpty()) {
                EmptyScreen(modifier.fillMaxSize())
            }
            // Danh sách tài liệu - Grid hoặc List
            else if (!(isSearchActive && searchQuery.isEmpty())) {
                // Danh sách tài liệu - Grid hoặc List
                if (isGridView) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(documents) { document ->
                            DocumentGridItem(
                                document = document,
                                onDocumentClick = { doc -> onDocumentClick(doc.docId) })
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(8.dp), modifier = Modifier.weight(1f)
                    ) {
                        items(documents) { document ->
                            DocumentListItem(
                                document = document,
                                onDocumentClick = { doc -> onDocumentClick(doc.docId) })
                        }
                    }
                }
            } else {
                // Hiển thị gợi ý tìm kiếm khi đang trong chế độ tìm kiếm với truy vấn trống
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
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

        // FAB để thêm tài liệu
        ExtendedFAB(
            onCameraClick = onOpenCamera,
            onFileClick = onImportFile,
        )
    }
}