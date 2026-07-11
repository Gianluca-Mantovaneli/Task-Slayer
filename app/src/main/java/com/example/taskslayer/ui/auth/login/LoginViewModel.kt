package com.example.taskslayer.ui.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.example.taskslayer.ui.auth.AuthUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun logarUsuario(email: String, senha: String) {
        if (email.isBlank() || senha.isBlank()) {
            _uiState.value = AuthUiState.Error("Preencha todos os campos!")
            return
        }

        _uiState.value = AuthUiState.Loading
        auth.signInWithEmailAndPassword(email, senha)
            .addOnSuccessListener {
                _uiState.value = AuthUiState.Success
            }
            .addOnFailureListener { exception ->
                _uiState.value = AuthUiState.Error(exception.localizedMessage ?: "Erro ao fazer login")
            }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }

    fun enviarEmailRecuperacao(email: String, onResultado: (Boolean, String) -> Unit) {
        if (email.isBlank()) {
            onResultado(false, "Por favor, digite o seu e-mail primeiro!")
            return
        }

        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                onResultado(true, "E-mail de redefinição enviado com sucesso!")
            }
            .addOnFailureListener { exception ->
                onResultado(false, exception.localizedMessage ?: "Erro ao enviar e-mail de recuperação")
            }
    }
}