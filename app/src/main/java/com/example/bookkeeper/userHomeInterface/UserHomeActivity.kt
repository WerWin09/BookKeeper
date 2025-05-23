package com.example.bookkeeper.userHomeInterface

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bookkeeper.MainActivity
import com.example.bookkeeper.addBook.addBookGoogleApi.*
import com.example.bookkeeper.ui.theme.BookKeeperTheme
import com.example.bookkeeper.utils.UserBooksViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.graphics.Color
import com.example.bookkeeper.addBook.ManualAddBookScreen
import com.example.bookkeeper.ui.theme.IconBottomMenu
import com.example.bookkeeper.ui.theme.IconBottomMenuFocus
import com.example.bookkeeper.ui.theme.ThirdBackgroundColor
import com.example.bookkeeper.ui.theme.ThirdBackgroundColorFocus

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
            return
        }

        val factory = UserBooksViewModelFactory(application)
        userBooksViewModel = ViewModelProvider(this, factory)[UserBooksViewModel::class.java]
        searchBooksViewModel = ViewModelProvider(this)[SearchBooksViewModel::class.java]

        setContent {
            BookKeeperTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val showBottomBar = currentRoute?.startsWith("userBooks") == true ||
                        currentRoute?.startsWith("booksByStatus") == true ||
                        currentRoute == "allBooks" ||
                        currentRoute == "settings"

                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background,
                    bottomBar = {
                        if (showBottomBar) {
                            NavigationBar(
                                containerColor = ThirdBackgroundColor, // Tło całego paska
                                contentColor = IconBottomMenu // domyślny kolor tekstu/ikon
                            ) {
                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            Icons.Default.Home,
                                            contentDescription = "Home"
                                        )
                                    },
                                    label = { Text("Główna") },
                                    selected = currentRoute?.startsWith("userBooks") == true,
                                    onClick = {
                                        navController.navigate("userBooks") {
                                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor = ThirdBackgroundColorFocus, // tło pod ikoną po zaznaczeniu (np. niebieskie)
                                        selectedIconColor = IconBottomMenuFocus,    // kolor ikony po zaznaczeniu
                                        selectedTextColor = Color.LightGray,    // kolor tekstu po zaznaczeniu
                                        unselectedIconColor = IconBottomMenu,   // kolor ikony bez zaznaczenia
                                        unselectedTextColor = Color.LightGray // kolor tekstu bez zaznaczenia
                                    )
                                )

                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Book, contentDescription = "Books") },
                                    label = { Text("Wszystkie") },
                                    selected = currentRoute == "allBooks",
                                    onClick = {
                                        navController.navigate("allBooks") {
                                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor = ThirdBackgroundColorFocus, // tło pod ikoną po zaznaczeniu (np. niebieskie)
                                        selectedIconColor = IconBottomMenuFocus,    // kolor ikony po zaznaczeniu
                                        selectedTextColor = Color.LightGray,    // kolor tekstu po zaznaczeniu
                                        unselectedIconColor = IconBottomMenu,   // kolor ikony bez zaznaczenia
                                        unselectedTextColor = Color.LightGray // kolor tekstu bez zaznaczenia
                                    )
                                )

                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                                    label = { Text("Ustawienia") },
                                    selected = currentRoute == "settings",
                                    onClick = {
                                        navController.navigate("settings") {
                                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor = ThirdBackgroundColorFocus, // tło pod ikoną po zaznaczeniu (np. niebieskie)
                                        selectedIconColor = IconBottomMenuFocus,    // kolor ikony po zaznaczeniu
                                        selectedTextColor = Color.LightGray,    // kolor tekstu po zaznaczeniu
                                        unselectedIconColor = IconBottomMenu,   // kolor ikony bez zaznaczenia
                                        unselectedTextColor = Color.LightGray // kolor tekstu bez zaznaczenia
                                    )
                                )
                            }

                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "userBooks",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("userBooks") {
                            UserBooksScreen(
                                viewModel = userBooksViewModel,
                                onAddBook = { navController.navigate("manualAddBook") },
                                onBookClick = { id -> navController.navigate("bookDetails/$id") },
                                onStatusClick = { status -> navController.navigate("booksByStatus/$status") }
                            )
                        }

                            composable("allBooks") {
                                AllBooksScreen(
                                    viewModel = userBooksViewModel,
                                    onBookClick = { id -> navController.navigate("bookDetails/$id") },
                                    onAddBook = { navController.navigate("manualAddBook") },
                                    onBack = { navController.popBackStack() }
                                )
                            }

                            composable("settings") {
                                SettingsScreen(
                                    onLogout = { logout() },
                                    viewModel = userBooksViewModel
                                )
                            }

                            composable(
                                route = "booksByStatus/{status}",
                                arguments = listOf(navArgument("status") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val status = backStackEntry.arguments?.getString("status") ?: ""
                                BooksByStatusScreen(
                                    status = status,
                                    viewModel = userBooksViewModel,
                                    onAddBook = { navController.navigate("manualAddBook") },
                                    onBookClick = { id -> navController.navigate("bookDetails/$id") },
                                    onBack = { navController.popBackStack() }
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
                                    searchViewModel = searchBooksViewModel
                                )
                            }

                            composable(
                                route = "scanIsbn?source={source}&input={input}",
                                arguments = listOf(
                                    navArgument("source") {
                                        type = NavType.StringType
                                        defaultValue = "searchBooks"
                                    },
                                    navArgument("input") {
                                        type = NavType.StringType
                                        defaultValue = "camera"
                                    }
                                )
                            ) { backStackEntry ->
                                val source = backStackEntry.arguments?.getString("source") ?: "searchBooks"
                                val input = backStackEntry.arguments?.getString("input") ?: "camera"

                                ScanIsbnScreen(
                                    navController = navController,
                                    input = input,
                                    onBackToCaller = {
                                        navController.navigate(source) {
                                            popUpTo(source) { inclusive = true }
                                        }
                                    },
                                    searchViewModel = searchBooksViewModel
                                )
                            }

                            composable("bookDetails/{bookId}") { backStackEntry ->
                                val bookId = backStackEntry.arguments?.getString("bookId")?.toIntOrNull()
                                BookDetailsScreen(
                                    bookId = bookId,
                                    onBack = { navController.popBackStack() },
                                    onEdit = { bookId -> navController.navigate("editBook/$bookId") },
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

