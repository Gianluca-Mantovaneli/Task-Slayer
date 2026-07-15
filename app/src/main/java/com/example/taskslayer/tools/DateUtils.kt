package com.example.taskslayer.tools

import com.example.taskslayer.domain.model.Repetition
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

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

    /**
     * Retorna a data de hoje formatada como dd/MM/yyyy.
     */
    fun getTodayDate(): String {
        return LocalDate.now().format(formatter)
    }

    /**
     * Verifica se uma tarefa diária deve ser resetada com base na sua repetição e frequência.
     */
    fun shouldReset(lastResetDate: String, repetition: Repetition, frequency: Int): Boolean {
        if (lastResetDate.isBlank()) return true
        
        val lastDate = try {
            LocalDate.parse(lastResetDate, formatter)
        } catch (e: Exception) {
            return true
        }
        val today = LocalDate.now()
        
        if (!lastDate.isBefore(today)) return false

        // Se a frequência for 0 ou 1, qualquer dia após o lastReset já dispara o reset
        val effectiveFreq = if (frequency <= 0) 1 else frequency

        return when (repetition) {
            Repetition.DIARIO -> ChronoUnit.DAYS.between(lastDate, today) >= effectiveFreq
            Repetition.SEMANAL -> ChronoUnit.WEEKS.between(lastDate, today) >= effectiveFreq
            Repetition.MENSAL -> ChronoUnit.MONTHS.between(lastDate, today) >= effectiveFreq
            Repetition.ANUAL -> ChronoUnit.YEARS.between(lastDate, today) >= effectiveFreq
            Repetition.NONE -> lastDate.isBefore(today) // Comportamento padrão: reset diário
        }
    }
}
