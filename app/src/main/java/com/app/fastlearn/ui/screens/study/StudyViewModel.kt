package com.app.fastlearn.ui.screens.study

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.fastlearn.data.repository.FlashcardRepository
import com.app.fastlearn.data.repository.ProgressRepository
import com.app.fastlearn.domain.model.Flashcard
import com.app.fastlearn.domain.model.Progress
import com.app.fastlearn.util.ProgressStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class StudyViewModel @Inject constructor(
    private val flashcardRepository: FlashcardRepository,
    private val progressRepository: ProgressRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _flashcards = MutableStateFlow<List<Flashcard>>(emptyList())
    val flashcards = _flashcards.asStateFlow()

    private val _currentCardIndex = MutableStateFlow(0)
    val currentCardIndex = _currentCardIndex.asStateFlow()

    private val _currentAnswerOptions = MutableStateFlow<List<String>>(emptyList())
    val currentAnswerOptions = _currentAnswerOptions.asStateFlow()

    private val _selectedAnswerIndex = MutableStateFlow(-1)
    val selectedAnswerIndex = _selectedAnswerIndex.asStateFlow()

    private val _isAnswerRevealed = MutableStateFlow(false)
    val isAnswerRevealed = _isAnswerRevealed.asStateFlow()

    private val _studyProgress = MutableStateFlow<List<Progress>>(emptyList())
    val studyProgress = _studyProgress.asStateFlow()

    private val _startTime = MutableStateFlow<LocalDateTime>(LocalDateTime.now())

    val currentDocumentId: String = savedStateHandle.get<String>("documentId")
        ?: savedStateHandle.get<String>("documentId")
        ?: "20250410115413" //Todo: Giá trị đang fix cứng, cần sửa

    init {
        loadFlashcards(currentDocumentId)
    }

    // Load flashcards from the repository based on the document ID
    private fun loadFlashcards(documentId: String) {
        viewModelScope.launch {
            try {
                flashcardRepository.getFlashcardsByDocumentId(documentId).collect { cards ->
                    _flashcards.value = cards.shuffled() // Shuffle the cards for better learning
                    if (cards.isNotEmpty()) {
                        _currentCardIndex.value = 0
                        prepareAnswerOptions()
                        _startTime.value = LocalDateTime.now()
                    } else {
                        Log.w("StudyViewModel", "No flashcards found for document ID: $documentId")
                    }
                }
            } catch (e: Exception) {
                Log.e("StudyViewModel", "Error loading flashcards: ${e.message}", e)
            }
        }
    }

    // Prepare answer options for the current flashcard
    fun prepareAnswerOptions() {
        val currentFlashcard = _flashcards.value.getOrNull(_currentCardIndex.value)

        if (currentFlashcard == null) {
            return
        }

        val correctAnswer = currentFlashcard.answer

        viewModelScope.launch {
            // Reset state for new card
            _selectedAnswerIndex.value = -1
            _isAnswerRevealed.value = false

            // Get all flashcards with the same DocID
            val allAnswersFromSameDoc = _flashcards.value
                .filter { it.flashId != currentFlashcard.flashId } // Exclude current flashcard
                .map { it.answer }
                .distinct()

            // Choose 3 random answers (or fewer if not enough available)
            val randomAnswers = allAnswersFromSameDoc
                .shuffled()
                .take(minOf(3, allAnswersFromSameDoc.size))
                .toMutableList()

            // Always include the correct answer
            if (!randomAnswers.contains(correctAnswer)) {
                randomAnswers.add(correctAnswer)
            }

            // If we don't have enough answers, generate some dummy answers
            while (randomAnswers.size < 4 && randomAnswers.size < allAnswersFromSameDoc.size + 1) {
                // This is just a placeholder - in a real app you might have better fallbacks
                val dummyAnswer = "Câu trả lời ${randomAnswers.size + 1}"
                if (!randomAnswers.contains(dummyAnswer)) {
                    randomAnswers.add(dummyAnswer)
                }
            }

            // Shuffle again so the correct answer isn't always in the same position
            val shuffledOptions = randomAnswers.shuffled()
            _currentAnswerOptions.value = shuffledOptions
        }
    }

    fun selectAnswer(index: Int) {
        _selectedAnswerIndex.value = index
    }

    fun checkAnswer() {
        val currentFlashcard = _flashcards.value.getOrNull(_currentCardIndex.value) ?: return
        val selectedAnswer = _currentAnswerOptions.value.getOrNull(_selectedAnswerIndex.value)
        val isCorrect = selectedAnswer == currentFlashcard.answer

        // Create study progress entry
        val timeTaken =
            java.time.Duration.between(_startTime.value, LocalDateTime.now()).toMinutes().toInt()

        val progress = Progress(
            progressId = "Prog_${
                LocalDateTime.now().toString().replace(":", "_").replace(".", "_")
            }_${UUID.randomUUID().toString().take(8)}",
            flashId = currentFlashcard.flashId,
            studyDate = LocalDateTime.now(),
            status = if (isCorrect) ProgressStatus.CORRECT.value else ProgressStatus.INCORRECT.value,
            timeSpent = timeTaken
        )

        viewModelScope.launch {
            progressRepository.insertProgress(progress)

            // Update the list of study progress
            val currentProgress = _studyProgress.value.toMutableList()
            currentProgress.add(progress)
            _studyProgress.value = currentProgress

            // Set answer as revealed
            _isAnswerRevealed.value = true
        }
    }

    fun goToNextCard() {
        if (_currentCardIndex.value < _flashcards.value.size - 1) {
            _currentCardIndex.value += 1
            _startTime.value = LocalDateTime.now()
            prepareAnswerOptions()
        }
    }

    fun goToPreviousCard() {
        if (_currentCardIndex.value > 0) {
            _currentCardIndex.value -= 1
            _startTime.value = LocalDateTime.now()
            prepareAnswerOptions()
        }
    }

    fun getStudyStats() {
        viewModelScope.launch {
            progressRepository.getStudyStats(currentDocumentId)
                .collect { stats ->
                    // Stats are available here for use in the UI
                    // You can expose them via StateFlow if needed
                }
        }
    }
}