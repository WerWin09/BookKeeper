package com.example.bookkeeper.userHomeInterface

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookkeeper.dataRoom.BookDatabase
import com.example.bookkeeper.dataRoom.BookEntity
import com.example.bookkeeper.dataRoom.BookRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserBooksViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BookRepository
    val books: StateFlow<List<BookEntity>>

    fun addBook(book: BookEntity) {
        viewModelScope.launch {
            try {
                repository.addBook(book)
            } catch (e: Exception) {
                // Tutaj możesz dodać obsługę błędów (np. pokazać snackbar)
            }
        }
    }

    init {
        val database = BookDatabase.getDatabase(application)
        repository = BookRepository(application, database)

        // lokalna baza jako główne źródło
        books = repository.getBooksFlow().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // synchronizacja z Firebase, jeśli jest internet
        viewModelScope.launch {
            repository.syncBooksFromFirebase()
        }
    }
}
