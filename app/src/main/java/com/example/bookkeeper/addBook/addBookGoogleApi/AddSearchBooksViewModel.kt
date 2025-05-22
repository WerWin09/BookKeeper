package com.example.bookkeeper.addBook.addBookGoogleApi

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookkeeper.dataRoom.BookEntity
import com.example.bookkeeper.googleBooksApi.GoogleBookItem
import com.example.bookkeeper.googleBooksApi.GoogleBooksApi
import com.example.bookkeeper.utils.mapGoogleBookToBookEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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

    val titleQuery = MutableStateFlow("")
    val authorQuery = MutableStateFlow("")
    val categoryQuery = MutableStateFlow("")
    val publisherQuery = MutableStateFlow("")

    private val api: GoogleBooksApi = Retrofit.Builder()
        .baseUrl("https://www.googleapis.com/books/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(GoogleBooksApi::class.java)

    fun searchBooks(
        query: String,
        onFound: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            try {
                // Jeśli to jest czysty ISBN — traktuj jako ISBN
                val isIsbnOnly = query.matches(Regex("""^\d{10,13}$"""))

                val searchQuery = if (isIsbnOnly) {
                    "isbn:$query"
                } else {
                    // Wyszukiwanie zaawansowane: rozbij na pola
                    val parts = mutableListOf<String>()

                    if (titleQuery.value.isNotBlank())    parts += "intitle:${titleQuery.value}"
                    if (authorQuery.value.isNotBlank())   parts += "inauthor:${authorQuery.value}"
                    if (categoryQuery.value.isNotBlank()) parts += "subject:${categoryQuery.value}"
                    if (publisherQuery.value.isNotBlank())parts += "inpublisher:${publisherQuery.value}"

                    parts.joinToString(" ")
                }

                Log.d("SearchBooksVM", "Wyszukiwanie książki po: $searchQuery")

                val response = api.searchBooks(searchQuery)

                Log.d("SearchBooksVM", "Odpowiedź zawiera ${response.items?.size ?: 0} książek")

                _searchResults.value = response.items ?: emptyList()

                if (isIsbnOnly) {
                    val firstItem = response.items?.firstOrNull()
                    if (firstItem != null) {
                        Log.d("SearchBooksVM", "Ustawiam selectedBook i navigateToEdit")
                        _selectedBook.value = mapGoogleBookToBookEntity(firstItem)
                        _navigateToEdit.value = true
                        onFound?.invoke()
                    } else {
                        Log.d("SearchBooksVM", "Brak wyników dla ISBN, selectedBook nie ustawiony")
                    }
                }

            } catch (e: Exception) {
                Log.e("SearchBooksVM", "Błąd podczas wyszukiwania książki: ${e.message}", e)
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

    fun clearFields() {
        titleQuery.value = ""
        authorQuery.value = ""
        publisherQuery.value = ""
        categoryQuery.value = ""
        _searchResults.value = emptyList()
    }

    fun clearSelectedBook() {
        _selectedBook.value = null
    }
}
