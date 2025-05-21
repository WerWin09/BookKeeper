package com.example.bookkeeper.utils

import android.graphics.Bitmap

fun resizeBitmap(bitmap: Bitmap, maxSize: Int = 300): Bitmap {
    val width = bitmap.width
    val height = bitmap.height

    val ratio = width.toFloat() / height.toFloat()

    val (targetWidth, targetHeight) = if (ratio > 1) {
        maxSize to (maxSize / ratio).toInt()
    } else {
        (maxSize * ratio).toInt() to maxSize
    }

    return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
}
