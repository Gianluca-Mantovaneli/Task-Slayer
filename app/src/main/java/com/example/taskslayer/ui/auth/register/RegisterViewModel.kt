package com.example.taskslayer.ui.auth.register

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.example.taskslayer.ui.auth.AuthUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RegisterViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun cadastrarUsuario(nickname: String, email: String, senha: String, confirmarSenha: String) {
        if (nickname.isBlank() || email.isBlank() || senha.isBlank() || confirmarSenha.isBlank()) {
            _uiState.value = AuthUiState.Error("Preencha todos os campos!")
            return
        }
        if (senha != confirmarSenha) {
            _uiState.value = AuthUiState.Error("As senhas não coincidem!")
            return
        }

        _uiState.value = AuthUiState.Loading
        auth.createUserWithEmailAndPassword(email, senha)
            .addOnSuccessListener { result ->
                // Atualiza o nickname no perfil do usuário recém-criado
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(nickname)
                    .build()

                result.user?.updateProfile(profileUpdates)
                    ?.addOnCompleteListener {
                        // Quando terminar de atualizar o nome, disparmaos o Sucesso!
                        // O usuário já está logado no Firebase neste momento.
                        _uiState.value = AuthUiState.Success
                    }
            }
            .addOnFailureListener { exception ->
                _uiState.value = AuthUiState.Error(exception.localizedMessage ?: "Erro ao cadastrar")
            }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}