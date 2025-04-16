package com.app.fastlearn.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.app.fastlearn.R

@Composable
fun FlashCardsConfirmationDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            icon = {
                Icon(
                    imageVector = Icons.Default.CreditCard,
                    contentDescription = stringResource(R.string.create_flashcards)
                )
            },
            title = { Text(text = stringResource(R.string.create_flashcards)) },
            text = { Text(text = stringResource(R.string.create_flashcard_confirmation)) },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.discard))
                }
            }
        )
    }
}