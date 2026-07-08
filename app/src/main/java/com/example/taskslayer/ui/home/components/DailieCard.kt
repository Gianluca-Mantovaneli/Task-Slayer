package com.example.taskslayer.ui.home.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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


@Composable
fun DailieCard(
    titulo : String ,
    frequencia : List<String> ,
    dificuldade : Dificulty,
    soundManager: SoundEffectsManager?,
){
    var done by remember { mutableStateOf(false) }

    val iconeDificuldade = when (dificuldade) {
        Dificulty.TRIVIAL -> TaskSlayerIcons.trivialDificultyIcon
        Dificulty.FACIL -> TaskSlayerIcons.easyDificultyIcon
        Dificulty.MEDIO -> TaskSlayerIcons.mediumDificultyIcon
        Dificulty.DIFICIL -> TaskSlayerIcons.hardDificultyIcon
        else -> { TaskSlayerIcons.trivialDificultyIcon}
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .padding(10.dp),
        shape = AbsoluteCutCornerShape(topLeft = 20.dp, bottomRight = 20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary
        ),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
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
                // Linha do Título
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = titulo,
                        style = MaterialTheme.typography.titleMedium.copy(
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.6f),
                                offset = Offset(
                                    x = 3f,
                                    y = 3f
                                ),
                                blurRadius = 4f
                            )
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        frequencia.forEach { dia ->
                            Text(
                                text = dia,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    shadow = Shadow(
                                        color = Color.Black.copy(alpha = 0.6f),
                                        offset = Offset(
                                            x = 3f,
                                            y = 3f
                                        ),
                                        blurRadius = 4f
                                    )
                                ),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                    }
                    SlayerChecker(
                        checked = done,
                        onCheckedChange = { atual ->
                            done = atual
                            if (atual) {
                                soundManager?.playSlashSound()
                            }
                        }
                    )
                }

            }
            Box(){
                Icon(
                    painter = painterResource(id = iconeDificuldade),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .offset(x = 2.dp, y = 2.dp),
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
    TaskSlayerTheme() {
        DailieCard(
            "Título grande pra testar essa porra haaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
            listOf("Seg", "Qua", "Sex"),
            Dificulty.TRIVIAL,
            soundManager = null
        )
    }
}