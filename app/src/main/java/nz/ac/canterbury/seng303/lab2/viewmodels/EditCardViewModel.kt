package nz.ac.canterbury.seng303.lab2.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import nz.ac.canterbury.seng303.lab2.models.FlashCard

class EditCardViewModel: ViewModel() {
    var question by mutableStateOf("")
        private set

    fun updateQuestion(newQuestion: String) {
        question = newQuestion;
    }

    var correctAnswer by mutableStateOf("")
        private set

    fun updateCorrectAnswer(newCorrectAnswer: String) {
        correctAnswer = newCorrectAnswer;
    }

    var answers by mutableStateOf<List<String>>(emptyList())
        private set

    fun updateAnswers(newAnswer: List<String>) {
        answers = newAnswer;
    }

    // Function to set the default values based on the selected note
    fun setDefaultValues(selectedFlashCard: FlashCard?) {
        selectedFlashCard?.let {
            question = it.question
            answers = it.answers
            correctAnswer = it.correctAnswer
        }
    }
}