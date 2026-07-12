package com.example.taskslayer.domain.model

/**
 * Modelo de dados que representa o Usuário (Guerreiro) no sistema.
 * Contém informações de perfil, nível de reputação (status) e estatísticas de jogo.
 */
data class User(
    val nome : String = "",
    val email : String = "",
    val imagenPerfilURL : String = "", // Armazena a imagem de perfil em formato String Base64
    
    /**
     * Pontuação do usuário que indica o estado visual do samurai:
     * 0-29: Fraco/Ferido
     * 30-69: Normal/Saudável
     * 70-100: Mestre/Poderoso
     */
    val statusAtual : Int = 50,
    
    val userID : String? = null, // UID único gerado pelo Firebase Auth
    
    // Contadores para o dashboard de estatísticas
    var tasksCriadas : Int = 0,
    var tasksConcluidas : Int = 0,
    var tasksPerdidas : Int = 0,
    var habitosAtivos : Int = 0
)
