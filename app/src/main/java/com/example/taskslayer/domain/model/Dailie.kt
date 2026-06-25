package com.example.taskslayer.domain.model

import java.util.Date

class Dailie : Task() {

    var dataStart : Date? = null
    var dataEnd : Date? = null
    var repetition : Repetition? = null
    var each : Int? = null
}
