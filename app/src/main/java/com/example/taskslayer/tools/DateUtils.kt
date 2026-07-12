package com.example.taskslayer.tools

import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Utilitários para manipulação de datas no formato do aplicativo (dd/MM/yyyy).
 */
object DateUtils {

    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    /**
     * Verifica se uma data de prazo (deadline) já expirou em relação à data atual.
     * Retorna false se o prazo for nulo ou inválido.
     */
    fun isExpired(deadline: String?): Boolean {
        if (deadline.isNullOrBlank()) return false
        return try {
            val date = LocalDate.parse(deadline, formatter)
            date.isBefore(LocalDate.now())
        } catch (e: Exception) {
            false
        }
    }
}
