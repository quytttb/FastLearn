package com.app.fastlearn.ui.screens.option

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.fastlearn.R
import com.app.fastlearn.domain.usecase.SummarizeDocumentUseCase
import com.app.fastlearn.ui.components.EmptyScreen
import com.app.fastlearn.ui.components.OptionItem
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionScreen(
    documentId: String,
    modifier: Modifier = Modifier,
    viewModel: OptionViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onViewDocument: (String) -> Unit = {},
    onViewFlashcards: (String) -> Unit = {},
    onStudy: (String) -> Unit = {}
) {
    val document by viewModel.document.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val flashcardsCount by viewModel.flashcardsCount.collectAsState()
    val documentSummary by viewModel.documentSummary.collectAsState()
    val isSummarizing by viewModel.isSummarizing.collectAsState()
    val isContentSuitable by viewModel.isContentSuitable.collectAsState()
    val scrollState = rememberScrollState()

    // Animation states
    var isPageLoaded by remember { mutableStateOf(false) }
    var showOptions by remember { mutableStateOf(false) }

    // Control sequential animations
    LaunchedEffect(isLoading) {
        if (!isLoading) {
            isPageLoaded = true
            delay(300) // Short delay before showing options
            showOptions = true
        }
    }

    // Scrollable top app bar behavior
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = document?.title?.split(
                            SummarizeDocumentUseCase.SUMMARY_SEPARATOR
                        )?.firstOrNull() ?: stringResource(R.string.document_options),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.navigate_back)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(bottom = 24.dp),
                        strokeCap = StrokeCap.Round
                    )
                    Text(
                        text = stringResource(R.string.loading_document),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Animated summary section
                    AnimatedVisibility(
                        visible = isPageLoaded,
                        enter = fadeIn(animationSpec = tween(500)) +
                                slideInVertically(
                                    initialOffsetY = { -40 },
                                    animationSpec = tween(500)
                                ),
                        exit = fadeOut()
                    ) {
                        SummarySection(
                            isSummarizing = isSummarizing,
                            documentSummary = documentSummary,
                            isContentSuitable = isContentSuitable,
                            onGenerateSummary = { viewModel.generateSummary() }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Animated options section
                    AnimatedVisibility(
                        visible = showOptions,
                        enter = fadeIn(animationSpec = tween(500)) +
                                slideInVertically(
                                    initialOffsetY = { 40 },
                                    animationSpec = tween(500)
                                ),
                        exit = fadeOut()
                    ) {
                        OptionsSection(
                            flashcardsCount = flashcardsCount,
                            isContentSuitable = isContentSuitable,
                            onViewDocument = { onViewDocument(documentId) },
                            onViewFlashcards = { onViewFlashcards(documentId) },
                            onStudy = { onStudy(documentId) },
                            onGenerateFlashcards = { viewModel.generateFlashcards() }
                        )
                    }

                    // Extra bottom space for better scrolling experience
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun SummarySection(
    isSummarizing: Boolean,
    documentSummary: String?,
    isContentSuitable: Boolean,
    onGenerateSummary: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.summary),
                style = MaterialTheme.typography.titleLarge
            )

            Divider()

            when {
                isSummarizing -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .clip(MaterialTheme.shapes.small),
                            strokeCap = StrokeCap.Round
                        )
                        Text(
                            text = stringResource(R.string.generating_summary),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                documentSummary != null -> {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = documentSummary,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                !isContentSuitable -> {
                    EmptyScreen(
                        title = stringResource(R.string.cannot_summarize),
                        message = stringResource(R.string.content_too_short),
                        vectorImage = Icons.Default.Summarize,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
                else -> {
                    EmptyScreen(
                        title = stringResource(R.string.no_summary),
                        message = stringResource(R.string.generate_summary_prompt),
                        vectorImage = Icons.Default.Summarize,
                        actionLabel = stringResource(R.string.generate_summary),
                        onAction = onGenerateSummary,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun OptionsSection(
    flashcardsCount: Int,
    isContentSuitable: Boolean,
    onViewDocument: () -> Unit,
    onViewFlashcards: () -> Unit,
    onStudy: () -> Unit,
    onGenerateFlashcards: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.select_action),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        OptionItem(
            icon = Icons.Default.Description,
            title = stringResource(R.string.view_document),
            subtitle = stringResource(R.string.view_document_description),
            onClick = onViewDocument
        )

        if (flashcardsCount > 0) {
            OptionItem(
                icon = Icons.Filled.CreditCard,
                title = stringResource(R.string.view_flashcards),
                subtitle = stringResource(R.string.flashcard_count, flashcardsCount),
                onClick = onViewFlashcards,
                badgeCount = flashcardsCount
            )

            OptionItem(
                icon = Icons.Filled.School,
                title = stringResource(R.string.study_flashcards),
                subtitle = stringResource(R.string.study_document_description),
                onClick = onStudy
            )
        } else if (!isContentSuitable) {
            OptionItem(
                icon = Icons.Filled.CreditCard,
                title = stringResource(R.string.view_flashcards),
                subtitle = stringResource(R.string.content_not_suitable_flashcards),
                onClick = {},
                enabled = false
            )
        } else {
            OptionItem(
                icon = Icons.Filled.CreditCard,
                title = stringResource(R.string.create_flashcards),
                subtitle = stringResource(R.string.generate_flashcards_prompt),
                onClick = onGenerateFlashcards,
                highlighted = true
            )
        }
    }
}