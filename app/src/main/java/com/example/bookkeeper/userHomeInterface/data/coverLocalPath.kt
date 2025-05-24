package com.example.bookkeeper.userHomeInterface.data

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.example.bookkeeper.utils.resizeBitmap
import java.io.File
import java.io.FileOutputStream

fun saveToLocalCache(context: Context, docId: String, uri: Uri): String {
    val inputStream = context.contentResolver.openInputStream(uri)
    val originalBitmap = android.graphics.BitmapFactory.decodeStream(inputStream)

    val resizedBitmap = resizeBitmap(originalBitmap ?: return "")

    val file = File(context.cacheDir, "$docId.jpg")
    FileOutputStream(file).use { output ->
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, output) // kompresja jako JPEG z jakością 85%
    }

    Log.d("BookKeeper_DEBUG", "Zapisano przeskalowaną okładkę: ${file.absolutePath}")
    return file.absolutePath
}

