package com.example.bookkeeper.userAccount

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = viewModel(),
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isLogin by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isLogin) "Logowanie" else "Rejestracja",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Hasło") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (isLogin) {
                    viewModel.login(email, password)
                } else {
                    viewModel.register(email, password)
                }
            },
            enabled = authState != AuthViewModel.AuthResult.Loading
        ) {
            Text(if (isLogin) "Zaloguj się" else "Zarejestruj się")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (authState) {
            is AuthViewModel.AuthResult.Loading -> CircularProgressIndicator()
            is AuthViewModel.AuthResult.Success -> {
                Text("Sukces!", color = MaterialTheme.colorScheme.primary)
                LaunchedEffect(Unit) { onAuthSuccess() }
            }
            is AuthViewModel.AuthResult.Error -> {
                Text(
                    text = (authState as AuthViewModel.AuthResult.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }
            else -> {}
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isLogin) "Nie masz konta? Zarejestruj się" else "Masz już konto? Zaloguj się",
            modifier = Modifier.clickable { isLogin = !isLogin },
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
    }
}
