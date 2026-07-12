package com.example.taskslayer.domain.model

/**
 * Enumeração que define os níveis de dificuldade para as tarefas e hábitos.
 * A dificuldade influencia diretamente no ganho ou perda de reputação do Guerreiro.
 */
enum class Dificulty {
    TRIVIAL, // Ganho/Perda mínimo
    FACIL,
    MEDIO,
    DIFICIL, // Ganho/Perda máximo
    NONE     // Estado inicial ou não definido
}
