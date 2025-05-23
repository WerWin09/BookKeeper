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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bookkeeper.utils.Constants
import com.example.bookkeeper.dataRoom.BookEntity
import com.example.bookkeeper.userHomeInterface.CombinedFilterDialog
import com.example.bookkeeper.R
import com.example.bookkeeper.ui.theme.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BooksByStatusScreen(
    status: String,
    viewModel: UserBooksViewModel,
    onAddBook: () -> Unit,
    onBookClick: (Int) -> Unit,
    onBack: () -> Unit
) {
    LaunchedEffect(status) {
        viewModel.filterByStatus(status)
    }

    val books by viewModel.books.collectAsStateWithLifecycle()
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
                title = { Text("Status: $status", color = Color.Black) },
                windowInsets = WindowInsets(0, 0, 0, 0),
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MainColor),
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.resetFilters()
                        onBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Wróć", tint = Color.Black)
                    }
                },
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

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(books) { book ->
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
                                Text(book.title, style = MaterialTheme.typography.titleMedium)
                                Text(book.author, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
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

