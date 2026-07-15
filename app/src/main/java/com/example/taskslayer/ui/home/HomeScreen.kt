package com.example.taskslayer.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.taskslayer.R
import com.example.taskslayer.tools.SoundEffectsManager
import com.example.taskslayer.ui.home.dailie.DailieRoute
import com.example.taskslayer.ui.home.habit.HabitsRoute
import com.example.taskslayer.ui.home.stats.StatsRoute
import com.example.taskslayer.ui.home.todo.TodoRoute
import com.example.taskslayer.ui.theme.FonteDoTituloSlayer
import com.example.taskslayer.ui.theme.TaskSlayerIcons
import com.example.taskslayer.ui.theme.TaskSlayerTheme
import kotlin.math.roundToInt
import com.example.taskslayer.ui.theme.AppThemeMode
import com.google.firebase.auth.FirebaseAuth

/**
 * Enumeração que define as abas principais da tela inicial.
 */
enum class AbasHome {STATS, TODO, DAILY, HABITS}

/**
 * Função de rota para a tela Home.
 * Envelopa o conteúdo no tema do aplicativo e gerencia o modo de tema.
 */
@Composable
fun HomeRoute(
    abaAtual: AbasHome,
    onAbaChange: (AbasHome) -> Unit,
    onSignOutClick: () -> Unit,
    onAddTodoClick: (String?) -> Unit,
    onAddDailieClick: (String?) -> Unit,
    onAddHabitClick: (String?) -> Unit
){
    var currentTheme by remember { mutableStateOf(AppThemeMode.SYSTEM) }

    TaskSlayerTheme(themeMode = currentTheme) {
        HomeContent(
            abaAtual = abaAtual,
            onAbaChange = onAbaChange,
            currentTheme = currentTheme,
            onThemeChange = { novoTema -> currentTheme = novoTema },
            onSignOutClick = onSignOutClick,
            onAddTodoClick = onAddTodoClick,
            onAddDailieClick = onAddDailieClick,
            onAddHabitClick = onAddHabitClick
        )
    }
}

