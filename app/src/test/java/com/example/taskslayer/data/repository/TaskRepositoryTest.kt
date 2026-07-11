package com.example.taskslayer.data.repository

import com.example.taskslayer.domain.model.Habit
import com.example.taskslayer.domain.model.Todo
import com.example.taskslayer.domain.model.Dailie
import com.example.taskslayer.domain.model.Dificulty
import com.example.taskslayer.domain.model.Repetition
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import io.mockk.*
import org.junit.Before
import org.junit.Test

class TaskRepositoryTest {

    private lateinit var db: FirebaseFirestore
    private lateinit var repository: TaskRepository
    private lateinit var userDocRef: DocumentReference

    private lateinit var habitCollectionRef: CollectionReference
    private lateinit var habitDocRef: DocumentReference

    private lateinit var todoCollectionRef: CollectionReference
    private lateinit var todoDocRef: DocumentReference

    private lateinit var dailieCollectionRef: CollectionReference
    private lateinit var dailieDocRef: DocumentReference

    @Before
    fun setup() {
        db = mockk(relaxed = true)
        userDocRef = mockk(relaxed = true)

        habitCollectionRef = mockk(relaxed = true)
        habitDocRef = mockk(relaxed = true)

        todoCollectionRef = mockk(relaxed = true)
        todoDocRef = mockk(relaxed = true)

        dailieCollectionRef = mockk(relaxed = true)
        dailieDocRef = mockk(relaxed = true)

        every { db.collection("usuarios") } returns mockk {
            every { document(any()) } returns userDocRef
        }

        every { userDocRef.collection("habits") } returns habitCollectionRef
        every { habitCollectionRef.document(any()) } returns habitDocRef
        every { habitCollectionRef.document() } returns habitDocRef

        every { userDocRef.collection("todos") } returns todoCollectionRef
        every { todoCollectionRef.document(any()) } returns todoDocRef
        every { todoCollectionRef.document() } returns todoDocRef

        every { userDocRef.collection("dailies") } returns dailieCollectionRef
        every { dailieCollectionRef.document(any()) } returns dailieDocRef
        every { dailieCollectionRef.document() } returns dailieDocRef

        repository = TaskRepository(db)
    }

    // --- HABITS ---

    @Test
    fun `salvarHabit testando salvar habito`() {
        val habit =
            Habit(id = "", title = "Test Habit", impact = true, dificuldade = Dificulty.MEDIO)
        every { habitDocRef.id } returns "habit_id"
        every { habitDocRef.set(any()) } returns mockk(relaxed = true)

        repository.salvarHabit("uid_123", habit, {}, {})

        verify { habitDocRef.set(match<Habit> { it.id == "habit_id" && it.title == habit.title }) }
    }

    @Test
    fun `deletarHabit testando deletar habito`() {
        every { habitDocRef.delete() } returns mockk(relaxed = true)
        repository.deletarHabit("uid_123", "habit_id", {}, {})
        verify { habitDocRef.delete() }
    }

    // --- TODOS ---

    @Test
    fun `salvarTodo testando salvar habito`() {
        val todo = Todo(id = "", title = "Test Todo")
        every { todoDocRef.id } returns "todo_id"
        every { todoDocRef.set(any()) } returns mockk(relaxed = true)

        repository.salvarTodo("uid_123", todo, {}, {})

        verify { todoDocRef.set(match<Todo> { it.id == "todo_id" && it.title == todo.title }) }
    }

    @Test
    fun `listarTodos testando o listar todos`() {
        val mockSnapshot = mockk<QuerySnapshot>()
        val mockTask = mockk<Task<QuerySnapshot>>(relaxed = true)

        every { todoCollectionRef.get() } returns mockTask
        val successSlot = slot<OnSuccessListener<QuerySnapshot>>()
        every { mockTask.addOnSuccessListener(capture(successSlot)) } returns mockTask

        val todos = listOf(Todo(id = "1", title = "Todo 1"))
        every { mockSnapshot.toObjects(Todo::class.java) } returns todos

        val onSucesso = mockk<(List<Todo>) -> Unit>(relaxed = true)

        repository.listarTodos("uid_123", onSucesso) {}
        successSlot.captured.onSuccess(mockSnapshot)

        verify { onSucesso(todos) }
    }

    // --- DAILIES ---

    @Test
    fun `salvarDailie testando salvar dailie`() {
        val dailie = Dailie(id = "", title = "Test Dailie")
        every { dailieDocRef.id } returns "dailie_id"
        every { dailieDocRef.set(any()) } returns mockk(relaxed = true)

        repository.salvarDailie("uid_123", dailie, {}, {})

        verify { dailieDocRef.set(match<Dailie> { it.id == "dailie_id" && it.title == dailie.title }) }
    }

    @Test
    fun `atualizarStatusTarefa testando o update de tarefa realizada`() {
        every { todoDocRef.update(any<String>(), any()) } returns mockk(relaxed = true)

        repository.atualizarStatusTarefa("uid_123", "todo_id", "todos", true, {}, {})

        verify { todoDocRef.update("done", true) }
    }
}
