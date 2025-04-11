package com.app.fastlearn.ui.screens.documents

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.fastlearn.R
import com.app.fastlearn.ui.components.DocumentDetailTopBar
import com.app.fastlearn.ui.components.LoadingOverlay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentDetailScreen(
    documentId: String,
    modifier: Modifier = Modifier,
    viewModel: DocumentsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val document = viewModel.getDocumentById(documentId)
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var isLoading by remember { mutableStateOf(false) }
    var loadingMessage by remember { mutableStateOf("") }
    var openDialog by remember { mutableStateOf(false) }

    // Tạo hành vi cuộn cho TopAppbar
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            DocumentDetailTopBar(
                title = document?.title ?: "N/A",
                scrollBehavior = scrollBehavior,
                onBackClick = onNavigateBack,
                onCreateFlashcardsClick = { openDialog = true }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Nội dung tài liệu
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = paddingValues.calculateTopPadding()
                    )
                    .verticalScroll(rememberScrollState())
            ) {
                document?.let { doc ->
                    Text(
                        text = doc.content,
                        modifier = Modifier.padding(16.dp)
                    )
                } ?: Text(
                    text = "N/A",
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Loading overlay
            LoadingOverlay(
                isLoading = isLoading,
                message = loadingMessage,
                useDialog = true
            )
        }
    }

    // Hộp thoại xác nhận tạo flashcards
    if (openDialog) {
        AlertDialog(
            onDismissRequest = { openDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.CreditCard,
                    contentDescription = stringResource(R.string.create_flashcards)
                )
            },
            title = { Text(text = stringResource(R.string.create_flashcards)) },
            text = { Text(text = stringResource(R.string.create_flashcard_confirmation)) },
            confirmButton = {
                TextButton(onClick = {
                    openDialog = false
                    isLoading = true
                    loadingMessage = context.getString(R.string.creating_flashcards)

                    coroutineScope.launch {
                        viewModel.createFlashcard(
                            document,
                            onSuccess = {
                                isLoading = false
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.flashcards_created_success),
                                    Toast.LENGTH_SHORT
                                ).show()
                                onNavigateBack()
                            },
                            onError = { error ->
                                isLoading = false
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.flashcards_creation_failed),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                }) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { openDialog = false }) {
                    Text(stringResource(R.string.discard))
                }
            }
        )
    }
}