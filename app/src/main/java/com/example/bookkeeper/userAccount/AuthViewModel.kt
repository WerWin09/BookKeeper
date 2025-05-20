package com.example.bookkeeper.userAccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow<AuthResult>(AuthResult.Idle)
    val authState: StateFlow<AuthResult> = _authState

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    //rejestracja konta
    fun register(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthResult.Loading
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    _authState.value = if (task.isSuccessful) {
                        AuthResult.Success
                    } else {
                        AuthResult.Error(task.exception?.message ?: "Nieznany błąd")
                    }
                }
        }
    }

    //logowanie sie na konto
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthResult.Loading
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Upewnij się, że stan jest trwale zapisany
                        auth.currentUser?.getIdToken(true)
                        _authState.value = AuthResult.Success
                    } else {
                        _authState.value = AuthResult.Error(task.exception?.message ?: "Nieznany błąd")
                    }
                }
        }
    }


    sealed class AuthResult {
        object Idle : AuthResult()
        object Loading : AuthResult()
        object Success : AuthResult()
        data class Error(val message: String) : AuthResult()
    }
}
