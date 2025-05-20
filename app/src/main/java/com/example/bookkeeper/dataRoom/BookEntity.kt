package com.example.bookkeeper.dataRoom

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val remoteDocId: String? = null,
    val author: String,
    val status: String,
    val category: String?,
    val description: String?,
    val rating: Int?,
    val userId: String,
    val tags: List<String> = emptyList(),
    val isSynced: Boolean = false,

    // okladka
    val coverUrlRemote: String?, // Link do Firebase Storage
    var coverLocalPath: String?  // Ścieżka lokalna do pliku (cache offline)


)


fun BookEntity.toFirestoreMap(): Map<String, Any?> = mapOf(
    "title"          to title,
    "author"         to author,
    "status"         to status,
    "category"       to category,
    "description"    to description,
    "rating"         to rating,
    "userId"         to userId,
    "tags"           to tags,
    "coverUrlRemote" to coverUrlRemote,
    "isSynced"       to isSynced
)
