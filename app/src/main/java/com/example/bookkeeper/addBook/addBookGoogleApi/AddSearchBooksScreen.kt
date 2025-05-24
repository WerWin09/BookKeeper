package com.example.bookkeeper.addBook.addBookGoogleApi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bookkeeper.ui.theme.*
import com.example.bookkeeper.utils.mapGoogleBookToBookEntity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBooksScreen(
    navController: NavController,
    viewModel: SearchBooksViewModel = viewModel()
) {
    val results by viewModel.searchResults.collectAsStateWithLifecycle()
    val navigateToEdit by viewModel.navigateToEdit.collectAsStateWithLifecycle()

    val title by viewModel.titleQuery.collectAsStateWithLifecycle()
    val author by viewModel.authorQuery.collectAsStateWithLifecycle()
    val category by viewModel.categoryQuery.collectAsStateWithLifecycle()
    val publisher by viewModel.publisherQuery.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    val showMessage = navController
        .currentBackStackEntry
        ?.savedStateHandle
        ?.get<String>("showSnackbarMessage")

    LaunchedEffect(showMessage) {
        showMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            navController.currentBackStackEntry?.savedStateHandle?.remove<String>("showSnackbarMessage")
        }
    }

    LaunchedEffect(navigateToEdit) {
        if (navigateToEdit) {
            navController.navigate("editImportedBook")
            viewModel.onNavigatedToEditScreen()
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Wyszukaj książkę", color = Color.Black) },
                windowInsets = WindowInsets(0, 0, 0, 0),
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MainColor),
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate("userBooks") {
                            popUpTo("userBooks") { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Wróć", tint = Color.Black)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                FloatingActionButton(
                    onClick = { navController.navigate("manualAddBook") },
                    containerColor = MainColor,
                    contentColor = Color.Black
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Dodaj ręcznie")
                }

                FloatingActionButton(
                    onClick = { showDialog = true },
                    containerColor = MainColor,
                    contentColor = Color.Black
                ) {
                    Icon(Icons.Default.AddAPhoto, contentDescription = "Skanuj ISBN")
                }
            }
        }
    )  { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(padding)) {

            Column(modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)) {

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { viewModel.titleQuery.value = it },
                    placeholder = { Text("Tytuł", color = Color.White) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SecondBackgroundColor, shape = MaterialTheme.shapes.medium),
                    textStyle = TextStyle(color = Color.White),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        disabledBorderColor = Color.Transparent,
                        cursorColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = author,
                    onValueChange = { viewModel.authorQuery.value = it },
                    placeholder = { Text("Autor", color = Color.White) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SecondBackgroundColor, shape = MaterialTheme.shapes.medium),
                    textStyle = TextStyle(color = Color.White),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        disabledBorderColor = Color.Transparent,
                        cursorColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = category,
                    onValueChange = { viewModel.categoryQuery.value = it },
                    placeholder = { Text("Tematyka", color = Color.White) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SecondBackgroundColor, shape = MaterialTheme.shapes.medium),
                    textStyle = TextStyle(color = Color.White),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        disabledBorderColor = Color.Transparent,
                        cursorColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = publisher,
                    onValueChange = { viewModel.publisherQuery.value = it },
                    placeholder = { Text("Wydawnictwo", color = Color.White) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SecondBackgroundColor, shape = MaterialTheme.shapes.medium),
                    textStyle = TextStyle(color = Color.White),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        disabledBorderColor = Color.Transparent,
                        cursorColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(Modifier.height(10.dp))

                LaunchedEffect(title, author, category, publisher) {
                    val parts = mutableListOf<String>()
                    if (title.isNotBlank()) parts += "intitle:$title"
                    if (author.isNotBlank()) parts += "inauthor:$author"
                    if (category.isNotBlank()) parts += "subject:$category"
                    if (publisher.isNotBlank()) parts += "inpublisher:$publisher"

                    val q = parts.joinToString(" ")
                    viewModel.searchBooks(q)
                }

                LazyColumn {
                    items(results) { item ->
                        GoogleBookCard(item = item, onClick = {
                            val bookEntity = mapGoogleBookToBookEntity(item)
                            viewModel.setSelectedBookAndNavigate(bookEntity)
                        })
                    }
                }
            }
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

