package com.app.fastlearn.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.app.fastlearn.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectTopBar(
    selectedCount: Int,
    onBackClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onCreateFlashcardsClick: () -> Unit,
    onSelectAllClick: () -> Unit
) {
    var showFlashCardDialog by remember { mutableStateOf(false) }
    var showDeleteDocumentDialog by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(
                text = if (selectedCount > 0)
                    stringResource(R.string.selected_items_count, selectedCount)
                else
                    stringResource(R.string.select_items),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.navigate_back)
                )
            }
        },
        actions = {
            IconButton(onClick = onSelectAllClick) {
                Icon(
                    imageVector = Icons.Default.SelectAll,
                    contentDescription = stringResource(R.string.select_all)
                )
            }
            IconButton(onClick = { showDeleteDocumentDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete)
                )
            }
            IconButton(onClick = { showFlashCardDialog = true }) {
                Icon(
                    imageVector = Icons.Outlined.CreditCard,
                    contentDescription = stringResource(R.string.create_flashcards)
                )
            }
        },
        modifier = Modifier.fillMaxWidth()
    )

    // Sử dụng FlashCardsConfirmationDialog
    FlashCardsConfirmationDialog(
        show = showFlashCardDialog,
        onDismiss = {
            showFlashCardDialog = false
            onBackClick()
        },
        onConfirm = {
            showFlashCardDialog = false
            onCreateFlashcardsClick()
        }
    )

    // Sử dụng DeleteDocumentDialog
    DeleteDocumentDialog(
        show = showDeleteDocumentDialog,
        documentsCount = selectedCount,
        onDismiss = {
            showDeleteDocumentDialog = false
            onBackClick()
        },
        onConfirm = {
            showDeleteDocumentDialog = false
            onDeleteClick()
        }
    )
}