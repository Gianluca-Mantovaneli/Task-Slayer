package com.example.taskslayer.ui.home.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.taskslayer.domain.model.Dificulty
import com.example.taskslayer.tools.SoundEffectsManager
import com.example.taskslayer.ui.theme.TaskSlayerIcons
import com.example.taskslayer.ui.theme.TaskSlayerTheme

/**
 * Componente de cartão para exibir uma missão diária (Dailie).
 * Similar ao TodoCard, mas focado na repetição frequente das tarefas.
 */
@Composable
fun DailieCard(
    titulo: String,
    frequencia: List<String>, // Lista de rótulos de frequência (ex: ["Diário", "a cada", "2", "dias"])
    dificuldade: Dificulty,
    done: Boolean,
    soundManager: SoundEffectsManager?,
    onCardClick: () -> Unit,
    onCheckedChange: (Boolean) -> Unit
){
    // Define o ícone de dificuldade
    val iconeDificuldade = when (dificuldade) {
        Dificulty.TRIVIAL -> TaskSlayerIcons.trivialDificultyIcon
        Dificulty.FACIL -> TaskSlayerIcons.easyDificultyIcon
        Dificulty.MEDIO -> TaskSlayerIcons.mediumDificultyIcon
        Dificulty.DIFICIL -> TaskSlayerIcons.hardDificultyIcon
        else -> TaskSlayerIcons.trivialDificultyIcon
    }

    Card(
        onClick = onCardClick,
        modifier = Modifier.fillMaxWidth().height(130.dp).padding(10.dp),
        shape = AbsoluteCutCornerShape(topLeft = 20.dp, bottomRight = 20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ){
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.fillMaxSize().weight(1f).padding(10.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Título da Missão com sombra
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                    Text(
                        text = titulo,
                        style = MaterialTheme.typography.titleMedium.copy(
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.6f),
                                offset = Offset(x = 3f, y = 3f),
                                blurRadius = 4f
                            )
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Rodapé do cartão: Frequência e Checkbox
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Exibição da frequência formatada
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        frequencia.forEach { termo ->
                            Text(
                                text = termo,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    shadow = Shadow(
                                        color = Color.Black.copy(alpha = 0.6f),
                                        offset = Offset(x = 3f, y = 3f),
                                        blurRadius = 4f
                                    )
                                ),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1
                            )
                        }
                    }

                    // Botão de conclusão personalizado
                    SlayerChecker(
                        checked = done,
                        onCheckedChange = { selecionado ->
                            if (selecionado) {
                                soundManager?.playSlashSound()
                            }
                            onCheckedChange(selecionado)
                        }
                    )
                }
            }

            // Ícone de Dificuldade
            Box {
                Icon(
                    painter = painterResource(id = iconeDificuldade),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp).offset(x = 2.dp, y = 2.dp),
                    tint = Color.Black.copy(alpha = 0.7f)
                )
                Icon(
                    painter = painterResource(id = iconeDificuldade),
                    contentDescription = "Dificuldade da Missão",
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewDailieCard(){
    TaskSlayerTheme {
        DailieCard(
            titulo = "Treinar artes marciais",
            frequencia = listOf("Diário"),
            dificuldade = Dificulty.MEDIO,
            done = false,
            soundManager = null,
            onCardClick = {},
            onCheckedChange = {}
        )
    }
}
