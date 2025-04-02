package com.app.fastlearn.ui.screens.documents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.app.fastlearn.R

@Composable
fun OCRScreen(
    viewModel: OCRViewModel = hiltViewModel(),
    onConfirm: (String) -> Unit,
    onDiscard: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.loadRecognizedText()
    }

    val recognizedText by viewModel.recognizedText.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        recognizedText?.let {
            var text by remember { mutableStateOf(it.text) }

            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text(stringResource(id = R.string.extracted_text_label)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = {
                    onDiscard()
                }) {
                    Text(stringResource(id = R.string.discard))
                }

                Button(onClick = {
                    onConfirm(text)
                }) {
                    Text(stringResource(id = R.string.confirm))
                }
            }
        }
    }
}