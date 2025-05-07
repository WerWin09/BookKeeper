package com.example.bookkeeper.userHomeInterface.addBook

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bookkeeper.dataRoom.BookEntity
import com.example.bookkeeper.userHomeInterface.UserBooksViewModel

class EditImportedBookScreen {
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookScreen(
    onBack: () -> Unit,
    viewModel: UserBooksViewModel = viewModel()
) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf<Int?>(null) }
    var statusExpanded by remember { mutableStateOf(false) }
    val statusOptions = listOf("Przeczytana", "W trakcie", "Planowana")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dodaj nową książkę") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Anuluj",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.Companion
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Tytuł *") },
                modifier = Modifier.Companion.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = author,
                onValueChange = { author = it },
                label = { Text("Autor *") },
                modifier = Modifier.Companion.fillMaxWidth(),
                singleLine = true
            )

            ExposedDropdownMenuBox(
                expanded = statusExpanded,
                onExpandedChange = { statusExpanded = !statusExpanded },
                modifier = Modifier.Companion.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = status,
                    onValueChange = {},
                    label = { Text("Status *") },
                    modifier = Modifier.Companion.menuAnchor(),
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded)
                    }
                )
                ExposedDropdownMenu(
                    expanded = statusExpanded,
                    onDismissRequest = { statusExpanded = false }
                ) {
                    statusOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                status = option
                                statusExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Kategoria") },
                modifier = Modifier.Companion.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Opis") },
                modifier = Modifier.Companion.fillMaxWidth(),
                maxLines = 3
            )

            OutlinedTextField(
                value = rating?.toString() ?: "",
                onValueChange = {
                    rating = when (val value = it.toIntOrNull()) {
                        null -> null
                        in 1..5 -> value
                        else -> rating
                    }
                },
                label = { Text("Ocena (1-5)") },
                modifier = Modifier.Companion.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Companion.Number),
                singleLine = true
            )

            Row(
                modifier = Modifier.Companion.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Companion.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.Companion.padding(end = 8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text("Anuluj")
                }

                Button(
                    onClick = {
                        val newBook = BookEntity(
                            title = title.trim(),
                            author = author.trim(),
                            status = status.trim(),
                            category = category.trim().takeIf { it.isNotEmpty() },
                            description = description.trim().takeIf { it.isNotEmpty() },
                            rating = rating,
                            userId = ""
                        )
                        viewModel.addBook(newBook)
                        onBack()
                    },
                    enabled = title.isNotBlank() && author.isNotBlank() && status.isNotBlank()
                ) {
                    Text("Dodaj książkę")
                }
            }

            Text(
                text = "* Wymagane pola",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.Companion.padding(top = 8.dp)
            )
        }
    }
}