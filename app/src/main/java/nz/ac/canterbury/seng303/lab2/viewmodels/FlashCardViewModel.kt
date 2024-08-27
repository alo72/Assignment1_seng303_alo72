package nz.ac.canterbury.seng303.lab2.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import nz.ac.canterbury.seng303.lab2.datastore.Storage
import nz.ac.canterbury.seng303.lab2.models.FlashCard
import kotlin.random.Random

class FlashCardViewModel(
    private val flashCardStorage: Storage<FlashCard>
) : ViewModel() {

    private val _notes = MutableStateFlow<List<FlashCard>>(emptyList())
    val notes: StateFlow<List<FlashCard>> get() = _notes

    private val _selectedFlashCard = MutableStateFlow<FlashCard?>(null)
    val selectedFlashCard: StateFlow<FlashCard?> = _selectedFlashCard

    fun getNotes() = viewModelScope.launch {
        flashCardStorage.getAll().catch { Log.e("NOTE_VIEW_MODEL", it.toString()) }
            .collect { _notes.emit(it) }
    }

    fun createNote(question: String, answers: List<String>, correctAnswer: String) = viewModelScope.launch {
        val flashCard = FlashCard(
            id = Random.nextInt(0, Int.MAX_VALUE),
            question = question,
            answers = answers,
            correctAnswer = correctAnswer
        )
        flashCardStorage.insert(flashCard).catch { Log.e("NOTE_VIEW_MODEL", "Could not insert note") }
            .collect()
        flashCardStorage.getAll().catch { Log.e("NOTE_VIEW_MODEL", it.toString()) }
            .collect { _notes.emit(it) }
    }

    fun getNoteById(noteId: Int?) = viewModelScope.launch {
        if (noteId != null) {
            _selectedFlashCard.value = flashCardStorage.get { it.getIdentifier() == noteId }.first()
        } else {
            _selectedFlashCard.value = null
        }
    }

    fun deleteNoteById(noteId: Int?) = viewModelScope.launch {
        Log.d("NOTE_VIEW_MODEL", "Deleting note: $noteId")
        if (noteId != null) {
            flashCardStorage.delete(noteId).collect()
            flashCardStorage.getAll().catch { Log.e("NOTE_VIEW_MODEL", it.toString()) }
                .collect { _notes.emit(it) }
        }
    }

    fun editNoteById(noteId: Int?, flashCard: FlashCard) = viewModelScope.launch {
        Log.d("NOTE_VIEW_MODEL", "Editing note: $noteId")
        if (noteId != null) {
            flashCardStorage.edit(noteId, flashCard).collect()
            flashCardStorage.getAll().catch { Log.e("NOTE_VIEW_MODEL", it.toString()) }
                .collect { _notes.emit(it) }
        }
    }
}