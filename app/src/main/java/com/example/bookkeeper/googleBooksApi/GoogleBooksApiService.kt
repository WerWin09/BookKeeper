package com.example.bookkeeper.googleBooksApi

import com.example.bookkeeper.addBook.addBookGoogleApi.GoogleBooksResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBooksApi {
    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String
    ): GoogleBooksResponse
}
