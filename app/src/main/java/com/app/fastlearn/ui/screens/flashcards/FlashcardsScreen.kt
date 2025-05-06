package com.app.fastlearn.ui.screens.flashcards

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.fastlearn.R
import com.app.fastlearn.ui.components.EmptyScreen
import com.app.fastlearn.ui.components.FlashcardItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardsScreen(
    modifier: Modifier = Modifier,
    flashcardsViewModel: FlashcardsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val flashcards by flashcardsViewModel.flashcards.collectAsState()
    val currentCardIndex by flashcardsViewModel.currentCardIndex.collectAsState()
    val isFlipped by flashcardsViewModel.isFlipped.collectAsState()

    // Lấy thẻ flashcard hiện tại
    val currentCard = if (flashcards.isNotEmpty()) {
        flashcards[currentCardIndex % flashcards.size]
    } else null

    // Lấy thẻ flashcard tiếp theo để hiển thị dưới thẻ hiện tại
    val nextCard = if (flashcards.isNotEmpty() && flashcards.size > 1) {
        flashcards[(currentCardIndex + 1) % flashcards.size]
    } else null

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
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

            // Flashcards
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (flashcards.isEmpty()) {
                    EmptyScreen()
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.tap_to_flip),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        FlashcardItem(
                            flashcard = currentCard,
                            nextFlashcard = nextCard,
                            isFlipped = isFlipped,
                            onFlip = { flashcardsViewModel.toggleFlip() },
                            onSwipe = { flashcardsViewModel.moveToNextCard() }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Badge(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ) {
                            Text(
                                text = "${currentCardIndex % flashcards.size + 1}/${flashcards.size}",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}