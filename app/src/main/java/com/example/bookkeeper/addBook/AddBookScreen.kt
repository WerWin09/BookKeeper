package com.example.bookkeeper.addBook

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bookkeeper.addBook.addBookGoogleApi.SearchBooksScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookScreen(
    navController: NavController
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Dodaj książkę") })
        }
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .padding(16.dp)
            .fillMaxSize()) {

            // Wyświetlenie książek z API (SearchBooksScreen)
            Text("Wyszukaj książkę z Google Books", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            SearchBooksScreen(navController = navController)

            Spacer(Modifier.height(24.dp))

            // Przycisk do przejścia do ręcznego dodawania
            Button(onClick = { navController.navigate("manualAddBook") }) {
                Text("Dodaj książkę ręcznie")
            }
        }
    }
}
