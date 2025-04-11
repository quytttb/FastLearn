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
import com.app.fastlearn.ui.screens.documents.DocumentsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardsScreen(
    modifier: Modifier = Modifier,
    flashcardsViewModel: FlashcardsViewModel = hiltViewModel(),
    documentsViewModel: DocumentsViewModel = hiltViewModel(),
    onAddFlashcardClick: () -> Unit = {}
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf(
        stringResource(R.string.tab_flashcards),
        stringResource(R.string.tab_flashcards_list)
    )

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text(text = stringResource(R.string.flashcards_screen_title)) },
                actions = {
                    if (selectedTabIndex == 0) {
                        IconButton(onClick = { flashcardsViewModel.shuffleFlashcards() }) {
                            Icon(
                                imageVector = Icons.Default.Shuffle,
                                contentDescription = stringResource(R.string.shuffle_flashcards)
                            )
                        }
                    }
                }
            )

            TabRow(selectedTabIndex = selectedTabIndex) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(text = title) }
                    )
                }
            }

            when (selectedTabIndex) {
                0 -> FlashcardsTab(flashcardsViewModel)
                1 -> FlashcardsListTab(documentsViewModel, flashcardsViewModel, onDocumentSelected = {
                    // Chuyển đến tab flashcard cập nhật flashcards tương ứng với tài liệu đã chọn
                    selectedTabIndex = 0
                })
            }
        }

        FloatingActionButton(
            onClick = { onAddFlashcardClick() },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd),
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(R.string.add_new_flashcard),
            )
        }
    }
}