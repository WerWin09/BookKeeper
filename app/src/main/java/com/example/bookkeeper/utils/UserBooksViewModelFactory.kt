package com.example.bookkeeper.utils

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bookkeeper.userHomeInterface.UserBooksViewModel

class UserBooksViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserBooksViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserBooksViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}