package com.example.taskslayer.ui.home.stats

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.taskslayer.domain.model.User
import com.example.taskslayer.data.repository.UserRepository // 👈 Importe o seu repositório
import com.google.firebase.auth.FirebaseAuth                 // 👈 Importe o Auth para pegar o UID do logado

class StatsViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val userRepository = UserRepository()
    private val _uiState = MutableStateFlow(User())
    val uiState: StateFlow<User> = _uiState.asStateFlow()

    init {
        carregarDadosDoFirebase()
    }

    private fun carregarDadosDoFirebase() {
        val uidLogado = auth.currentUser?.uid

        if (uidLogado != null) {
            userRepository.buscarUsuario(
                uid = uidLogado,
                onSucesso = { usuarioReal ->
                    _uiState.value = usuarioReal
                },
                onErro = { exception ->
                    // TODO: tratar erro melhor
                    println("TaskSlayer_Erro: Não foi possível carregar os status do Samurai: ${exception.localizedMessage}")
                }
            )
        } else {
            println("TaskSlayer_Erro: Nenhum usuário autenticado no Firebase Auth!")
        }
    }
}