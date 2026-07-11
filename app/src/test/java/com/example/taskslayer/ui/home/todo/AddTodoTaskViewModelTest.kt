package com.example.taskslayer.ui.home.todo

import com.example.taskslayer.data.repository.TaskRepository
import com.example.taskslayer.data.repository.UserRepository
import com.example.taskslayer.domain.model.Dificulty
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
class AddTodoTaskViewModelTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var userRepository: UserRepository
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var viewModel: AddTodoTaskViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        taskRepository = mockk(relaxed = true)
        userRepository = mockk(relaxed = true)
        auth = mockk(relaxed = true)
        firebaseUser = mockk(relaxed = true)

        every { auth.currentUser } returns firebaseUser
        every { firebaseUser.uid } returns "uid_123"

        viewModel = AddTodoTaskViewModel(taskRepository, userRepository, auth)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `salvarTarefaTodo testando o salvar todo`() {
        viewModel.salvarTarefaTodo("", "desc", Dificulty.MEDIO)
        assertTrue(viewModel.uiState.value is AddTodoUiState.Error)
        assertEquals(
            "O título não pode estar em branco!",
            (viewModel.uiState.value as AddTodoUiState.Error).message
        )
    }

    @Test
    fun `salvarTarefaTodo testando o salvar todo com sucesso`() {
        // Given
        viewModel.idTaskAtual = "" // New task
        val successSlot = slot<() -> Unit>()
        every { taskRepository.salvarTodo(any(), any(), capture(successSlot), any()) } answers {
            successSlot.captured.invoke()
        }

        // When
        viewModel.salvarTarefaTodo("Title", "Desc", Dificulty.MEDIO)

        // Then
        assertEquals(AddTodoUiState.Success, viewModel.uiState.value)
        verify {
            userRepository.modificarEstatisticaUsuario(
                "uid_123",
                "tasksCriadas",
                1,
                "increment"
            )
        }
    }

    @Test
    fun `deletarTarefaTodo testando o deletar todo`() {
        // Given
        val successSlot = slot<() -> Unit>()
        every { taskRepository.deletarTodo(any(), any(), capture(successSlot), any()) } answers {
            successSlot.captured.invoke()
        }

        // When
        viewModel.deletarTarefaTodo("id_1")

        // Then
        assertEquals(AddTodoUiState.Success, viewModel.uiState.value)
        verify {
            userRepository.modificarEstatisticaUsuario(
                "uid_123",
                "tasksCriadas",
                1,
                "decrement"
            )
        }
    }
}
