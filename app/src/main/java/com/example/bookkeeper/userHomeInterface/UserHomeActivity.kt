package com.example.bookkeeper.userHomeInterface

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.bookkeeper.addBook.addBookGoogleApi.*
import com.example.bookkeeper.ui.theme.BookKeeperTheme
import com.example.bookkeeper.utils.UserBooksViewModelFactory

class UserHomeActivity : ComponentActivity() {
    private lateinit var userBooksViewModel: UserBooksViewModel
    private lateinit var searchBooksViewModel: SearchBooksViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = UserBooksViewModelFactory(application)
        userBooksViewModel = ViewModelProvider(this, factory)[UserBooksViewModel::class.java]
        searchBooksViewModel = ViewModelProvider(this)[SearchBooksViewModel::class.java]

        setContent {
            BookKeeperTheme {
                val navController = rememberNavController()
                Surface {
                    NavHost(navController = navController, startDestination = "userBooks") {
                        composable("userBooks") {
                            UserBooksScreen(
                                viewModel = userBooksViewModel,
                                onAddBook = { navController.navigate("manualAddBook") }
                            )
                        }
                        composable("manualAddBook") {
                            ManualAddBookScreen(
                                navController = navController,
                                onBackToHome = {
                                    navController.navigate("userBooks") {
                                        popUpTo("userBooks") { inclusive = true }
                                    }
                                },
                                onSearchOnline = { navController.navigate("searchBooks") },
                                viewModel = userBooksViewModel
                            )
                        }
                        composable("searchBooks") {
                            SearchBooksScreen(
                                navController = navController,
                                viewModel = searchBooksViewModel
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
                        composable(
                            route = "scanIsbn?source={source}&input={input}",
                            arguments = listOf(
                                navArgument("source") { type = NavType.StringType; defaultValue = "searchBooks" },
                                navArgument("input") { type = NavType.StringType; defaultValue = "camera" }
                            )
                        ) { backStackEntry ->
                            val source = backStackEntry.arguments?.getString("source") ?: "searchBooks"
                            val input = backStackEntry.arguments?.getString("input") ?: "camera"

                            ScanIsbnScreen(
                                navController = navController,
                                source = source,
                                input = input,
                                onIsbnFound = { isbn ->
                                    searchBooksViewModel.searchBooks(isbn)
                                },
                                onBackToCaller = {
                                    navController.navigate(source) {
                                        popUpTo(source) { inclusive = true }
                                    }
                                }
                            )
                        }

                    }
                }
            }
        }
    }
}
