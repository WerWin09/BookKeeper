package com.example.bookkeeper.userHomeInterface

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Photo
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

import androidx.compose.material.icons.filled.Photo
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.delay
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import com.example.bookkeeper.ui.theme.BackgroundColor
import com.example.bookkeeper.ui.theme.MainColor


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualAddBookScreen(
    navController: NavController,
    onBackToHome: () -> Unit,
    onSearchOnline: () -> Unit,
    viewModel: UserBooksViewModel = viewModel()
) {
    var coverPath by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        imageUri = uri
    }

    var showDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf<Int?>(null) }
    var statusExpanded by remember { mutableStateOf(false) }
    var tags by remember { mutableStateOf("") }

    val displayBitmap = remember(coverPath to imageUri) {
        when {
            imageUri != null -> {
                try {
                    val inputStream = context.contentResolver.openInputStream(imageUri!!)
                    android.graphics.BitmapFactory.decodeStream(inputStream)?.asImageBitmap()
                } catch (e: Exception) {
                    null
                }
            }
            !coverPath.isNullOrEmpty() -> {
                try {
                    android.graphics.BitmapFactory.decodeFile(coverPath)?.asImageBitmap()
                } catch (e: Exception) {
                    null
                }
            }
            else -> null
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
            Text(text = "Okładka:", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .size(150.dp)
                    .align(Alignment.CenterHorizontally)
                    .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                if (displayBitmap != null) {
                    Image(
                        bitmap = displayBitmap,
                        contentDescription = "Okładka książki",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Photo,
                        contentDescription = "Brak okładki",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Button(
                onClick = { galleryLauncher.launch("image/*") },
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text("Dodaj okładkę")
            }

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
                            tags = tags.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                            coverUrlRemote = null,
                            coverLocalPath = null

                        )

                        Log.d("BookKeeper_DEBUG", "Dodaję książkę z imageUri = $imageUri")

                        viewModel.saveBookWithCover(newBook, imageUri = imageUri)
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
        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = BackgroundColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Wybierz źródło zdjęcia",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                showDialog = false
                                navController.navigate("scanIsbn?source=manualAddBook&input=camera")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MainColor,
                                contentColor = Color.Black
                            )
                        ) {
                            Text("Aparat")
                        }

                        Button(
                            onClick = {
                                showDialog = false
                                navController.navigate("scanIsbn?source=manualAddBook&input=gallery")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MainColor,
                                contentColor = Color.Black
                            )
                        ) {
                            Text("Galeria")
                        }
                    }
                }

            }
        }
    }
}