package com.example.bookkeeper.userAccount

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bookkeeper.R
import com.example.bookkeeper.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: AuthViewModel = viewModel(),
    onLoginSuccess: () -> Unit,
    onGoToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()

    // gradient tła
    val bgGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF000000), Color(0xFF1A1A1A)),
        startY = 0f,
        endY = Float.POSITIVE_INFINITY
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient)
    ) {
        // nagłówek z grafiką
        Image(
            painter = painterResource(R.drawable.background),
            contentDescription = "Header Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .align(Alignment.TopCenter)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black.copy(alpha = 0.8f))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Logowanie",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                textStyle = TextStyle(color = Color.White),
                placeholder = { Text("wpisz email", color = Color.Gray) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black),    // tu tło pola
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor   = Color.Gray,
                    unfocusedBorderColor = Color.DarkGray,
                    disabledBorderColor  = Color.DarkGray,
                    cursorColor          = Color.White,
                    focusedLabelColor    = Color.LightGray,
                    unfocusedLabelColor  = Color.Gray,
                    disabledLabelColor   = Color.Gray,
                )
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Hasło") },
                textStyle = TextStyle(color = Color.White),
                placeholder = { Text("wpisz hasło", color = Color.Gray) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor   = Color.Gray,
                    unfocusedBorderColor = Color.DarkGray,
                    disabledBorderColor  = Color.DarkGray,
                    cursorColor          = Color.White,
                    focusedLabelColor    = Color.LightGray,
                    unfocusedLabelColor  = Color.Gray,
                    disabledLabelColor   = Color.Gray,
                )
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { viewModel.login(email, password) },
                enabled = authState != AuthViewModel.AuthResult.Loading,
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Zaloguj się")
            }

            Spacer(Modifier.height(16.dp))

            when (authState) {
                is AuthViewModel.AuthResult.Loading -> CircularProgressIndicator()
                is AuthViewModel.AuthResult.Success -> {
                    Text("Logowanie udane!", color = MaterialTheme.colorScheme.primary)
                    LaunchedEffect(Unit) { onLoginSuccess() }
                }
                is AuthViewModel.AuthResult.Error -> {
                    Text(
                        (authState as AuthViewModel.AuthResult.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                else -> {}
            }

            Spacer(Modifier.height(24.dp))

            Text(
                buildAnnotatedString {
                    append("Nie masz konta? ")
                    withStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline,
                            color = MainColor
                        )
                    ) {
                        append("Zarejestruj się")
                    }
                },
                modifier = Modifier.clickable { onGoToRegister() }
            )
        }
    }
}
