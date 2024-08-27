package nz.ac.canterbury.seng303.lab2.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import nz.ac.canterbury.seng303.lab2.viewmodels.FlashCardViewModel
import nz.ac.canterbury.seng303.lab2.viewmodels.PlayCardViewModel

@SuppressLint("UnusedContentLambdaTargetStateParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun FlashCard(
    navController: NavController,
    flashCardViewModel: FlashCardViewModel,
    playCardViewModel: PlayCardViewModel
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    flashCardViewModel.getNotes()
    val flashCards: List<FlashCard> by flashCardViewModel.notes.collectAsState(emptyList())

    if (flashCards.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LightColorScheme.background),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("No notes exist", style = MaterialTheme.typography.headlineMedium)
        }
    } else {
        playCardViewModel.updateCardCount(flashCards.size)
        var currentFlashcard = flashCards[playCardViewModel.currentIndex]
        val providedAnswers = rememberSaveable(saver = stateListSaver) {
            mutableStateListOf<ToggleableInfo>()
        }
        if (providedAnswers.isEmpty()) {
            for (i in currentFlashcard.answers.indices) {
                providedAnswers.add(
                    ToggleableInfo(
                        isChecked = false,
                        position = i,
                        text = currentFlashcard.answers[i]
                    )
                )
            }
        }

        var answerIndex = rememberSaveable { mutableIntStateOf(-1) };

        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
                .background(LightColorScheme.background, RoundedCornerShape(20.dp))
                .padding(12.dp)
                .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally)

        {
            Text(
                text = "Play flash cards",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 24.dp)
            )

            AnimatedContent(
                targetState = 0,
                transitionSpec = {
                        slideInVertically { height -> -height } + fadeIn() with
                                slideOutVertically { height -> height } + fadeOut()
                    }, label = ""
            ) {
                Text(
                    text = currentFlashcard.question,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth(0.8f)
                        .wrapContentHeight(Alignment.CenterVertically)
                        .background(LightColorScheme.tertiary, RoundedCornerShape(4.dp))
                        .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                        .padding(26.dp)
                )
            }

            providedAnswers.forEachIndexed { index, option ->
                if (option.text.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                    ) {
                        RadioButton(
                            selected = option.isChecked,
                            onClick = {
                                providedAnswers.replaceAll {
                                    it.copy(
                                        isChecked = it.text == option.text)
                                }
                                answerIndex.value = index;
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color.Green,
                                unselectedColor = Color.White
                            ),
                        )
                        Text(
                            text = option.text
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    text = "${playCardViewModel.currentIndex + 1}/${playCardViewModel.cardCount}",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )

                Button(onClick = {
                    if (answerIndex.value == -1) {
                        val text = "Answer is required!!"
                        val duration = Toast.LENGTH_SHORT

                        val toast = Toast.makeText(context, text, duration)
                        toast.show()
                        return@Button
                    }
                    if (currentFlashcard.correctAnswer == currentFlashcard.answers[answerIndex.value!!]) {
                        val text = "Correct!"
                        val duration = Toast.LENGTH_SHORT

                        val toast = Toast.makeText(context, text, duration)
                        toast.show()
                        playCardViewModel.updateScore(playCardViewModel.score + 1)
                        playCardViewModel.updateResultsList(playCardViewModel.resultsList + true)

                    } else {
                        val text = "Wrong!"
                        val duration = Toast.LENGTH_SHORT

                        val toast = Toast.makeText(context, text, duration)
                        toast.show()
                        playCardViewModel.updateResultsList(playCardViewModel.resultsList + false)
                    }

                    playCardViewModel.updateQuestionsList(currentFlashcard.question)
                    if (playCardViewModel.currentIndex < flashCards.size - 1) {
                        playCardViewModel.updateCurrentIndex(playCardViewModel.currentIndex + 1)
                        providedAnswers.clear()
                        currentFlashcard = flashCards[playCardViewModel.currentIndex]
                        answerIndex.value = -1
                    } else {
                        navController.navigate("Summary")
                    }
                }) {
                    Text("Submit")
                }
            }
            Button(
                onClick = { navController.navigate("Home") },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp)
            ) {
                Text("Back to Home")
            }

        }
    }
}