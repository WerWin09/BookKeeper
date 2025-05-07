package com.example.bookkeeper.userHomeInterface

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bookkeeper.addBook.addBookGoogleApi.EditImportedBookScreen
import com.example.bookkeeper.addBook.addBookGoogleApi.SearchBooksScreen
import com.example.bookkeeper.addBook.addBookGoogleApi.SearchBooksViewModel
import com.example.bookkeeper.ui.theme.BookKeeperTheme
import com.example.bookkeeper.utils.UserBooksViewModelFactory

class UserHomeActivity : ComponentActivity() {
    private lateinit var userBooksViewModel: UserBooksViewModel
    private lateinit var searchBooksViewModel: SearchBooksViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = UserBooksViewModelFactory(application)
        userBooksViewModel = ViewModelProvider(this, factory)[UserBooksViewModel::class.java]
        searchBooksViewModel = ViewModelProvider(this)[SearchBooksViewModel::class.java] // wspólna instancja

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
                                viewModel = userBooksViewModel,
                                onAddBook = { navController.navigate("manualAddBook") }
                            )
                        }
                        composable("manualAddBook") {
                            ManualAddBookScreen(
                                onBack = { navController.popBackStack() },
                                onSearchOnline = { navController.navigate("searchBooks") },
                                viewModel = userBooksViewModel
                            )
                        }
                        composable("searchBooks") {
                            SearchBooksScreen(
                                navController = navController,
                                viewModel = searchBooksViewModel // przekazujemy ten sam viewModel
                            )
                        }
                        composable("editImportedBook") {
                            val selectedBook by searchBooksViewModel.selectedBook.collectAsState()
                            EditImportedBookScreen(
                                onBack = { navController.popBackStack() },
                                viewModel = userBooksViewModel,
                                selectedBook = selectedBook
                            )
                        }
                    }
                }
            }
        }
    }
}
