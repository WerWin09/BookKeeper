package com.example.bookkeeper.addBook.addBookGoogleApi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookkeeper.dataRoom.BookEntity
import com.example.bookkeeper.googleBooksApi.GoogleBookItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Retrofit interfejs do Google Books API
interface GoogleBooksApi {
    @GET("volumes")
    suspend fun searchBooks(@Query("q") query: String): GoogleBooksResponse
}

// Odpowiedź z Google Books API
data class GoogleBooksResponse(
    val items: List<GoogleBookItem>?
)

class SearchBooksViewModel : ViewModel() {

    private val _searchResults = MutableStateFlow<List<GoogleBookItem>>(emptyList())
    val searchResults: StateFlow<List<GoogleBookItem>> = _searchResults

    private val _selectedBook = MutableStateFlow<BookEntity?>(null)
    val selectedBook: StateFlow<BookEntity?> = _selectedBook

    private val _navigateToEdit = MutableStateFlow(false)
    val navigateToEdit: StateFlow<Boolean> = _navigateToEdit

    private val api: GoogleBooksApi = Retrofit.Builder()
        .baseUrl("https://www.googleapis.com/books/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(GoogleBooksApi::class.java)

    fun searchBooks(query: String) {
        viewModelScope.launch {
            try {
                val response = api.searchBooks(query)
                _searchResults.value = response.items ?: emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
                _searchResults.value = emptyList()
            }
        }
    }

    fun setSelectedBookAndNavigate(book: BookEntity) {
        _selectedBook.value = book
        _navigateToEdit.value = true
    }

    fun onNavigatedToEditScreen() {
        _navigateToEdit.value = false
    }
}