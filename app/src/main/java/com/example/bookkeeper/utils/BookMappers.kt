package com.example.bookkeeper.utils

import com.example.bookkeeper.dataRoom.BookEntity
import com.example.bookkeeper.googleBooksApi.GoogleBookItem

// konwertowanie danych z formatu Google Books do formatu lokalnego w BookEntity
fun mapGoogleBookToBookEntity(item: GoogleBookItem): BookEntity {
    val volume = item.volumeInfo

    return BookEntity(
        title = volume.title,
        author = volume.authors?.joinToString(", ") ?: "Nieznany autor",
        status = "Planowana",
        category = volume.categories?.firstOrNull(),
        description = volume.description,
        rating = volume.averageRating?.toInt(),
        userId = "",
        isSynced = false
    )
}
