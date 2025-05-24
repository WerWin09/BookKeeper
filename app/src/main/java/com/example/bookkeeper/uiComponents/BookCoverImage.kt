package com.example.bookkeeper.uiComponents

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import coil.compose.AsyncImage
import java.io.File

@Composable
fun BookCoverImage(
    coverLocalPath: String?,
    coverUrlRemote: String?,
    modifier: Modifier = Modifier
) {
    when {
        coverLocalPath != null -> {
            val file = File(coverLocalPath)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Okładka książki",
                        modifier = modifier
                    )
                    return
                }
            }
        }
        coverUrlRemote != null -> {
            AsyncImage(
                model = coverUrlRemote,
                contentDescription = "Okładka książki",
                modifier = modifier
            )
        }
        else -> {
            Icon(
                imageVector = Icons.Default.Book,
                contentDescription = "Brak okładki",
                modifier = modifier
            )
        }
    }
}
