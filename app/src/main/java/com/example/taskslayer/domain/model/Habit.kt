package com.example.taskslayer.domain.model

class Habit : Task() {

    var impact: Boolean = false // Indica se o hábito é negativo (false) ou positivo (true)
    var repetitions: Repetition? = null // Indica a frequência de repetição do hábito
}