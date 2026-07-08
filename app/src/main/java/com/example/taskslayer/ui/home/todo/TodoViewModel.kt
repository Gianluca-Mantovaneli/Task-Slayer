package com.example.taskslayer.ui.home.todo

import androidx.lifecycle.ViewModel
import com.example.taskslayer.data.repository.TaskRepository
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

class TodoViewModel : ViewModel() {

    private val taskRepository = TaskRepository()
    private val auth = FirebaseAuth.getInstance()

    // A tela começa exibindo a rodinha de carregamento (Loading)
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

    fun atualizarStatusTodo(todoId: String, novoStatus: Boolean) {
        val uidLogado = auth.currentUser?.uid ?: return

        taskRepository.atualizarStatusTarefa(
            uid = uidLogado,
            taskId = todoId,
            tipoColecao = "todos",
            novoStatus = novoStatus,
            onSucesso = {
                carregarTarefasTodo()
            },
            onErro = {
                _uiState.value = TodoUiState.Error(
                    it.localizedMessage ?: "Erro ao atualizar o status da tarefa."
                )
            }
        )
    }
}