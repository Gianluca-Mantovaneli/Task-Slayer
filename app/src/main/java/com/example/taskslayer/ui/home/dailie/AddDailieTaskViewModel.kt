package com.example.taskslayer.ui.home.dailie

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.taskslayer.data.repository.TaskRepository
import com.example.taskslayer.data.repository.UserRepository
import com.example.taskslayer.domain.model.Dificulty
import com.example.taskslayer.domain.model.Dailie
import com.example.taskslayer.domain.model.Repetition
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Estados da interface de usuário para criação ou edição de missões diárias.
 */
sealed interface AddDailieUiState {
    object Idle : AddDailieUiState
    object Loading : AddDailieUiState
    object Success : AddDailieUiState
    data class Loaded(val dailie: Dailie) : AddDailieUiState // Indica que os dados da diária foram carregados para edição
    data class Error(val message: String) : AddDailieUiState
}

/**
 * ViewModel responsável por gerenciar a criação, edição e exclusão de missões diárias (Dailies).
 */
class AddDailieTaskViewModel(
    private val taskRepository: TaskRepository = TaskRepository(),
    private val userRepository: UserRepository = UserRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {
    private val _uiState = MutableStateFlow<AddDailieUiState>(AddDailieUiState.Idle)
    val uiState: StateFlow<AddDailieUiState> = _uiState.asStateFlow()

    var idTaskAtual: String = ""
    var isEditMode by mutableStateOf(false)

    private var statusDoneAtual: Boolean = false

    private val ehTarefaNova get() = idTaskAtual.isBlank()

    /**
     * Busca os dados de uma missão diária existente para preencher o formulário de edição.
     */
    fun prepararParaEdicao(dailieId: String) {
        val uidLogado = auth.currentUser?.uid
        if (uidLogado == null) {
            _uiState.value = AddDailieUiState.Error("Erro: Usuário não autenticado.")
            return
        }

        idTaskAtual = dailieId
        isEditMode = true
        _uiState.value = AddDailieUiState.Loading

        taskRepository.buscarDailiePorId(
            uid = uidLogado,
            taskId = dailieId,
            onSucesso = { dailieCarregada ->
                statusDoneAtual = dailieCarregada.done
                _uiState.value = AddDailieUiState.Loaded(dailieCarregada)
            },
            onErro = { exception ->
                _uiState.value = AddDailieUiState.Error(
                    exception.localizedMessage ?: "Erro ao carregar dados da diária."
                )
            }
        )
    }

    /**
     * Salva a missão diária (nova ou editada) no Firestore.
     * Valida campos obrigatórios antes de persistir.
     */
    fun salvarTarefaDailie(
        titulo: String,
        descricao: String,
        dificuldade: Dificulty,
        dataInicio: String,
        repeticao: Repetition,
        aCada: Int
    ) {
        if (titulo.isBlank()) {
            _uiState.value = AddDailieUiState.Error("O título da diária não pode estar em branco!")
            return
        }
        if (dificuldade == Dificulty.NONE) {
            _uiState.value = AddDailieUiState.Error("Selecione uma dificuldade válida!")
            return
        }

        val uidLogado = auth.currentUser?.uid
        if (uidLogado == null) {
            _uiState.value = AddDailieUiState.Error("Erro: Usuário não autenticado.")
            return
        }

        _uiState.value = AddDailieUiState.Loading

        val seEhTarefaNova = ehTarefaNova
        val novaDailie = Dailie(
            id = idTaskAtual,
            title = titulo,
            description = descricao,
            dificuldade = dificuldade,
            done = statusDoneAtual,
            dataInicio = dataInicio,
            repeticao = repeticao,
            aCada = aCada
        )

        taskRepository.salvarDailie(
            uid = uidLogado,
            dailie = novaDailie,
            onSucesso = {
                _uiState.value = AddDailieUiState.Success

                // Incrementa estatísticas se for uma nova criação
                if (seEhTarefaNova) {
                    userRepository.modificarEstatisticaUsuario(
                        uid = uidLogado,
                        campo = "tasksCriadas",
                        quantidade = 1,
                        modificacao = "increment"
                    )
                }
            },
            onErro = { exception ->
                _uiState.value = AddDailieUiState.Error(
                    exception.localizedMessage ?: "Houve um erro ao salvar sua diária."
                )
            }
        )
    }

    /**
     * Remove uma missão diária permanentemente.
     */
    fun deletarTarefaDailie(dailieId: String) {
        val uidLogado = auth.currentUser?.uid
        if (uidLogado == null) {
            _uiState.value = AddDailieUiState.Error("Erro: Usuário não autenticado.")
            return
        }

        _uiState.value = AddDailieUiState.Loading

        taskRepository.deletarDailie(
            uid = uidLogado,
            taskId = dailieId,
            onSucesso = {
                _uiState.value = AddDailieUiState.Success
                // Removido o decremento de 'tasksCriadas' para manter a estatística histórica
            },
            onErro = { exception ->
                _uiState.value = AddDailieUiState.Error(
                    exception.localizedMessage ?: "Erro ao deletar a diária."
                )
            }
        )
    }

    /**
     * Reseta o estado da UI para Idle.
     */
    fun resetUiStateToIdle() {
        _uiState.value = AddDailieUiState.Idle
    }

    /**
     * Limpa completamente o estado do ViewModel.
     */
    fun resetCompletamente() {
        _uiState.value = AddDailieUiState.Idle
        idTaskAtual = ""
        isEditMode = false
        statusDoneAtual = false
    }
}
