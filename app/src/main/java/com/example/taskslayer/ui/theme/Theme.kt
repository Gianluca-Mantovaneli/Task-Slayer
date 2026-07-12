package com.example.taskslayer.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Opções de modo de tema suportadas pelo aplicativo.
 */
enum class AppThemeMode {
    SYSTEM, // Segue a configuração do sistema Android
    LIGHT,  // Força o uso do tema claro
    DARK    // Força o uso do tema escuro
}

/**
 * Esquema de cores para o Tema Escuro.
 */
private val DarkColorScheme = darkColorScheme(
    primary = LaranjaSamurai,
    secondary = CinzaAco,
    tertiary = AzulLamina,
    background = CinzaGraphite
)

/**
 * Esquema de cores para o Tema Claro.
 */
private val LightColorScheme = lightColorScheme(
    primary = LaranjaTerracota,
    secondary = PretoObisidiana,
    tertiary = AzulIndico,
    background = BrancoPergaminho
)

/**
 * Tema principal do Task Slayer.
 * Gerencia a alternância entre temas e aplica as cores e tipografia definidas.
 */
@Composable
fun TaskSlayerTheme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    dynamicColor: Boolean = false, // Suporte a cores dinâmicas do Android 12+
    content: @Composable () -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()

    // Decide se deve usar o tema escuro com base na preferência do usuário ou sistema
    val darkTheme = when (themeMode) {
        AppThemeMode.SYSTEM -> isSystemDark
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Ajusta a cor da barra de status para combinar com o tema
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
