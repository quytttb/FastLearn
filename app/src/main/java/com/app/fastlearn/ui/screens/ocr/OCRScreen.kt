package com.app.fastlearn.ui.screens.ocr

import android.widget.Toast
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.fastlearn.R
import com.app.fastlearn.domain.model.Category

@Composable
fun OCRScreen(
    modifier: Modifier = Modifier,
    imageUri: String?,
    viewModel: OCRViewModel = hiltViewModel(),
    onConfirmNavigate: () -> Unit,
    onDiscardNavigate: () -> Unit
) {
    val context = LocalContext.current
    val processingState by viewModel.processingState.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    // Thiết lập URI ảnh khi màn hình được tải
    LaunchedEffect(imageUri) {
        imageUri?.let {
            viewModel.setImageUri(it.toUri())
            // Tự động xử lý OCR khi có ảnh
            viewModel.processImage(context)
        }
    }

    // Kiểm tra trạng thái và hiển thị nội dung phù hợp
    when (processingState) {
        is OCRProcessingState.Processing -> {
            LoadingScreen()
        }

        is OCRProcessingState.Success -> {
            val text = (processingState as OCRProcessingState.Success).text
            var editableText by rememberSaveable { mutableStateOf(text) }
            var title by rememberSaveable { mutableStateOf("") }
            var showRequiredFieldsError by remember { mutableStateOf(false) }

            OCRScreenContent(
                modifier = modifier,
                title = title,
                text = editableText,
                categories = categories,
                selectedCategory = selectedCategory,
                showRequiredFieldsError = showRequiredFieldsError,
                onTitleChange = { title = it },
                onTextChange = { editableText = it },
                onCategorySelected = { viewModel.selectCategory(it) },
                onDiscard = {
                    viewModel.discardImage()
                    onDiscardNavigate()
                },
                onConfirm = {
                    val isValid = viewModel.confirmRecognizedText(title, editableText)
                    if (isValid) {
                        // Hiện thông báo khi lưu tài liệu thành công
                        Toast.makeText(
                            context,
                            context.getString(R.string.document_saved),
                            Toast.LENGTH_SHORT
                        ).show()
                        onConfirmNavigate()
                    } else {
                        showRequiredFieldsError = true
                    }
                }
            )
        }

        is OCRProcessingState.Error -> {
            val errorMessage = (processingState as OCRProcessingState.Error).errorMessage
            ErrorScreen(
                errorMessage = errorMessage,
                onDiscard = onDiscardNavigate
            )
        }

        is OCRProcessingState.EmptyText -> {
            EmptyTextScreen(onDiscard = onDiscardNavigate)
        }

        else -> {
            // Idle state - không làm gì hoặc hiển thị màn hình chờ
            Box(modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = stringResource(id = R.string.processing_image),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun ErrorScreen(errorMessage: String, onDiscard: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.height(48.dp)
            )

            Text(
                text = stringResource(id = R.string.error_processing_image),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )

            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = onDiscard) {
                Text(stringResource(id = R.string.try_again))
            }
        }
    }
}

@Composable
fun EmptyTextScreen(onDiscard: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.TextFields,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.height(48.dp)
            )

            Text(
                text = stringResource(id = R.string.no_text_detected),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )

            Text(
                text = stringResource(id = R.string.try_another_image),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = onDiscard) {
                Text(stringResource(id = R.string.try_again))
            }
        }
    }
}

@Composable
fun OCRScreenContent(
    modifier: Modifier = Modifier,
    title: String,
    text: String,
    categories: List<Category>,
    selectedCategory: Category?,
    showRequiredFieldsError: Boolean = false,
    onTitleChange: (String) -> Unit,
    onTextChange: (String) -> Unit,
    onCategorySelected: (Category) -> Unit,
    onDiscard: () -> Unit,
    onConfirm: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Trường nhập tiêu đề
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text(stringResource(id = R.string.title_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                maxLines = 1,
                isError = showRequiredFieldsError && title.isBlank()
            )

            if (showRequiredFieldsError && title.isBlank()) {
                Text(
                    text = stringResource(id = R.string.required_field),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Trường chọn danh mục
            CategoryDropdown(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelected = onCategorySelected,
                isError = showRequiredFieldsError && selectedCategory == null
            )

            if (showRequiredFieldsError && selectedCategory == null) {
                Text(
                    text = stringResource(id = R.string.category_required),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Trường văn bản đã trích xuất
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                label = { Text(stringResource(id = R.string.extracted_text_label)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                textStyle = MaterialTheme.typography.bodyMedium
            )

            // Nút xác nhận hoặc hủy
            ButtonRow(
                onDiscard = onDiscard,
                onConfirm = onConfirm
            )
        }
    }
}

@Composable
fun CategoryDropdown(
    categories: List<Category>,
    selectedCategory: Category?,
    onCategorySelected: (Category) -> Unit,
    isError: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = stringResource(id = R.string.category_label),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp),
            color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .clickable { expanded = true },
            tonalElevation = 2.dp,
            color = if (isError)
                MaterialTheme.colorScheme.errorContainer
            else
                MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedCategory?.name ?: stringResource(id = R.string.select_category),
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isError && selectedCategory == null)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.name) },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    }
                )
            }
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
            .padding(vertical = 16.dp)
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