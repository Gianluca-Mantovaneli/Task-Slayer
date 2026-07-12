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
import com.example.taskslayer.domain.model.Dificulty
import com.example.taskslayer.tools.SoundEffectsManager
import com.example.taskslayer.ui.theme.TaskSlayerIcons
import com.example.taskslayer.ui.theme.TaskSlayerTheme

/**
 * Componente de cartão para exibir um Hábito.
 * Contém um botão lateral para registrar a prática do hábito, o título e o ícone de dificuldade.
 */
@Composable
fun HabitCard(
    titulo : String,
    habitEffect: Boolean, // true = Positivo (+ Exp), false = Negativo (- HP)
    dificuldade : Dificulty,
    soundManager: SoundEffectsManager?,
    onHabitClick: () -> Unit = {},
    onActionClick: () -> Unit = {}
){
    // Determina o ícone de dificuldade
    val iconeDificuldade = when (dificuldade) {
        Dificulty.TRIVIAL -> TaskSlayerIcons.trivialDificultyIcon
        Dificulty.FACIL -> TaskSlayerIcons.easyDificultyIcon
        Dificulty.MEDIO -> TaskSlayerIcons.mediumDificultyIcon
        Dificulty.DIFICIL -> TaskSlayerIcons.hardDificultyIcon
        else -> TaskSlayerIcons.trivialDificultyIcon
    }
    
    // Determina o ícone de ação do hábito (Positivo ou Negativo)
    val iconeHabit = if (habitEffect) TaskSlayerIcons.positiveHabitIcon else TaskSlayerIcons.negativeHabitIcon

    Card(
        onClick = onHabitClick,
        modifier = Modifier.fillMaxWidth().height(130.dp).padding(10.dp),
        shape = AbsoluteCutCornerShape(topLeft = 20.dp, bottomRight = 20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ){
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Botão de Ação do Hábito (Ícone à esquerda)
            Box(
                modifier = Modifier.clickable {
                    soundManager?.playSlashSound() // Som de corte ao praticar o hábito
                    onActionClick()
                }
            ){
                Icon(
                    painter = painterResource(id = iconeHabit),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp).offset(x = 2.dp, y = 2.dp),
                    tint = Color.Black.copy(alpha = 0.7f)
                )
                Icon(
                    painter = painterResource(id = iconeHabit),
                    contentDescription = "Botão de marcar hábito",
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }
            
            // Título do Hábito
            Text(
                modifier = Modifier.padding(horizontal = 10.dp).weight(1f),
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
            
            // Ícone de Dificuldade (à direita)
            Box {
                Icon(
                    painter = painterResource(id = iconeDificuldade),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp).offset(x = 2.dp, y = 2.dp),
                    tint = Color.Black.copy(alpha = 0.7f)
                )
                Icon(
                    painter = painterResource(id = iconeDificuldade),
                    contentDescription = "Dificuldade da Tarefa",
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewHabitCard(){
    TaskSlayerTheme {
        HabitCard(
            "Treinar Caligrafia Japonesa",
            true,
            Dificulty.MEDIO,
            soundManager = null
        )
    }
}
