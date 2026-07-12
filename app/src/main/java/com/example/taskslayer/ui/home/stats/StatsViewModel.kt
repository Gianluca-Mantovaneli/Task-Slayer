package com.example.taskslayer.ui.home.stats

import androidx.lifecycle.ViewModel
import com.example.taskslayer.data.repository.UserRepository
import com.example.taskslayer.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import java.io.InputStream

/**
 * Estados da interface para a tela de estatísticas.
 */
sealed interface StatsUiState {
    object Loading : StatsUiState
    data class Success(val user: User) : StatsUiState
    data class Error(val message: String) : StatsUiState
}

/**
 * ViewModel que gerencia os dados do perfil e estatísticas do usuário.
 * Também lida com o processamento de imagem de perfil (conversão para Base64).
 */
class StatsViewModel(
    private val userRepository: UserRepository = UserRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow<StatsUiState>(StatsUiState.Loading)
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    /**
     * Busca os dados atualizados do usuário (guerreiro) no Firestore.
     */
    fun carregarEstatisticas() {
        val uidLogado = auth.currentUser?.uid
        if (uidLogado == null) {
            _uiState.value = StatsUiState.Error("Usuário não autenticado.")
            return
        }

        _uiState.value = StatsUiState.Loading

        userRepository.buscarUsuario(
            uid = uidLogado,
            onSucesso = { usuario ->
                _uiState.value = StatsUiState.Success(user = usuario)
            },
            onErro = { excecao ->
                _uiState.value = StatsUiState.Error(
                    excecao.localizedMessage ?: "Erro ao carregar os atributos do guerreiro."
                )
            }
        )
    }

    /**
     * Processa uma imagem selecionada da galeria:
     * 1. Lê a URI
     * 2. Redimensiona para economia de espaço (200x200)
     * 3. Comprime em JPEG
     * 4. Converte em String Base64
     * 5. Salva no Firestore
     */
    fun transformarFotoEmBase64(context: Context, uri: Uri) {
        val userId = auth.currentUser?.uid ?: return
        _uiState.value = StatsUiState.Loading

        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bitmapOriginal = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (bitmapOriginal == null) {
                _uiState.value = StatsUiState.Error("Não foi possível ler a imagem.")
                return
            }

            // Redimensionamento para manter o banco leve
            val bitmapReduzido = Bitmap.createScaledBitmap(bitmapOriginal, 200, 200, true)

            val outputStream = ByteArrayOutputStream()
            bitmapReduzido.compress(Bitmap.CompressFormat.JPEG, 75, outputStream)
            val bytesDaImagem = outputStream.toByteArray()

            // Codificação para string
            val stringBase64 = Base64.encodeToString(bytesDaImagem, Base64.NO_WRAP)

            // Atualização direta no documento do usuário
            FirebaseFirestore.getInstance().collection("usuarios").document(userId)
                .update("imagenPerfilURL", stringBase64)
                .addOnSuccessListener {
                    // Recarrega estatísticas para atualizar a UI com a nova foto
                    carregarEstatisticas()
                }

        } catch (e: Exception) {
            _uiState.value = StatsUiState.Error("Erro ao processar imagem: ${e.localizedMessage}")
        }
    }
}
