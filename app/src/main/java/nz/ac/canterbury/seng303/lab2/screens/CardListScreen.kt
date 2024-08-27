package nz.ac.canterbury.seng303.lab2.screens

//import org.koin.androidx.viewmodel.ext.android.viewModel
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import nz.ac.canterbury.seng303.lab2.models.FlashCard
import nz.ac.canterbury.seng303.lab2.ui.theme.LightColorScheme
import nz.ac.canterbury.seng303.lab2.viewmodels.FlashCardViewModel
import java.net.URLEncoder

@Composable
    fun FlashCardList(
    navController: NavController,
    flashCardViewModel: FlashCardViewModel
    ) {
        flashCardViewModel.getNotes()
        val flashCards: List<FlashCard> by flashCardViewModel.notes.collectAsState(emptyList())
        if (flashCards.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize()
                    .background(LightColorScheme.background),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("No notes exist", style = MaterialTheme.typography.headlineMedium)
            }
        } else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .background(LightColorScheme.background, RoundedCornerShape(20.dp))
                    .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
                    .padding(12.dp),
//                    .verticalScroll(scrollState)
//                    .background(LightColorScheme.background),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Flash Cards",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                }

                items(flashCards) { note ->
                    FlashCardItem(
                        navController = navController,
                        flashCard = note,
                        deleteFn = { id: Int -> flashCardViewModel.deleteNoteById(id) })
                }
            }
        }
    }

    @Composable
    fun FlashCardItem(navController: NavController, flashCard: FlashCard, deleteFn: (id: Int) -> Unit) {
        val context = LocalContext.current
        Row(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .background(Color.White, RoundedCornerShape(20.dp))
                .padding(16.dp)
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center

        ) {
            Column(
                modifier = Modifier
                    .weight(3f)
                    .fillMaxWidth()
            ) {
                Text(
                    text = flashCard.question,
                    color = Color.Black,
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxWidth()
                ) {
                    IconButton(onClick = {
                        val query = URLEncoder.encode(flashCard.question, "UTF-8")
                        val searchUrl = "https://www.google.com/search?q=$query"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(searchUrl))
                        context.startActivity(intent);
                    },
                        modifier = Modifier
                            .background(LightColorScheme.secondary, CircleShape)
                            .padding(horizontal = 14.dp)) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = Color.White
                        )
                    }
                    IconButton(
                        onClick = {
                            val builder = AlertDialog.Builder(context)
                            builder.setMessage("Delete Flash Card \"${flashCard.question}\"?")
                                .setCancelable(false)
                                .setPositiveButton("Delete") { dialog, id ->
                                    deleteFn(flashCard.id)
                                    dialog.dismiss()
                                }
                                .setNegativeButton("Cancel") { dialog, id ->
                                    dialog.dismiss()
                                }
                            val alert = builder.create()
                            alert.show()
                        },
                        modifier = Modifier
                            .background(LightColorScheme.secondary, CircleShape)
                            .padding(horizontal = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete",
                            tint = Color.White
                        )
                    }

                    IconButton(onClick = {
                        navController.navigate("EditFlashCard/${flashCard.id}")
                    },
                        modifier = Modifier
                            .background(LightColorScheme.secondary, CircleShape)
                            .padding(horizontal = 16.dp)

                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Edit",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
