package com.example.bookkeeper.userHomeInterface

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bookkeeper.dataRoom.BookEntity
import com.example.bookkeeper.utils.Constants.statusOptions
import androidx.compose.material.icons.filled.Photo
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import com.example.bookkeeper.ui.theme.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBookScreen(
    bookId: Int?,
    onBack: () -> Unit,
    viewModel: UserBooksViewModel
) {
    var bookState by remember { mutableStateOf<BookEntity?>(null) }
    var coverPath by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        imageUri = uri
    }

    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf<Int?>(null) }
    var tags by remember { mutableStateOf<List<String>>(emptyList()) }
    var statusExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        if (bookId != null) {
            val refreshedBook = viewModel.getBookById(bookId)
            Log.d("BookKeeper_DEBUG", "Załadowano książkę: ${refreshedBook?.title}, okładka: ${refreshedBook?.coverLocalPath}")
            bookState = refreshedBook
            coverPath = refreshedBook?.coverLocalPath
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
            coverPath = book.coverLocalPath
        }
    }

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
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text("Edytuj książkę", color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Wróć", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MainColor),
                windowInsets = WindowInsets(0, 0, 0, 0)
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
                    .background(BackgroundColor)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            )
                {
                    Text(
                        text = "Okładka:",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

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

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        OutlinedButton(
                            onClick = { galleryLauncher.launch("image/*") },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = BackgroundColor,
                                contentColor = MainColor
                            )
                        ) {
                            Text(
                                text = if (imageUri != null || !coverPath.isNullOrEmpty()) "Zmień okładkę" else "Dodaj okładkę"
                            )
                        }
                    }


                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Tytuł", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = SecondBackgroundColor,
                        unfocusedBorderColor = SecondBackgroundColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White,
                        containerColor = SecondBackgroundColor
                    )

                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("Autor", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = SecondBackgroundColor,
                        unfocusedBorderColor = SecondBackgroundColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White,
                        containerColor = SecondBackgroundColor
                    )

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
                        label = { Text("Status", color = Color.White) },
                        readOnly = true,
                        modifier = Modifier.menuAnchor(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = SecondBackgroundColor,
                            unfocusedBorderColor = SecondBackgroundColor,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = Color.White,
                            unfocusedLabelColor = Color.White,
                            containerColor = SecondBackgroundColor
                        ),
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
                    label = { Text("Kategoria", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = SecondBackgroundColor,
                        unfocusedBorderColor = SecondBackgroundColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White,
                        containerColor = SecondBackgroundColor
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Opis", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = SecondBackgroundColor,
                        unfocusedBorderColor = SecondBackgroundColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White,
                        containerColor = SecondBackgroundColor
                    ),
                    singleLine = false,
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val updatedBook = bookState!!.copy(
                            title = title,
                            author = author,
                            status = status,
                            category = category.ifEmpty { null },
                            description = description.ifEmpty { null },
                            rating = rating,
                            tags = tags
                        )

                        // ⬇⬇⬇ Tutaj dodaj log
                        Log.d("BookKeeper_DEBUG", "Zapisuję książkę z imageUri = $imageUri")

                        viewModel.saveBookWithCover(updatedBook, imageUri = imageUri)
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainColor,
                        contentColor = Color.Black,
                        disabledContainerColor = MainColor.copy(alpha = 0.5f),
                        disabledContentColor = Color.Black.copy(alpha = 0.5f)
                    ),
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
