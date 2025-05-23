package com.example.bookkeeper.userHomeInterface

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.bookkeeper.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CombinedFilterDialog(
    showDialog: Boolean,
    categories: List<String>,
    authors: List<String>,
    selectedCategory: String?,
    selectedAuthor: String?,
    onCategorySelected: (String) -> Unit,
    onAuthorSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    if (!showDialog) return

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = BackgroundColor,
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Text(
                    "Filtruj książki",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.height(16.dp))

                // KATEGORIA
                Text(
                    "Kategoria",
                    style = MaterialTheme.typography.titleMedium,
                    color = MainColor
                )
                categories.forEach { cat ->
                    FilterRow(
                        label = cat,
                        isSelected = cat == selectedCategory,
                        onClick = { onCategorySelected(cat) }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // AUTOR
                Text(
                    "Autor",
                    style = MaterialTheme.typography.titleMedium,
                    color = MainColor
                )
                authors.forEach { author ->
                    FilterRow(
                        label = author,
                        isSelected = author == selectedAuthor,
                        onClick = { onAuthorSelected(author) }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Przycisk Zamknij
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text("Zamknij", color = Color.LightGray)
                    }
                }
            }
        }
    }
}



@Composable
private fun FilterRow(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            label,
            color = Color.LightGray,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}


