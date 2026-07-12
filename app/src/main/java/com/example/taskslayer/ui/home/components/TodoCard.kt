package com.example.taskslayer.ui.home.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.alpha
import com.example.taskslayer.tools.DateUtils
import com.example.taskslayer.domain.model.Dificulty
import com.example.taskslayer.tools.SoundEffectsManager
import com.example.taskslayer.ui.theme.TaskSlayerIcons
import com.example.taskslayer.ui.theme.TaskSlayerTheme

/**
 * Componente de cartão para exibir uma tarefa To-Do individual.
 * Apresenta título, prazo, nível de dificuldade e um checkbox personalizado (SlayerChecker).
 */
@Composable
fun TodoCard(
    titulo: String ,
    deadline: String? = null,
    dificuldade: Dificulty,
    soundManager: SoundEffectsManager?,
    done: Boolean,
    onTaskCheckedChange: (Boolean) -> Unit = {},
    onCardClick: () -> Unit = {}
){
    // Verifica se a tarefa está expirada
    val isExpired = DateUtils.isExpired(deadline)
    // Bloqueia a conclusão se estiver expirada e não feita
    val isEnabled = !(isExpired && !done)

    // Seleciona o ícone baseado na dificuldade da tarefa
    val iconeDificuldade = when (dificuldade) {
        Dificulty.TRIVIAL -> TaskSlayerIcons.trivialDificultyIcon
        Dificulty.FACIL -> TaskSlayerIcons.easyDificultyIcon
        Dificulty.MEDIO -> TaskSlayerIcons.mediumDificultyIcon
        Dificulty.DIFICIL -> TaskSlayerIcons.hardDificultyIcon
        else -> TaskSlayerIcons.trivialDificultyIcon
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .padding(10.dp)
            .clickable { onCardClick() },
        // Formato com cantos cortados para temática samurai
        shape = AbsoluteCutCornerShape(topLeft = 20.dp, bottomRight = 20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isExpired && !done) 
                MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f) 
            else 
                MaterialTheme.colorScheme.secondary
        ),
        border = BorderStroke(
            width = 2.dp,
            color = if (isExpired && !done) Color.Red.copy(alpha = 0.5f) else MaterialTheme.colorScheme.primary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ){
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(10.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Título da Tarefa com efeito de sombra
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
                        color = if (isExpired && !done) Color.Gray else MaterialTheme.colorScheme.primary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                // Prazo final e Checkbox de conclusão
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = if (isExpired && !done) "$deadline (EXPIRADA)" else deadline ?: "Sem prazo",
                        style = MaterialTheme.typography.titleMedium.copy(
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.6f),
                                offset = Offset(x = 3f, y = 3f),
                                blurRadius = 4f
                            )
                        ),
                        color = if (isExpired && !done) Color.Red else MaterialTheme.colorScheme.primary
                    )

                    // Checkbox temático (SlayerChecker) que toca som ao marcar
                    SlayerChecker(
                        checked = done,
                        onCheckedChange = { selecionado ->
                            if (isEnabled) {
                                if (selecionado) {
                                    soundManager?.playSlashSound()
                                }
                                onTaskCheckedChange(selecionado)
                            }
                        },
                        modifier = Modifier.alpha(if (isEnabled) 1f else 0.5f)
                    )
                }
            }
            
            // Ícone de Dificuldade com efeito de sombra
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
                    tint = if (isExpired && !done) Color.Gray else MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewTodoCard(){
    TaskSlayerTheme {
        TodoCard(
            "Cortar lenha no pátio",
            "12/12/2024",
            Dificulty.TRIVIAL,
            done = false,
            soundManager = null
        )
    }
}
