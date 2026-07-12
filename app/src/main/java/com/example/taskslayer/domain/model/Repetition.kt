package com.example.taskslayer.domain.model

/**
 * Enumeração que define os tipos de repetição (recorrência) para missões diárias (Dailies) e hábitos.
 */
enum class Repetition {
    DIARIO,
    SEMANAL,
    MENSAL,
    ANUAL,
    NONE // Utilizado para tarefas únicas (To-Dos)
}
