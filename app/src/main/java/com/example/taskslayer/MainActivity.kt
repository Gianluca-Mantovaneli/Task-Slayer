package com.example.taskslayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.taskslayer.home.HomeRoute
import com.example.taskslayer.ui.auth.login.LoginRoute
import com.example.taskslayer.ui.auth.register.RegisterRoute
import com.example.taskslayer.ui.theme.TaskSlayerTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaskSlayerTheme {
                // Estado que controla qual tela deve ser exibida ("login", "register" ou "home")
                var telaAtual by remember {
                    val usuarioLogado = FirebaseAuth.getInstance().currentUser
                    val telaInicial = if (usuarioLogado != null) "home" else "login"
                    mutableStateOf(telaInicial)
                }

                // Sistema de roteamento baseado em string
                when (telaAtual) {
                    "login" -> LoginRoute(
                        onLoginSuccess = {
                            telaAtual = "home"
                        },
                        onNavigateToRegister = {
                            telaAtual = "register"
                        }
                    )
                    "register" -> RegisterRoute(
                        onRegisterSuccess = {
                            telaAtual = "home" // Vai para a home após criar a conta
                        }
                    )
                    "home" -> HomeRoute(
                        onSignOutClick = {
                            telaAtual = "login" // Vai para o login após deslogar
                        }
                    )
                }
            }
        }
    }
}