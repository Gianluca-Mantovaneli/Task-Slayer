package com.example.taskslayer.ui.home.stats

import com.example.taskslayer.data.repository.UserRepository
import com.example.taskslayer.domain.model.User
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
class StatsViewModelTest {

    private lateinit var userRepository: UserRepository
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var viewModel: StatsViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        userRepository = mockk(relaxed = true)
        auth = mockk(relaxed = true)
        firebaseUser = mockk(relaxed = true)

        every { auth.currentUser } returns firebaseUser
        every { firebaseUser.uid } returns "uid_123"

        viewModel = StatsViewModel(userRepository, auth)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `carregarEstatisticas testando o carregar estatisticas`() {
        // Given
        val user = User(nome = "Samurai", userID = "uid_123")
        val successSlot = slot<(User) -> Unit>()
        every { userRepository.buscarUsuario(any(), capture(successSlot), any()) } answers {
            successSlot.captured.invoke(user)
        }

        // When
        viewModel.carregarEstatisticas()

        // Then
        assertTrue(viewModel.uiState.value is StatsUiState.Success)
        assertEquals(user, (viewModel.uiState.value as StatsUiState.Success).user)
    }

    @Test
    fun `carregarEstatisticas testando o carregar estatisticas com erro`() {
        // Given
        val errorSlot = slot<(Exception) -> Unit>()
        every { userRepository.buscarUsuario(any(), any(), capture(errorSlot)) } answers {
            errorSlot.captured.invoke(Exception("Error"))
        }

        // When
        viewModel.carregarEstatisticas()

        // Then
        assertTrue(viewModel.uiState.value is StatsUiState.Error)
        assertEquals("Error", (viewModel.uiState.value as StatsUiState.Error).message)
    }
}
