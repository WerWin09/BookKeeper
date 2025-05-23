package com.example.bookkeeper.addBook.addBookGoogleApi

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.bookkeeper.googleBooksApi.GoogleBookItem
import com.example.bookkeeper.ui.theme.*

@Composable
fun GoogleBookCard(
    item: GoogleBookItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = SecondBackgroundColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = item.volumeInfo.title ?: "Brak tytułu",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
            Text(
                text = item.volumeInfo.authors?.joinToString(", ") ?: "Nieznany autor",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    Log.d("GoogleBookCard", "Kliknięto przycisk Dodaj")
                    onClick()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MainColor,
                    contentColor = Color.Black
                )
            ) {
                Text("Dodaj")
            }
        }
    }
}

