package com.example.taskslayer.domain.model

import java.util.Date

class Todo : Task() {
    var deadline: Date? = null
    var reminder: Date? = null
}