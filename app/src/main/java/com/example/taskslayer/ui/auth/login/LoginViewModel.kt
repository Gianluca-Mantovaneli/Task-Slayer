package com.example.taskslayer.ui.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.example.taskslayer.ui.auth.AuthUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsável pela lógica de autenticação na tela de Login.
 * Gerencia a comunicação com o Firebase Auth e atualiza o estado da UI.
 */
class LoginViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    // Estado interno da UI (privado para modificação apenas aqui)
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    // Estado exposto para a UI (somente leitura)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    /**
     * Tenta realizar o login do usuário com e-mail e senha.
     * Atualiza o estado para Loading, Success ou Error conforme o resultado.
     */
    fun logarUsuario(email: String, senha: String) {
        // Validação básica de campos vazios
        if (email.isBlank() || senha.isBlank()) {
            _uiState.value = AuthUiState.Error("Preencha todos os campos!")
            return
        }

        _uiState.value = AuthUiState.Loading
        auth.signInWithEmailAndPassword(email, senha)
            .addOnSuccessListener {
                // Login bem-sucedido
                _uiState.value = AuthUiState.Success
            }
            .addOnFailureListener { exception ->
                // Erro no login (credenciais inválidas, etc)
                _uiState.value = AuthUiState.Error(exception.localizedMessage ?: "Erro ao fazer login")
            }
    }

    /**
     * Reseta o estado da UI para Idle (Ocioso).
     * Útil para limpar estados de sucesso ou erro após o consumo pela UI.
     */
    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }

    /**
     * Envia um e-mail para redefinição de senha do usuário.
     */
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
