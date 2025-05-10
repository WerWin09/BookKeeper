package com.example.bookkeeper.dataRoom

import android.content.Context
import android.util.Log
import com.example.bookkeeper.utils.hasInternet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
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

        val syncedBook = if (hasInternet(context)) {
            try {
                firestore.collection("books").add(bookWithUserId).await()
                bookWithUserId.copy(isSynced = true)
            } catch (e: Exception) {
                bookWithUserId
            }
        } else {
            bookWithUserId
        }
        db.bookDao().insertBook(syncedBook)


        if (hasInternet(context)) {
            try {
                firestore.collection("books").add(bookWithUserId).await()
            } catch (e: Exception) {
                // Jeśli synchronizacja się nie uda, książka i tak jest w lokalnej bazie
            }
        }
    }

    suspend fun getBookById(bookId: Int): BookEntity? {
        return db.bookDao().getBookById(bookId)
    }


    //synchronizacja baz po dodaniu z reki ksiazki w bazie room
    suspend fun syncUnsyncedBooks() {
        if (!hasInternet(context)) return

        val uid = auth.currentUser?.uid ?: return
        val unsyncedBooks = db.bookDao().getUnsyncedBooks(uid)
        for (book in unsyncedBooks) {
            try {
                firestore.collection("books").add(book).await()
                db.bookDao().markAsSynced(book.id)
            } catch (e: Exception) {

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

            val books = snapshot.mapNotNull { doc ->
                try {
                    val book = BookEntity(
                        title = doc.getString("title") ?: "",
                        author = doc.getString("author") ?: "",
                        status = doc.getString("status") ?: "",
                        category = doc.getString("category"),
                        description = doc.getString("description"),
                        rating = (doc.get("rating") as? Long)?.toInt(),
                        userId = doc.getString("userId") ?: "",
                        tags = (doc.get("tags") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                        isSynced = true
                    )
                    Log.d("FirestoreSync", "Załadowano książkę: ${doc.data}")
                    book
                } catch (e: Exception) {
                    Log.e("FirestoreSync", "Błąd przetwarzania dokumentu: ${doc.id}", e)
                    null
                }
            }

            db.bookDao().deleteBooksByUser(uid) // wyczyść stare dane
            db.bookDao().insertBooks(books)
            Log.d("FirestoreSync", "Zsynchronizowano ${books.size} książek z Firestore")
        } catch (e: Exception) {
            Log.e("FirestoreSync", "Błąd przy pobieraniu danych z Firestore", e)
        }
    }


}
