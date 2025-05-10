package com.example.bookkeeper.userHomeInterface

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import android.util.Log


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserBooksScreen(
    viewModel: UserBooksViewModel,
    onAddBook: () -> Unit  // Dodaj ten parametr
) {
    val books by viewModel.books.collectAsStateWithLifecycle()

    LaunchedEffect(books) {
        Log.d("UserBooksScreen", "Liczba książek w stanie: ${books.size}")
        books.forEach {
            Log.d("UserBooksScreen", "Książka: ${it.title} by ${it.author}")
        }
    }


    Scaffold(
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
            Text("Twoje książki:", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            if (books.isEmpty()) {
                Text("Nie masz jeszcze dodanych książek.")
            } else {
                LazyColumn {
                    items(books) { book ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(book.title, style = MaterialTheme.typography.titleMedium)
                                Text("Autor: ${book.author}")
                                Text("Status: ${book.status}")
                                book.rating?.let {
                                    Text("Ocena: ${"★".repeat(it)}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}