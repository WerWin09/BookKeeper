package com.example.bookkeeper.userHomeInterface

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bookkeeper.dataRoom.BookEntity
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Photo
import android.graphics.BitmapFactory
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.asImageBitmap
import com.example.bookkeeper.ui.theme.*


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BookDetailsScreen(
    bookId: Int?,
    onBack: () -> Unit,
    onEdit: (Int) -> Unit,
    viewModel: UserBooksViewModel
) {
    val books by viewModel.books.collectAsStateWithLifecycle()
    val book = remember(bookId, books) {
        books.firstOrNull { it.id == bookId }
    }

    var showDeleteConfirmation by remember { mutableStateOf(false) }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = {
                Text(
                    "Usunąć książkę?",
                    color = Color.White
                )
            },
            text = {
                Text(
                    "Czy na pewno chcesz usunąć tę książkę? Tej operacji nie można cofnąć.",
                    color = Color.White
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        book?.let { viewModel.deleteBook(it) }
                        onBack()
                    }
                ) {
                    Text("Usuń", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Anuluj", color = Color.White)
                }
            },
            containerColor = BackgroundColor
        )
    }


    Scaffold(
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(0, 0, 0, 0),
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MainColor),
                title = { Text("Szczegóły książki", color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Wróć",
                                                    tint = Color.Black)
                    }
                },
                actions = {
                    book?.let { nonNullBook ->
                        IconButton(onClick = { onEdit(nonNullBook.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edytuj",
                                                    tint = Color.Black)
                        }
                        IconButton(
                            onClick = { showDeleteConfirmation = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Usuń",
                                tint =  Color.Red
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (book == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Nie znaleziono książki")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundColor)
                    .verticalScroll(rememberScrollState())
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    // Okładka po lewej
                    val coverBitmap = remember(book.coverLocalPath) {
                        try {
                            book.coverLocalPath?.let {
                                BitmapFactory.decodeFile(it)?.asImageBitmap()
                            }
                        } catch (e: Exception) {
                            null
                        }
                    }

                    if (coverBitmap != null) {
                        Image(
                            bitmap = coverBitmap,
                            contentDescription = "Okładka",
                            modifier = Modifier
                                .size(120.dp)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium)
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Photo,
                                contentDescription = "Brak okładki",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                        }
                    }

                    // Dane tekstowe po prawej
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = book.title,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MainColor
                        )
                        InfoRow(label = "Autor:", value = book.author)
                        InfoRow(label = "Status:", value = book.status)

                        book.category?.let {
                            InfoRow(label = "Kategoria:", value = it)
                        }

                        book.rating?.let {
                            InfoRow(label = "Ocena:", rating = it)
                        }


                    }
                }



                Spacer(modifier = Modifier.height(24.dp))

                // Tags section
                if (book.tags.isNotEmpty()) {
                    Text(
                        text = "Tagi:",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // This is the experimental FlowRow
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        book.tags.forEach { tag ->
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    text = tag,
                                    modifier = Modifier.padding(8.dp),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Description section
                book.description?.let { description ->
                    Text(
                        text = "Opis:",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, rating: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            ),
            modifier = Modifier.widthIn(min = 100.dp)
        )
        Row {
            repeat(rating) {
                Text("★", color = MainColor, fontSize = MaterialTheme.typography.titleMedium.fontSize)
            }
            repeat(5 - rating) {
                Text("☆", color = Color.Gray, fontSize = MaterialTheme.typography.titleMedium.fontSize)
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = Color.White // lub inny kolor etykiety
            ),
            modifier = Modifier.widthIn(min = 100.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )
    }
}
