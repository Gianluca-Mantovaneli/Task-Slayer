package com.example.taskslayer.ui.home.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.taskslayer.R

@Composable
fun SamuraiCharacter(
    currentScore: Int,
    modifier: Modifier = Modifier
) {
    if (currentScore < 30) {
        SpriteAnimator(
            spriteSheetResId = R.drawable.samurai_fraco,
            frameCount = 4,
            frameDurationMs = 180L,
            modifier = modifier.size(100.dp)
        )
    } else if (currentScore in 30..<70) {
        SpriteAnimator(
            spriteSheetResId = R.drawable.samurai_normal,
            frameCount = 10,
            frameDurationMs = 180L,
            modifier = modifier.size(100.dp)
        )
    } else {
        SpriteAnimator(
            spriteSheetResId = R.drawable.samurai_forte,
            frameCount = 10,
            frameDurationMs = 100L,
            modifier = modifier.size(100.dp)
        )
    }
}