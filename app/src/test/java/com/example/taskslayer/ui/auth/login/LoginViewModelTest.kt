package com.example.taskslayer.ui.auth.login

import com.example.taskslayer.ui.auth.AuthUiState
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
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
class LoginViewModelTest {

    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: LoginViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        auth = mockk(relaxed = true)
        viewModel = LoginViewModel(auth)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `logarUsuario testando o logar usuario`() {
        viewModel.logarUsuario("", "")
        assertTrue(viewModel.uiState.value is AuthUiState.Error)
        assertEquals(
            "Preencha todos os campos!",
            (viewModel.uiState.value as AuthUiState.Error).theMessage
        )
    }

    @Test
    fun `logarUsuario testando o logar usuario com sucesso`() {
        // Given
        val successSlot = slot<OnSuccessListener<AuthResult>>()
        val mockTask = mockk<Task<AuthResult>>()

        every { auth.signInWithEmailAndPassword(any(), any()) } returns mockTask
        every { mockTask.addOnSuccessListener(capture(successSlot)) } returns mockTask
        every { mockTask.addOnFailureListener(any()) } returns mockTask

        // When
        viewModel.logarUsuario("test@test.com", "password")

        // Then
        assertEquals(AuthUiState.Loading, viewModel.uiState.value)

        // Simulate Success
        successSlot.captured.onSuccess(mockk())
        assertEquals(AuthUiState.Success, viewModel.uiState.value)
    }

    @Test
    fun `logarUsuario testando o logar usuario com erro`() {
        // Given
        val failureSlot = slot<OnFailureListener>()
        val mockTask = mockk<Task<AuthResult>>()

        every { auth.signInWithEmailAndPassword(any(), any()) } returns mockTask
        every { mockTask.addOnSuccessListener(any()) } returns mockTask
        every { mockTask.addOnFailureListener(capture(failureSlot)) } returns mockTask

        // When
        viewModel.logarUsuario("test@test.com", "wrong")

        // Then
        assertEquals(AuthUiState.Loading, viewModel.uiState.value)

        // Simulate Failure
        val exception = Exception("Login failed")
        failureSlot.captured.onFailure(exception)

        assertTrue(viewModel.uiState.value is AuthUiState.Error)
        assertEquals("Login failed", (viewModel.uiState.value as AuthUiState.Error).theMessage)
    }

    @Test
    fun `enviarEmailRecuperacao testando o enviar email de recuperacao`() {
        // Given
        val successSlot = slot<OnSuccessListener<Void>>()
        val mockTask = mockk<Task<Void>>()

        every { auth.sendPasswordResetEmail(any()) } returns mockTask
        every { mockTask.addOnSuccessListener(capture(successSlot)) } returns mockTask
        every { mockTask.addOnFailureListener(any()) } returns mockTask

        val callback = mockk<(Boolean, String) -> Unit>(relaxed = true)

        // When
        viewModel.enviarEmailRecuperacao("test@test.com", callback)

        // Simulate Success
        successSlot.captured.onSuccess(null)

        // Then
        verify { callback(true, "E-mail de redefinição enviado com sucesso!") }
    }
}
