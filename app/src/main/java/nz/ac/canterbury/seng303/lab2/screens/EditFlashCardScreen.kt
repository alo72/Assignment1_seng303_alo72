package nz.ac.canterbury.seng303.lab2.screens

import android.app.AlertDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import nz.ac.canterbury.seng303.lab2.models.FlashCard
import nz.ac.canterbury.seng303.lab2.ui.theme.LightColorScheme
import nz.ac.canterbury.seng303.lab2.viewmodels.EditCardViewModel
import nz.ac.canterbury.seng303.lab2.viewmodels.FlashCardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditFlashCard(
    cardId: String,
    editCardViewModel: EditCardViewModel,
    flashCardViewModel: FlashCardViewModel,
    navController: NavController
) {

    val context = LocalContext.current
    val selectedNoteState by flashCardViewModel.selectedFlashCard.collectAsState(null)
    val flashCard: FlashCard? = selectedNoteState // we explicitly assign to note to help the compilers smart cast out
    val scrollState = rememberScrollState()

    val providedAnswers = rememberSaveable(saver = stateListSaver) {
        mutableStateListOf<ToggleableInfo>()
    }

    var answerIndex = rememberSaveable { mutableIntStateOf(0) }
    if (providedAnswers.isEmpty()) {
        LaunchedEffect(flashCard) {
            if (flashCard == null) {
                flashCardViewModel.getNoteById(cardId.toIntOrNull())
            } else {
                val correctAnswerIndex = flashCard.answers.indexOf(flashCard.correctAnswer)
                answerIndex.value = if (correctAnswerIndex != -1) correctAnswerIndex else 0
                editCardViewModel.setDefaultValues(flashCard)
                for (i in flashCard.answers.indices) {
                    providedAnswers.add(
                        ToggleableInfo(
                            isChecked = flashCard.correctAnswer == flashCard.answers[i],
                            position = i,
                            text = flashCard.answers[i]
                        )
                    )
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
            .background(LightColorScheme.background, RoundedCornerShape(20.dp))
            .padding(12.dp)
            .verticalScroll(scrollState)
    ) {
        Text(text = "Edit Flash Card",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        if (flashCard != null) {
            OutlinedTextField(
                value = editCardViewModel.question,
                onValueChange = { editCardViewModel.updateQuestion(it)},
                label = { Text("Input question here") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }

        providedAnswers.forEachIndexed { index, option ->
            if (option.text.isNotBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(
                        checked = option.isChecked,
                        onCheckedChange = { isChecked ->
                            providedAnswers.replaceAll { item ->
                                item.copy(isChecked = item == option && isChecked)
                            }
                            answerIndex.value = index;
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color.Green,
                            uncheckedColor = Color.White,
                            checkmarkColor = LightColorScheme.secondary
                        ),
                    )
                    OutlinedTextField(
                        value = option.text,
                        onValueChange = { providedAnswers[index] = option.copy(text = it)},
                        label = { Text("Add an option") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp)
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { providedAnswers.add(
                ToggleableInfo(
                    isChecked = false,
                    position = providedAnswers.size,
                    text = "Add a new Option!"
                )
            ) },
            modifier = Modifier.align(Alignment.CenterHorizontally)
                .padding(8.dp)
                .padding(top = 12.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add option")
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                if (editCardViewModel.question.isBlank()) {
                    val builder = AlertDialog.Builder(context)
                    builder.setMessage("A flash card must have a question")
                        .setNegativeButton("Close") { dialog, id ->
                            dialog.dismiss()
                        }
                    val alert = builder.create()
                    alert.show()
                    return@Button
                }

                if (providedAnswers.count { it.text.isNotBlank()} < 2) {
                    val builder = AlertDialog.Builder(context)
                    builder.setMessage("A flash card must have at least 2 answers")
                        .setNegativeButton("Close") { dialog, id ->
                            dialog.dismiss()
                        }
                    val alert = builder.create()
                    alert.show()
                    return@Button
                }

                val updatedAnswers = providedAnswers.map { it.text }
                val updatedCorrectAnswer = providedAnswers[answerIndex.value!!].text
                editCardViewModel.updateAnswers(updatedAnswers)
                editCardViewModel.updateCorrectAnswer(updatedCorrectAnswer)

                flashCardViewModel.editNoteById(cardId.toIntOrNull(), flashCard = FlashCard(cardId.toInt(), editCardViewModel.question, editCardViewModel.answers, editCardViewModel.correctAnswer))
                val builder = AlertDialog.Builder(context)
                builder.setMessage("Edited Flash Card!")
                    .setCancelable(false)
                    .setPositiveButton("Ok") { dialog, id ->
                        navController.navigate("FlashCardList")
                    }
                val alert = builder.create()
                alert.show()

            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = "Save")
        }
    }
}
