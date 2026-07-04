package com.example.taskslayer.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.taskslayer.domain.model.User
//import com.google.firebase.firestore.FirebaseFirestore

class StatsViewModel : ViewModel() {
    // O StateFlow segura o estado dos dados e avisa o Compose quando algo muda
    private val _uiState = MutableStateFlow(User())
    val uiState: StateFlow<User> = _uiState.asStateFlow()

    init {
        carregarDadosDoFirebase()
    }

    //private val db = FirebaseFirestore.getInstance() // Se já tiver a lib do Firebase ativa

    private fun carregarDadosDoFirebase() {
        // TODO: Buscar do Firebase real. Por enquanto, simulamos os dados chegando:
        _uiState.value = User(
            nome = "Fulano Samurai",
            statusAtual = 83,
            imagenPerfilURL = "https://rlv.zcache.com.br/adesivo_redondo_foto_de_cao_cachorro_pet_personalizado-r9b188fed564a4ae4a4d26ece493e043a_zg2qos_644.webp?rlvnet=1",
            tasksCriadas = 142,
            tasksConcluidas = 118,
            tasksPerdidas = 10,
            habitosAtivos = 5
        )
    }
}