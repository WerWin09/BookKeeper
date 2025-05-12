package com.example.bookkeeper.userHomeInterface

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
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
    val statuses: List<String> by viewModel.statuses.collectAsStateWithLifecycle()
    val tags by viewModel.tags.collectAsStateWithLifecycle()
    LaunchedEffect(tags) {
        Log.d("UserBooksScreen", "Dostępne tagi: $tags")
    }

    var searchQuery by remember { mutableStateOf("") }
    var showCategoryFilterDialog by remember { mutableStateOf(false) }
    var showStatusFilterDialog by remember { mutableStateOf(false) }
    var showTagFilterDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedStatus by remember { mutableStateOf<String?>(null) }
    var selectedTag by remember { mutableStateOf<String?>(null) }



        LaunchedEffect(searchQuery) {
        if (searchQuery.isNotEmpty()) {
            viewModel.searchBooks(searchQuery)
        } else if (selectedCategory == null && selectedStatus == null && selectedTag == null) {
            viewModel.resetFilters()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Twoje książki") },
                actions = {
                    IconButton(onClick = { showCategoryFilterDialog = true }) {
                        Icon(Icons.Default.FilterAlt, contentDescription = "Filtruj po kategoriach")
                    }
                    IconButton(onClick = { showStatusFilterDialog = true }) {
                        Icon(Icons.Default.Star, contentDescription = "Filtruj po statusach")
                    }
                    IconButton(onClick = { showTagFilterDialog = true }) {
                        Icon(Icons.Default.Label, contentDescription = "Filtruj po tagach")
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

            if (selectedCategory != null || selectedStatus != null || selectedTag != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = buildString {
                            if (selectedCategory != null) append("Kategoria: $selectedCategory ")
                            if (selectedStatus != null) append("Status: $selectedStatus ")
                            if (selectedTag != null) append("Tag: $selectedTag")
                        },
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1
                    )
                    TextButton(onClick = {
                        selectedCategory = null
                        selectedStatus = null
                        selectedTag = null
                        viewModel.resetFilters()
                    }) {
                        Text("Wyczyść filtry")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (books.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Nie znaleziono książek.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
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

    FilterDialog(
        showDialog = showCategoryFilterDialog,
        title = "Filtruj po kategoriach",
        items = categories,
        selectedItem = selectedCategory,
        onItemSelected = { category ->
            selectedCategory = category
            selectedStatus = null
            selectedTag = null
            viewModel.filterByCategory(category)
            showCategoryFilterDialog = false
        },
        onDismiss = { showCategoryFilterDialog = false }
    )

    FilterDialog(
        showDialog = showStatusFilterDialog,
        title = "Filtruj po statusach",
        items = statuses,
        selectedItem = selectedStatus,
        onItemSelected = { status ->
            selectedStatus = status
            selectedCategory = null
            selectedTag = null
            viewModel.filterByStatus(status)
            showStatusFilterDialog = false
        },
        onDismiss = { showStatusFilterDialog = false }
    )

    FilterDialog(
        showDialog = showTagFilterDialog,
        title = "Filtruj po tagach",
        items = tags,
        selectedItem = selectedTag,
        onItemSelected = { tag ->
            selectedTag = tag
            viewModel.filterByTag(tag)
            showTagFilterDialog = false
        },
        onDismiss = { showTagFilterDialog = false }
    )
}

@Composable
private fun FilterDialog(
    showDialog: Boolean,
    title: String,
    items: List<String>,
    selectedItem: String?,
    onItemSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(title) },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                ) {
                    if (items.isEmpty()) {
                        Text("Brak dostępnych opcji")
                    } else {
                        items.forEach { item ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onItemSelected(item) }
                                    .padding(8.dp)
                            ) {
                                RadioButton(
                                    selected = selectedItem == item,
                                    onClick = { onItemSelected(item) }
                                )
                                Text(
                                    text = item,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
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
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = book.title,
                style = MaterialTheme.typography.titleMedium
            )
            Text("Autor: ${book.author}")
            Text("Status: ${book.status}")
            book.category?.let {
                Text("Kategoria: $it")
            }
            book.rating?.let {
                Text("Ocena: ${"★".repeat(it)}${"☆".repeat(5 - it)}")
            }
            if (book.tags.isNotEmpty()) {
                Text("Tagi: ${book.tags.joinToString(", ")}")
            }
        }
    }
}