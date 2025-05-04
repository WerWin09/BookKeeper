package com.example.bookkeeper.userHomeInterface

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.*
import com.example.bookkeeper.ui.theme.BookKeeperTheme

class UserHomeActivity : ComponentActivity() {

    private val viewModel: UserBooksViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BookKeeperTheme {
                Surface {
                    UserBooksScreen(viewModel)
                }
            }
        }
    }
}
