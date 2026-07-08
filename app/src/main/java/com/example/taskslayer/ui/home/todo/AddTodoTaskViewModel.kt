package com.example.taskslayer.ui.home.todo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.taskslayer.data.repository.TaskRepository
import com.example.taskslayer.domain.model.Dificulty
import com.example.taskslayer.domain.model.Todo
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed interface AddTodoUiState {
    object Idle : AddTodoUiState
    object Loading : AddTodoUiState
    object Success : AddTodoUiState
    data class Loaded(val todo: Todo) : AddTodoUiState
    data class Error(val message: String) : AddTodoUiState
}

class AddTodoTaskViewModel : ViewModel() {

    private val taskRepository = TaskRepository()
    private val auth = FirebaseAuth.getInstance()
    private val _uiState = MutableStateFlow<AddTodoUiState>(AddTodoUiState.Idle)
    val uiState: StateFlow<AddTodoUiState> = _uiState.asStateFlow()

    var idTaskAtual: String = ""
    var isEditMode by mutableStateOf(false)

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
                _uiState.value = AddTodoUiState.Loaded(todoCarregado)
            },
            onErro = { exception ->
                _uiState.value = AddTodoUiState.Error(
                    exception.localizedMessage ?: "Erro ao carregar dados da missão."
                )
            }
        )
    }

    fun salvarTarefaTodo(
        titulo: String,
        descricao: String,
        dificuldade: Dificulty,
        deadline: String = ""
    ) {

        // Validações pra avisar o usuario que algo está errado
        if (titulo.isBlank()) {
            _uiState.value = AddTodoUiState.Error("O título não pode estar em branco!")
            return
        }
        if (dificuldade == Dificulty.NONE) {
            _uiState.value = AddTodoUiState.Error("Selecione uma dificuldade válida!")
            return
        }

        // Verificando se o usuário está logado
        val uidLogado = auth.currentUser?.uid
        if (uidLogado == null) {
            _uiState.value = AddTodoUiState.Error("Erro: Usuário não autenticado.")
            return
        }

        // Ativando o loading na tela
        _uiState.value = AddTodoUiState.Loading

        // Criando o novo Tod0
        val novoTodo = Todo(
            id = idTaskAtual,
            title = titulo,
            description = descricao,
            dificuldade = dificuldade,
            done = false,
            deadline = deadline
        )

        // Salvando o novo Tod0 na nuvem
        taskRepository.salvarTodo(
            uid = uidLogado,
            todo = novoTodo,
            onSucesso = {
                _uiState.value = AddTodoUiState.Success

            },
            onErro = { exception ->
                _uiState.value = AddTodoUiState.Error(
                    exception.localizedMessage ?: "Houve um erro ao salvar sua tarefa."
                )
            }
        )
    }

    //  Reseta apenas o fluxo de UI para Idle, sem apagar o modo de edição
    fun resetUiStateToIdle() {
        _uiState.value = AddTodoUiState.Idle
    }

    // Limpa tudo (usado ao fechar a tela ou terminar de salvar)
    fun resetCompletamente() {
        _uiState.value = AddTodoUiState.Idle
        idTaskAtual = ""
        isEditMode = false
    }
}