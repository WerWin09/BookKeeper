package com.example.bookkeeper.userHomeInterface

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bookkeeper.utils.Constants
import com.example.bookkeeper.dataRoom.BookEntity
import com.example.bookkeeper.userHomeInterface.CombinedFilterDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BooksByStatusScreen(
    status: String,
    viewModel: UserBooksViewModel,
    onAddBook: () -> Unit,
    onBookClick: (Int) -> Unit,
    onBack: () -> Unit
) {
    // 1) subskrybuj książki z danego statusu
    LaunchedEffect(status) {
        viewModel.filterByStatus(status)
    }
    val books      by viewModel.books.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val authors by viewModel.author.collectAsStateWithLifecycle()

    // stan dla dialogu
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var query            by remember { mutableStateOf("") }
    var selectedAuthor by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Status: $status") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.resetFilters()
                        onBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Wróć")
                    }
                },
                actions = {
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(Icons.Default.FilterAlt, contentDescription = "Filtruj")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddBook) {
                Icon(Icons.Default.Add, contentDescription = "Dodaj książkę")
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(books) { book ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onBookClick(book.id) },
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
                            Text(
                                book.title,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                book.author,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }


        // 4) dialog filtrowania kategorii i autora
        CombinedFilterDialog(
            showDialog         = showFilterDialog,
            categories         = categories,
            authors            = authors,
            selectedCategory   = selectedCategory,
            selectedAuthor     = selectedAuthor,
            onCategorySelected = { cat ->
                selectedCategory = cat
                viewModel.filterByCategory(cat)
            },
            onAuthorSelected = { author ->
                selectedAuthor = author
                viewModel.filterByAuthor(author)
            },
            onDismiss = { showFilterDialog = false }
        )

    }
}
