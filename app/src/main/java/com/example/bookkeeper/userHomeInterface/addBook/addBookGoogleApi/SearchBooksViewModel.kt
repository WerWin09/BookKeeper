package com.example.bookkeeper.userHomeInterface.addBook.addBookGoogleApi

class SearchBooksViewModel : ViewModel() {
    private val _selectedBook = MutableStateFlow<BookEntity?>(null)
    val selectedBook: StateFlow<BookEntity?> = _selectedBook

    fun setSelectedBook(book: BookEntity) {
        _selectedBook.value = book
    }
}
