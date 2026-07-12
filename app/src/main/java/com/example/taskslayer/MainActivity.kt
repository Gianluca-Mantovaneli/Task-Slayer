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

/**
 * Atividade principal do aplicativo Task Slayer.
 * Responsável por gerenciar a navegação básica (roteamento) entre as principais telas do app:
 * Login, Registro, Home e as telas de adição/edição de tarefas.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Habilita o modo de tela cheia (edge-to-edge)
        enableEdgeToEdge()
        
        setContent {
            TaskSlayerTheme {
                // Estado que controla qual tela deve ser exibida ("login", "register", "home", etc.)
                // Verifica se o usuário já está autenticado no Firebase para decidir a tela inicial.
                var telaAtual by remember {
                    val usuarioLogado = FirebaseAuth.getInstance().currentUser
                    val telaInicial = if (usuarioLogado != null) "home" else "login"
                    mutableStateOf(telaInicial)
                }

                // Estado para controlar a aba selecionada na Home
                var abaHomeAtual by remember { mutableStateOf(AbasHome.STATS) }

                // Armazena o ID da tarefa/hábito que está sendo editado (null se for uma nova criação)
                var idTaskParaEditar by remember { mutableStateOf<String?>(null) }

                // Sistema de navegação condicional simples baseado no estado 'telaAtual'
                when (telaAtual) {
                    "login" -> LoginRoute(
                        onLoginSuccess = {
                            telaAtual = "home" // Navega para Home após sucesso no login
                        },
                        onNavigateToRegister = {
                            telaAtual = "register" // Navega para a tela de cadastro
                        }
                    )
                    "register" -> RegisterRoute(
                        onRegisterSuccess = {
                            telaAtual = "home" // Navega para Home após criar conta com sucesso
                        },
                        onBackToLogin = {
                            telaAtual = "login" // Retorna para a tela de login
                        }
                    )
                    "home" -> HomeRoute(
                        abaAtual = abaHomeAtual,
                        onAbaChange = { novaAba ->
                            abaHomeAtual = novaAba
                        },
                        onSignOutClick = {
                            telaAtual = "login" // Retorna ao login após sair da conta
                        },
                        onAddTodoClick = { idRecebido ->
                            idTaskParaEditar = idRecebido
                            telaAtual = "addTodoTask" // Vai para a tela de criação/edição de To-Do
                        },
                        onAddDailieClick = { idRecebido ->
                            idTaskParaEditar = idRecebido
                            telaAtual = "addDailieTask" // Vai para a tela de criação/edição de Diária
                        },
                        onAddHabitClick = { idRecebido ->
                            idTaskParaEditar = idRecebido
                            telaAtual = "addHabitsTask" // Vai para a tela de criação/edição de Hábito
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
                            telaAtual = "home"
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
