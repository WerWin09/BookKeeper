package com.example.bookkeeper.userHomeInterface

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bookkeeper.userHomeInterface.UserBooksViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bookkeeper.userHomeInterface.data.Book


@Composable
fun UserBooksScreen(viewModel: UserBooksViewModel) {
    val books by viewModel.books.collectAsStateWithLifecycle()

    Column(modifier = Modifier.padding(16.dp)) {
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
                        }
                    }
                }
            }
        }
    }
}
