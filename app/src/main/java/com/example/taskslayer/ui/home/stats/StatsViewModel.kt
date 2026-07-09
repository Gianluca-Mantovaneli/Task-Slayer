package com.example.taskslayer.ui.home.stats

import androidx.lifecycle.ViewModel
import com.example.taskslayer.data.repository.UserRepository
import com.example.taskslayer.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed interface StatsUiState {
    object Loading : StatsUiState
    data class Success(val user: User) : StatsUiState
    data class Error(val message: String) : StatsUiState
}

class StatsViewModel : ViewModel() {

    private val userRepository = UserRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow<StatsUiState>(StatsUiState.Loading)
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    fun carregarEstatisticas() {
        val uidLogado = auth.currentUser?.uid
        if (uidLogado == null) {
            _uiState.value = StatsUiState.Error("Usuário não autenticado.")
            return
        }

        _uiState.value = StatsUiState.Loading

        userRepository.buscarUsuario(
            uid = uidLogado,
            onSucesso = { usuario ->
                _uiState.value = StatsUiState.Success(user = usuario)
            },
            onErro = { excecao ->
                _uiState.value = StatsUiState.Error(
                    excecao.localizedMessage ?: "Erro ao carregar os atributos do guerreiro."
                )
            }
        )
    }
}