package com.example.taskslayer.ui.home.dailie

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskslayer.domain.model.Dailie
import com.example.taskslayer.domain.model.Dificulty
import com.example.taskslayer.domain.model.Repetition
import com.example.taskslayer.ui.home.components.DailieCard
import com.example.taskslayer.tools.SoundEffectsManager
import com.example.taskslayer.ui.theme.TaskSlayerTheme

/**
 * Função de rota para a aba de Dailies (Missões Diárias).
 * Gerencia o estado de carregamento e a interação com o ViewModel de Dailies.
 */
@Composable
fun DailieRoute(
    soundManager: SoundEffectsManager?,
    onNavigateToEdit: (String) -> Unit,
    viewModel: DailiesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Carrega as missões diárias ao iniciar a aba
    LaunchedEffect(Unit) {
        viewModel.carregarDailies()
    }

    when (val state = uiState) {
        is DailiesUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }

        is DailiesUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = state.message, color = MaterialTheme.colorScheme.error)
            }
        }

        is DailiesUiState.Success -> {
            DailieContent(
                dailies = state.dailies,
                soundManager = soundManager,
                onNavigateToEdit = onNavigateToEdit,
                onCheckedChange = { dailie, novoStatus ->
                    // Alterna o status de conclusão da missão no dia atual
                    viewModel.alternarStatusDailie(dailie, novoStatus)
                }
            )
        }
    }
}

/**
 * Conteúdo visual da lista de missões diárias.
 */
@Composable
fun DailieContent(
    dailies: List<Dailie>,
    soundManager: SoundEffectsManager?,
    onNavigateToEdit: (String) -> Unit,
    onCheckedChange: (Dailie, Boolean) -> Unit = { _, _ -> }
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        if (dailies.isEmpty()) {
            // Mensagem quando não há missões cadastradas
            Text(
                text = "Nenhuma missão diária ativa.\nAproveite o descanso, Guerreiro!",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            // Lista de cartões de missões diárias
            LazyColumn(
                modifier = Modifier.padding(top = 4.dp)
            ) {
                items(dailies) { dailie ->
                    DailieCard(
                        titulo = dailie.title,
                        frequencia = dailie.formatarFrequencia(),
                        dificuldade = dailie.dificuldade,
                        done = dailie.done,
                        soundManager = soundManager,
                        onCardClick = { onNavigateToEdit(dailie.id) },
                        onCheckedChange = { novoStatus -> onCheckedChange(dailie, novoStatus) }
                    )
                }
            }
        }
    }
}

/**
 * Extensão utilitária para formatar a exibição da repetição da missão diária.
 */
private fun Dailie.formatarFrequencia(): List<String> {
    val tipo = when (this.repeticao) {
        Repetition.DIARIO -> "Diário"
        Repetition.SEMANAL -> "Semanal"
        Repetition.MENSAL -> "Mensal"
        else -> "Diário"
    }
    return if (this.aCada > 1) {
        listOf(tipo, "a cada", "${this.aCada}", "unidades")
    } else {
        listOf(tipo)
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
fun DailieContentPreview() {
    TaskSlayerTheme {
        DailieContent(
            dailies = listOf(
                Dailie(
                    id = "1",
                    title = "Treinar na Espada",
                    done = false,
                    dificuldade = Dificulty.MEDIO,
                    repeticao = Repetition.DIARIO,
                    aCada = 1
                ),
                Dailie(
                    id = "2",
                    title = "Estudar Alquimia",
                    done = true,
                    dificuldade = Dificulty.TRIVIAL,
                    repeticao = Repetition.SEMANAL,
                    aCada = 2
                )
            ),
            soundManager = null,
            onNavigateToEdit = {},
            onCheckedChange = { _, _ -> }
        )
    }
}
