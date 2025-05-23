package com.example.bookkeeper.userHomeInterface

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.bookkeeper.dataRoom.BookEntity
import com.example.bookkeeper.ui.theme.BackgroundColor

@Composable
fun StatusSection(
    status: String,
    books: List<BookEntity>,
    onSectionClick: () -> Unit,
    onBookClick: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = BackgroundColor)
            .padding(vertical = 12.dp)
    ) {
        Column {
            // nagłówek z tytułem i strzałką
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSectionClick() }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(status, style = MaterialTheme.typography.titleLarge)
                Icon(Icons.Default.ArrowForward, contentDescription = null)
            }

            Spacer(Modifier.height(8.dp))

            if (books.isEmpty()) {
                // brak książek — komunikat
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nie posiadasz żadnej przypisanej książki w tej kategorii",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.LightGray
                    )
                }
            } else {
                // poziome przewijanie kart
                LazyRow(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(books.sortedBy { it.title.lowercase() }) { book ->
                    BookItemCard(book = book, onClick = { onBookClick(book.id) })
                    }
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}


