package com.example.taskslayer.ui.home.todo

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.taskslayer.data.repository.TaskRepository
import com.example.taskslayer.data.repository.UserRepository
import com.example.taskslayer.domain.model.Todo
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed interface TodoUiState {
    object Loading : TodoUiState
    data class Success(val tasks: List<Todo>) : TodoUiState
    data class Error(val message: String) : TodoUiState
}

class TodoViewModel(
    private val taskRepository: TaskRepository = TaskRepository(),
    private val userRepository: UserRepository = UserRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow<TodoUiState>(TodoUiState.Loading)
    val uiState: StateFlow<TodoUiState> = _uiState.asStateFlow()

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

    fun atualizarStatusTodo(todo: Todo, todoId: String, novoStatus: Boolean) {
        val uidLogado = auth.currentUser?.uid ?: return

        val estadoAtual = _uiState.value
        if (estadoAtual is TodoUiState.Success) {
            val listaAtualizada = estadoAtual.tasks.map { todo ->
                if (todo.id == todoId) todo.copy(done = novoStatus) else todo
            }
            _uiState.value = TodoUiState.Success(listaAtualizada)
        }

        taskRepository.atualizarStatusTarefa(
            uid = uidLogado,
            taskId = todo.id,
            tipoColecao = "todos",
            novoStatus = novoStatus,
            onSucesso = {
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
                carregarTarefasTodo()
            }
        )
    }
}