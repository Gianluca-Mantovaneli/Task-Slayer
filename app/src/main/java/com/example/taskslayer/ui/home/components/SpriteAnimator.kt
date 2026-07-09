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

@Composable
fun SpriteAnimator(
    spriteSheetResId: Int,
    frameCount: Int,
    frameDurationMs: Long,
    modifier: Modifier = Modifier,
) {
    val bitmap = ImageBitmap.imageResource(id = spriteSheetResId)
    var currentFrame by remember { mutableIntStateOf(0) }

    // Loop infinito controlado para atualizar o frame do sprite sheet
    LaunchedEffect(spriteSheetResId, frameCount) {
        while (isActive) {
            delay(frameDurationMs.milliseconds)
            currentFrame = (currentFrame + 1) % frameCount
        }
    }

    Canvas(modifier = modifier) {
        // Calcula a largura de um único frame com base no tamanho total da imagem
        val frameWidth = bitmap.width / frameCount
        val frameHeight = bitmap.height

        // Define a janela de corte na imagem original
        val srcOffset = IntOffset(x = currentFrame * frameWidth, y = 0)
        val srcSize = IntSize(width = frameWidth, height = frameHeight)

        // Define o tamanho final onde ele vai ser desenhado no Canvas
        val dstSize = IntSize(width = size.width.toInt(), height = size.height.toInt())

        // Desenha apenas o frame atual
        drawImage(
            image = bitmap,
            srcOffset = srcOffset,
            srcSize = srcSize,
            dstSize = dstSize,
            filterQuality = FilterQuality.None
        )
    }
}