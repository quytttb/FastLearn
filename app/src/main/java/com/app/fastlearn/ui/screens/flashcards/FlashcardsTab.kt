package com.app.fastlearn.ui.screens.flashcards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.app.fastlearn.R
import com.app.fastlearn.ui.components.EmptyScreen
import com.app.fastlearn.ui.components.FlashcardItem

@Composable
fun FlashcardsTab(viewModel: FlashcardsViewModel) {
    val flashcards by viewModel.flashcards.collectAsState()
    val currentCardIndex by viewModel.currentCardIndex.collectAsState()
    val isFlipped by viewModel.isFlipped.collectAsState()
    val currentCard = if (flashcards.isNotEmpty()) {
        flashcards[currentCardIndex % flashcards.size]
    } else null

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
                    isFlipped = isFlipped,
                    onFlip = { viewModel.toggleFlip() },
                    onSwipe = { viewModel.moveToNextCard() }
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