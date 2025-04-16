package com.app.fastlearn.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.app.fastlearn.R
import com.app.fastlearn.domain.model.Document

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    query: String = "",
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    documents: List<Document>,
    onBackClick: () -> Unit,
    onDocumentClick: (Document) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onSelect: () -> Unit,
    onProfileClick: () -> Unit,
    isGridView: Boolean,
    onToggleViewClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            //.fillMaxWidth()
            .semantics { isTraversalGroup = true }
    ) {
        SearchBar(
            inputField = @Composable {
                SearchBarDefaults.InputField(
                    query = query,
                    onQueryChange = onQueryChange,
                    onSearch = {
                        onSearch(query)
                        onExpandedChange(false)
                    },
                    expanded = expanded,
                    onExpandedChange = onExpandedChange,
                    enabled = true,
                    placeholder = { Text(text = stringResource(id = R.string.search_documents)) },
                    leadingIcon = {
                        if (expanded) {
                            IconButton(onClick = { onBackClick() }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Quay lại"
                                )
                            }
                        } else {
                            Icon(Icons.Default.Search, contentDescription = "Biểu tượng tìm kiếm")
                        }
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { onQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = stringResource(id = R.string.clear_search)
                                )
                            }
                        } else if (!expanded) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = onToggleViewClick) {
                                    Icon(
                                        imageVector = if (isGridView)
                                            Icons.AutoMirrored.Outlined.ListAlt
                                        else
                                            Icons.Outlined.GridView,
                                        contentDescription = stringResource(id = R.string.toggle_view)
                                    )
                                }
                                IconButton(onClick = onSelect) {
                                    Icon(
                                        imageVector = Icons.Outlined.CheckCircle,
                                        contentDescription = stringResource(id = R.string.select)
                                    )
                                }
                                IconButton(onClick = onProfileClick) {
                                    Icon(
                                        imageVector = Icons.Outlined.AccountCircle,
                                        contentDescription = stringResource(id = R.string.profile),
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }
                )
            },
            expanded = expanded,
            onExpandedChange = onExpandedChange,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .semantics { traversalIndex = 0f },
            content = @Composable {
                // Hiển thị danh sách tài liệu khi SearchBar được mở rộng
                if (documents.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (query.isEmpty() && documents.isEmpty()) {
                            Text(
                                text = stringResource(id = R.string.add_document),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else if (query.isNotEmpty()) {
                            Text(
                                text = stringResource(id = R.string.no_search_results),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(documents) { document ->
                            ListItem(
                                headlineContent = {
                                    Text(
                                        text = document.title,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                supportingContent = {
                                    Text(
                                        text = document.content.take(100),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                },
                                leadingContent = {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = "Kết quả tìm kiếm",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                                modifier = Modifier
                                    .clickable {
                                        onDocumentClick(document)
                                        onExpandedChange(false)
                                    }
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        )
    }
}