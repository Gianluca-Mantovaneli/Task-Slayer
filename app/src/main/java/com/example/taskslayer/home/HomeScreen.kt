package com.example.taskslayer.home

import android.content.res.Configuration
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.taskslayer.R
import com.example.taskslayer.tools.SoundEffectsManager
import com.example.taskslayer.ui.theme.FonteDoTituloSlayer
import com.example.taskslayer.ui.theme.TaskSlayerIcons
import com.example.taskslayer.ui.theme.TaskSlayerTheme

enum class AbasHome {STATS, TODO, DAILY, HABITS}

@Composable
fun HomeRoute(){
    HomeContent()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(){
    val snackbarHostState = remember { SnackbarHostState() }
    var expandido: Boolean by remember { mutableStateOf(false) }
    var abaAtual by remember { mutableStateOf(AbasHome.STATS) }
    var tituloTopBar by remember { mutableStateOf("TaskSlayer") }

    // criando o soundManager
    val context = LocalContext.current
    val soundManager = remember { SoundEffectsManager(context) }



    Scaffold(
        // 1. Gaveta do Topo
        topBar = {
             tituloTopBar = when (abaAtual) {
                AbasHome.TODO -> stringResource(R.string.title_aba_home_todo)
                AbasHome.DAILY -> stringResource(R.string.title_aba_home_dailie)
                AbasHome.HABITS -> stringResource(R.string.title_aba_home_habit)
                AbasHome.STATS -> stringResource(R.string.title_aba_home_stats)
            }
            TopAppBar(
                title = { Text(text = tituloTopBar, fontFamily = FonteDoTituloSlayer, color = MaterialTheme.colorScheme.primary) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                actions = {
                    Box{
                        IconButton(onClick = { expandido = true }) {
                            Icon(
                                modifier = Modifier.size(30.dp),
                                contentDescription = "Menu",
                                painter = painterResource(id = TaskSlayerIcons.MenuCog),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
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
                                // TODO: Fazer a logica de Logout aqui
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Idioma") },
                                leadingIcon = {
                                    Icon(
                                        modifier = Modifier.size(15.dp),
                                        painter = painterResource(id = TaskSlayerIcons.ChangeLanguageIcon),
                                        tint = MaterialTheme.colorScheme.primary,
                                        contentDescription = "Idioma"
                                    )
                                },
                                onClick = {
                                    expandido = false
                                // TODO: Fazer a logica de linguagem aqui
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
                                // TODO: Fazer a logica de tema aqui
                                }
                            )
                        }
                    }

                }
            )
        },
        // 2. Gaveta do Botão Flutuante
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                /* TODO: Abrir criação de tarefa */
                },
                containerColor = MaterialTheme.colorScheme.tertiary
            ) {
                Icon(
                    modifier = Modifier
                        .size(30.dp)
                        .offset(x = 2.dp, y = 2.dp),
                    painter = painterResource(id = TaskSlayerIcons.AddIcon),
                    contentDescription = "Adicionar Tarefa",
                    tint = Color.Black.copy(alpha = 0.7f)
                )
                Icon(
                    modifier = Modifier.size(30.dp),
                    painter = painterResource(id = TaskSlayerIcons.AddIcon),
                    contentDescription = "Adicionar Tarefa",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        // 3. Gaveta da Barra de Navegação
        bottomBar = {

            NavigationBar(
                containerColor = Color.Transparent
            ) {
                // Itens para navegar entre Tarefas e Estatísticas
                NavigationBarItem(
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.tertiary
                    ),
                    selected = abaAtual == AbasHome.STATS,
                    onClick = { abaAtual = AbasHome.STATS },
                    icon = {
                        Icon(
                            modifier = Modifier.size(40.dp),
                            painter = painterResource(id = TaskSlayerIcons.statsMenuIcon),
                            contentDescription = "Estatísticas"
                        )
                    }
                )
                NavigationBarItem(
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.tertiary
                    ),
                    selected = abaAtual == AbasHome.TODO,
                    onClick = { abaAtual = AbasHome.TODO },
                    icon = {
                        Icon(
                            modifier = Modifier.size(40.dp),
                            painter = painterResource(id = TaskSlayerIcons.todoMenuIcon),
                            contentDescription = "Tarefas"
                        )
                    }
                )
                NavigationBarItem(
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.tertiary
                    ),
                    selected = abaAtual == AbasHome.HABITS,
                    onClick = { abaAtual = AbasHome.HABITS },
                    icon = {
                        Icon(
                            modifier = Modifier.size(40.dp),
                            painter = painterResource(id = TaskSlayerIcons.habitsMenuIcon),
                            contentDescription = "Hábitos"
                        )
                    }
                )
                NavigationBarItem(
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.tertiary
                    ),
                    selected = abaAtual == AbasHome.DAILY,
                    onClick = { abaAtual = AbasHome.DAILY },
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
        // 4. Gaveta de Avisos (Snackbar)
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        // O CONTEÚDO PRINCIPAL FICA AQUI:
        // O Scaffold te dá o 'paddingValues' automaticamente para a sua lista de tarefas
        // não sumir para baixo da bottomBar nem da topBar.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // IMPORTANTE: aplica os espaçamentos automáticos
        ) {
            when (abaAtual) {
                // Roteamento das abas
                AbasHome.STATS -> StatsRoute()
                AbasHome.TODO -> TodoRoute()
                AbasHome.DAILY -> DailieRoute()
                AbasHome.HABITS -> HabitsRoute()
            }

        }
    }


}

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeContentPreview(){
    TaskSlayerTheme {
        HomeContent()
    }
}