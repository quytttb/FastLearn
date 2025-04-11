package com.app.fastlearn.ui.screens.study

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.fastlearn.R
import com.app.fastlearn.ui.components.EmptyScreen
import com.app.fastlearn.util.ProgressStatus
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyScreen(
    modifier: Modifier = Modifier,
    documentId: String? = null,
    viewModel: StudyViewModel = hiltViewModel(),
    onSendProgress: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val flashcards by viewModel.flashcards.collectAsState(initial = emptyList())
    val currentCardIndex by viewModel.currentCardIndex.collectAsState()
    val answerOptions by viewModel.currentAnswerOptions.collectAsState(initial = emptyList())
    val selectedAnswerIndex by viewModel.selectedAnswerIndex.collectAsState()
    val isAnswerRevealed by viewModel.isAnswerRevealed.collectAsState()
    val studyProgress by viewModel.studyProgress.collectAsState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Chuẩn bị các tùy chọn trả lời khi flashcards được tải
    LaunchedEffect(flashcards) {
        Log.d("StudyScreen", "LaunchedEffect triggered with ${flashcards.size} flashcards")
        if (flashcards.isNotEmpty()) {
            viewModel.prepareAnswerOptions()
        } else {
            Log.w("StudyScreen", "No flashcards available yet")
        }
    }

    // Khi người dùng chọn một câu trả lời, cập nhật trạng thái
    LaunchedEffect(currentCardIndex) {
        listState.scrollToItem(0)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.study_flashcards)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.navigate_back)
                        )
                    }
                }
            )
        },
        bottomBar = {
            if (flashcards.isNotEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    tonalElevation = 3.dp,
                    shadowElevation = 3.dp
                ) {
                    // Navigation buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.goToPreviousCard()
                                scope.launch {
                                    listState.scrollToItem(0)
                                }
                            },
                            enabled = !isAnswerRevealed && currentCardIndex > 0,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.previous_card)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.previous))
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                if (isAnswerRevealed) {
                                    if (currentCardIndex == flashcards.size - 1) {
                                        // Todo: Gửi tiến trình học tập khi hoàn thành
                                        onSendProgress(viewModel.currentDocumentId)
                                    } else {
                                        viewModel.goToNextCard()
                                        scope.launch {
                                            listState.scrollToItem(0)
                                        }
                                    }
                                } else {
                                    if (selectedAnswerIndex >= 0) {
                                        viewModel.checkAnswer()
                                    }
                                }
                            },
                            enabled = !isAnswerRevealed || currentCardIndex < flashcards.size - 1 || selectedAnswerIndex >= 0,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                if (!isAnswerRevealed) {
                                    stringResource(R.string.check_answer)
                                } else if (currentCardIndex < flashcards.size - 1) {
                                    stringResource(R.string.next_card)
                                } else {
                                    stringResource(R.string.finish)
                                }
                            )
                            if (isAnswerRevealed && currentCardIndex < flashcards.size - 1) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = stringResource(R.string.next_card)
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (flashcards.isEmpty()) {
            Log.d("StudyScreen", "Rendering loading state - no flashcards available")
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                EmptyScreen()
            }
        } else {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 88.dp)  // Add padding to bottom for the fixed buttons
            ) {
                // Progress indicator
                item {
                    Column {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth(),
                            progress = { (currentCardIndex + 1).toFloat() / flashcards.size }
                        )

                        Text(
                            text = "${currentCardIndex + 1}/${flashcards.size}",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.End,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                        )
                    }
                }

                // Flashcard question
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = flashcards[currentCardIndex].question,
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = stringResource(R.string.select_answer),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // Answer options
                items(answerOptions.size) { index ->
                    val answer = answerOptions[index]
                    val isSelected = selectedAnswerIndex == index
                    val isCorrectAnswer = isAnswerRevealed &&
                            answer == flashcards[currentCardIndex].answer
                    val isWrongSelected = isAnswerRevealed &&
                            isSelected &&
                            !isCorrectAnswer

                    // Improved background colors with better contrast
                    val backgroundColor = when {
                        isAnswerRevealed && isCorrectAnswer -> Color(0xFF81C784) // Medium green
                        isWrongSelected -> Color(0xFFEF5350) // Medium red
                        isSelected -> Color(0xFF64B5F6) // Medium blue
                        else -> MaterialTheme.colorScheme.surface
                    }

                    val borderColor = when {
                        isAnswerRevealed && isCorrectAnswer -> Color(0xFF2E7D32) // Dark green
                        isWrongSelected -> Color(0xFFC62828) // Dark red
                        isSelected -> Color(0xFF1976D2) // Dark blue
                        else -> Color.LightGray
                    }

                    // Improved text color for better contrast
                    val textColor = when {
                        isAnswerRevealed && isCorrectAnswer -> Color.White
                        isWrongSelected -> Color.White
                        isSelected -> Color.White
                        else -> MaterialTheme.colorScheme.onSurface
                    }

                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, borderColor),
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = backgroundColor
                        ),
                        onClick = {
                            if (!isAnswerRevealed) {
                                viewModel.selectAnswer(index)
                            }
                        }
                    ) {
                        Text(
                            text = answer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Start,
                            color = textColor
                        )
                    }
                }

                // Show the study summary if all cards are completed
                item {
                    if (currentCardIndex == flashcards.size - 1 && isAnswerRevealed) {
                        Spacer(modifier = Modifier.height(24.dp))

                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = stringResource(R.string.study_completed),
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = stringResource(
                                            R.string.correct_answers_count,
                                            studyProgress.count { it.status == ProgressStatus.CORRECT.value },
                                            flashcards.size
                                        ),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}