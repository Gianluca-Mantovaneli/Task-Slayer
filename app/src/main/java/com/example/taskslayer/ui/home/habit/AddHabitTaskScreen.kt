package com.example.taskslayer.ui.home.habit

import android.content.res.Configuration
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskslayer.domain.model.Dificulty
import com.example.taskslayer.domain.model.Repetition
import com.example.taskslayer.ui.theme.TaskSlayerIcons
import com.example.taskslayer.ui.theme.TaskSlayerTheme

/**
 * Função de rota para a tela de Adição/Edição de Hábitos.
 * Gerencia a comunicação entre o ViewModel e a interface do usuário.
 */
@Composable
fun AddHabitTaskRoute(
    habitId: String? = null, // Recebe o ID caso seja edição
    onBackClick: () -> Unit,
    viewModel: AddHabitTaskViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val ctx = LocalContext.current
    var mensagemSucesso by remember { mutableStateOf("") }

    // Carrega dados se estiver editando um hábito existente
    LaunchedEffect(habitId) {
        if (!habitId.isNullOrBlank()) {
            viewModel.prepararParaEdicao(habitId)
        }
    }

    // Trata o botão de voltar do sistema
    BackHandler {
        viewModel.resetCompletamente()
        onBackClick()
    }

    // Reage aos estados do ViewModel (Sucesso ou Erro)
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is AddHabitUiState.Success -> {
                Toast.makeText(ctx, mensagemSucesso, Toast.LENGTH_SHORT).show()
                viewModel.resetCompletamente()
                onBackClick()
            }

            is AddHabitUiState.Error -> {
                Toast.makeText(ctx, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetUiStateToIdle()
            }

            else -> {}
        }
    }

    AddHabitTaskContent(
        viewModel = viewModel,
        isEditMode = viewModel.isEditMode,
        onBackClick = {
            viewModel.resetCompletamente()
            onBackClick()
        },
        onSaveTask = { titulo, descricao, dificuldade, efeitoHabito ->
            mensagemSucesso = "Hábito salvo com sucesso!"
            viewModel.salvarTarefaHabit(
                titulo,
                descricao,
                dificuldade,
                efeitoHabito,
                Repetition.NONE // Atualmente não utiliza repetição customizada
            )
        },
        onDeleteTask = {
            if (habitId != null) {
                mensagemSucesso = "Hábito deletado com sucesso!"
                viewModel.deletarTarefaHabit(habitId)
            } else {
                viewModel.resetCompletamente()
                onBackClick()
            }
        }
    )
}

/**
 * Conteúdo visual do formulário de Hábito.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitTaskContent(
    viewModel: AddHabitTaskViewModel = viewModel(),
    isEditMode: Boolean = false,
    onBackClick: () -> Unit = {},
    onSaveTask: (titulo: String, descricao: String, dificuldade: Dificulty, efeitoHabito: Boolean) -> Unit = {_,_,_,_ ->},
    onDeleteTask: () -> Unit = {}
){
    // Estados locais dos campos
    var titulo by rememberSaveable { mutableStateOf("") }
    var descricao by rememberSaveable { mutableStateOf("") }
    var dificuldadeSelecionada by rememberSaveable { mutableStateOf(Dificulty.NONE) }
    var efeitoHabito by rememberSaveable { mutableStateOf(true) } // true = Positivo, false = Negativo

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Preenche campos ao carregar dados para edição
    LaunchedEffect(uiState) {
        if (uiState is AddHabitUiState.Loaded) {
            val habit = (uiState as AddHabitUiState.Loaded).habit
            titulo = habit.title
            descricao = habit.description
            dificuldadeSelecionada = habit.dificuldade
            efeitoHabito = habit.impact

            viewModel.resetUiStateToIdle()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = if (isEditMode) "Editar Hábito" else "Adicionar Hábito") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    if (isEditMode) {
                        IconButton(onClick = onDeleteTask) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Deletar",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    IconButton(
                        onClick = {
                            onSaveTask(titulo, descricao, dificuldadeSelecionada, efeitoHabito)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Salvar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Campo: Título
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título") },
            )
            // Campo: Descrição
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .height(100.dp),
                value = descricao,
                onValueChange = { descricao = it },
                label = { Text("Descrição") },
            )
            
            // Seção: Dificuldade
            Text(
                text = "Dificuldade",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 10.dp),
                textAlign = TextAlign.Center
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Opções de Dificuldade
                DifficultyItem(label = "Trivial", icon = TaskSlayerIcons.trivialDificultyIcon, isSelected = dificuldadeSelecionada == Dificulty.TRIVIAL, onClick = { dificuldadeSelecionada = Dificulty.TRIVIAL })
                DifficultyItem(label = "Fácil", icon = TaskSlayerIcons.easyDificultyIcon, isSelected = dificuldadeSelecionada == Dificulty.FACIL, onClick = { dificuldadeSelecionada = Dificulty.FACIL })
                DifficultyItem(label = "Média", icon = TaskSlayerIcons.mediumDificultyIcon, isSelected = dificuldadeSelecionada == Dificulty.MEDIO, onClick = { dificuldadeSelecionada = Dificulty.MEDIO })
                DifficultyItem(label = "Difícil", icon = TaskSlayerIcons.hardDificultyIcon, isSelected = dificuldadeSelecionada == Dificulty.DIFICIL, onClick = { dificuldadeSelecionada = Dificulty.DIFICIL })
            }

            // Seção: Efeito do Hábito (Se ajuda ou atrapalha o Guerreiro)
            Text(
                text = "Efeito",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Positivo", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Text("Negativo", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { efeitoHabito = true }, modifier = Modifier.weight(1f)) {
                    Icon(
                        painter = painterResource(id = TaskSlayerIcons.positiveHabitIcon),
                        contentDescription = "Efeito positivo",
                        tint = if (efeitoHabito) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                    )
                }
                IconButton(onClick = { efeitoHabito = false }, modifier = Modifier.weight(1f)) {
                    Icon(
                        painter = painterResource(id = TaskSlayerIcons.negativeHabitIcon),
                        contentDescription = "Efeito negativo",
                        tint = if (!efeitoHabito) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }
    }
}

/**
 * Item para seleção de dificuldade.
 */
@Composable
private fun DifficultyItem(label: String, icon: Int, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(4.dp)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
        Icon(painter = painterResource(id = icon), contentDescription = label, tint = color, modifier = Modifier.size(28.dp))
        Text(text = label, color = color, style = MaterialTheme.typography.bodySmall, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, modifier = Modifier.padding(top = 4.dp))
    }
}

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AddHabitTaskContentPreview(){
    TaskSlayerTheme {
        AddHabitTaskContent()
    }
}
