package com.example.taskslayer.ui.home.habit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.taskslayer.data.repository.TaskRepository
import com.example.taskslayer.data.repository.UserRepository
import com.example.taskslayer.domain.model.Dificulty
import com.example.taskslayer.domain.model.Habit
import com.example.taskslayer.domain.model.Repetition
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed interface AddHabitUiState {
    object Idle : AddHabitUiState
    object Loading : AddHabitUiState
    object Success : AddHabitUiState
    data class Loaded(val habit: Habit) : AddHabitUiState
    data class Error(val message: String) : AddHabitUiState
}

class AddHabitTaskViewModel(
    private val taskRepository: TaskRepository = TaskRepository(),
    private val userRepository: UserRepository = UserRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddHabitUiState>(AddHabitUiState.Idle)
    val uiState: StateFlow<AddHabitUiState> = _uiState.asStateFlow()

    var idTaskAtual: String = ""
    var isEditMode by mutableStateOf(false)

    fun prepararParaEdicao(habitId: String) {
        val uidLogado = auth.currentUser?.uid
        if (uidLogado == null) {
            _uiState.value = AddHabitUiState.Error("Erro: Usuário não autenticado.")
            return
        }

        idTaskAtual = habitId
        isEditMode = true
        _uiState.value = AddHabitUiState.Loading

        taskRepository.buscarHabitPorId(
            uid = uidLogado,
            taskId = habitId,
            onSucesso = { habitCarregado ->
                _uiState.value = AddHabitUiState.Loaded(habitCarregado)
            },
            onErro = { exception ->
                _uiState.value = AddHabitUiState.Error(
                    exception.localizedMessage ?: "Erro ao carregar dados do hábito."
                )
            }
        )
    }

    val ehTarefaNova = idTaskAtual.isBlank()
    fun salvarTarefaHabit(
        titulo: String,
        descricao: String,
        dificuldade: Dificulty,
        impact: Boolean,
        repeticao: Repetition
    ) {
        if (titulo.isBlank()) {
            _uiState.value = AddHabitUiState.Error("O título do hábito não pode estar em branco!")
            return
        }
        if (dificuldade == Dificulty.NONE) {
            _uiState.value = AddHabitUiState.Error("Selecione uma dificuldade válida!")
            return
        }

        val uidLogado = auth.currentUser?.uid
        if (uidLogado == null) {
            _uiState.value = AddHabitUiState.Error("Erro: Usuário não autenticado.")
            return
        }

        _uiState.value = AddHabitUiState.Loading

        val novoHabit = Habit(
            id = idTaskAtual,
            title = titulo,
            description = descricao,
            dificuldade = dificuldade,
            done = false,
            impact = impact,
            repeticao = repeticao
        )

        taskRepository.salvarHabit(
            uid = uidLogado,
            habit = novoHabit,
            onSucesso = {
                _uiState.value = AddHabitUiState.Success
                if (ehTarefaNova) {
                    userRepository.modificarEstatisticaUsuario(
                        uid = uidLogado,
                        campo = "habitosAtivos",
                        quantidade = 1,
                        modificacao = "increment"
                    )
                }
            },
            onErro = { exception ->
                _uiState.value = AddHabitUiState.Error(
                    exception.localizedMessage ?: "Houve um erro ao salvar seu hábito."
                )
            }
        )
    }

    fun deletarTarefaHabit(habitId: String) {
        val uidLogado = auth.currentUser?.uid
        if (uidLogado == null) {
            _uiState.value = AddHabitUiState.Error("Erro: Usuário não autenticado.")
            return
        }

        _uiState.value = AddHabitUiState.Loading

        taskRepository.deletarHabit(
            uid = uidLogado,
            taskId = habitId,
            onSucesso = {
                _uiState.value = AddHabitUiState.Success
                userRepository.modificarEstatisticaUsuario(
                    uid = uidLogado,
                    campo = "habitosAtivos",
                    quantidade = 1,
                    modificacao = "decrement"
                )
            },
            onErro = { exception ->
                _uiState.value = AddHabitUiState.Error(
                    exception.localizedMessage ?: "Erro ao deletar o hábito."
                )
            }
        )
    }

    fun resetUiStateToIdle() {
        _uiState.value = AddHabitUiState.Idle
    }

    fun resetCompletamente() {
        _uiState.value = AddHabitUiState.Idle
        idTaskAtual = ""
        isEditMode = false
    }
}