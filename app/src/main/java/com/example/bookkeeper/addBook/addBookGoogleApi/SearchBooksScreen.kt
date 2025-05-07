package com.example.bookkeeper.addBook.addBookGoogleApi

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bookkeeper.utils.mapGoogleBookToBookEntity
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SearchBooksScreen(
    navController: NavController,
    viewModel: SearchBooksViewModel = viewModel()
) {
    val results by viewModel.searchResults.collectAsStateWithLifecycle()
    val navigateToEdit by viewModel.navigateToEdit.collectAsStateWithLifecycle()
    var query by remember { mutableStateOf("") }

    // obsługa nawigacji jako efekt uboczny
    LaunchedEffect(navigateToEdit) {
        if (navigateToEdit) {
            navController.navigate("editImportedBook")
            viewModel.onNavigatedToEditScreen() // reset flagi
        }
    }

    Column {
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                if (query.length >= 3) {
                    viewModel.searchBooks(query)
                }
            },
            label = { Text("Wyszukaj książkę") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
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