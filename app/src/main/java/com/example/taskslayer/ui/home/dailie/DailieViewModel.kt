package com.example.taskslayer.ui.home.dailie

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.taskslayer.data.repository.TaskRepository
import com.example.taskslayer.domain.model.Dailie
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed interface DailiesUiState {
    object Loading : DailiesUiState
    data class Success(val dailies: List<Dailie>) : DailiesUiState
    data class Error(val message: String) : DailiesUiState
}

class DailiesViewModel : ViewModel() {

    private val taskRepository = TaskRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow<DailiesUiState>(DailiesUiState.Loading)
    val uiState: StateFlow<DailiesUiState> = _uiState.asStateFlow()

    fun carregarDailies() {
        val uidLogado = auth.currentUser?.uid
        if (uidLogado == null) {
            _uiState.value = DailiesUiState.Error("Usuário não autenticado.")
            return
        }

        _uiState.value = DailiesUiState.Loading

        taskRepository.listarDailies(
            uid = uidLogado,
            onSucesso = { lista ->
                _uiState.value = DailiesUiState.Success(lista)
            },
            onErro = { excecao ->
                _uiState.value = DailiesUiState.Error(
                    excecao.localizedMessage ?: "Erro ao carregar suas missões diárias."
                )
            }
        )
    }

    fun alternarStatusDailie(dailie: Dailie, novoStatus: Boolean) {
        val uidLogado = auth.currentUser?.uid ?: return

        val estadoAtual = _uiState.value
        if (estadoAtual is DailiesUiState.Success) {
            val listaAtualizada = estadoAtual.dailies.map {
                if (it.id == dailie.id) it.copy(done = novoStatus) else it
            }
            _uiState.value = DailiesUiState.Success(listaAtualizada)
        }

        taskRepository.atualizarStatusTarefa(
            uid = uidLogado,
            taskId = dailie.id,
            tipoColecao = "dailies",
            novoStatus = novoStatus,
            onSucesso = {
                Log.d("DailiesViewModel", "Status atualizado no Firebase com sucesso!")
            },
            onErro = { excecao ->
                Log.e(
                    "DailiesViewModel",
                    "Falha ao atualizar no Firebase. Revertendo UI...",
                    excecao
                )
                carregarDailies()
            }
        )
    }
}