package com.example.bookkeeper.addBook.addBookGoogleApi

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bookkeeper.utils.await
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import android.Manifest
import android.content.Intent
import android.provider.Settings
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.core.app.ActivityCompat
import android.os.Build


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanIsbnScreen(
    navController: NavController,
    source: String,
    input: String,
    onBackToCaller: () -> Unit,
    searchViewModel: SearchBooksViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var showNoResultDialog by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }

    // Launchery
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri == null) {
            onBackToCaller()
        } else {
            val bmp = BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))
            if (bmp != null) {
                bitmap = bmp
                isProcessing = true
                runOcrAndExtractIsbn(bmp, scope, searchViewModel) {
                    showNoResultDialog = true
                    isProcessing = false
                }
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (!success) {
            onBackToCaller()
        } else {
            imageUri?.let { uri ->
                val bmp = BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))
                if (bmp != null) {
                    bitmap = bmp
                    isProcessing = true
                    runOcrAndExtractIsbn(bmp, scope, searchViewModel) {
                        showNoResultDialog = true
                        isProcessing = false
                    }
                }
            }
        }
    }

    // Funkcja pomocnicza do tworzenia URI
    fun createImageUri(): Uri? {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_$timestamp.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }
        return context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    }

    // Funkcja uruchamiająca aparat lub galerię
    fun proceedWithImageInput() {
        if (input == "camera") {
            imageUri = createImageUri()
            imageUri?.let { cameraLauncher.launch(it) } ?: onBackToCaller()
        } else {
            galleryLauncher.launch("image/*")
        }
    }

    // Prośba o uprawnienia – tylko jeśli nie są już nadane
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            proceedWithImageInput()
        } else {
            showPermissionDialog = true
        }
    }

    val requiredPermissions = when (input) {
        "camera" -> arrayOf(Manifest.permission.CAMERA)
        "gallery" -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
        else -> emptyArray()
    }

    LaunchedEffect(Unit) {
        val allGranted = requiredPermissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

        if (allGranted) {
            proceedWithImageInput()
        } else {
            permissionLauncher.launch(requiredPermissions)
        }
    }


    BackHandler {
        onBackToCaller()
    }

    // Nawigacja po znalezieniu książki
    val navigateToEdit by searchViewModel.navigateToEdit.collectAsState()
    LaunchedEffect(navigateToEdit) {
        if (navigateToEdit) {
            navController.navigate("editImportedBook")
            searchViewModel.onNavigatedToEditScreen()
        }
    }

    // UI
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Zeskanuj ISBN") },
                navigationIcon = {
                    IconButton(onClick = onBackToCaller) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Cofnij")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Zrób zdjęcie lub wybierz z galerii, aby zeskanować ISBN")

            bitmap?.let {
                Image(bitmap = it.asImageBitmap(), contentDescription = "Wybrane zdjęcie")
            }

            if (isProcessing) {
                CircularProgressIndicator()
            }

            if (showNoResultDialog) {
                AlertDialog(
                    onDismissRequest = { showNoResultDialog = false },
                    title = { Text("Nie znaleziono książki") },
                    text = { Text("Nie udało się znaleźć książki z tym ISBN. Spróbuj ponownie z innym zdjęciem.") },
                    confirmButton = {
                        TextButton(onClick = {
                            showNoResultDialog = false
                            onBackToCaller()
                        }) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }

    // Dialog z przekierowaniem do ustawień aplikacji
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Brak uprawnień") },
            text = { Text("Aby korzystać z tej funkcji, nadaj uprawnienia w ustawieniach systemowych.") },
            confirmButton = {
                TextButton(onClick = {
                    showPermissionDialog = false
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }) {
                    Text("Przejdź do ustawień")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPermissionDialog = false
                    onBackToCaller()
                }) {
                    Text("Anuluj")
                }
            }
        )
    }
}



fun runOcrAndExtractIsbn(
    bmp: Bitmap,
    scope: CoroutineScope,
    searchViewModel: SearchBooksViewModel,
    onNotFound: () -> Unit
) {
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    val image = InputImage.fromBitmap(bmp, 0)

    scope.launch(Dispatchers.IO) {
        try {
            val result = recognizer.process(image).await()
            val rawText = result.text
            Log.d("ScanIsbn", "OCR Text: $rawText")

            val isbnRegex = Regex("""97[89][\d\- ]{10,}""")
            val match = isbnRegex.find(rawText)
            val isbn = match?.value?.replace(Regex("""[^\dX]"""), "")

            if (isbn != null) {
                Log.d("ScanIsbn", "ISBN znaleziony: $isbn")
                withContext(Dispatchers.Main) {
                    searchViewModel.searchBooks(isbn)
                }
            } else {
                Log.d("ScanIsbn", "Nie znaleziono ISBN")
                withContext(Dispatchers.Main) {
                    onNotFound()
                }
            }
        } catch (e: Exception) {
            Log.e("ScanIsbn", "Błąd OCR", e)
            withContext(Dispatchers.Main) {
                onNotFound()
            }
        }
    }


}



