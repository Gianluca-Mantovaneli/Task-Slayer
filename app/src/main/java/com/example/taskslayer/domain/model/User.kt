package com.example.taskslayer.domain.model

data class User(
    val nome : String = "",
    val email : String = "",
    val imagenPerfilURL : String = "",
    val statusAtual : Int = 50, // Pontuação do usuario indica como o samurai vai estar (0 = Ruim / 50 = meio / 100 = Impecavel)
    val userID : String? = null
)