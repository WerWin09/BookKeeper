package com.example.bookkeeper.addBook.addBookGoogleApi

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bookkeeper.googleBooksApi.GoogleBookItem

@Composable
fun GoogleBookCard(
    item: GoogleBookItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
        // (opcjonalnie, jeśli chcesz, by kliknięcie w kartę też działało)
        // .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = item.volumeInfo.title ?: "Brak tytułu",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = item.volumeInfo.authors?.joinToString(", ") ?: "Nieznany autor"
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    Log.d("GoogleBookCard", "Kliknięto przycisk Dodaj")
                    onClick()
                }
            ) {
                Text("Dodaj")
            }
        }
    }
}
