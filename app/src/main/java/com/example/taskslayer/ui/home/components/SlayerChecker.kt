package com.example.taskslayer.ui.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * Checkbox personalizado com temática Samurai (SlayerChecker).
 * Utiliza cantos cortados e um desenho de "visto" feito manualmente via Canvas.
 */
@Composable
fun SlayerChecker(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val checkerSize = 26.dp
    val shape = AbsoluteCutCornerShape(topRight = 6.dp, bottomLeft = 6.dp)
    val primaryColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier
            .size(checkerSize)
            .clip(shape)
            .background(if (checked) primaryColor else Color.Transparent)
            .border(
                width = 2.dp,
                color = if (checked) primaryColor else MaterialTheme.colorScheme.onSecondary.copy(
                    alpha = 0.5f
                ),
                shape = shape
            )
            .clickable { onCheckedChange(!checked) }
    ) {
        if (checked) {
            // Desenha o símbolo de "visto" personalizado quando marcado
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val checkmarkPath = Path().apply {
                    // Ponto inicial
                    moveTo(w * 0.22f, h * 0.50f)
                    // Curva/Linha para a base do V
                    lineTo(w * 0.32f, h * 0.62f)
                    lineTo(w * 0.42f, h * 0.75f)

                    // Subida do V
                    lineTo(w * 0.58f, h * 0.50f)
                    lineTo(w * 0.72f, h * 0.32f)
                    lineTo(w * 0.88f, h * 0.18f)
                }
                
                // Desenha o contorno principal do visto
                drawPath(
                    path = checkmarkPath,
                    color = Color.Black,
                    style = Stroke(
                        width = 3.5.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
                
                // Desenha um detalhe interno para profundidade
                drawPath(
                    path = checkmarkPath,
                    color = Color(0xFF252525),
                    style = Stroke(
                        width = 1.2.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
                
                // Pequenos detalhes estilísticos nas pontas do traço
                drawLine(
                    color = Color.Black.copy(alpha = 0.4f),
                    start = Offset(w * 0.42f, h * 0.75f),
                    end = Offset(w * 0.46f, h * 0.90f),
                    strokeWidth = 1.dp.toPx()
                )
                drawLine(
                    color = Color.Black.copy(alpha = 0.4f),
                    start = Offset(w * 0.22f, h * 0.50f),
                    end = Offset(w * 0.12f, h * 0.44f),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }
    }
}
