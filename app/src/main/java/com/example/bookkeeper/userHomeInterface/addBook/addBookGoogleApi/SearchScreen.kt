package com.example.bookkeeper.userHomeInterface.addBook.addBookGoogleApi

@Composable
fun SearchBooksScreen(
    navController: NavController,
    viewModel: SearchBooksViewModel = hiltViewModel()
) {
    val results by viewModel.searchResults.collectAsState()

    LazyColumn {
        items(results) { item ->
            GoogleBookCard(item = item, onClick = {
                val bookEntity = mapGoogleBookToBookEntity(item)
                viewModel.setSelectedBook(bookEntity)
                navController.navigate("editImportedBook") // przejście do ekranu edycji
            })
        }
    }
}
