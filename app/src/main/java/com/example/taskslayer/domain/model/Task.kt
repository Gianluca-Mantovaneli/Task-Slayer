package com.example.taskslayer.domain.model

sealed class Task {
    abstract val id: String
    abstract val title: String
    abstract val description: String?
    abstract val dificuldade: Dificulty
    abstract val done: Boolean
}

data class Todo(
    override val id: String = "",
    override val title: String = "",
    override val description: String = "",
    override val dificuldade: Dificulty = Dificulty.NONE,
    override val done: Boolean = false,
    val deadline: String? = null,
) : Task()

data class Dailie(
    override val id: String = "",
    override val title: String = "",
    override val description: String = "",
    override val dificuldade: Dificulty = Dificulty.NONE,
    override val done: Boolean = false,
    val dataInicio: String = "",
    val repeticao: Repetition = Repetition.NONE,
    val aCada: Int = 0,
) : Task()

data class Habit(
    override val id: String = "",
    override val title: String = "",
    override val description: String = "",
    override val dificuldade: Dificulty = Dificulty.NONE,
    override val done: Boolean = false,
    val impact: Boolean = true, // False se for negativo, True se for positivo
    val repeticao: Repetition = Repetition.NONE,
) : Task()