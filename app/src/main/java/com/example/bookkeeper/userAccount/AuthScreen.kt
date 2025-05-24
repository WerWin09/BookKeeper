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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bookkeeper.R
import com.example.bookkeeper.ui.theme.*
import androidx.compose.ui.unit.sp


@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = viewModel(),
    onAuthSuccess: () -> Unit
) {
    var isLogin by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        // 1) pełnoekranowy obrazek w tle
        Image(
            painter = painterResource(R.drawable.background),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )



        // 3) panel logowania/rejestracji na dole
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 0.dp, start = 0.dp, end = 0.dp)
                .clip(RoundedCornerShape(0.dp))
                .background(color = BackgroundColor)
                .padding(bottom = 60.dp, start = 24.dp, end = 24.dp, top = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isLogin) "Logowanie" else "Rejestracja",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = Color.White,
                                            fontSize = 18.sp) },
                placeholder = { Text("wpisz email", color = Color.Gray) },
                textStyle = TextStyle(color = Color.White,
                                    fontSize = 18.sp),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = SecondBackgroundColor, shape = RoundedCornerShape(8.dp)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SecondBackgroundColor,
                    unfocusedBorderColor = SecondBackgroundColor
                )
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Hasło", color = Color.White,
                                            fontSize = 18.sp) },
                placeholder = { Text("wpisz hasło", color = Color.Gray) },
                textStyle = TextStyle(color = Color.White,
                                        fontSize = 18.sp),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = SecondBackgroundColor, shape = RoundedCornerShape(8.dp)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SecondBackgroundColor,
                    unfocusedBorderColor = SecondBackgroundColor
                )
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (isLogin) viewModel.login(email, password)
                    else viewModel.register(email, password)
                },
                enabled = authState != AuthViewModel.AuthResult.Loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MainColor,
                    contentColor = Color.Black,
                    disabledContainerColor = MainColor.copy(alpha = 0.3f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isLogin) "Zaloguj się" else "Zarejestruj się",
                    color = Color.Black
                )
            }


            Spacer(Modifier.height(16.dp))

            when (authState) {
                is AuthViewModel.AuthResult.Loading ->
                    CircularProgressIndicator(color = Color.White)
                is AuthViewModel.AuthResult.Success -> {
                    Text("Sukces!", color = Color.White)
                    LaunchedEffect(Unit) { onAuthSuccess() }
                }
                is AuthViewModel.AuthResult.Error -> {
                    Text(
                        text = ("Podano błędne dane"),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                else -> {}
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = buildAnnotatedString {
                    if (isLogin) {
                        append("Nie masz konta? ")
                        withStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.Underline
                            )
                        ) {
                            append("Zarejestruj się")
                        }
                    } else {
                        append("Masz już konto? ")
                        withStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.Underline
                            )
                        ) {
                            append("Zaloguj się")
                        }
                    }
                },
                modifier = Modifier
                    .clickable { isLogin = !isLogin }
                    .fillMaxWidth(),
                color = MainColor,
                textAlign = TextAlign.Center
            )
        }
    }
}
