package com.example.bookkeeper.addBook.addBookGoogleApi

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bookkeeper.utils.mapGoogleBookToBookEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBooksScreen(
    navController: NavController,
    viewModel: SearchBooksViewModel = viewModel()
) {
    val results by viewModel.searchResults.collectAsStateWithLifecycle()
    val navigateToEdit by viewModel.navigateToEdit.collectAsStateWithLifecycle()
    var query by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(navigateToEdit) {
        if (navigateToEdit) {
            navController.navigate("editImportedBook")
            viewModel.onNavigatedToEditScreen()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wyszukaj książkę") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate("userBooks") {
                            popUpTo("userBooks") { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Wróć")
                    }
                }
            )
        },
        floatingActionButton = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                FloatingActionButton(onClick = { navController.navigate("manualAddBook") }) {
                    Icon(Icons.Default.Add, contentDescription = "Dodaj ręcznie")
                }
                FloatingActionButton(onClick = { showDialog = true }) {
                    Icon(Icons.Default.AddAPhoto, contentDescription = "Skanuj ISBN")
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                    if (query.length >= 3) {
                        viewModel.searchBooks(query)
                    }
                },
                label = { Text("Tytuł lub autor") },
                modifier = Modifier.fillMaxWidth()
            )

            LazyColumn {
                items(results) { item ->
                    GoogleBookCard(item = item, onClick = {
                        val bookEntity = mapGoogleBookToBookEntity(item)
                        viewModel.setSelectedBookAndNavigate(bookEntity)
                    })
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Wybierz źródło zdjęcia") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    navController.navigate("scanIsbn?source=searchBooks&input=camera")
                }) {
                    Text("Aparat")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    navController.navigate("scanIsbn?source=searchBooks&input=gallery")
                }) {
                    Text("Galeria")
                }
            }
        )
    }
}



