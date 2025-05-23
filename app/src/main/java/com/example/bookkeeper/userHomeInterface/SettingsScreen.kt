package com.example.bookkeeper.userHomeInterface

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bookkeeper.ui.theme.BackgroundColor
import com.example.bookkeeper.ui.theme.MainColor

@Composable
fun SettingsScreen(
    onLogout: () -> Unit,
    viewModel: UserBooksViewModel = viewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(0.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tytuł z tłem MainColor i czarnym tekstem
        Text(
            text = "Ustawienia",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .background(MainColor)
                .padding(15.dp)
        )

        Text(
            text = "Liczba książek w bibliotece: ${viewModel.booksCount}",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            modifier = Modifier
                .padding(15.dp)
        )

        Button(
            onClick = onLogout,
            modifier = Modifier.align(Alignment.End)
                                .padding(15.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MainColor,
                contentColor = Color.Black
            )
        ) {
            Text("Wyloguj się")
        }
    }
}

