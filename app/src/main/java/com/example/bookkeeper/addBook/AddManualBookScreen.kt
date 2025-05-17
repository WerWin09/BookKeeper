package com.example.bookkeeper.userHomeInterface

import android.graphics.Bitmap
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bookkeeper.dataRoom.BookEntity
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import com.example.bookkeeper.utils.Constants.statusOptions
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.asImageBitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.platform.LocalContext
import java.io.File


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualAddBookScreen(
    navController: NavController,
    onBackToHome: () -> Unit,
    onSearchOnline: () -> Unit,
    viewModel: UserBooksViewModel = viewModel()
) {
    var showDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf<Int?>(null) }
    var statusExpanded by remember { mutableStateOf(false) }
    var tags by remember { mutableStateOf("") }
    var localCoverPath by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val file = File(context.filesDir, "cover_${System.currentTimeMillis()}.jpg")
            file.outputStream().use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            localCoverPath = file.absolutePath
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dodaj książkę ręcznie") },
                navigationIcon = {
                    IconButton(onClick = onBackToHome) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Wróć")
                    }
                }
            )
        },
        floatingActionButton = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                FloatingActionButton(onClick = onSearchOnline) {
                    Icon(Icons.Default.Search, contentDescription = "Szukaj przez Google Books")
                }
                FloatingActionButton(onClick = { showDialog = true }) {
                    Icon(Icons.Default.AddAPhoto, contentDescription = "Skanuj ISBN")
                }
            }
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
                value = tags,
                onValueChange = { tags = it },
                label = { Text("Tagi (oddziel przecinkami)") },
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
                    rating = it.toIntOrNull()?.takeIf { it in 0..5 }
                },
                label = { Text("Ocena (0-5)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            Text(
                text = "Okładka:",
                style = MaterialTheme.typography.titleMedium
            )

            if (localCoverPath != null) {
                Image(
                    bitmap = BitmapFactory.decodeFile(localCoverPath).asImageBitmap(),
                    contentDescription = "Okładka książki",
                    modifier = Modifier
                        .size(150.dp)
                        .clickable { galleryLauncher.launch("image/*") }
                )
            } else {
                Button(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text("Dodaj okładkę")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onBackToHome,
                    modifier = Modifier.padding(end = 8.dp)
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
                            userId = "",
                            localCoverPath = localCoverPath,
                            tags = tags.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        )
                        viewModel.addBook(newBook)
                        onBackToHome()
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

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Wybierz źródło zdjęcia") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    navController.navigate("scanIsbn?source=manualAddBook&input=camera")
                }) {
                    Text("Aparat")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    navController.navigate("scanIsbn?source=manualAddBook&input=gallery")
                }) {
                    Text("Galeria")
                }
            }
        )
    }
}