package com.example.taskslayer.domain.model

open class Task {

    var title: String = ""
    var description: String = ""
    var dificuldade: Difficulty? = null
    var done: Boolean = false

}