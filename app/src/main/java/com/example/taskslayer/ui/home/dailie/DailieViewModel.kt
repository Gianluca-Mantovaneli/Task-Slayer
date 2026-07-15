package com.example.taskslayer.ui.home.dailie

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.taskslayer.data.repository.TaskRepository
import com.example.taskslayer.data.repository.UserRepository
import com.example.taskslayer.domain.model.Dailie
import com.example.taskslayer.tools.DateUtils
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Estados da interface de usuário para a lista de missões diárias (Dailies).
 */
sealed interface DailiesUiState {
    object Loading : DailiesUiState
    data class Success(val dailies: List<Dailie>) : DailiesUiState
    data class Error(val message: String) : DailiesUiState
}

/**
 * ViewModel que gerencia a aba de Dailies.
 * Lida com a listagem e alternância de status das missões recorrentes.
 */
class DailiesViewModel(
    private val taskRepository: TaskRepository = TaskRepository(),
    private val userRepository: UserRepository = UserRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow<DailiesUiState>(DailiesUiState.Loading)
    val uiState: StateFlow<DailiesUiState> = _uiState.asStateFlow()

    /**
     * Carrega as missões diárias do usuário logado e reseta as expiradas.
     */
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
                val hoje = DateUtils.getTodayDate()
                val listaProcessada = lista.map { dailie ->
                    // Verifica se deve resetar com base na repetição e frequência
                    if (DateUtils.shouldReset(dailie.lastReset, dailie.repeticao, dailie.aCada)) {
                        taskRepository.resetDailieStatus(
                            uid = uidLogado,
                            taskId = dailie.id,
                            novoStatus = false,
                            lastResetDate = hoje
                        )
                        dailie.copy(done = false, lastReset = hoje)
                    } else {
                        dailie
                    }
                }
                _uiState.value = DailiesUiState.Success(listaProcessada)
            },
            onErro = { excecao ->
                _uiState.value = DailiesUiState.Error(
                    excecao.localizedMessage ?: "Erro ao carregar suas missões diárias."
                )
            }
        )
    }

    /**
     * Alterna o status de conclusão de uma missão diária.
     * Atualiza a UI de forma otimista e sincroniza com o Firestore.
     */
    fun alternarStatusDailie(dailie: Dailie, novoStatus: Boolean) {
        val uidLogado = auth.currentUser?.uid ?: return
        val hoje = DateUtils.getTodayDate()

        // Atualização Otimista local
        val estadoAtual = _uiState.value
        if (estadoAtual is DailiesUiState.Success) {
            val listaAtualizada = estadoAtual.dailies.map { item ->
                if (item.id == dailie.id) item.copy(done = novoStatus, lastReset = hoje) else item
            }
            _uiState.value = DailiesUiState.Success(listaAtualizada)
        }

        // Para Dailies, ao marcar/desmarcar, também garantimos que o lastReset é hoje
        taskRepository.resetDailieStatus(
            uid = uidLogado,
            taskId = dailie.id,
            novoStatus = novoStatus,
            lastResetDate = hoje,
            onSucesso = {
                // Ao concluir ou desmarcar, atualiza a experiência/status do usuário
                userRepository.computarProgressoTarefa(
                    uid = uidLogado,
                    isConcluido = novoStatus,
                    dificuldade = dailie.dificuldade
                )
                Log.d("DailiesViewModel", "Status atualizado no Firebase com sucesso!")
            },
            onErro = { excecao ->
                Log.e(
                    "DailiesViewModel",
                    "Falha ao atualizar no Firebase. Revertendo UI...",
                    excecao
                )
                // Em caso de erro de rede, recarrega os dados reais para manter consistência
                carregarDailies()
            }
        )
    }
}
