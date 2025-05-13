package com.example.bookkeeper.addBook.addBookGoogleApi

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bookkeeper.dataRoom.BookEntity
import com.example.bookkeeper.userHomeInterface.UserBooksViewModel
import com.example.bookkeeper.addBook.addBookGoogleApi.SearchBooksViewModel
import com.example.bookkeeper.utils.Constants.statusOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditImportedBookScreen(
    navController: NavController,
    viewModel: UserBooksViewModel = viewModel(),
    searchViewModel: SearchBooksViewModel = viewModel(),
    selectedBook: BookEntity?
) {
    if (selectedBook == null) {
        Text("Nie wybrano książki.")
        return
    }

    var status by remember { mutableStateOf(selectedBook.status) }
    var rating by remember { mutableStateOf(selectedBook.rating) }
    var statusExpanded by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    var showSnackbar by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dodaj nową książkę") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Anuluj")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- Pól tylko do odczytu ---
            OutlinedTextField(
                value = selectedBook.title,
                onValueChange = {},
                label = { Text("Tytuł") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = selectedBook.author,
                onValueChange = {},
                label = { Text("Autor") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = selectedBook.category ?: "",
                onValueChange = {},
                label = { Text("Kategoria") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = selectedBook.description ?: "",
                onValueChange = {},
                label = { Text("Opis") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            // --- Dropdown statusu ---
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
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = statusExpanded
                        )
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

            // --- Pola rating ---
            OutlinedTextField(
                value = rating?.toString() ?: "",
                onValueChange = {
                    rating = it.toIntOrNull()?.takeIf { it in 1..5 }
                },
                label = { Text("Ocena (1-5)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // --- Przyciski Anuluj / Dodaj ---
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("Anuluj")
                }
                Button(
                    onClick = {
                        val book = selectedBook.copy(
                            status = status.trim(),
                            rating = rating
                        )
                        viewModel.addBook(book)
                        searchViewModel.clearFields()
                        showSnackbar = true
                    },
                    enabled = status.isNotBlank()
                ) {
                    Text("Dodaj książkę")
                }
            }

            Text(
                "* Edytowalne pola",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }

    // --- Efekt: snackbar + nawigacja do SearchBooksScreen ---
    LaunchedEffect(showSnackbar) {
        if (showSnackbar) {
            // 1) nawigujemy od razu do SearchBooksScreen, czyszcząc wszystko powyżej
            navController.navigate("searchBooks") {
                popUpTo("searchBooks") { inclusive = true }
            }
            // 2) dopiero teraz ustawiamy flagę na tym entry, żeby SearchBooksScreen odczytał ją natychmiast
            navController
                .getBackStackEntry("searchBooks")
                .savedStateHandle
                .set("showSnackbarMessage", "Dodano książkę")
        }
    }

}
