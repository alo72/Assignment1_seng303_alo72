package nz.ac.canterbury.seng303.lab2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import nz.ac.canterbury.seng303.lab2.screens.CreateFlashCard
import nz.ac.canterbury.seng303.lab2.screens.EditFlashCard
import nz.ac.canterbury.seng303.lab2.screens.FlashCard
import nz.ac.canterbury.seng303.lab2.screens.FlashCardList
import nz.ac.canterbury.seng303.lab2.screens.Summary
import nz.ac.canterbury.seng303.lab2.ui.theme.Lab1Theme
import nz.ac.canterbury.seng303.lab2.viewmodels.CreateCardViewModel
import nz.ac.canterbury.seng303.lab2.viewmodels.PlayCardViewModel
import nz.ac.canterbury.seng303.lab2.viewmodels.EditCardViewModel
import nz.ac.canterbury.seng303.lab2.viewmodels.FlashCardViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel as koinViewModel
import nz.ac.canterbury.seng303.lab2.ui.theme.LightColorScheme

class MainActivity : ComponentActivity() {

    private val flashCardViewModel: FlashCardViewModel by koinViewModel()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Lab1Theme {
                val navController = rememberNavController()
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(
                                text = "Flash Rem",
                                textAlign = TextAlign.Center
                            ) },
                            navigationIcon = {
                                IconButton(onClick = { navController.popBackStack() }) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.smallTopAppBarColors(
                                containerColor = LightColorScheme.tertiary
                            )
                        )
                    }
                ) {

                    Box(modifier = Modifier.padding(it).background(LightColorScheme.secondary)) {
                        val createCardViewModel: CreateCardViewModel = viewModel()
                        val editCardViewModel: EditCardViewModel = viewModel()
                        val playCardViewModel: PlayCardViewModel = viewModel()
                        NavHost(navController = navController, startDestination = "Home") {
                            composable("Home") {
                                Home(navController = navController)
                            }
                            composable("EditFlashCard/{cardId}", arguments = listOf(navArgument("cardId") {
                                type = NavType.StringType
                            })
                            ) { backStackEntry ->
                                val cardId = backStackEntry.arguments?.getString("cardId")
                                cardId?.let { noteIdParam: String -> EditFlashCard(noteIdParam, editCardViewModel, flashCardViewModel, navController = navController) }
                            }
                            composable("FlashCardList") {
                                FlashCardList(navController, flashCardViewModel)
                            }
                            composable("PlayCards") {
                                FlashCard(navController = navController, flashCardViewModel = flashCardViewModel, playCardViewModel = playCardViewModel)
                            }
                            composable("Summary") {
                                Summary(
                                    navController = navController,
                                    playCardViewModel = playCardViewModel
                                )
                            }
                            composable("CreateFlashCard") {
                                CreateFlashCard(navController = navController,
                                    question = createCardViewModel.question,
                                    onQuestionChange = {newQuestion -> createCardViewModel.updateQuestion(newQuestion)},
                                    correctAnswer = createCardViewModel.correctAnswer,
                                    onCorrectAnswerChange = {newCorrectAnswer -> createCardViewModel.updateCorrectAnswer(newCorrectAnswer)},
                                    answers = createCardViewModel.answers,
                                    onAnswersChange = {newAnswers -> createCardViewModel.updateAnswers(newAnswers)},
                                    title = createCardViewModel.title,
                                    onTitleChange = {newTitle ->
                                            val title = newTitle.replace("badword", "*******")
                                            createCardViewModel.updateTitle(title)
                                    },
                                    content = createCardViewModel.content, onContentChange = { newContent -> createCardViewModel.updateContent(newContent)},
                                    createNoteFn = {question, answers, correctAnswer -> flashCardViewModel.createNote(question, answers, correctAnswer)}
                                    )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun Home(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(LightColorScheme.background, RoundedCornerShape(20.dp))
            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { navController.navigate("FlashCardList") }) {
            Text("View Flash Cards")
        }
        Button(onClick = { navController.navigate("CreateFlashCard") }) {
            Text("Create Flash Card")
        }
        Button(onClick = { navController.navigate("PlayCards") }) {
            Text("Play Flash Cards")
        }
    }
}
