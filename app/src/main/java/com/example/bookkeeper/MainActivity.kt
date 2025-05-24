package com.example.bookkeeper

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.bookkeeper.ui.theme.BookKeeperTheme
import com.example.bookkeeper.userAccount.AuthScreen
import com.example.bookkeeper.userHomeInterface.UserHomeActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            // Użytkownik jest już zalogowany, przejdź do UserHomeActivity
            startActivity(Intent(this, UserHomeActivity::class.java))
            finish()
        } else {
            // Nie ma zalogowanego użytkownika – pokaż ekran logowania
            setContent {
                BookKeeperTheme {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        AuthScreen(
                            onAuthSuccess = {
                                startActivity(Intent(this, UserHomeActivity::class.java))
                                finish()
                            },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

