package com.app.fastlearn.ui.screens.documents

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun OCRScreen(
    navController: NavController,
    ocrText: String,
    onConfirm: (String) -> Unit,
    onDiscard: () -> Unit
) {
    var text by remember { mutableStateOf(ocrText) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Extracted Text") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = {
                onDiscard()
                navController.popBackStack() // Navigate back to PreviewImageScreen
            }) {
                Text("Discard")
            }

            Button(onClick = {
                onConfirm(text)
            }) {
                Text("Confirm")
            }
        }
    }
}