package com.example.taskslayer.ui.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.time.Duration.Companion.milliseconds

/**
 * Componente genérico para animação de Sprites (2D).
 * Recebe uma imagem contendo vários frames (Sprite Sheet) e os alterna ciclicamente.
 */
@Composable
fun SpriteAnimator(
    spriteSheetResId: Int, // Recurso da imagem (ex: R.drawable.samurai_idle)
    frameCount: Int,       // Total de frames na horizontal da imagem
    frameDurationMs: Long, // Velocidade da animação
    modifier: Modifier = Modifier,
) {
    val bitmap = ImageBitmap.imageResource(id = spriteSheetResId)
    var currentFrame by remember { mutableIntStateOf(0) }

    // Loop de animação que roda enquanto o componente estiver visível
    LaunchedEffect(spriteSheetResId, frameCount) {
        while (isActive) {
            delay(frameDurationMs.milliseconds)
            currentFrame = (currentFrame + 1) % frameCount
        }
    }

    Canvas(modifier = modifier) {
        // Calcula as dimensões de cada frame individual
        val frameWidth = bitmap.width / frameCount
        val frameHeight = bitmap.height

        // Define o retângulo de origem (qual parte da imagem ler)
        val srcOffset = IntOffset(x = currentFrame * frameWidth, y = 0)
        val srcSize = IntSize(width = frameWidth, height = frameHeight)

        // Define o tamanho de destino (onde desenhar na tela)
        val dstSize = IntSize(width = size.width.toInt(), height = size.height.toInt())

        // Desenha o frame recortado. FilterQuality.None mantém o aspecto "pixel art" se a imagem for pequena.
        drawImage(
            image = bitmap,
            srcOffset = srcOffset,
            srcSize = srcSize,
            dstSize = dstSize,
            filterQuality = FilterQuality.None
        )
    }
}
