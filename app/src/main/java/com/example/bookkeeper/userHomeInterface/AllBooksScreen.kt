package com.example.bookkeeper.userHomeInterface

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.example.bookkeeper.dataRoom.BookEntity

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Photo
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.bookkeeper.R
import com.example.bookkeeper.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllBooksScreen(
    viewModel: UserBooksViewModel,
    onBookClick: (Int) -> Unit,
    onAddBook: () -> Unit,
    onBack: () -> Unit
) {
    val books by viewModel.books.collectAsState()


    Scaffold(
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            FloatingActionButton(
                        onClick = onAddBook,
                        containerColor = MainColor,
                        contentColor = Color.Black) {
                Icon(Icons.Default.Add, contentDescription = "Dodaj książkę")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Image(
                painter = painterResource(R.drawable.background),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                TopAppBar(
                    title = {
                        Text("Wszystkie książki", color = Color.Black)
                    },
                    windowInsets = WindowInsets(0, 0, 0, 0),
                    colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MainColor),
                    navigationIcon = {
                        IconButton(onClick = {
                            viewModel.resetFilters()
                            onBack()
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Wróć", tint = Color.Black)
                        }
                    }
                )


                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(books.sortedBy { it.title.lowercase() }) { book ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onBookClick(book.id) },
                            colors = CardDefaults.cardColors(containerColor = SecondBackgroundColor),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val coverBitmap = remember(book.coverLocalPath) {
                                    try {
                                        book.coverLocalPath?.let {
                                            android.graphics.BitmapFactory.decodeFile(it)?.asImageBitmap()
                                        }
                                    } catch (e: Exception) {
                                        null
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .padding(end = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (coverBitmap != null) {
                                        Image(
                                            bitmap = coverBitmap,
                                            contentDescription = "Okładka",
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Photo,
                                            contentDescription = "Brak okładki",
                                            modifier = Modifier.size(32.dp),
                                            tint = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }

                                Column {
                                    Text(book.title, style = MaterialTheme.typography.titleMedium, color = Color.White)
                                    Text(book.author, style = MaterialTheme.typography.bodyMedium, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun BookListItem(book: BookEntity, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RectangleShape,
        tonalElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = book.title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = book.author,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}