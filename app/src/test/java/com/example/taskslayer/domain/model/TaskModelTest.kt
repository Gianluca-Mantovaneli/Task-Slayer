package com.example.taskslayer.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TaskModelTest {

    @Test
    fun `Todo testando os valores padrao para o todo`() {
        val todo = Todo()
        assertEquals("", todo.id)
        assertEquals("", todo.title)
        assertEquals("", todo.description)
        assertEquals(Dificulty.NONE, todo.dificuldade)
        assertFalse(todo.done)
        assertNull(todo.deadline)
    }

    @Test
    fun `Todo testando os valores especificos para o todo`() {
        val todo = Todo(
            id = "t1",
            title = "Task 1",
            description = "Desc 1",
            dificuldade = Dificulty.MEDIO,
            done = true,
            deadline = "2024-12-31"
        )
        assertEquals("t1", todo.id)
        assertEquals("Task 1", todo.title)
        assertEquals("Desc 1", todo.description)
        assertEquals(Dificulty.MEDIO, todo.dificuldade)
        assertTrue(todo.done)
        assertEquals("2024-12-31", todo.deadline)
    }

    @Test
    fun `Dailie testando os valores padrao para a dailie`() {
        val dailie = Dailie()
        assertEquals("", dailie.id)
        assertEquals("", dailie.title)
        assertEquals("", dailie.description)
        assertEquals(Dificulty.NONE, dailie.dificuldade)
        assertFalse(dailie.done)
        assertEquals("", dailie.dataInicio)
        assertEquals(Repetition.NONE, dailie.repeticao)
        assertEquals(0, dailie.aCada)
    }

    @Test
    fun `Habit testando os valores padrao para o habit`() {
        val habit = Habit()
        assertEquals("", habit.id)
        assertEquals("", habit.title)
        assertEquals("", habit.description)
        assertEquals(Dificulty.NONE, habit.dificuldade)
        assertFalse(habit.done)
        assertTrue(habit.impact)
        assertEquals(Repetition.NONE, habit.repeticao)
    }
}
