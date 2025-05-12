package com.example.bookkeeper.addBook.addBookGoogleApi

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bookkeeper.dataRoom.BookEntity
import com.example.bookkeeper.userHomeInterface.UserBooksViewModel
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarHost
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditImportedBookScreen(
    navController: NavController,
    viewModel: UserBooksViewModel = viewModel(),
    searchViewModel: SearchBooksViewModel = viewModel(),
    selectedBook: BookEntity?
)
 {
    if (selectedBook == null) {
        Text("Nie wybrano książki.")
        return
    }

    var status by remember { mutableStateOf(selectedBook.status) }
    var rating by remember { mutableStateOf(selectedBook.rating) }
    var statusExpanded by remember { mutableStateOf(false) }
    val statusOptions = listOf("Przeczytana", "W trakcie", "Planowana")

    val snackbarHostState = remember { SnackbarHostState() }
    var showSnackbar by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                    title = { Text("Dodaj nową książkę") },
                    navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Anuluj")}
                    }
                    )
                },
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
            ) { padding ->
                Column(
                    modifier = Modifier
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
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = { navController.navigate("userBooks") {
                                popUpTo("userBooks") { inclusive = true }
                            } },
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

     if (showSnackbar) {
         LaunchedEffect(Unit) {
             snackbarHostState.showSnackbar("Dodano książkę")

             // Ustaw flagę, by inne ekrany wiedziały że dodano książkę
             navController.previousBackStackEntry
                 ?.savedStateHandle
                 ?.set("bookAdded", true)

             // Nawigacja do UserBooksScreen (startDestination)
             navController.navigate("userBooks") {
                 popUpTo("userBooks") { inclusive = true }
             }
         }
     }




}
