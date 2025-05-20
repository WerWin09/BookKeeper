package com.example.bookkeeper.userHomeInterface

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.bookkeeper.MainActivity
import com.example.bookkeeper.addBook.addBookGoogleApi.*
import com.example.bookkeeper.ui.theme.BookKeeperTheme
import com.example.bookkeeper.utils.UserBooksViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class UserHomeActivity : ComponentActivity() {
    private lateinit var userBooksViewModel: UserBooksViewModel
    private lateinit var searchBooksViewModel: SearchBooksViewModel

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

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
                                onAddBook = { navController.navigate("manualAddBook") },
                                onBookClick = { id -> navController.navigate("bookDetails/$id") },
                                onStatusClick = { status ->
                                    navController.navigate("booksByStatus/$status")
                                },
                                onLogout = { logout() }
                            )
                        }
                        composable(
                            route = "booksByStatus/{status}",
                            arguments = listOf(navArgument("status") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val status = backStackEntry.arguments!!.getString("status")!!
                            BooksByStatusScreen(
                                status = status,
                                viewModel = userBooksViewModel,
                                onAddBook = { navController.navigate("manualAddBook") },
                                onBookClick = { id -> navController.navigate("bookDetails/$id") },
                                onBack = {
                                    // po cofnięciu idziemy do głównego ekranu kategorii/statusów
                                    navController.popBackStack()
                                }
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
                                navController = navController,
                                viewModel = userBooksViewModel,
                                searchViewModel = searchBooksViewModel,
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
                        composable("bookDetails/{bookId}") { backStackEntry ->
                            val bookId = backStackEntry.arguments?.getString("bookId")?.toIntOrNull()
                            BookDetailsScreen(
                                bookId = bookId,
                                onBack = { navController.popBackStack() },
                                onEdit = { bookId ->
                                    navController.navigate("editBook/$bookId")
                                },
                                viewModel = userBooksViewModel

                            )
                        }
                        composable("editBook/{bookId}") { backStackEntry ->
                            val bookId = backStackEntry.arguments?.getString("bookId")?.toIntOrNull()
                            EditBookScreen(
                                bookId = bookId,
                                onBack = { navController.popBackStack() },
                                viewModel = userBooksViewModel
                            )
                        }
                    }
                }
            }

        }
    }
}
