package com.example.taskslayer.ui.home.todo

import android.util.Log
import com.example.taskslayer.data.repository.TaskRepository
import com.example.taskslayer.data.repository.UserRepository
import com.example.taskslayer.domain.model.Todo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TodoViewModelTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var userRepository: UserRepository
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var viewModel: TodoViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        taskRepository = mockk(relaxed = true)
        userRepository = mockk(relaxed = true)
        auth = mockk(relaxed = true)
        firebaseUser = mockk(relaxed = true)

        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        every { auth.currentUser } returns firebaseUser
        every { firebaseUser.uid } returns "uid_123"

        viewModel = TodoViewModel(taskRepository, userRepository, auth)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
        unmockkStatic(Log::class)
    }

    @Test
    fun `carregarTarefasTodo testando o carregar tarefas todo`() {
        // Given
        val todos = listOf(Todo(id = "1", title = "Test Todo"))
        val successSlot = slot<(List<Todo>) -> Unit>()
        every { taskRepository.listarTodos(any(), capture(successSlot), any()) } answers {
            successSlot.captured.invoke(todos)
        }

        // When
        viewModel.carregarTarefasTodo()

        // Then
        assertTrue(viewModel.uiState.value is TodoUiState.Success)
        assertEquals(todos, (viewModel.uiState.value as TodoUiState.Success).tasks)
    }

    @Test
    fun `carregarTarefasTodo testando o carregar tarefas todo com erro`() {
        // Given
        val errorSlot = slot<(Exception) -> Unit>()
        every { taskRepository.listarTodos(any(), any(), capture(errorSlot)) } answers {
            errorSlot.captured.invoke(Exception("Error"))
        }

        // When
        viewModel.carregarTarefasTodo()

        // Then
        assertTrue(viewModel.uiState.value is TodoUiState.Error)
        assertEquals("Error", (viewModel.uiState.value as TodoUiState.Error).message)
    }

    @Test
    fun `atualizarStatusTodo testando o atualizar status todo`() {
        // Given
        val todo = Todo(id = "1", title = "Test")
        val successSlot = slot<() -> Unit>()
        every {
            taskRepository.atualizarStatusTarefa(
                any(),
                any(),
                any(),
                any(),
                capture(successSlot),
                any()
            )
        } answers {
            successSlot.captured.invoke()
        }

        // When
        viewModel.atualizarStatusTodo(todo, "1", true)

        // Then
        verify { taskRepository.atualizarStatusTarefa(any(), "1", "todos", true, any(), any()) }
        verify { userRepository.computarProgressoTarefa("uid_123", true, todo.dificuldade) }
    }
}
