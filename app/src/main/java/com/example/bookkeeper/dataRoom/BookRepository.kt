package com.example.bookkeeper.dataRoom

import android.content.Context
import android.util.Log
import com.example.bookkeeper.utils.hasInternet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await

class BookRepository (
    private val context: Context,
    private val db: BookDatabase,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    fun getBooksFlow(): Flow<List<BookEntity>> {
        val uid = auth.currentUser?.uid ?: ""
        return db.bookDao().getBooksByUser(uid)
    }

    fun getBooksByCategory(category: String): Flow<List<BookEntity>> {
        val uid = auth.currentUser?.uid ?: ""
        return db.bookDao().getBooksByCategory(uid, category)
    }

    fun searchBooks(query: String): Flow<List<BookEntity>> {
        val uid = auth.currentUser?.uid ?: ""
        return db.bookDao().searchBooks(uid, query)
    }

    suspend fun getCategories(): List<String> {
        val uid = auth.currentUser?.uid ?: return emptyList()
        return db.bookDao().getCategories(uid)
    }

    suspend fun addBook(book: BookEntity) {
        val uid = auth.currentUser?.uid ?: throw Exception("User not logged in")
        val bookWithUserId = book.copy(userId = uid)

        // 1) Dodajemy raz do Firestore (jeśli jest internet) i pobieramy documentId
        val finalBook = if (hasInternet(context)) {
            try {
                val docRef = firestore.collection("books")
                    .add(bookWithUserId)
                    .await()
                // zwracamy encję z remoteDocId i isSynced = true
                bookWithUserId.copy(
                    remoteDocId = docRef.id,
                    isSynced    = true
                )
            } catch (e: Exception) {
                // jeśli błąd – zapisujemy lokalnie bez remoteDocId
                bookWithUserId
            }
        } else {
            bookWithUserId
        }

        // 2) Wstawiamy do Room tylko raz, z ustawionym remoteDocId (albo bez)
        db.bookDao().insertBook(finalBook)
    }



    suspend fun getBookById(bookId: Int): BookEntity? {
        return db.bookDao().getBookById(bookId)
    }

    suspend fun getStatuses(): List<String> {
        val uid = auth.currentUser?.uid ?: return emptyList()
        return db.bookDao().getStatuses(uid)
    }

    suspend fun getTags(): List<String> {
        val uid = auth.currentUser?.uid ?: return emptyList()
        val tags = db.bookDao().getTags(uid)
        Log.d("BookRepository", "Pobrane tagi: $tags")
        return tags
    }

    fun getBooksByStatus(status: String): Flow<List<BookEntity>> {
        val uid = auth.currentUser?.uid ?: return flowOf(emptyList())
        return db.bookDao().getBooksByStatus(uid, status)
    }

    fun getBooksByTag(tag: String): Flow<List<BookEntity>> {
        val uid = auth.currentUser?.uid ?: return flowOf(emptyList())
        Log.d("BookRepository", "Wyszukuję książki z tagiem: $tag")
        return db.bookDao().getBooksByTag(uid, tag.trim())
    }

    suspend fun updateBook(book: BookEntity) {
        val uid = auth.currentUser?.uid ?: throw Exception("User not logged in")
        // Upewniamy się, że przekazujemy userId w encji
        val updatedBook = book.copy(userId = uid)

        // 1) Jeśli mamy internet i znamy remoteDocId, zaktualizuj dokument w Firestore
        if (hasInternet(context) && !book.remoteDocId.isNullOrBlank()) {
            try {
                firestore.collection("books")
                    .document(book.remoteDocId!!)
                    .set(updatedBook)
                    .await()  // czekamy na zakończenie
            } catch (e: Exception) {
                Log.e("BookRepository", "Error updating book in Firestore", e)
            }
        }

        // 2) Zawsze wstawiamy zaktualizowaną encję do Room
        db.bookDao().insertBook(updatedBook)
    }


    suspend fun deleteBook(book: BookEntity) {
        val uid = auth.currentUser?.uid ?: throw Exception("User not logged in")

        // 1) jeśli mamy Internet i zapisane remoteDocId, usuwamy bezpośrednio
        if (hasInternet(context) && !book.remoteDocId.isNullOrBlank()) {
            try {
                firestore.collection("books")
                    .document(book.remoteDocId!!)
                    .delete()
                    .await()
            } catch (e: Exception) {
                Log.e("BookRepository", "Error deleting from Firestore", e)
            }
        }

        // 2) zawsze usuwamy lokalnie
        db.bookDao().deleteBook(book)
    }



    //synchronizacja baz po dodaniu z reki ksiazki w bazie room
    suspend fun syncUnsyncedBooks() {
        if (!hasInternet(context)) return

        val uid = auth.currentUser?.uid ?: return
        val unsyncedBooks = db.bookDao().getUnsyncedBooks(uid)

        for (book in unsyncedBooks) {
            try {
                // 1) dodajemy do Firestore i pobieramy documentId
                val docRef = firestore.collection("books")
                    .add(book.copy(userId = uid))
                    .await()

                // 2) oznaczamy w Room jako zsynchronizowane z tym remoteId
                db.bookDao().markAsSynced(book.id, docRef.id)
            } catch (e: Exception) {
                Log.e("BookRepository", "Error syncing book id=${book.id}", e)
            }
        }
    }



    suspend fun syncBooksFromFirebase() {
        if (!hasInternet(context)) return

        val uid = auth.currentUser?.uid ?: return

        try {
            val snapshot = firestore.collection("books")
                .whereEqualTo("userId", uid)
                .get()
                .await()

            // Mapujemy każde doc na encję z remoteDocId
            val books = snapshot.mapNotNull { doc ->
                try {
                    BookEntity(
                        id           = 0,                 // autogenerowane w Room
                        remoteDocId  = doc.id,            // klucz Firestore
                        title        = doc.getString("title") ?: "",
                        author       = doc.getString("author") ?: "",
                        status       = doc.getString("status") ?: "",
                        category     = doc.getString("category"),
                        description  = doc.getString("description"),
                        rating       = (doc.get("rating") as? Long)?.toInt(),
                        userId       = uid,
                        tags         = (doc.get("tags") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                        isSynced     = true
                    )
                } catch (e: Exception) {
                    Log.e("FirestoreSync", "Error processing doc=${doc.id}", e)
                    null
                }
            }

            // 1) Wyczyść stare dane lokalne
            db.bookDao().deleteBooksByUser(uid)
            // 2) Wstaw nowe, zsynchronizowane
            db.bookDao().insertBooks(books)
            Log.d("FirestoreSync", "Synchronized ${books.size} books from Firebase")
        } catch (e: Exception) {
            Log.e("FirestoreSync", "Error fetching from Firebase", e)
        }
    }



}
