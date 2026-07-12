package com.example.taskslayer.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.example.taskslayer.R

/**
 * Configuração de Tipografia do Material Design 3.
 */
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    // Outros estilos de texto podem ser personalizados aqui (h1, bodySmall, etc)
)

/**
 * Fonte personalizada com temática Samurai para títulos e elementos de destaque.
 */
val FonteDoTituloSlayer = FontFamily(Font(R.font.samurai_blast))
