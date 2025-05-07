package com.example.bookkeeper.userHomeInterface

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bookkeeper.ui.theme.BookKeeperTheme
import androidx.navigation.compose.rememberNavController
import com.example.bookkeeper.userHomeInterface.addBook.AddBookScreen

class UserHomeActivity : ComponentActivity() {
    private val viewModel: UserBooksViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BookKeeperTheme {
                Surface {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "userBooks"
                    ) {
                        composable("userBooks") {
                            UserBooksScreen(
                                viewModel = viewModel,
                                onAddBook = { navController.navigate("addBook") }
                            )
                        }
                        composable("addBook") {
                            AddBookScreen(
                                onBack = { navController.popBackStack() },
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }
}