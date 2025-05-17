package com.example.bookkeeper.userHomeInterface

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bookkeeper.R
import com.example.bookkeeper.ui.theme.BackgroundColor
import com.example.bookkeeper.ui.theme.MainColor
import com.example.bookkeeper.utils.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserBooksScreen(
    viewModel: UserBooksViewModel,
    onAddBook: () -> Unit,
    onBookClick: (Int) -> Unit,
    onStatusClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val allBooks    by viewModel.books.collectAsStateWithLifecycle()
    val statuses    = Constants.statusOptions
    val categories  by viewModel.categories.collectAsStateWithLifecycle()
    val tags        by viewModel.tags.collectAsStateWithLifecycle()

    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedTag      by remember { mutableStateOf<String?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        // 1) pełnoekranowy obrazek w tle
        Image(
            painter = painterResource(R.drawable.background),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Twoje książki", color = Color.Black )},
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = MainColor
                    ),
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
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                // 1) Wyszukiwarka
                item {
                    var query by remember { mutableStateOf("") }
                    OutlinedTextField(
                        value = query,
                        onValueChange = {
                            query = it
                            viewModel.searchBooks(it)
                        },

                        label = { Text("Wyszukaj książki") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp)
                            .background(BackgroundColor)
                    )
                }

                // 2) Przycisk "Wyczyść filtry", tylko gdy są aktywne
                item {
                    if (selectedCategory != null || selectedTag != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(modifier = Modifier.weight(1f))
                            TextButton(onClick = {
                                selectedCategory = null
                                selectedTag = null
                                viewModel.resetFilters()
                            }) {
                                Text("Wyczyść filtry")
                            }
                        }
                    }
                }

                // 3) Sekcje wg statusów
                statuses.forEach { status ->
                    val booksForStatus = allBooks.filter { it.status == status }
                    item {
                        StatusSection(
                            status         = status,
                            books          = booksForStatus,
                            onSectionClick = { onStatusClick(status) },
                            onBookClick    = onBookClick
                        )
                    }
                }
            }

            // 4) Dialog – tylko kategorie i tagi
            CombinedFilterDialog(
                showDialog       = showFilterDialog,
                categories       = categories,
                tags             = tags,
                selectedCategory = selectedCategory,
                selectedTag      = selectedTag,
                onCategorySelected = { cat ->
                    selectedCategory = cat
                    viewModel.filterByCategory(cat)
                },
                onTagSelected    = { tg ->
                    selectedTag = tg
                    viewModel.filterByTag(tg)
                },
                onDismiss        = { showFilterDialog = false }
            )
        }
    }
}
