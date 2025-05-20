package com.example.bookkeeper

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class BookKeeperApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        FirebaseAuth.getInstance().signOut()

        // Globalny przechwyt wyjątków
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("AppCrash", "Nieprzechwycony wyjątek: ${throwable.localizedMessage}", throwable)
        }
    }
}
