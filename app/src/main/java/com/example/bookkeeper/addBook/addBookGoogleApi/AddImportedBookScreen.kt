package com.example.bookkeeper.addBook.addBookGoogleApi

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    viewModel: UserBooksViewModel,
    searchViewModel: SearchBooksViewModel
) {
    val selectedBookState = searchViewModel.selectedBook.collectAsStateWithLifecycle()
    val selectedBook = selectedBookState.value

    Log.d("ScanIsbn", "SearchBooksVM hash in EditImportedBookScreen: ${searchViewModel.hashCode()}")
    Log.d("ScanIsbn", "selectedBook = $selectedBook")

    if (selectedBook == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Nie wybrano książki.")
        }
        return
    }

    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> imageUri = uri }

    var status by remember { mutableStateOf(selectedBook.status) }
    var rating by remember { mutableStateOf(selectedBook.rating) }
    var statusExpanded by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    var showSnackbar by remember { mutableStateOf(false) }

    val displayBitmap = remember(imageUri to selectedBook.coverUrlRemote) {
        try {
            when {
                imageUri != null -> {
                    val inputStream = context.contentResolver.openInputStream(imageUri!!)
                    BitmapFactory.decodeStream(inputStream)?.asImageBitmap()
                }
                !selectedBook.coverUrlRemote.isNullOrEmpty() -> {
                    val url = java.net.URL(selectedBook.coverUrlRemote)
                    BitmapFactory.decodeStream(url.openStream())?.asImageBitmap()
                }
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }

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
            Text("Okładka:", style = MaterialTheme.typography.titleMedium)

            Box(
                modifier = Modifier
                    .size(150.dp)
                    .align(Alignment.CenterHorizontally)
                    .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                displayBitmap?.let {
                    Image(bitmap = it, contentDescription = "Okładka książki", modifier = Modifier.fillMaxSize())
                } ?: Icon(
                    imageVector = Icons.Default.Photo,
                    contentDescription = "Brak okładki",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.outline
                )
            }

            Button(onClick = { galleryLauncher.launch("image/*") }) {
                Text("Zmień okładkę")
            }

            OutlinedTextField(value = selectedBook.title, onValueChange = {}, label = { Text("Tytuł") }, readOnly = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = selectedBook.author, onValueChange = {}, label = { Text("Autor") }, readOnly = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = selectedBook.category.orEmpty(), onValueChange = {}, label = { Text("Kategoria") }, readOnly = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = selectedBook.description.orEmpty(), onValueChange = {}, label = { Text("Opis") }, readOnly = true, modifier = Modifier.fillMaxWidth(), maxLines = 3)

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
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded)
                    }
                )
                ExposedDropdownMenu(expanded = statusExpanded, onDismissRequest = { statusExpanded = false }) {
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
                value = rating?.toString().orEmpty(),
                onValueChange = { rating = it.toIntOrNull()?.takeIf { it in 1..5 } },
                label = { Text("Ocena (1–5)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                OutlinedButton(onClick = { navController.popBackStack() }, modifier = Modifier.padding(end = 8.dp)) {
                    Text("Anuluj")
                }
                Button(
                    onClick = {
                        val book = selectedBook.copy(status = status.trim(), rating = rating)
                        viewModel.saveBookWithCover(book, imageUri = imageUri)
                        searchViewModel.clearFields()
                        searchViewModel.clearSelectedBook()
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

    LaunchedEffect(Unit) {
        snapshotFlow { showSnackbar }.collect { show ->
            if (show) {
                navController.navigate("manualAddBook") {
                    popUpTo("manualAddBook") { inclusive = true }
                }
                navController.getBackStackEntry("manualAddBook")
                    .savedStateHandle
                    .set("showSnackbarMessage", "Dodano książkę")

                showSnackbar = false // zresetuj po użyciu
            }
        }
    }
}


