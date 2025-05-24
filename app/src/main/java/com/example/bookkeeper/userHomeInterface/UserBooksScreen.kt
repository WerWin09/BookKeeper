package com.example.bookkeeper.userHomeInterface

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.bookkeeper.utils.Constants
import com.example.bookkeeper.ui.theme.*
import com.example.bookkeeper.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserBooksScreen(
    modifier: Modifier = Modifier,
    viewModel: UserBooksViewModel,
    onAddBook: () -> Unit,
    onBookClick: (Int) -> Unit,
    onStatusClick: (String) -> Unit
) {
    val allBooks by viewModel.books.collectAsStateWithLifecycle()
    val statuses = Constants.statusOptions
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val authors by viewModel.author.collectAsStateWithLifecycle()
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedAuthor by remember { mutableStateOf<String?>(null) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("BookKeeper", color = Color.Black) },
                windowInsets = WindowInsets(0, 0, 0, 0),
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MainColor),
                actions = {
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(Icons.Default.FilterAlt, contentDescription = "Filtruj", tint = Color.Black)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddBook,
                containerColor = MainColor,
                contentColor = Color.Black
            ) {
                Icon(Icons.Default.Add, contentDescription = "Dodaj książkę")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Image(
                painter = painterResource(R.drawable.background),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    var query by remember { mutableStateOf("") }
                    OutlinedTextField(
                        value = query,
                        onValueChange = {
                            query = it
                            viewModel.searchBooks(it)
                        },
                        placeholder = {
                            Text("Wyszukaj książki", color = Color.White)
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = Color.White)
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp)
                            .background(color = SecondBackgroundColor, shape = RoundedCornerShape(8.dp)),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SecondBackgroundColor,
                            unfocusedBorderColor = SecondBackgroundColor,
                            cursorColor = Color.White,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }

                item {
                    if (selectedCategory != null || selectedAuthor != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(modifier = Modifier.weight(1f))
                            TextButton(onClick = {
                                selectedCategory = null
                                selectedAuthor = null
                                viewModel.resetFilters()
                            }) {
                                Text("Wyczyść filtry", color = Color.White)
                            }
                        }
                    }
                }

                statuses.forEach { status ->
                    val booksForStatus = allBooks
                        .filter { it.status == status }
                        .sortedBy { it.title.lowercase() }

                    item {
                        StatusSection(
                            status = status,
                            books = booksForStatus,
                            onSectionClick = { onStatusClick(status) },
                            onBookClick = onBookClick,
                            textColor = Color.White,
                            authorColor = Color.White
                        )
                    }
                }

            }

            CombinedFilterDialog(
                showDialog = showFilterDialog,
                categories = categories,
                authors = authors,
                selectedCategory = selectedCategory,
                selectedAuthor = selectedAuthor,
                onCategorySelected = {
                    selectedCategory = it
                    viewModel.filterByCategory(it)
                },
                onAuthorSelected = {
                    selectedAuthor = it
                    viewModel.filterByAuthor(it)
                },
                onDismiss = { showFilterDialog = false }
            )
        }
    }
}



