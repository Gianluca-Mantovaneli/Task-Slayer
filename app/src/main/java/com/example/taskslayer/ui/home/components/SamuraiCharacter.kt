package com.example.taskslayer.ui.home.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.taskslayer.R

/**
 * Representação visual do Guerreiro Samurai que evolui conforme o progresso do usuário.
 * Alterna entre diferentes sprites (Fraco, Normal, Forte) baseado na pontuação/reputação atual.
 */
@Composable
fun SamuraiCharacter(
    currentScore: Int, // Pontuação de 0 a 100
    modifier: Modifier = Modifier
) {
    // Escolha do estado do Samurai baseado na faixa de pontuação
    if (currentScore < 30) {
        // Estado: Ferido / Cansado
        SpriteAnimator(
            spriteSheetResId = R.drawable.samurai_fraco,
            frameCount = 4,
            frameDurationMs = 180L,
            modifier = modifier.size(100.dp)
        )
    } else if (currentScore in 30..<70) {
        // Estado: Saudável / Padrão
        SpriteAnimator(
            spriteSheetResId = R.drawable.samurai_normal,
            frameCount = 10,
            frameDurationMs = 180L,
            modifier = modifier.size(100.dp)
        )
    } else {
        // Estado: Mestre / Poderoso (animação mais rápida)
        SpriteAnimator(
            spriteSheetResId = R.drawable.samurai_forte,
            frameCount = 10,
            frameDurationMs = 100L,
            modifier = modifier.size(100.dp)
        )
    }
}
