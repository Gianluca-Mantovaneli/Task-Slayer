package com.example.taskslayer.domain.model

import androidx.compose.ui.graphics.ImageBitmap

class User {
    var nome : String? = null
    var email : String? = null
    var imagenPerfil : ImageBitmap? = null
    var statusAtual : Int = 50 // Pontuação do usuario indica como o samurai vai estar (0 = Ruim / 50 = meio / 100 = Impecavel)
}