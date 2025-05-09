package com.example.bookkeeper.userHomeInterface

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookkeeper.dataRoom.BookRepository
import com.example.bookkeeper.dataRoom.BookDatabase
import com.example.bookkeeper.dataRoom.BookEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class UserBooksViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: BookRepository
    private val _books = MutableStateFlow<List<BookEntity>>(emptyList())
    val books: StateFlow<List<BookEntity>> = _books
    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories

    init {
        val database = BookDatabase.getDatabase(application)
        repository = BookRepository(application, database)

        loadBooks()
        loadCategories()
    }

    private fun loadBooks() {
        viewModelScope.launch {
            repository.getBooksFlow().collect { books ->
                _books.value = books
            }
        }
    }

    suspend fun getBookById(bookId: Int): BookEntity? {
        return repository.getBookById(bookId)
    }

    fun filterByCategory(category: String) {
        viewModelScope.launch {
            repository.getBooksByCategory(category).collect { books ->
                _books.value = books
            }
        }
    }

    fun searchBooks(query: String) {
        viewModelScope.launch {
            repository.searchBooks(query).collect { books ->
                _books.value = books
            }
        }
    }

    fun resetFilters() {
        loadBooks()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _categories.value = repository.getCategories()
        }
    }

    fun addBook(book: BookEntity) {
        viewModelScope.launch {
            repository.addBook(book)
        }
    }
}