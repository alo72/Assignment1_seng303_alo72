package nz.ac.canterbury.seng303.lab2.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class CreateCardViewModel: ViewModel() {
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

    //old stuff
    var title by mutableStateOf("")
        private set

    fun updateTitle(newTitle: String) {
        title = newTitle
    }

    var content by mutableStateOf("")
        private set

    fun updateContent(newContent: String) {
        content = newContent
    }
}