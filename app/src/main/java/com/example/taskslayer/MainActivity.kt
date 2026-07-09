package com.example.taskslayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.taskslayer.ui.home.AbasHome
import com.example.taskslayer.ui.home.dailie.AddDailieTaskRoute
import com.example.taskslayer.ui.home.habit.AddHabitTaskRoute
import com.example.taskslayer.ui.home.HomeRoute
import com.example.taskslayer.ui.auth.login.LoginRoute
import com.example.taskslayer.ui.auth.register.RegisterRoute
import com.example.taskslayer.ui.theme.TaskSlayerTheme
import com.example.taskslayer.ui.home.todo.AddTodoTaskRoute
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

                var abaHomeAtual by remember { mutableStateOf(AbasHome.STATS) }

                var idTaskParaEditar by remember { mutableStateOf<String?>(null) }

                // Sistema de roteamento baseado em string
                when (telaAtual) {
                    "login" -> LoginRoute(
                        onLoginSuccess = {
                            telaAtual = "home" // Vai para a home após fazer login
                        },
                        onNavigateToRegister = {
                            telaAtual =
                                "register" // Vai para a tela de registro após clicar no botão
                        }
                    )
                    "register" -> RegisterRoute(
                        onRegisterSuccess = {
                            telaAtual = "home" // Vai para a home após criar a conta
                        }
                    )
                    "home" -> HomeRoute(
                        abaAtual = abaHomeAtual,
                        onAbaChange = { novaAba ->
                            abaHomeAtual = novaAba
                        },
                        onSignOutClick = {
                            telaAtual = "login" // Vai para o login após deslogar
                        },
                        onAddTodoClick = { idRecebido ->
                            idTaskParaEditar = idRecebido
                            telaAtual =
                                "addTodoTask" // Vai para a tela de adicionar tarefa após clicar no botão
                        },
                        onAddDailieClick = { idRecebido ->
                            idTaskParaEditar = idRecebido
                            telaAtual =
                                "addDailieTask" // Vai para a tela de adicionar tarefa após clicar no botão
                        },
                        onAddHabitClick = { idRecebido ->
                            idTaskParaEditar = idRecebido
                            telaAtual =
                                "addHabitsTask" // Vai para a tela de adicionar tarefa após clicar no botão
                        }
                    )

                    "addTodoTask" -> AddTodoTaskRoute(
                        taskId = idTaskParaEditar,
                        onBackClick = {
                            idTaskParaEditar = null
                            telaAtual = "home"
                        }
                    )

                    "addDailieTask" -> AddDailieTaskRoute(
                        taskId = idTaskParaEditar,
                        onBackClick = {
                            idTaskParaEditar = null
                            telaAtual =
                                "home" // Vai para a home após voltar da tela de adicionar tarefa
                        }
                    )

                    "addHabitsTask" -> AddHabitTaskRoute(
                        habitId = idTaskParaEditar,
                        onBackClick = {
                            idTaskParaEditar = null
                            telaAtual = "home"
                        }
                    )
                }
            }
        }
    }
}