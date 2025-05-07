package com.example.bookkeeper.googleBooksApi

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response

//komunikowanie sie z API Google Books
interface GoogleBooksApiService {
    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String
    ): Response<GoogleBookResponse>
}
