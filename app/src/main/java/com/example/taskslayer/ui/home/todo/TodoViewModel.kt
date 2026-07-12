package com.example.taskslayer.ui.home.todo

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.taskslayer.data.repository.TaskRepository
import com.example.taskslayer.data.repository.UserRepository
import com.example.taskslayer.tools.DateUtils
import com.example.taskslayer.domain.model.Todo
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Estados da interface de usuário para a lista de tarefas To-Do.
 */
sealed interface TodoUiState {
    object Loading : TodoUiState
    data class Success(val tasks: List<Todo>) : TodoUiState
    data class Error(val message: String) : TodoUiState
}

/**
 * ViewModel que gerencia a lista de tarefas pendentes (To-Do).
 * Lida com a busca de dados no repositório e atualização de progresso do usuário.
 */
class TodoViewModel(
    private val taskRepository: TaskRepository = TaskRepository(),
    private val userRepository: UserRepository = UserRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow<TodoUiState>(TodoUiState.Loading)
    val uiState: StateFlow<TodoUiState> = _uiState.asStateFlow()

    /**
     * Busca todas as tarefas do tipo "Todo" do usuário logado.
     */
    fun carregarTarefasTodo() {
        val uidLogado = auth.currentUser?.uid
        if (uidLogado == null) {
            _uiState.value = TodoUiState.Error("Usuário não autenticado.")
            return
        }

        _uiState.value = TodoUiState.Loading

        taskRepository.listarTodos(
            uid = uidLogado,
            onSucesso = { listaDeTodos ->
                _uiState.value = TodoUiState.Success(tasks = listaDeTodos)
            },
            onErro = { exception ->
                _uiState.value = TodoUiState.Error(
                    exception.localizedMessage ?: "Erro ao carregar suas tarefas."
                )
            }
        )
    }

    /**
     * Atualiza o estado de conclusão (feito/não feito) de uma tarefa específica.
     * Realiza uma atualização otimista na UI e sincroniza com o banco de dados.
     */
    fun atualizarStatusTodo(todo: Todo, todoId: String, novoStatus: Boolean) {
        val uidLogado = auth.currentUser?.uid ?: return

        // Validação: Se a tarefa expirou e não estava concluída, impede marcar como concluída
        if (novoStatus && DateUtils.isExpired(todo.deadline) && !todo.done) {
            Log.w("TodoViewModel", "Tentativa de concluir tarefa expirada bloqueada.")
            return
        }

        // Atualização Otimista: Atualiza a lista local antes da resposta do servidor
        val estadoAtual = _uiState.value
        if (estadoAtual is TodoUiState.Success) {
            val listaAtualizada = estadoAtual.tasks.map { item ->
                if (item.id == todoId) item.copy(done = novoStatus) else item
            }
            _uiState.value = TodoUiState.Success(listaAtualizada)
        }

        // Sincronização com o repositório
        taskRepository.atualizarStatusTarefa(
            uid = uidLogado,
            taskId = todo.id,
            tipoColecao = "todos",
            novoStatus = novoStatus,
            onSucesso = {
                // Se concluída, computa o progresso/experiência para o usuário
                userRepository.computarProgressoTarefa(
                    uid = uidLogado,
                    isConcluido = novoStatus,
                    dificuldade = todo.dificuldade
                )
                Log.d("TodoViewModel", "Status do Todo atualizado no Firebase com sucesso!")
            },
            onErro = { exception ->
                Log.e(
                    "TodoViewModel",
                    "Falha ao atualizar no Firebase. Revertendo UI...",
                    exception
                )
                // Em caso de erro, recarrega a lista real do banco para reverter a UI
                carregarTarefasTodo()
            }
        )
    }
}
