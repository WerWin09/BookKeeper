package com.example.bookkeeper

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp

class BookKeeperApp : Application() {
    override fun onCreate() {
        super.onCreate()


        // Globalny przechwyt wyjątków
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("BookKeeper_DEBUG", "Nieprzechwycony wyjątek: ${throwable.localizedMessage}", throwable)
        }
    }
}
