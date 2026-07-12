package com.example.taskslayer.ui.home.habit

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.taskslayer.data.repository.TaskRepository
import com.example.taskslayer.data.repository.UserRepository
import com.example.taskslayer.domain.model.Dificulty
import com.example.taskslayer.domain.model.Habit
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
class HabitsViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var repository: TaskRepository
    private lateinit var userRepository: UserRepository
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: HabitsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        userRepository = mockk(relaxed = true)
        auth = mockk(relaxed = true)
        viewModel = HabitsViewModel(repository, userRepository, auth)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `carregarHabitos emite Success quando o repositorio acha habitos`() {
        // Given
        val mockUser = mockk<FirebaseUser>()
        every { auth.currentUser } returns mockUser
        every { mockUser.uid } returns "test_uid"

        val mockHabits = listOf(
            Habit(id = "1", title = "Hábito 1", impact = true, dificuldade = Dificulty.FACIL)
        )

        val successSlot = slot<(List<Habit>) -> Unit>()
        every {
            repository.listarHabits("test_uid", capture(successSlot), any())
        } answers {
            successSlot.captured(mockHabits)
        }

        // When
        viewModel.carregarHabitos()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is HabitsUiState.Success)
        assertEquals(mockHabits, (state as HabitsUiState.Success).habits)
    }

    @Test
    fun `carregarHabitos eimite Error se o usuario nao estiver logado`() {
        // Given
        every { auth.currentUser } returns null

        // When
        viewModel.carregarHabitos()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is HabitsUiState.Error)
        assertEquals("Usuário não autenticado.", (state as HabitsUiState.Error).message)
    }

    @Test
    fun `carregarHabitos emite erro se o banco de dados nao conectar`() {
        // Given
        val mockUser = mockk<FirebaseUser>()
        every { auth.currentUser } returns mockUser
        every { mockUser.uid } returns "test_uid"

        val errorSlot = slot<(Exception) -> Unit>()
        every {
            repository.listarHabits("test_uid", any(), capture(errorSlot))
        } answers {
            errorSlot.captured(Exception("Erro de banco"))
        }

        // When
        viewModel.carregarHabitos()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is HabitsUiState.Error)
        assertEquals("Erro de banco", (state as HabitsUiState.Error).message)
    }

    @Test
    fun `registrarCliqueHabito atualiza valor de pontos qnd clica no habito`() {
        // Given
        val mockUser = mockk<FirebaseUser>()
        every { auth.currentUser } returns mockUser
        every { mockUser.uid } returns "test_uid"

        val habit = Habit(id = "1", title = "Hábito", impact = true, dificuldade = Dificulty.MEDIO)

        // When
        viewModel.registrarCliqueHabito(habit)

        // Then
        verify {
            userRepository.computarProgressoHabito("test_uid", true, Dificulty.MEDIO)
        }
    }
}