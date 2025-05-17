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
    val coverUrl: String? = null,
    val localCoverPath: String? = null
)
