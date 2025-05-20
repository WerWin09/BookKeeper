package com.example.bookkeeper.dataRoom

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.bookkeeper.userHomeInterface.data.saveToLocalCache
import com.example.bookkeeper.utils.hasInternet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.IOException


class BookRepository (
    private val context: Context,
    private val db: BookDatabase,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val storage: FirebaseStorage = Firebase.storage
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
        Log.d("BookKeeper_DEBUG", "Pobrane tagi: $tags")
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

    suspend fun getAuthor(): List<String> {
        val uid = auth.currentUser?.uid ?: return emptyList()
        return db.bookDao().getAuthor(uid)
    }

    fun getBooksByAuthor(author: String): Flow<List<BookEntity>> {
        val uid = auth.currentUser?.uid ?: return flowOf(emptyList())
        return db.bookDao().getBooksByAuthor(uid, author)
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
                        id           = 0,
                        remoteDocId  = doc.id,
                        title        = doc.getString("title") ?: "",
                        author       = doc.getString("author") ?: "",
                        status       = doc.getString("status") ?: "",
                        category     = doc.getString("category"),
                        description  = doc.getString("description"),
                        rating       = (doc.get("rating") as? Long)?.toInt(),
                        userId       = uid,
                        tags         = (doc.get("tags") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                        coverUrlRemote = doc.getString("coverUrlRemote"),
                        coverLocalPath = null,
                        isSynced     = true
                    )

                } catch (e: Exception) {
                    Log.e("FirestoreSync", "Error processing doc=${doc.id}", e)
                    null
                }
            }

            for (book in books) {
                if (book.coverUrlRemote != null && book.coverLocalPath == null) {
                    val file = File(context.cacheDir, "${book.remoteDocId}.jpg")
                    if (!file.exists()) {
                        try {
                            val url = URL(book.coverUrlRemote)
                            url.openStream().use { input ->
                                FileOutputStream(file).use { output ->
                                    input.copyTo(output)
                                }
                            }
                            book.coverLocalPath = file.absolutePath
                        } catch (e: Exception) {
                            Log.e("DownloadCover", "Nie udało się pobrać okładki dla ${book.title}", e)
                        }
                    } else {
                        book.coverLocalPath = file.absolutePath
                    }
                }
            }


            // 1) Wyczyść stare dane lokalne
            db.bookDao().deleteBooksByUser(uid)
            // 2) Wstaw nowe, zsynchronizowane
            db.bookDao().insertBooks(books)
            Log.d("BookKeeper_DEBUG", "Synchronized ${books.size} books from Firebase")
        } catch (e: Exception) {
            Log.e("BookKeeper_DEBUG", "Error fetching from Firebase", e)
        }
    }

    //okladki
    suspend fun uploadCoverToFirebaseStorage(docId: String, imageUri: Uri): String {
        val ref = storage.reference.child("book_covers/$docId.jpg")

        val localPath = saveToLocalCache(context, docId, imageUri)
        Log.d("BookKeeper_DEBUG", "Ścieżka lokalna pliku do uploadu: $localPath")

        if (localPath.isNotEmpty()) {
            ref.putFile(imageUri).await()
            return ref.downloadUrl.await().toString()
        } else {
            Log.e("BookKeeper_DEBUG", "Nie udało się zapisać okładki lokalnie – przerwano upload.")
            throw IOException("Błąd lokalnego zapisu okładki")
        }
    }





    suspend fun saveBookWithCover(book: BookEntity, imageUri: Uri?) {
        val uid = auth.currentUser?.uid ?: return
        val docId = book.remoteDocId ?: firestore.collection("books").document().id

        val updatedBook = if (imageUri != null) {
            Log.d("BookKeeper_DEBUG", "URI do przesłania: $imageUri")

            val remoteUrl = uploadCoverToFirebaseStorage(docId, imageUri)
            val localPath = saveToLocalCache(context, docId, imageUri)

            Log.d("BookKeeper_DEBUG", "Zapisano okładkę lokalnie: $localPath")
            Log.d("BookKeeper_DEBUG", "Zapisano lokalnie do: $localPath")
            Log.d("BookKeeper_DEBUG", "Zdalny URL: $remoteUrl")


            book.copy(
                userId = uid,
                remoteDocId = docId,
                isSynced = true,
                coverUrlRemote = remoteUrl,
                coverLocalPath = localPath
            )
        } else {
            book.copy(
                userId = uid,
                remoteDocId = docId,
                isSynced = true
            )
        }

        saveToRoomAndFirestore(updatedBook)
    }




    private suspend fun saveToRoomAndFirestore(book: BookEntity) {
        val uid = auth.currentUser?.uid ?: return

        // Jeśli nie ma remoteDocId, generujemy nowe
        val docId = book.remoteDocId ?: firestore.collection("books").document().id

        // Tworzymy encję z userId i docId
        val updatedBook = book.copy(
            userId = uid,
            remoteDocId = docId,
            isSynced = true
        )

        // Zapis do Room
        db.bookDao().insertBook(updatedBook)

        // Zapis do Firestore
        firestore.collection("books")
            .document(docId)
            .set(updatedBook.toFirestoreMap())
    }





}
