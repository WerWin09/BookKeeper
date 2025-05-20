package com.example.bookkeeper.userHomeInterface.data

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.IOException

fun saveToLocalCache(context: Context, docId: String, uri: Uri): String {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IOException("Nie można otworzyć inputStream dla URI: $uri")

        val file = File(context.cacheDir, "$docId.jpg")

        inputStream.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        Log.d("BookKeeper_DEBUG", "Zapisano plik: ${file.absolutePath}")
        file.absolutePath

    } catch (e: Exception) {
        Log.e("BookKeeper_DEBUG", "Błąd zapisu do cache: ${e.message}", e)
        ""
    }
}
