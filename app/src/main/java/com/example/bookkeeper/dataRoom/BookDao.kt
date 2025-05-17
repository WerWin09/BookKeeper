package com.example.bookkeeper.dataRoom

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    /** Pobranie wszystkich książek użytkownika */
    @Query("SELECT * FROM books WHERE userId = :userId")
    fun getBooksByUser(userId: String): Flow<List<BookEntity>>

    /** Filtrowanie po kategorii */
    @Query("SELECT * FROM books WHERE userId = :userId AND category LIKE :category")
    fun getBooksByCategory(userId: String, category: String): Flow<List<BookEntity>>

    /** Wyszukiwanie w tytule lub autorze */
    @Query("""
        SELECT * FROM books 
        WHERE userId = :userId 
          AND (title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%')
    """)
    fun searchBooks(userId: String, query: String): Flow<List<BookEntity>>

    /** Unikalne kategorie */
    @Query("SELECT DISTINCT category FROM books WHERE userId = :userId AND category IS NOT NULL")
    suspend fun getCategories(userId: String): List<String>

    /** Wstawienie wielu książek (np. przy syncBooksFromFirebase) */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<BookEntity>)

    /** Pobranie po lokalnym ID */
    @Query("SELECT * FROM books WHERE id = :bookId")
    suspend fun getBookById(bookId: Int): BookEntity?

    /** Usunięcie wszystkich książek danego użytkownika */
    @Query("DELETE FROM books WHERE userId = :userId")
    suspend fun deleteBooksByUser(userId: String)

    /** Unikalne statusy */
    @Query("SELECT DISTINCT status FROM books WHERE userId = :userId")
    suspend fun getStatuses(userId: String): List<String>

    // ─── TAGS ─────────────────────────────────────────────────────────────────────

    /**
     * Pobiera z bazy surowe wartości pola `tags` (lista w formacie JSON/string).
     * W kodzie Kotlin możesz je rozbić na pojedyncze tagi.
     */
    @Query("SELECT DISTINCT tags FROM books WHERE userId = :userId AND tags IS NOT NULL")
    suspend fun getTags(userId: String): List<String>

    /**
     * Proste wyszukiwanie książek po tagu za pomocą LIKE.
     * Uwaga: może dawać częściowe dopasowania (np. "art" → "Cartoon").
     */
    @Query("SELECT * FROM books WHERE userId = :userId AND tags LIKE '%' || :tag || '%'")
    fun getBooksByTag(userId: String, tag: String): Flow<List<BookEntity>>

    // ─── AUTHORS ─────────────────────────────────────────────────────────────────────

    /** Zwraca unikalne nazwiska autorów dla danego użytkownika */
    @Query("SELECT DISTINCT author FROM books WHERE userId = :userId AND author IS NOT NULL")
    suspend fun getAuthor(userId: String): List<String>

    /** Zwraca książki danego użytkownika, których autor pasuje do zapytania */
    @Query("SELECT * FROM books WHERE userId = :userId AND author LIKE :author")
    fun getBooksByAuthor(userId: String, author: String): Flow<List<BookEntity>>


    // ─── STATUS ────────────────────────────────────────────────────────────────────

    /** Filtrowanie po statusie */
    @Query("SELECT * FROM books WHERE userId = :userId AND status LIKE :status")
    fun getBooksByStatus(userId: String, status: String): Flow<List<BookEntity>>

    // ─── CRUD ──────────────────────────────────────────────────────────────────────

    /** Wstaw pojedynczą książkę, nadpisując istniejącą o tym samym kluczu */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity)

    /** Usuń pojedynczą książkę */
    @Delete
    suspend fun deleteBook(book: BookEntity)

    // ─── SYNC ──────────────────────────────────────────────────────────────────────

    /** Pobierz lokalnie niesynchronizowane wpisy */
    @Query("SELECT * FROM books WHERE userId = :uid AND isSynced = 0")
    suspend fun getUnsyncedBooks(uid: String): List<BookEntity>

    /**
     * Oznacz książkę jako zsynchronizowaną i zapisz w niej identyfikator dokumentu Firestore
     * @param bookId   lokalne id wpisu w Room
     * @param remoteId dokumentId z Firestore
     */
    @Query("""
        UPDATE books 
        SET remoteDocId = :remoteId,
            isSynced     = 1
        WHERE id = :bookId
    """)
    suspend fun markAsSynced(bookId: Int, remoteId: String)
}
