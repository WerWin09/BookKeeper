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

@Composable
fun StatusSection(
    status: String,
    books: List<BookEntity>,
    onSectionClick: () -> Unit,
    onBookClick: (Int) -> Unit
) {
    // cały bloczek w zielonym tle
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFA5D6A7))   // jasna zieleń
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
                        color = Color.Black
                    )
                }
            } else {
                // poziome przewijanie kart
                LazyRow(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(books) { book ->
                        Card(
                            modifier = Modifier
                                .size(width = 140.dp, height = 100.dp)
                                .clickable { onBookClick(book.id) },
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF59D)), // żółty
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = book.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    maxLines = 2
                                )
                                Text(
                                    text = book.author,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}


