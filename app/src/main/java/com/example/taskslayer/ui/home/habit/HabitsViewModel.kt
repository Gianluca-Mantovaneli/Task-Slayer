package com.example.taskslayer.ui.home.habit

import androidx.lifecycle.ViewModel
import com.example.taskslayer.data.repository.TaskRepository
import com.example.taskslayer.data.repository.UserRepository
import com.example.taskslayer.domain.model.Habit
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Estados da interface de usuário para a lista de hábitos.
 */
sealed interface HabitsUiState {
    object Loading : HabitsUiState
    data class Success(val habits: List<Habit>) : HabitsUiState
    data class Error(val message: String) : HabitsUiState
}

/**
 * ViewModel que gerencia a aba de hábitos.
 * Lida com a busca de hábitos e o registro de progresso quando um hábito é praticado.
 */
class HabitsViewModel(
    private val repository: TaskRepository = TaskRepository(),
    private val userRepository: UserRepository = UserRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow<HabitsUiState>(HabitsUiState.Loading)
    val uiState: StateFlow<HabitsUiState> = _uiState.asStateFlow()

    /**
     * Busca todos os hábitos cadastrados pelo usuário no Firestore.
     */
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

    /**
     * Registra o impacto (positivo ou negativo) na experiência do usuário ao realizar um hábito.
     */
    fun registrarCliqueHabito(habit: Habit) {
        val uidLogado = auth.currentUser?.uid ?: return

        userRepository.computarProgressoHabito(
            uid = uidLogado,
            isPositivo = habit.impact, // Define se ganha ou perde HP/Experiência
            dificuldade = habit.dificuldade
        )
    }
}
