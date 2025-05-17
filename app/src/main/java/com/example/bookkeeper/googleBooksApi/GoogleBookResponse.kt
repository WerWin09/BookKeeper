package com.example.bookkeeper.googleBooksApi

//model danych - odpowiada strukturze JSON z Google Books
data class GoogleBookResponse(
    val items: List<GoogleBookItem>?
)

data class GoogleBookItem(
    val volumeInfo: VolumeInfo
)

data class VolumeInfo(
    val title: String,
    val authors: List<String>?,
    val description: String?,
    val categories: List<String>?,
    val averageRating: Float?,
    val imageLinks: ImageLinks?
)

data class ImageLinks(
    val thumbnail: String?
)