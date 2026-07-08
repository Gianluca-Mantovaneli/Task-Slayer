package com.example.taskslayer.ui.home.habit

import android.content.res.Configuration
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskslayer.domain.model.Dificulty
import com.example.taskslayer.domain.model.Repetition
import com.example.taskslayer.ui.theme.TaskSlayerIcons
import com.example.taskslayer.ui.theme.TaskSlayerTheme

@Composable
fun AddHabitTaskRoute(
    habitId: String? = null, // 🎯 Recebe o ID opcional para caso de Edição
    onBackClick: () -> Unit,
    viewModel: AddHabitTaskViewModel = viewModel() // 🎯 Injeta a ViewModel de Hábitos
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val ctx = LocalContext.current
    var mensagemSucesso by remember { mutableStateOf("") }

    // Preparando o terreno caso seja uma edição de Hábito existente
    LaunchedEffect(habitId) {
        if (!habitId.isNullOrBlank()) {
            viewModel.prepararParaEdicao(habitId)
        }
    }

    // Captura quando o usuário clica no botão físico/gesto de voltar do Android
    BackHandler {
        viewModel.resetCompletamente()
        onBackClick()
    }

    // Vigia do estado: Reage aos retornos da ViewModel
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
            // Salvando com Repetition.NONE padrão já que a tela não possui esse seletor ainda
            viewModel.salvarTarefaHabit(
                titulo,
                descricao,
                dificuldade,
                efeitoHabito,
                Repetition.NONE
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitTaskContent(
    viewModel: AddHabitTaskViewModel = viewModel(),
    isEditMode: Boolean = false,
    onBackClick: () -> Unit = {},
    onSaveTask: (titulo: String, descricao: String, dificuldade: Dificulty, efeitoHabito: Boolean) -> Unit = {_,_,_,_ ->},
    onDeleteTask: () -> Unit = {}
){
    var titulo by rememberSaveable { mutableStateOf("") }
    var descricao by rememberSaveable { mutableStateOf("") }
    var dificuldadeSelecionada by rememberSaveable { mutableStateOf(Dificulty.NONE) }
    var efeitoHabito by rememberSaveable { mutableStateOf(true) }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 🎯 Captura o Hábito carregado do banco para preencher a tela na edição
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
                // 🎯 Título dinâmico baseado no modo da tela
                title = { Text(text = if (isEditMode) "Editar Hábito" else "Adicionar Hábito") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
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
                        // 🎯 enabled removido para permitir validação com Toast via ViewModel!
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
            // Título
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título") },
            )
            // Descrição
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .height(100.dp),
                value = descricao,
                onValueChange = { descricao = it },
                label = { Text("Descrição") },
            )
            // Dificuldade
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
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { dificuldadeSelecionada = Dificulty.TRIVIAL },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        painter = painterResource(id = TaskSlayerIcons.trivialDificultyIcon),
                        contentDescription = "Dificuldade Trivial",
                        tint = if (dificuldadeSelecionada == Dificulty.TRIVIAL) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                    )
                }
                IconButton(
                    onClick = { dificuldadeSelecionada = Dificulty.FACIL },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        painter = painterResource(id = TaskSlayerIcons.easyDificultyIcon),
                        contentDescription = "Dificuldade Fácil",
                        tint = if (dificuldadeSelecionada == Dificulty.FACIL) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                    )
                }
                IconButton(
                    onClick = { dificuldadeSelecionada = Dificulty.MEDIO },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        painter = painterResource(id = TaskSlayerIcons.mediumDificultyIcon),
                        contentDescription = "Dificuldade Média",
                        tint = if (dificuldadeSelecionada == Dificulty.MEDIO) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                    )
                }
                IconButton(
                    onClick = { dificuldadeSelecionada = Dificulty.DIFICIL },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        painter = painterResource(id = TaskSlayerIcons.hardDificultyIcon),
                        contentDescription = "Dificuldade Difícil",
                        tint = if (dificuldadeSelecionada == Dificulty.DIFICIL) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                    )
                }
            }

            // Seção de Efeito
            Text(
                text = "Efeito",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Positivo",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Negativo",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { efeitoHabito = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        painter = painterResource(id = TaskSlayerIcons.positiveHabitIcon),
                        contentDescription = "Efeito positivo",
                        tint = if (efeitoHabito) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                    )
                }
                IconButton(
                    onClick = { efeitoHabito = false },
                    modifier = Modifier.weight(1f)
                ) {
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

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AddHabitTaskContentPreview(){
    TaskSlayerTheme {
        AddHabitTaskContent()
    }
}