package com.example.bookkeeper.userHomeInterface

import android.app.Application
import android.net.Uri
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

    private val _statuses = MutableStateFlow<List<String>>(emptyList())
    val statuses: StateFlow<List<String>> = _statuses

    private val _tags = MutableStateFlow<List<String>>(emptyList())
    val tags: StateFlow<List<String>> = _tags

    private val _author = MutableStateFlow<List<String>>(emptyList())
    val author: StateFlow<List<String>> = _author

    init {
        val database = BookDatabase.getDatabase(application)
        repository = BookRepository(application, database)

        loadInitialBooks()
        loadCategories()
        loadStatuses()
        loadTags()
        loadAuthor()

        viewModelScope.launch {
            repository.syncBooksFromFirebase()
            repository.syncUnsyncedBooks()
        }
    }

    private fun loadInitialBooks() {
        viewModelScope.launch {
            repository.getBooksFlow().collect {
                _books.value = it
            }
        }
    }

    private fun loadStatuses() {
        viewModelScope.launch {
            _statuses.value = repository.getStatuses()
        }
    }

    private fun loadTags() {
        viewModelScope.launch {
            _tags.value = repository.getTags()
        }
    }

    private fun loadAuthor() {
        viewModelScope.launch {
            _author.value = repository.getAuthor()
        }
    }

    fun filterByStatus(status: String) {
        viewModelScope.launch {
            repository.getBooksByStatus(status).collect { books ->
                _books.value = books
            }
        }
    }

    fun filterByTag(tag: String) {
        viewModelScope.launch {
            repository.getBooksByTag(tag).collect { books ->
                _books.value = books
            }
        }
    }

    fun filterByAuthor(author: String) {
        viewModelScope.launch {
            repository.getBooksByAuthor(author).collect { books ->
                _books.value = books
            }
        }
    }

    fun updateBook(book: BookEntity) {
        viewModelScope.launch {
            repository.updateBook(book)
        }
    }

    fun saveBookWithCover(book: BookEntity, imageUri: Uri?) {
        viewModelScope.launch {
            repository.saveBookWithCover(book, imageUri)
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
        viewModelScope.launch {
            repository.getBooksFlow().collect {
                _books.value = it
            }
        }
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

    fun deleteBook(book: BookEntity) {
        viewModelScope.launch {
            repository.deleteBook(book)
        }
    }



}