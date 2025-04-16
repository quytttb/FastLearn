package com.app.fastlearn.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.app.fastlearn.R

@Composable
fun DeleteDocumentDialog(
    show: Boolean,
    documentsCount: Int = 1,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete_documents)
                )
            },
            title = { Text(text = stringResource(R.string.delete_documents)) },
            text = {
                Text(
                    text = stringResource(
                        if (documentsCount > 1)
                            R.string.delete_multiple_documents_confirmation
                        else
                            R.string.delete_document_confirmation,
                        documentsCount
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text(stringResource(R.string.delete))
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