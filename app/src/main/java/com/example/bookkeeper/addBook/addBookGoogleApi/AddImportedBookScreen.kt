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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
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
                        val book = selectedBook.copy(status = status.trim(), rating = rating)
                        viewModel.addBook(book)
                        searchViewModel.clearFields()
                        showSnackbar = true
                    },
                    enabled = status.isNotBlank()
                ) {
                    Text("Dodaj książkę")
                }
            }

            Text("* Edytowalne pola", style = MaterialTheme.typography.labelSmall)
        }
    }

    // efekt pokazujący snackbar i zawsze wracający do SearchBooksScreen
    LaunchedEffect(showSnackbar) {
        if (showSnackbar) {
            // lokalny snackbar (opcjonalnie)
            snackbarHostState.showSnackbar("Dodano książkę")

            // ustaw flagę dla SearchBooksScreen niezależnie od poprzednika
            navController
                .getBackStackEntry("searchBooks")
                .savedStateHandle
                .set("showSnackbarMessage", "Dodano książkę")

            // nawigacja i czyszczenie backstacku
            navController.navigate("searchBooks") {
                popUpTo("searchBooks") { inclusive = true }
            }
        }
    }
}
