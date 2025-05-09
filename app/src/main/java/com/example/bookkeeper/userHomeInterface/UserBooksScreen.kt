package com.example.bookkeeper.userHomeInterface

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bookkeeper.dataRoom.BookEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserBooksScreen(
    viewModel: UserBooksViewModel,
    onAddBook: () -> Unit,
    onBookClick: (Int) -> Unit
) {
    val books: List<BookEntity> by viewModel.books.collectAsStateWithLifecycle()
    val categories: List<String> by viewModel.categories.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotEmpty()) {
            viewModel.searchBooks(searchQuery)
        } else if (selectedCategory == null) {
            viewModel.resetFilters()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Twoje książki") },
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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Wyszukaj książki") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Szukaj") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedCategory != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Filtr: $selectedCategory",
                        style = MaterialTheme.typography.labelLarge
                    )
                    TextButton(onClick = {
                        selectedCategory = null
                        viewModel.resetFilters()
                    }) {
                        Text("Wyczyść filtr")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (books.isEmpty()) {
                Text("Nie znaleziono książek.")
            } else {
                LazyColumn {
                    items(books) { book ->
                        BookItem(
                            book = book,
                            onClick = { onBookClick(book.id) }
                        )
                    }
                }
            }
        }
    }

    if (showFilterDialog) {
        AlertDialog(
            onDismissRequest = { showFilterDialog = false },
            title = { Text("Filtruj książki") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text("Kategorie:", style = MaterialTheme.typography.labelLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    if (categories.isEmpty()) {
                        Text("Brak dostępnych kategorii")
                    } else {
                        categories.forEach { category ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedCategory = category
                                        viewModel.filterByCategory(category)
                                        showFilterDialog = false
                                    }
                                    .padding(8.dp)
                            ) {
                                RadioButton(
                                    selected = selectedCategory == category,
                                    onClick = {
                                        selectedCategory = category
                                        viewModel.filterByCategory(category)
                                        showFilterDialog = false
                                    }
                                )
                                Text(category, modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFilterDialog = false }) {
                    Text("Zamknij")
                }
            }
        )
    }
}

@Composable
private fun BookItem(
    book: BookEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(book.title, style = MaterialTheme.typography.titleMedium)
            Text("Autor: ${book.author}")
            Text("Status: ${book.status}")
            book.category?.let {
                Text("Kategoria: $it")
            }
            book.rating?.let {
                Text("Ocena: ${"★".repeat(it)}")
            }
            if (book.tags.isNotEmpty()) {
                Text("Tagi: ${book.tags.joinToString(", ")}")
            }
        }
    }
}