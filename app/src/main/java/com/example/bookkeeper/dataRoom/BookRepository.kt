package com.example.bookkeeper.dataRoom

import android.content.Context
import com.example.bookkeeper.userHomeInterface.data.Book
import com.example.bookkeeper.utils.hasInternet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class BookRepository(
    private val context: Context,
    private val db: BookDatabase,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    fun getBooksFlow(): Flow<List<BookEntity>> {
        val uid = auth.currentUser?.uid ?: ""
        return db.bookDao().getBooksByUser(uid)
    }

    suspend fun syncBooksFromFirebase() {
        if (!hasInternet(context)) return

        val uid = auth.currentUser?.uid ?: return

        val snapshot = firestore.collection("books")
            .whereEqualTo("userId", uid)
            .get()
            .await()

        val books = snapshot.map { doc ->
            BookEntity(
                title = doc.getString("title") ?: "",
                author = doc.getString("author") ?: "",
                status = doc.getString("status") ?: "",
                category = doc.getString("category"),
                description = doc.getString("description"),
                rating = doc.getLong("rating")?.toInt(),
                userId = uid,
                tags = doc.get("tags") as? List<String> ?: emptyList()
            )
        }

        db.bookDao().deleteBooksByUser(uid) // czyść stare dane
        db.bookDao().insertBooks(books)
    }
}
