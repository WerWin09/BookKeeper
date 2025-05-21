package com.example.bookkeeper.userHomeInterface

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SettingsScreen(
    onLogout: () -> Unit,
    viewModel: UserBooksViewModel = viewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Ustawienia",
            style = MaterialTheme.typography.headlineMedium
        )

        // Wyświetl liczbę książek
        Text(
            text = "Liczba książek w bibliotece: ${viewModel.booksCount}",
            style = MaterialTheme.typography.bodyLarge
        )

        Button(
            onClick = onLogout,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Wyloguj się")
        }
    }
}