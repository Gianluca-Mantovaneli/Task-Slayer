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

/**
 * ViewModel responsável pela lógica de cadastro de novos usuários.
 * Integra o Firebase Auth com o repositório de dados do usuário (Firestore).
 */
class RegisterViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    // Estado interno da UI
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    // Estado exposto para a UI
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    /**
     * Realiza o cadastro de um novo usuário.
     * Valida os campos, cria a conta no Firebase Auth e salva os dados adicionais no banco.
     */
    fun cadastrarUsuario(nickname: String, email: String, senha: String, confirmarSenha: String) {
        // Validações básicas de preenchimento e igualdade de senha
        if (nickname.isBlank() || email.isBlank() || senha.isBlank() || confirmarSenha.isBlank()) {
            _uiState.value = AuthUiState.Error("Preencha todos os campos!")
            return
        }
        if (senha != confirmarSenha) {
            _uiState.value = AuthUiState.Error("As senhas não coincidem!")
            return
        }

        _uiState.value = AuthUiState.Loading

        // Criando o usuário no Firebase Auth (Email e Senha)
        auth.createUserWithEmailAndPassword(email, senha)
            .addOnSuccessListener { result ->

                // Atualizando o nickname no perfil de autenticação do Firebase
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(nickname)
                    .build()

                result.user?.updateProfile(profileUpdates)
                    ?.addOnCompleteListener { profileTask ->

                        // Pegando o ID do usuário gerado no Auth
                        val uidGerado = result.user?.uid

                        if (uidGerado != null) {

                            // Montando o objeto User com os dados iniciais
                            val novoUsuario = User(
                                userID = uidGerado,
                                nome = nickname,
                                email = email,
                                statusAtual = 50 // Inicia com status equilibrado
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
                // Trata erros vindos do Firebase (ex: e-mail já em uso)
                _uiState.value = AuthUiState.Error(exception.localizedMessage ?: "Erro ao cadastrar")
            }
    }

    /**
     * Reseta o estado da UI para Idle.
     */
    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}
