package com.example.taskslayer.ui.home.habit

import androidx.lifecycle.ViewModel
import com.example.taskslayer.data.repository.TaskRepository
import com.example.taskslayer.data.repository.UserRepository
import com.example.taskslayer.domain.model.Habit
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed interface HabitsUiState {
    object Loading : HabitsUiState
    data class Success(val habits: List<Habit>) : HabitsUiState
    data class Error(val message: String) : HabitsUiState
}

class HabitsViewModel : ViewModel() {

    private val repository = TaskRepository()
    private val userRepository = UserRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow<HabitsUiState>(HabitsUiState.Loading)
    val uiState: StateFlow<HabitsUiState> = _uiState.asStateFlow()

    fun carregarHabitos() {
        val uidLogado = auth.currentUser?.uid
        if (uidLogado == null) {
            _uiState.value = HabitsUiState.Error("Usuário não autenticado.")
            return
        }

        _uiState.value = HabitsUiState.Loading

        repository.listarHabits(
            uid = uidLogado,
            onSucesso = { listaBuscada ->
                _uiState.value = HabitsUiState.Success(listaBuscada)
            },
            onErro = { exception ->
                _uiState.value = HabitsUiState.Error(
                    exception.localizedMessage ?: "Erro ao carregar seus hábitos."
                )
            }
        )
    }

    fun registrarCliqueHabito(habit: Habit) {
        val uidLogado = auth.currentUser?.uid ?: return

        userRepository.computarProgressoHabito(
            uid = uidLogado,
            isPositivo = habit.impact,
            dificuldade = habit.dificuldade
        )
    }
}