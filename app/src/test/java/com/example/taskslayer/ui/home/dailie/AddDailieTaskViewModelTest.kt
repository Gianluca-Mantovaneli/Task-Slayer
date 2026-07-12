package com.example.taskslayer.ui.home.dailie

import com.example.taskslayer.data.repository.TaskRepository
import com.example.taskslayer.data.repository.UserRepository
import com.example.taskslayer.domain.model.Dificulty
import com.example.taskslayer.domain.model.Repetition
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
class AddDailieTaskViewModelTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var userRepository: UserRepository
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var viewModel: AddDailieTaskViewModel
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

        viewModel = AddDailieTaskViewModel(taskRepository, userRepository, auth)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `salvarTarefaDailie testando o salvar dailie`() {
        viewModel.salvarTarefaDailie("", "desc", Dificulty.MEDIO, "2024", Repetition.DIARIO, 1)
        assertTrue(viewModel.uiState.value is AddDailieUiState.Error)
        assertEquals(
            "O título da diária não pode estar em branco!",
            (viewModel.uiState.value as AddDailieUiState.Error).message
        )
    }

    @Test
    fun `salvarTarefaDailie testando o salvar dailie com sucesso`() {
        // Given
        viewModel.idTaskAtual = ""
        val successSlot = slot<() -> Unit>()
        every { taskRepository.salvarDailie(any(), any(), capture(successSlot), any()) } answers {
            successSlot.captured.invoke()
        }

        // When
        viewModel.salvarTarefaDailie("Daily", "Desc", Dificulty.MEDIO, "2024", Repetition.DIARIO, 1)

        // Then
        assertEquals(AddDailieUiState.Success, viewModel.uiState.value)
        verify {
            userRepository.modificarEstatisticaUsuario(
                "uid_123",
                "tasksCriadas",
                1,
                "increment"
            )
        }
    }
}
