package com.example.taskslayer.ui.auth.register

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.example.taskslayer.ui.auth.AuthUiState
import com.example.taskslayer.data.repository.UserRepository
import com.example.taskslayer.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RegisterViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

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

        // Criando o usuário no Firebase Auth
        auth.createUserWithEmailAndPassword(email, senha)
            .addOnSuccessListener { result ->

                // Atualizando o nickname no perfil do Auth
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(nickname)
                    .build()

                result.user?.updateProfile(profileUpdates)
                    ?.addOnCompleteListener { profileTask ->

                        // Pegando o ID do usuário gerado no Auth
                        val uidGerado = result.user?.uid

                        if (uidGerado != null) {

                            // Montando o Usuario com os dados do Auth
                            val novoUsuario = User(
                                userID = uidGerado, // ID gerado no Auth
                                nome = nickname,
                                email = email,
                                statusAtual = 50 // Status inicial equilibrado
                            )

                            // Salvando o Samurai no banco
                            userRepository.salvarUsuario(
                                user = novoUsuario,
                                onSucesso = {
                                    _uiState.value = AuthUiState.Success
                                },
                                onErro = { exception ->
                                    _uiState.value = AuthUiState.Error(
                                        exception.localizedMessage
                                            ?: "Erro ao criar perfil do samurai."
                                    )
                                }
                            )
                        } else {
                            _uiState.value =
                                AuthUiState.Error("Erro crítico: ID do usuário não encontrado.")
                        }
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