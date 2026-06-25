package com.example.taskslayer.domain.model

sealed class Task {
    abstract val id: String
    abstract val title: String
    abstract val description: String
    abstract val dificuldade: Dificulty
    abstract val done: Boolean
}

data class Todo(
    override val id: String,
    override val title: String,
    override val description: String,
    override val dificuldade: Dificulty = Dificulty.NONE,
    override val done: Boolean,
    val deadline: Long? = null,
    val lembrete: Long? = null
) : Task()

data class Dailie(
    override val id: String,
    override val title: String,
    override val description: String,
    override val dificuldade: Dificulty = Dificulty.NONE,
    override val done: Boolean,
    val dataInicio: Long = 0L,
    val repeticao: Repetition = Repetition.NONE,
    val aCada: Int = 0,
    val lembrete: Long? = null
) : Task()

data class Habit(
    override val id: String,
    override val title: String,
    override val description: String,
    override val dificuldade: Dificulty = Dificulty.NONE,
    override val done: Boolean,
    val impact : Boolean, // False se for negativo, True se for positivo
    val repeticao: Repetition = Repetition.NONE,
) : Task()