package com.app.fastlearn.ui.screens.documents

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.fastlearn.R

import androidx.compose.ui.platform.LocalContext

@Composable
fun OCRScreen(
    modifier: Modifier = Modifier,
    viewModel: OCRViewModel = hiltViewModel(),
    onConfirmNavigate: () -> Unit,
    onDiscardNavigate: () -> Unit
) {

    val context = LocalContext.current

    val recognizedText by viewModel.recognizedText.collectAsState()

    recognizedText?.let {
        var text by rememberSaveable { mutableStateOf(it.text) }
        var title by rememberSaveable { mutableStateOf("") }

        OCRScreenContent(
            modifier = modifier,
            title = title,
            text = text,
            onTitleChange = { title = it },
            onTextChange = { text = it },
            onDiscard = {
                viewModel.deleteRecognizedText()
                onDiscardNavigate()
            },
            onConfirm = {
                viewModel.confirmRecognizedText(title, text)

                // Hiện thông báo khi lưu tài liệu thành công
                Toast.makeText(
                    context,
                    context.getString(R.string.document_saved),
                    Toast.LENGTH_SHORT
                ).show()
                onConfirmNavigate()
            }
        )
    }
}


@Composable
fun OCRScreenContent(
    modifier: Modifier = Modifier,
    title: String,
    text: String,
    onTitleChange: (String) -> Unit,
    onTextChange: (String) -> Unit,
    onDiscard: () -> Unit,
    onConfirm: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = onTitleChange,
                    label = { Text(stringResource(id = R.string.title_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = text,
                    onValueChange = onTextChange,
                    label = { Text(stringResource(id = R.string.extracted_text_label)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    textStyle = MaterialTheme.typography.bodyMedium
                )
            }

            ButtonRow(
                onDiscard = onDiscard,
                onConfirm = onConfirm
            )
        }
    }
}

@Composable
private fun ButtonRow(
    onDiscard: () -> Unit,
    onConfirm: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        OutlinedButton(
            onClick = onDiscard,
            modifier = Modifier.weight(1f)
        ) {
            Text(stringResource(id = R.string.discard))
        }

        Button(
            onClick = onConfirm,
            modifier = Modifier.weight(1f)
        ) {
            Text(stringResource(id = R.string.confirm))
        }
    }
}