/**
 * Conteúdo principal da tela Home, incluindo TopAppBar, BottomBar e o conteúdo das abas.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    abaAtual: AbasHome,
    onAbaChange: (AbasHome) -> Unit,
    currentTheme: AppThemeMode,
    onThemeChange: (AppThemeMode) -> Unit,
    onSignOutClick: () -> Unit,
    onAddDailieClick: (String?) -> Unit = {},
    onAddTodoClick: (String?) -> Unit = {},
    onAddHabitClick: (String?) -> Unit = {}
){
    val snackbarHostState = remember { SnackbarHostState() }
    var expandido: Boolean by remember { mutableStateOf(false) }
    var tituloTopBar by remember { mutableStateOf("TaskSlayer") }

    // Gerenciador de efeitos sonoros
    val context = LocalContext.current
    val soundManager = remember { SoundEffectsManager(context) }

    // Configurações para o botão flutuante arrastável (FAB)
    var floatingButtonOffsetX by remember { mutableFloatStateOf(0f) }
    var floatingButtonOffsetY by remember { mutableFloatStateOf(0f) }
    var containerWidth by remember { mutableFloatStateOf(0f) }
    var containerHeight by remember { mutableFloatStateOf(0f) }

    Scaffold(
        // Barra Superior (TopAppBar)
        topBar = {
             tituloTopBar = when (abaAtual) {
                AbasHome.TODO -> stringResource(R.string.title_aba_home_todo)
                AbasHome.DAILY -> stringResource(R.string.title_aba_home_dailie)
                AbasHome.HABITS -> stringResource(R.string.title_aba_home_habit)
                AbasHome.STATS -> stringResource(R.string.title_aba_home_stats)
            }
            TopAppBar(
                title = {
                    Text(text = tituloTopBar,
                        fontFamily = FonteDoTituloSlayer,
                        color = MaterialTheme.colorScheme.primary)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                actions = {
                    Box{
                        // Botão de Menu de Configurações
                        IconButton(onClick = { expandido = true }) {
                            Icon(
                                modifier = Modifier.size(30.dp),
                                contentDescription = "Menu",
                                painter = painterResource(id = TaskSlayerIcons.MenuCog),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        // Menu suspenso com opções de Logout, Idioma e Tema
                        DropdownMenu(
                            expanded = expandido,
                            onDismissRequest = { expandido = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Logout") },
                                leadingIcon = {
                                    Icon(
                                        modifier = Modifier.size(15.dp),
                                        painter = painterResource(id = TaskSlayerIcons.LogoutIcon),
                                        tint = MaterialTheme.colorScheme.primary,
                                        contentDescription = "Logout"
                                    )
                                },
                                onClick = {
                                    expandido = false
                                    onSignOutClick()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Tema") },
                                leadingIcon = {
                                    Icon(
                                        modifier = Modifier.size(15.dp),
                                        painter = painterResource(id = TaskSlayerIcons.ChangeThemeIcon),
                                        tint = MaterialTheme.colorScheme.primary,
                                        contentDescription = "Tema"
                                    )
                                },
                                onClick = {
                                    expandido = false

                                    // Alterna entre tema claro e escuro
                                    val proximoTema = when (currentTheme) {
                                        AppThemeMode.LIGHT -> AppThemeMode.DARK
                                        else -> AppThemeMode.LIGHT
                                    }

                                    onThemeChange(proximoTema)
                                }
                            )
                        }
                    }

                }
            )
        },
        // Barra Inferior de Navegação (BottomBar)
        bottomBar = {

            NavigationBar(
                containerColor = Color.Transparent
            ) {
                // Item: Estatísticas
                NavigationBarItem(
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.tertiary
                    ),
                    selected = abaAtual == AbasHome.STATS,
                    onClick = { onAbaChange(AbasHome.STATS) },
                    icon = {
                        Icon(
                            modifier = Modifier.size(40.dp),
                            painter = painterResource(id = TaskSlayerIcons.statsMenuIcon),
                            contentDescription = "Estatísticas"
                        )
                    }
                )
                // Item: To-Do (Tarefas)
                NavigationBarItem(
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.tertiary
                    ),
                    selected = abaAtual == AbasHome.TODO,
                    onClick = { onAbaChange(AbasHome.TODO) },
                    icon = {
                        Icon(
                            modifier = Modifier.size(40.dp),
                            painter = painterResource(id = TaskSlayerIcons.todoMenuIcon),
                            contentDescription = "Tarefas"
                        )
                    }
                )
                // Item: Hábitos
                NavigationBarItem(
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.tertiary
                    ),
                    selected = abaAtual == AbasHome.HABITS,
                    onClick = { onAbaChange(AbasHome.HABITS) },
                    icon = {
                        Icon(
                            modifier = Modifier.size(40.dp),
                            painter = painterResource(id = TaskSlayerIcons.habitsMenuIcon),
                            contentDescription = "Hábitos"
                        )
                    }
                )
                // Item: Dailies (Tarefas Diárias)
                NavigationBarItem(
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.tertiary
                    ),
                    selected = abaAtual == AbasHome.DAILY,
                    onClick = { onAbaChange(AbasHome.DAILY) },
                    icon = {
                        Icon(
                            modifier = Modifier.size(40.dp),
                            painter = painterResource(id = TaskSlayerIcons.dailyMenuIcon),
                            contentDescription = "Dailies"
                        )
                    }
                )
            }
        },
        // Host para SnackBar (mensagens rápidas)
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        // Área de conteúdo principal
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .onSizeChanged { size ->
                    containerWidth = size.width.toFloat()
                    containerHeight = size.height.toFloat()
                }
        ) {
            when (abaAtual) {
                // Roteamento dinâmico baseado na aba selecionada
                AbasHome.STATS -> StatsRoute(soundManager)
                AbasHome.TODO -> TodoRoute(
                    soundManager,
                    onNavigateToEdit = { todoId ->
                        onAddTodoClick(todoId)
                    }
                )

                AbasHome.DAILY -> DailieRoute(
                    soundManager,
                    onNavigateToEdit = { dailieId ->
                        onAddDailieClick(dailieId)
                    }
                )
                AbasHome.HABITS -> HabitsRoute(
                    soundManager,
                    onHabitClick = { habitId ->
                        onAddHabitClick(habitId)
                    }
                )
            }

            // Botão Flutuante (FAB) para adicionar itens (não exibido em STATS)
            if(abaAtual != AbasHome.STATS) {
                FloatingActionButton(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .offset {
                            IntOffset(
                                floatingButtonOffsetX.roundToInt(),
                                floatingButtonOffsetY.roundToInt()
                            )
                        }
                        .pointerInput(containerWidth, containerHeight) {
                            if (containerWidth == 0f || containerHeight == 0f) return@pointerInput
                            val fabSizePx = 56.dp.toPx()
                            val marginPx = 16.dp.toPx()
                            val minX = -(containerWidth - fabSizePx - (marginPx * 2))
                            val maxX = 0f
                            val minY = -(containerHeight - fabSizePx - (marginPx * 2))
                            val maxY = 0f

                            // Detecta gestos de arraste para mover o FAB na tela
                            detectDragGestures { change, dragAmount ->
                                change.consume()

                                floatingButtonOffsetX =
                                    (floatingButtonOffsetX + dragAmount.x).coerceIn(minX, maxX)
                                floatingButtonOffsetY =
                                    (floatingButtonOffsetY + dragAmount.y).coerceIn(minY, maxY)
                            }
                        },
                    onClick = {
                        when (abaAtual) {
                            AbasHome.TODO -> onAddTodoClick(null)
                            AbasHome.DAILY -> onAddDailieClick(null)
                            AbasHome.HABITS -> onAddHabitClick(null)
                            else -> {}
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.tertiary
                ) {
                    // Ícone com sombra para o botão de adicionar
                    Icon(
                        modifier = Modifier
                            .size(30.dp)
                            .offset(x = 2.dp, y = 2.dp),
                        painter = painterResource(id = TaskSlayerIcons.AddIcon),
                        contentDescription = null,
                        tint = Color.Black.copy(alpha = 0.7f)
                    )
                    Icon(
                        modifier = Modifier.size(30.dp),
                        painter = painterResource(id = TaskSlayerIcons.AddIcon),
                        contentDescription = "Adicionar Tarefa",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeContentPreview(){
    TaskSlayerTheme {
        HomeContent(
            abaAtual = AbasHome.STATS,
            onAbaChange = {},
            currentTheme = AppThemeMode.DARK,
            onThemeChange = {},
            onSignOutClick = {}
        )
    }
}
