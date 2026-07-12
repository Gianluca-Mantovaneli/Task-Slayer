package com.example.taskslayer.ui.auth

/**
 * Representa os diferentes estados da interface de usuário durante processos de autenticação.
 */
sealed interface AuthUiState {
    // Estado inicial ou em repouso
    object Idle : AuthUiState
    
    // Estado de carregamento (processando requisição)
    object Loading : AuthUiState
    
    // Indica que a operação foi concluída com sucesso
    object Success : AuthUiState
    
    // Indica que ocorreu um erro, contendo a mensagem de erro
    data class Error(val theMessage: String) : AuthUiState
}
