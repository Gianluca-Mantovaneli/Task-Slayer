package com.example.taskslayer.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class UserModelTest {

    @Test
    fun `User testando os valores padrao para o user`() {
        val user = User()
        assertEquals("", user.nome)
        assertEquals("", user.email)
        assertEquals("", user.imagenPerfilURL)
        assertEquals(50, user.statusAtual)
        assertNull(user.userID)
        assertEquals(0, user.tasksCriadas)
        assertEquals(0, user.tasksConcluidas)
        assertEquals(0, user.tasksPerdidas)
        assertEquals(0, user.habitosAtivos)
    }

    @Test
    fun `User testando os valores especificos para o user`() {
        val user = User(
            nome = "Samurai",
            email = "samurai@slayer.com",
            imagenPerfilURL = "url://image",
            statusAtual = 80,
            userID = "uid_999",
            tasksCriadas = 10,
            tasksConcluidas = 5,
            tasksPerdidas = 2,
            habitosAtivos = 3
        )
        assertEquals("Samurai", user.nome)
        assertEquals("samurai@slayer.com", user.email)
        assertEquals("url://image", user.imagenPerfilURL)
        assertEquals(80, user.statusAtual)
        assertEquals("uid_999", user.userID)
        assertEquals(10, user.tasksCriadas)
        assertEquals(5, user.tasksConcluidas)
        assertEquals(2, user.tasksPerdidas)
        assertEquals(3, user.habitosAtivos)
    }
}
