package com.example.bookkeeper.userHomeInterface

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bookkeeper.dataRoom.BookEntity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.ui.text.input.KeyboardType

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
            modifier = Modifier
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
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = author,
                onValueChange = { author = it },
                label = { Text("Autor *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            ExposedDropdownMenuBox(
                expanded = statusExpanded,
                onExpandedChange = { statusExpanded = !statusExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = status,
                    onValueChange = {},
                    label = { Text("Status *") },
                    modifier = Modifier.menuAnchor(),
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
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Opis") },
                modifier = Modifier.fillMaxWidth(),
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
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.padding(end = 8.dp),
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
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}