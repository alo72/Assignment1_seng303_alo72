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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import nz.ac.canterbury.seng303.lab2.ui.theme.LightColorScheme

// via chatGPT to help get around errors regarding screen orientation
val stateListSaver = Saver<SnapshotStateList<ToggleableInfo>, List<Pair<Boolean, String>>>(
    save = { list ->
        list.map { it.isChecked to it.text }
    },
    restore = { list ->
        mutableStateListOf(
            *list.mapIndexed { index, pair -> ToggleableInfo(pair.first, index, pair.second) }.toTypedArray()
        )
    }
)

//https://www.youtube.com/watch?v=NYWTQZODr74 reference for helper class idea
data class ToggleableInfo(
    val isChecked: Boolean,
    val position: Int,
    val text: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateFlashCard(
    navController: NavController,
    question: String,
    onQuestionChange: (String) -> Unit,
    correctAnswer: String,
    onCorrectAnswerChange: (String) -> Unit,
    answers: List<String>,
    onAnswersChange: (List<String>) -> Unit,

    title: String,
    onTitleChange: (String) -> Unit,
    content: String,
    onContentChange: (String) -> Unit,
    createNoteFn: (String, List<String>, String) -> Unit
) {

    val scrollState = rememberScrollState()
    //4 answers max, w/o adding another answer feature
    val providedAnswers = rememberSaveable(saver = stateListSaver) { mutableStateListOf(
        ToggleableInfo(
            isChecked = false,
            position = 0,
            text = ""
        ),
        ToggleableInfo(
            isChecked = false,
            position = 1,
            text = ""
        ),
        ToggleableInfo(
            isChecked = false,
            position = 2,
            text = ""
        ),
        ToggleableInfo(
            isChecked = false,
            position = 3,
            text = ""
        )
    )}
    var answerIndex = rememberSaveable {mutableStateOf(-1) };
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(LightColorScheme.background, RoundedCornerShape(20.dp))
            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
            .padding(12.dp)
            .verticalScroll(scrollState)
    ) {
        Text(text = "Add a new Flash Card",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = question,
            onValueChange = { onQuestionChange(it) },
            label = { Text("Input question here") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        providedAnswers.forEachIndexed { index, option ->
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

        FloatingActionButton(
            onClick = { providedAnswers.add(
                ToggleableInfo(
                    isChecked = false,
                    position = providedAnswers.size,
                    text = ""
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
                if (question.isBlank()) {
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

                if (answerIndex.value == -1) {
                    val builder = AlertDialog.Builder(context)
                    builder.setMessage("Requires a correct answer")
                        .setNegativeButton("Close") { dialog, id ->
                            dialog.dismiss()
                        }
                    val alert = builder.create()
                    alert.show()
                    return@Button
                }

                val updatedAnswers = providedAnswers.map { it.text }
                val updatedCorrectAnswer = providedAnswers[answerIndex.value].text

                createNoteFn(question, updatedAnswers, updatedCorrectAnswer)
                val builder = AlertDialog.Builder(context)
                builder.setMessage("Created Flash Card!")
                    .setCancelable(false)
                    .setPositiveButton("Ok") { dialog, id ->
                        onTitleChange("")
                        onContentChange("")
                        navController.navigate("FlashCardList")
                    }
                    .setNegativeButton("Cancel") { dialog, id -> dialog.dismiss() }
                val alert = builder.create()
                alert.show()

            },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(text = "Save and return")
        }
    }
}
