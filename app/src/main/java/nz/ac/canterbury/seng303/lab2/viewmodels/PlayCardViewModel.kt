package nz.ac.canterbury.seng303.lab2.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class PlayCardViewModel: ViewModel() {
    var currentIndex by mutableIntStateOf(0)
        private set

    fun updateCurrentIndex(newIndex: Int) {
        currentIndex = newIndex
    }

    var score by mutableIntStateOf(0)
        private set

    fun updateScore(newScore: Int) {
        score = newScore
    }

    var resultsList by mutableStateOf<List<Boolean>>(emptyList())
        private set

    fun updateResultsList(newResultsList: List<Boolean>) {
        resultsList = newResultsList
    }

    var cardCount by mutableIntStateOf(0)
        private set

    fun updateCardCount(newCardCount: Int) {
        cardCount = newCardCount
    }

    var questionsList by mutableStateOf<MutableList<String>>(mutableListOf())
        private set

    fun updateQuestionsList(newQuestion: String) {
        questionsList.add(newQuestion)
    }

    fun resetModel() {
        currentIndex = 0
        score = 0
        resultsList = emptyList()
        cardCount = 0
        questionsList = mutableListOf()
    }

}