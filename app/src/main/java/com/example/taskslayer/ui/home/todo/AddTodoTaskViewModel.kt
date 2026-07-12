package com.example.taskslayer.ui.home.todo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.taskslayer.data.repository.TaskRepository
import com.example.taskslayer.data.repository.UserRepository
import com.example.taskslayer.domain.model.Dificulty
import com.example.taskslayer.domain.model.Todo
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Estados possíveis para a interface de criação/edição de tarefas.
 */
sealed interface AddTodoUiState {
    object Idle : AddTodoUiState
    object Loading : AddTodoUiState
    object Success : AddTodoUiState
    data class Loaded(val todo: Todo) : AddTodoUiState // Indica que os dados para edição foram carregados
    data class Error(val message: String) : AddTodoUiState
}

/**
 * ViewModel que gerencia a lógica de criação, edição e exclusão de tarefas To-Do.
 */
class AddTodoTaskViewModel(
    private val taskRepository: TaskRepository = TaskRepository(),
    private val userRepository: UserRepository = UserRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<AddTodoUiState>(AddTodoUiState.Idle)
    val uiState: StateFlow<AddTodoUiState> = _uiState.asStateFlow()

    // Controle de estado de edição
    var idTaskAtual: String = ""
    var isEditMode by mutableStateOf(false)
    private var statusDoneAtual: Boolean = false
    
    val ehTarefaNova get() = idTaskAtual.isBlank()

    /**
     * Busca os detalhes de uma tarefa existente para preencher o formulário em modo de edição.
     */
    fun prepararParaEdicao(todoId: String) {
        val uidLogado = auth.currentUser?.uid
        if (uidLogado == null) {
            _uiState.value = AddTodoUiState.Error("Erro: Usuário não autenticado.")
            return
        }

        idTaskAtual = todoId
        isEditMode = true
        _uiState.value = AddTodoUiState.Loading

        taskRepository.buscarTodoPorId(
            uid = uidLogado,
            taskId = todoId,
            onSucesso = { todoCarregado ->
                statusDoneAtual = todoCarregado.done
                _uiState.value = AddTodoUiState.Loaded(todoCarregado)
            },
            onErro = { exception ->
                _uiState.value = AddTodoUiState.Error(
                    exception.localizedMessage ?: "Erro ao carregar dados da missão."
                )
            }
        )
    }

    /**
     * Salva ou atualiza a tarefa no Firestore.
     * Realiza validações de campos obrigatórios antes de persistir.
     */
    fun salvarTarefaTodo(
        titulo: String,
        descricao: String,
        dificuldade: Dificulty,
        deadline: String = ""
    ) {
        // Validações de negócio
        if (titulo.isBlank()) {
            _uiState.value = AddTodoUiState.Error("O título não pode estar em branco!")
            return
        }
        if (dificuldade == Dificulty.NONE) {
            _uiState.value = AddTodoUiState.Error("Selecione uma dificuldade válida!")
            return
        }

        val uidLogado = auth.currentUser?.uid
        if (uidLogado == null) {
            _uiState.value = AddTodoUiState.Error("Erro: Usuário não autenticado.")
            return
        }

        _uiState.value = AddTodoUiState.Loading

        // Guardamos se é nova para saber se incrementamos o contador de tarefas criadas
        val seEhTarefaNova = ehTarefaNova

        val novoTodo = Todo(
            id = idTaskAtual,
            title = titulo,
            description = descricao,
            dificuldade = dificuldade,
            done = statusDoneAtual,
            deadline = deadline
        )

        taskRepository.salvarTodo(
            uid = uidLogado,
            todo = novoTodo,
            onSucesso = {
                _uiState.value = AddTodoUiState.Success

                // Atualiza estatísticas globais do usuário se for uma nova tarefa
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
                _uiState.value = AddTodoUiState.Error(
                    exception.localizedMessage ?: "Houve um erro ao salvar sua tarefa."
                )
            }
        )
    }

    /**
     * Remove uma tarefa permanentemente e atualiza as estatísticas do usuário.
     */
    fun deletarTarefaTodo(taskId: String) {
        val uidLogado = auth.currentUser?.uid
        if (uidLogado == null) {
            _uiState.value = AddTodoUiState.Error("Erro: Usuário não autenticado.")
            return
        }

        _uiState.value = AddTodoUiState.Loading

        taskRepository.deletarTodo(
            uid = uidLogado,
            taskId = taskId,
            onSucesso = {
                _uiState.value = AddTodoUiState.Success
                // Removido o decremento de 'tasksCriadas' para manter a estatística histórica
            },
            onErro = { exception ->
                _uiState.value = AddTodoUiState.Error(
                    exception.localizedMessage ?: "Erro ao deletar a missão."
                )
            }
        )
    }

    /**
     * Reseta o estado da UI para Idle, mantendo o contexto de edição.
     */
    fun resetUiStateToIdle() {
        _uiState.value = AddTodoUiState.Idle
    }

    /**
     * Limpa completamente o ViewModel, resetando campos de edição e estados.
     * Chamado ao fechar a tela.
     */
    fun resetCompletamente() {
        _uiState.value = AddTodoUiState.Idle
        idTaskAtual = ""
        isEditMode = false
        statusDoneAtual = false
    }
}
