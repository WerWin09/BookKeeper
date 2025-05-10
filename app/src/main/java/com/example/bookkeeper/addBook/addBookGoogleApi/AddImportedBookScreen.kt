package com.example.bookkeeper.addBook.addBookGoogleApi

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


// wyglad ekranu do edycji ksiazek po znalezienu w Google Books
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditImportedBookScreen(
    onBack: () -> Unit,
    viewModel: UserBooksViewModel = viewModel(),
    selectedBook: BookEntity?
) {
    if (selectedBook == null) {
        Text("Nie wybrano książki.")
        return
    }

    var status by remember { mutableStateOf(selectedBook.status) }
    var rating by remember { mutableStateOf(selectedBook.rating) }
    var statusExpanded by remember { mutableStateOf(false) }
    val statusOptions = listOf("Przeczytana", "W trakcie", "Planowana")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dodaj nową książkę") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Anuluj")
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
                value = selectedBook.title,
                onValueChange = {},
                label = { Text("Tytuł") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true
            )
            OutlinedTextField(
                value = selectedBook.author,
                onValueChange = {},
                label = { Text("Autor") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true
            )
            OutlinedTextField(
                value = selectedBook.category ?: "",
                onValueChange = {},
                label = { Text("Kategoria") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true
            )
            OutlinedTextField(
                value = selectedBook.description ?: "",
                onValueChange = {},
                label = { Text("Opis") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                maxLines = 3
            )

            // Edytowalny status
            ExposedDropdownMenuBox(
                expanded = statusExpanded,
                onExpandedChange = { statusExpanded = !statusExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = status,
                    onValueChange = {},
                    label = { Text("Status *") },
                    readOnly = true,
                    modifier = Modifier.menuAnchor(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) }
                )
                ExposedDropdownMenu(
                    expanded = statusExpanded,
                    onDismissRequest = { statusExpanded = false }
                ) {
                    statusOptions.forEach {
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = {
                                status = it
                                statusExpanded = false
                            }
                        )
                    }
                }
            }

            // Edytowalna ocena
            OutlinedTextField(
                value = rating?.toString() ?: "",
                onValueChange = {
                    rating = it.toIntOrNull()?.takeIf { it in 1..5 }
                },
                label = { Text("Ocena (1-5)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(onClick = onBack, modifier = Modifier.padding(end = 8.dp)) {
                    Text("Anuluj")
                }

                Button(
                    onClick = {
                        val book = selectedBook.copy(
                            status = status.trim(),
                            rating = rating
                        )
                        viewModel.addBook(book)
                        onBack()
                    },
                    enabled = status.isNotBlank()
                ) {
                    Text("Dodaj książkę")
                }
            }

            Text("* Edytowalne pola", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
        }
    }
}
