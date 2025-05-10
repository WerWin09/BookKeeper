package com.example.bookkeeper.dataRoom

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("SELECT * FROM books WHERE userId = :userId")
    fun getBooksByUser(userId: String): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE userId = :userId AND category LIKE :category")
    fun getBooksByCategory(userId: String, category: String): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE userId = :userId AND title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%'")
    fun searchBooks(userId: String, query: String): Flow<List<BookEntity>>

    @Query("SELECT DISTINCT category FROM books WHERE userId = :userId AND category IS NOT NULL")
    suspend fun getCategories(userId: String): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<BookEntity>)

    @Query("SELECT * FROM books WHERE id = :bookId")
    suspend fun getBookById(bookId: Int): BookEntity?

    @Query("DELETE FROM books WHERE userId = :userId")
    suspend fun deleteBooksByUser(userId: String)

    @Insert
    suspend fun insertBook(book: BookEntity)

    //synchronizacja baz po dodaniu z reki ksiazki w bazie room
    @Query("SELECT * FROM books WHERE userId = :uid AND isSynced = 0")
    suspend fun getUnsyncedBooks(uid: String): List<BookEntity>
    @Query("UPDATE books SET isSynced = 1 WHERE id = :bookId")
    suspend fun markAsSynced(bookId: Int)
}
