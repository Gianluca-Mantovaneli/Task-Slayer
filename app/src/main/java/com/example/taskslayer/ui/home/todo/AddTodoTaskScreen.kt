package com.example.taskslayer.ui.home.todo

import android.content.res.Configuration
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
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
import com.example.taskslayer.ui.theme.TaskSlayerIcons
import com.example.taskslayer.ui.theme.TaskSlayerTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Função de rota para a tela de Adição/Edição de tarefas To-Do.
 * Gerencia a inicialização dos dados para edição e reações aos estados do ViewModel.
 */
@Composable
fun AddTodoTaskRoute(
    taskId: String? = null,
    onBackClick: () -> Unit,
    onDeleteClick: () -> Unit = {},
    viewModel: AddTodoTaskViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val ctx = LocalContext.current
    var mensagemSucesso by remember { mutableStateOf("") }

    // Efeito para carregar dados se estiver em modo de edição
    LaunchedEffect(taskId) {
        if (!taskId.isNullOrBlank()) {
            viewModel.prepararParaEdicao(taskId)
        }
    }

    // Trata o botão de voltar físico do Android
    BackHandler {
        viewModel.resetCompletamente()
        onBackClick()
    }

    // Observa mudanças no estado para exibir feedbacks (Toasts) e navegar
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is AddTodoUiState.Success -> {
                Toast.makeText(ctx, mensagemSucesso, Toast.LENGTH_SHORT).show()
                viewModel.resetCompletamente()
                onBackClick()
            }

            is AddTodoUiState.Error -> {
                Toast.makeText(ctx, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetUiStateToIdle()
            }

            else -> {}
        }
    }

    AddTodoTaskContent(
        viewModel = viewModel,
        isEditMode = viewModel.isEditMode,
        onBackClick = {
            viewModel.resetCompletamente()
            onBackClick()
        },
        onSaveTask = { titulo, descricao, dificuldade, deadline ->
            mensagemSucesso = "Tarefa salva com sucesso!"
            viewModel.salvarTarefaTodo(titulo, descricao, dificuldade, deadline)
        },
        onDeleteTask = {
            if (taskId != null) {
                mensagemSucesso = "Tarefa deletada com sucesso!"
                viewModel.deletarTarefaTodo(taskId)
            } else {
                viewModel.resetCompletamente()
                onBackClick()
            }
        }
    )
}

/**
 * Conteúdo visual da tela de cadastro de tarefas.
 * Inclui campos de texto, seleção de dificuldade por ícones e seletor de data.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTodoTaskContent(
    viewModel: AddTodoTaskViewModel = viewModel(),
    isEditMode: Boolean = false,
    onBackClick: () -> Unit = {},
    onSaveTask: (titulo: String, descricao: String, dificuldade: Dificulty, deadline: String) -> Unit = {_,_,_,_ ->},
    onDeleteTask: () -> Unit = {}
){
    // Estados locais dos campos do formulário
    var titulo by rememberSaveable { mutableStateOf("") }
    var descricao by rememberSaveable { mutableStateOf("") }
    var dificuldadeSelecionada by rememberSaveable { mutableStateOf(Dificulty.NONE) }
    var deadline: String? by rememberSaveable { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Atualiza os campos locais quando os dados da tarefa para edição são carregados
    LaunchedEffect(uiState) {
        if (uiState is AddTodoUiState.Loaded) {
            val todo = (uiState as AddTodoUiState.Loaded).todo
            titulo = todo.title
            descricao = todo.description
            dificuldadeSelecionada = todo.dificuldade
            deadline = todo.deadline
            viewModel.resetUiStateToIdle()
        }
    }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = if (isEditMode) "Editar Task Todo" else "Adicionar Task Todo") },
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
                    // Botão de Deletar (apenas em modo de edição)
                    if(isEditMode){
                        IconButton(onClick = onDeleteTask) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Deletar",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    // Botão de Salvar/Enviar
                    IconButton(
                        onClick = {
                            onSaveTask(
                                titulo, descricao, dificuldadeSelecionada,
                                deadline.toString()
                            )
                        },
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
        ){
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
                // Opções de Dificuldade representadas por colunas clicáveis
                
                // Trivial
                DifficultyItem(
                    label = "Trivial",
                    icon = TaskSlayerIcons.trivialDificultyIcon,
                    isSelected = dificuldadeSelecionada == Dificulty.TRIVIAL,
                    onClick = { dificuldadeSelecionada = Dificulty.TRIVIAL }
                )

                // Fácil
                DifficultyItem(
                    label = "Fácil",
                    icon = TaskSlayerIcons.easyDificultyIcon,
                    isSelected = dificuldadeSelecionada == Dificulty.FACIL,
                    onClick = { dificuldadeSelecionada = Dificulty.FACIL }
                )

                // Médio
                DifficultyItem(
                    label = "Média",
                    icon = TaskSlayerIcons.mediumDificultyIcon,
                    isSelected = dificuldadeSelecionada == Dificulty.MEDIO,
                    onClick = { dificuldadeSelecionada = Dificulty.MEDIO }
                )

                // Difícil
                DifficultyItem(
                    label = "Difícil",
                    icon = TaskSlayerIcons.hardDificultyIcon,
                    isSelected = dificuldadeSelecionada == Dificulty.DIFICIL,
                    onClick = { dificuldadeSelecionada = Dificulty.DIFICIL }
                )
            }
            
            // Seção: Deadline (Prazo)
            Text(
                text = "Prazo final (Deadline)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 10.dp),
                textAlign = TextAlign.Center
            )
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)) {
                OutlinedTextField(
                    value = deadline.toString(),
                    onValueChange = {},
                    label = { Text("Selecione uma data...") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                // Camada invisível para capturar o clique e abrir o seletor de data
                Box(modifier = Modifier
                    .matchParentSize()
                    .clickable { showDatePicker = true })
            }
        }
    }

    // Diálogo de seleção de data (DatePicker)
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val selectedDateMillis = datePickerState.selectedDateMillis
                    if (selectedDateMillis != null) {
                        val date = Instant.ofEpochMilli(selectedDateMillis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()

                        val hoje = LocalDate.now()

                        // Validação para não permitir datas retroativas
                        if (date.isBefore(hoje)) {
                            Toast.makeText(
                                context,
                                "Não é permitido escolher uma data no passado, Guerreiro!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                            deadline = date.format(formatter)
                            showDatePicker = false
                        }
                    } else {
                        showDatePicker = false
                    }
                }) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

/**
 * Componente interno para representar um item de seleção de dificuldade.
 */
@Composable
private fun DifficultyItem(
    label: String,
    icon: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(4.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
        Icon(
            painter = painterResource(id = icon),
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(28.dp)
        )
        Text(
            text = label,
            color = color,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AddTodoTaskContentPreview(){
    TaskSlayerTheme {
        AddTodoTaskContent()
    }
}
