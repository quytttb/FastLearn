package com.app.fastlearn.ui.screens.flashcards

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.app.fastlearn.R

@Composable
fun FlashcardsScreen(
    viewModel: FlashcardsViewModel = hiltViewModel()
) {
    val flashcards by viewModel.flashcards.collectAsState()


    Column {
        Text(stringResource(id = R.string.flashcards_screen_title))
        LazyColumn {
            items(flashcards) { flashcards ->
                Text(
                    text = flashcards.question,
                    modifier = Modifier.padding(2.dp)
                )
                Text(
                    text = flashcards.answer,
                    modifier = Modifier.padding(16.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}