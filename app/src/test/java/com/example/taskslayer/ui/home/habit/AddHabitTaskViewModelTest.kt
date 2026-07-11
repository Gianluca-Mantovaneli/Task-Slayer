package com.example.taskslayer.ui.home.habit

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.taskslayer.data.repository.TaskRepository
import com.example.taskslayer.data.repository.UserRepository
import com.example.taskslayer.domain.model.Dificulty
import com.example.taskslayer.domain.model.Habit
import com.example.taskslayer.domain.model.Repetition
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddHabitTaskViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var taskRepository: TaskRepository
    private lateinit var userRepository: UserRepository
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: AddHabitTaskViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        taskRepository = mockk(relaxed = true)
        userRepository = mockk(relaxed = true)
        auth = mockk(relaxed = true)
        viewModel = AddHabitTaskViewModel(taskRepository, userRepository, auth)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `salvarTarefaHabit emite Sucess e salva habito no banco`() {
        val mockUser = mockk<FirebaseUser>()
        every { auth.currentUser } returns mockUser
        every { mockUser.uid } returns "test_uid"

        val successSlot = slot<() -> Unit>()
        every {
            taskRepository.salvarHabit("test_uid", any(), capture(successSlot), any())
        } answers {
            successSlot.captured()
        }

        // When
        viewModel.salvarTarefaHabit(
            "Novo Hábito",
            "Descrição",
            Dificulty.FACIL,
            true,
            Repetition.DIARIO
        )

        // Then
        assertTrue(viewModel.uiState.value is AddHabitUiState.Success)
        verify {
            taskRepository.salvarHabit("test_uid", any(), any(), any())
            userRepository.modificarEstatisticaUsuario("test_uid", "habitosAtivos", 1, "increment")
        }
    }

    @Test
    fun `salvarTarefaHabit emite Error se titulo tiver em branco`() {
        // When
        viewModel.salvarTarefaHabit("", "Descrição", Dificulty.FACIL, true, Repetition.DIARIO)

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is AddHabitUiState.Error)
        assertEquals(
            "O título do hábito não pode estar em branco!",
            (state as AddHabitUiState.Error).message
        )
    }

    @Test
    fun `prepararParaEdicao emite Loaded se achar tarefa para editar`() {
        // Given
        val mockUser = mockk<FirebaseUser>()
        every { auth.currentUser } returns mockUser
        every { mockUser.uid } returns "test_uid"

        val mockHabit = Habit(
            id = "habit_123",
            title = "Hábito Existente",
            impact = true,
            dificuldade = Dificulty.MEDIO
        )
        val successSlot = slot<(Habit) -> Unit>()
        every {
            taskRepository.buscarHabitPorId("test_uid", "habit_123", capture(successSlot), any())
        } answers {
            successSlot.captured(mockHabit)
        }

        // When
        viewModel.prepararParaEdicao("habit_123")

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is AddHabitUiState.Loaded)
        assertEquals(mockHabit, (state as AddHabitUiState.Loaded).habit)
        assertTrue(viewModel.isEditMode)
        assertEquals("habit_123", viewModel.idTaskAtual)
    }

    @Test
    fun `deletarTarefaHabit emite Success e decrementa no statistics`() {
        // Given
        val mockUser = mockk<FirebaseUser>()
        every { auth.currentUser } returns mockUser
        every { mockUser.uid } returns "test_uid"

        val successSlot = slot<() -> Unit>()
        every {
            taskRepository.deletarHabit("test_uid", "habit_123", capture(successSlot), any())
        } answers {
            successSlot.captured()
        }

        // When
        viewModel.deletarTarefaHabit("habit_123")

        // Then
        assertTrue(viewModel.uiState.value is AddHabitUiState.Success)
        verify {
            taskRepository.deletarHabit("test_uid", "habit_123", any(), any())
            userRepository.modificarEstatisticaUsuario("test_uid", "habitosAtivos", 1, "decrement")
        }
    }
}