package com.example.bookkeeper.userHomeInterface

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CombinedFilterDialog(
    showDialog: Boolean,
    categories: List<String>,
    tags: List<String>,
    selectedCategory: String?,
    selectedTag: String?,
    onCategorySelected: (String) -> Unit,
    onTagSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    if (!showDialog) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filtruj książki") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                // KATEGORIE
                Text("Kategoria", style = MaterialTheme.typography.titleMedium)
                categories.forEach { cat ->
                    FilterRow(
                        label      = cat,
                        isSelected = cat == selectedCategory,
                        onClick    = { onCategorySelected(cat) }
                    )
                }
                Spacer(Modifier.height(12.dp))

                // TAGI
                Text("Tag", style = MaterialTheme.typography.titleMedium)
                tags.forEach { tag ->
                    FilterRow(
                        label      = tag,
                        isSelected = tag == selectedTag,
                        onClick    = { onTagSelected(tag) }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Zamknij")
            }
        }
    )
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
            .padding(8.dp)
    ) {
        RadioButton(selected = isSelected, onClick = onClick)
        Spacer(Modifier.width(8.dp))
        Text(label)
    }
}
