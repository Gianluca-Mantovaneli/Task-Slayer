package com.example.taskslayer.ui.auth.register

import com.example.taskslayer.data.repository.UserRepository
import com.example.taskslayer.ui.auth.AuthUiState
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
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
class RegisterViewModelTest {

    private lateinit var auth: FirebaseAuth
    private lateinit var userRepository: UserRepository
    private lateinit var viewModel: RegisterViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        auth = mockk(relaxed = true)
        userRepository = mockk(relaxed = true)
        viewModel = RegisterViewModel(auth, userRepository)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `cadastrarUsuario testando o cadastrar usuario`() {
        viewModel.cadastrarUsuario("", "", "", "")
        assertTrue(viewModel.uiState.value is AuthUiState.Error)
        assertEquals(
            "Preencha todos os campos!",
            (viewModel.uiState.value as AuthUiState.Error).theMessage
        )
    }

    @Test
    fun `cadastrarUsuario testando o cadastrar usuario com sucesso`() {
        viewModel.cadastrarUsuario("nick", "email@test.com", "pass1", "pass2")
        assertTrue(viewModel.uiState.value is AuthUiState.Error)
        assertEquals(
            "As senhas não coincidem!",
            (viewModel.uiState.value as AuthUiState.Error).theMessage
        )
    }

    @Test
    fun `cadastrarUsuario testando o cadastrar usuario com erro`() {
        // Given
        val email = "fail@test.com"
        val password = "password"
        val mockTask = mockk<Task<AuthResult>>(relaxed = true)

        every { auth.createUserWithEmailAndPassword(any(), any()) } returns mockTask

        val failureSlot = slot<OnFailureListener>()
        every { mockTask.addOnSuccessListener(any()) } answers { mockTask }
        every { mockTask.addOnFailureListener(capture(failureSlot)) } answers { mockTask }

        // When
        viewModel.cadastrarUsuario("nick", email, password, password)

        // Simulate Failure
        failureSlot.captured.onFailure(Exception("Auth Error"))

        // Then
        assertTrue(viewModel.uiState.value is AuthUiState.Error)
        assertEquals("Auth Error", (viewModel.uiState.value as AuthUiState.Error).theMessage)
    }
}
