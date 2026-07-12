package com.example.taskslayer.ui.home.stats

import android.content.res.Configuration
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.taskslayer.domain.model.User
import com.example.taskslayer.tools.SoundEffectsManager
import com.example.taskslayer.ui.home.components.SamuraiCharacter
import com.example.taskslayer.ui.theme.FonteDoTituloSlayer
import com.example.taskslayer.ui.theme.TaskSlayerTheme
import android.graphics.BitmapFactory
import android.util.Base64
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView

/**
 * Função de rota para a aba de Estatísticas do Guerreiro.
 * Gerencia a lógica de seleção e corte de imagem de perfil, além de exibir os dados do usuário.
 */
@Composable
fun StatsRoute(
    soundManager: SoundEffectsManager?,
    viewModel: StatsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Launcher para o processo de corte de imagem
    val cropImageLauncher = rememberLauncherForActivityResult(
        contract = CropImageContract()
    ) { result ->
        if (result.isSuccessful) {
            val uriCortada = result.uriContent
            uriCortada?.let { viewModel.transformarFotoEmBase64(context, it) }
        }
    }

    // Launcher para selecionar imagem da galeria
    val galeriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { uriOriginal ->
            val cropOptions = CropImageContractOptions(
                uri = uriOriginal,
                cropImageOptions = CropImageOptions().apply {
                    cropShape = CropImageView.CropShape.OVAL
                    fixAspectRatio = true
                    aspectRatioX = 1
                    aspectRatioY = 1
                    allowRotation = true
                    allowFlipping = false
                }
            )
            cropImageLauncher.launch(cropOptions)
        }
    }

    // Carrega estatísticas ao iniciar
    LaunchedEffect(Unit) {
        viewModel.carregarEstatisticas()
    }

    when (val state = uiState) {
        is StatsUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }

        is StatsUiState.Success -> {
            StatsContent(
                soundManager = soundManager,
                user = state.user,
                onFotoClick = { galeriaLauncher.launch("image/*") }
            )
        }

        is StatsUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(24.dp)
                )
            }
        }
    }
}

/**
 * Conteúdo visual das estatísticas do usuário.
 * Exibe foto de perfil, animação do samurai, barra de progresso e contadores.
 */
@Composable
fun StatsContent(
    soundManager: SoundEffectsManager?,
    user: User,
    onFotoClick: () -> Unit = {}
) {
    // Decodifica a string Base64 da imagem de perfil para um Bitmap exibível
    val bitmapPerfil = remember(user.imagenPerfilURL) {
        if (user.imagenPerfilURL.isNotBlank()) {
            decodificarBase64ParaBitmap(user.imagenPerfilURL)
        } else {
            null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Avatar do Usuário (clicável para trocar)
        AsyncImage(
            model = bitmapPerfil,
            contentDescription = "Foto de perfil de ${user.nome}",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f))
                .clickable { onFotoClick() },
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Nome do Guerreiro
        Text(
            text = user.nome,
            fontFamily = FonteDoTituloSlayer,
            fontSize = 28.sp,
            color = MaterialTheme.colorScheme.primary
        )

        // Ilustração do Personagem Samurai (reage ao status/pontuação)
        SamuraiCharacter(
            currentScore = user.statusAtual,
            modifier = Modifier.size(220.dp).offset(y = (-40).dp),
        )

        // Barra de progresso (Reputação)
        LinearProgressIndicator(
            progress = { user.statusAtual.toFloat() / 100 },
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f),
            strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Reputação ${user.statusAtual}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Seção de contadores detalhados
        Text(
            text = "ESTATÍSTICAS DE BATALHA",
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val tasksConcluidas = user.tasksConcluidas
                val totalDeTasks = user.tasksCriadas
                val textoPorcentagem = if (totalDeTasks > 0) {
                    val porcentagem = (tasksConcluidas.toFloat() / totalDeTasks.toFloat()) * 100
                    String.format("%.0f%%", porcentagem)
                } else {
                    "0%"
                }

                ItemLinhaStats(label = "Tasks Criadas", valor = totalDeTasks.toString())
                ItemLinhaStats(label = "Tasks Concluídas", valor = tasksConcluidas.toString())
                ItemLinhaStats(label = "Tasks Perdidas", valor = user.tasksPerdidas.toString())
                ItemLinhaStats(label = "Hábitos Ativos", valor = user.habitosAtivos.toString())
                ItemLinhaStats(label = "Aproveitamento", valor = textoPorcentagem)
            }
        }
    }
}

/**
 * Componente para exibir uma linha de estatística (Rótulo : Valor).
 */
@Composable
fun ItemLinhaStats(label: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 16.sp, color = MaterialTheme.colorScheme.secondary)
        Text(
            text = valor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            fontFamily = FonteDoTituloSlayer
        )
    }
}

/**
 * Utilitário para converter uma string Base64 em um Bitmap do Android.
 */
fun decodificarBase64ParaBitmap(base64String: String): android.graphics.Bitmap? {
    return try {
        val stringLimpa = base64String
            .replace("data:image/jpeg;base64,", "")
            .replace("data:image/png;base64,", "")
            .trim()

        val imageBytes = Base64.decode(stringLimpa, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    } catch (e: Exception) {
        null
    }
}

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun StatsContentPreview() {
    TaskSlayerTheme {
        StatsContent(
            soundManager = null,
            user = User(
                nome = "Guerreiro Preview",
                statusAtual = 60,
                imagenPerfilURL = "",
                tasksCriadas = 50,
                tasksConcluidas = 45,
                tasksPerdidas = 5,
                habitosAtivos = 2
            )
        )
    }
}
