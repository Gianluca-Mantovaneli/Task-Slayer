package com.example.taskslayer.domain.model

/**
 * Classe base selada (sealed) para representar os diferentes tipos de tarefas do sistema.
 */
sealed class Task {
    abstract val id: String
    abstract val title: String
    abstract val description: String?
    abstract val dificuldade: Dificulty
    abstract val done: Boolean
}

/**
 * Representa uma tarefa do tipo "To-Do" (única).
 * @param deadline Prazo final opcional para a conclusão da tarefa.
 */
data class Todo(
    override val id: String = "",
    override val title: String = "",
    override val description: String = "",
    override val dificuldade: Dificulty = Dificulty.NONE,
    override val done: Boolean = false,
    val deadline: String? = null,
) : Task()

/**
 * Representa uma "Dailie" (Missão Diária ou Recorrente).
 * @param dataInicio Data em que a missão começa a valer.
 * @param repeticao Tipo de recorrência (Diária, Semanal, etc).
 * @param aCada Intervalo da repetição (ex: a cada 2 dias).
 */
data class Dailie(
    override val id: String = "",
    override val title: String = "",
    override val description: String = "",
    override val dificuldade: Dificulty = Dificulty.NONE,
    override val done: Boolean = false,
    val dataInicio: String = "",
    val repeticao: Repetition = Repetition.NONE,
    val aCada: Int = 0,
    val lastReset: String = "", // Armazena a última data (dd/MM/yyyy) em que o status foi verificado/resetado
) : Task()

/**
 * Representa um "Hábito".
 * @param impact Define se o hábito é positivo (ganha reputação) ou negativo (perde vida/reputação).
 * @param repeticao Frequência sugerida para o hábito.
 */
data class Habit(
    override val id: String = "",
    override val title: String = "",
    override val description: String = "",
    override val dificuldade: Dificulty = Dificulty.NONE,
    override val done: Boolean = false,
    val impact: Boolean = true, // False se for negativo, True se for positivo
    val repeticao: Repetition = Repetition.NONE,
) : Task()
