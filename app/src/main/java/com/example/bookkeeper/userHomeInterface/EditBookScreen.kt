package com.example.bookkeeper.userHomeInterface

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bookkeeper.dataRoom.BookEntity
import com.example.bookkeeper.utils.Constants.statusOptions
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBookScreen(
    bookId: Int?,
    onBack: () -> Unit,
    viewModel: UserBooksViewModel
) {
    var bookState by remember { mutableStateOf<BookEntity?>(null) }
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf<Int?>(null) }
    var tags by remember { mutableStateOf<List<String>>(emptyList()) }
    var statusExpanded by remember { mutableStateOf(false) }
    var localCoverPath by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                val file = File(context.filesDir, "cover_${System.currentTimeMillis()}.jpg")
                file.outputStream().use { out ->
                    bitmap?.compress(Bitmap.CompressFormat.JPEG, 90, out)
                }
                localCoverPath = file.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    LaunchedEffect(bookId) {
        bookId?.let { id ->
            viewModel.getBookById(id)?.let { book ->
                bookState = book
            }
        }
    }

    LaunchedEffect(bookState) {
        bookState?.let { book ->
            title = book.title
            author = book.author
            status = book.status
            category = book.category ?: ""
            description = book.description ?: ""
            rating = book.rating
            tags = book.tags
            localCoverPath = book.localCoverPath
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edytuj książkę") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Wróć")
                    }
                }
            )
        }
    ) { padding ->
        if (bookState == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Sekcja okładki
                Text(
                    text = "Okładka:",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                localCoverPath?.let { path ->
                    val imageBitmap = remember(path) {
                        try {
                            BitmapFactory.decodeFile(path)?.asImageBitmap()
                        } catch (e: Exception) {
                            null
                        }
                    }

                    imageBitmap?.let { bitmap ->
                        Image(
                            bitmap = bitmap,
                            contentDescription = "Okładka książki",
                            modifier = Modifier
                                .size(150.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    }
                }

                Button(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text(localCoverPath?.let { "Zmień okładkę" } ?: "Dodaj okładkę")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Reszta formularza
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Tytuł") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("Autor") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

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
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded)
                        }
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

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Kategoria") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Opis") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        bookState?.let { originalBook ->
                            val updatedBook = originalBook.copy(
                                title = title,
                                author = author,
                                status = status,
                                category = category.ifEmpty { null },
                                description = description.ifEmpty { null },
                                rating = rating,
                                tags = tags,
                                localCoverPath = localCoverPath
                            )
                            viewModel.updateBook(updatedBook)
                            onBack()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                ) {
                    Text("Zapisz zmiany")
                }
            }
        }
    }
}