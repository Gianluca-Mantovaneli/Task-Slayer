package com.example.taskslayer.ui.home.dailie

import android.util.Log
import com.example.taskslayer.data.repository.TaskRepository
import com.example.taskslayer.data.repository.UserRepository
import com.example.taskslayer.domain.model.Dailie
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
class DailiesViewModelTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var userRepository: UserRepository
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var viewModel: DailiesViewModel
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

        viewModel = DailiesViewModel(taskRepository, userRepository, auth)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
        unmockkStatic(Log::class)
    }

    @Test
    fun `carregarDailies testando o carregar dailies`() {
        // Given
        val dailies = listOf(Dailie(id = "1", title = "Test Daily"))
        val successSlot = slot<(List<Dailie>) -> Unit>()
        every { taskRepository.listarDailies(any(), capture(successSlot), any()) } answers {
            successSlot.captured.invoke(dailies)
        }

        // When
        viewModel.carregarDailies()

        // Then
        assertTrue(viewModel.uiState.value is DailiesUiState.Success)
        assertEquals(dailies, (viewModel.uiState.value as DailiesUiState.Success).dailies)
    }

    @Test
    fun `alternarStatusDailie testando o alternar status dailie`() {
        // Given
        val dailie = Dailie(id = "1", title = "Test")
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
        viewModel.alternarStatusDailie(dailie, true)

        // Then
        verify { taskRepository.atualizarStatusTarefa(any(), "1", "dailies", true, any(), any()) }
        verify { userRepository.computarProgressoTarefa("uid_123", true, dailie.dificuldade) }
    }
}
