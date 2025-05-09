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
import androidx.navigation.NavController
import com.example.bookkeeper.utils.await
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanIsbnScreen(
    navController: NavController,
    source: String,
    input: String,
    onIsbnFound: (String) -> Unit,
    onBackToCaller: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var showNoResultDialog by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri == null) {
            navigateBack(navController)
        } else {
            imageUri = uri
            val bmp = BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))
            bmp?.let {
                bitmap = it
                runOcrAndExtractIsbn(
                    it, scope,
                    onIsbnFound = { foundIsbn ->
                        onIsbnFound(foundIsbn)
                        navController.navigate("editImportedBook") {
                            popUpTo("searchBooks") { inclusive = false }
                        }
                    },
                    onNotFound = {
                        showNoResultDialog = true
                        isProcessing = false
                    }
                )
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (!success) {
            navigateBack(navController)
        } else if (imageUri != null) {
            val bmp = BitmapFactory.decodeStream(context.contentResolver.openInputStream(imageUri!!))
            bmp?.let {
                bitmap = it
                runOcrAndExtractIsbn(
                    it, scope,
                    onIsbnFound = { foundIsbn ->
                        onIsbnFound(foundIsbn)
                        navController.navigate("editImportedBook") {
                            popUpTo("searchBooks") { inclusive = false }
                        }
                    },
                    onNotFound = {
                        showNoResultDialog = true
                        isProcessing = false
                    }
                )
            }
        }
    }

    fun createImageUri(): Uri? {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_$timestamp.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }
        return context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    }

    LaunchedEffect(Unit) {
        if (input == "camera") {
            imageUri = createImageUri()
            imageUri?.let { cameraLauncher.launch(it) } ?: navigateBack(navController)
        } else {
            galleryLauncher.launch("image/*")
        }
    }

    BackHandler {
        navigateBack(navController)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Zeskanuj ISBN") },
                navigationIcon = {
                    IconButton(onClick = { navigateBack(navController) }) {
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
                            navigateBack(navController)
                        }) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
}

private fun navigateBack(navController: NavController) {
    navController.navigate("manualAddBook") {
        popUpTo("manualAddBook") { inclusive = true }
    }
}

fun runOcrAndExtractIsbn(
    bmp: Bitmap,
    scope: CoroutineScope,
    onIsbnFound: (String) -> Unit,
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
                    onIsbnFound(isbn)
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
