package com.example.taskslayer.ui.home.dailie

import android.content.res.Configuration
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskslayer.domain.model.Dificulty
import com.example.taskslayer.domain.model.Repetition
import com.example.taskslayer.ui.theme.TaskSlayerIcons
import com.example.taskslayer.ui.theme.TaskSlayerTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.LocalDate

/**
 * Função de rota para a tela de Adição/Edição de Missões Diárias (Dailies).
 * Gerencia a inicialização para edição e o tratamento de estados de sucesso/erro.
 */
@Composable
fun AddDailieTaskRoute(
    taskId: String? = null,
    onBackClick: () -> Unit,
    viewModel: AddDailieTaskViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var mensagemSucesso by remember { mutableStateOf("") }

    // Carrega dados se estiver em modo de edição
    LaunchedEffect(taskId) {
        if (!taskId.isNullOrBlank()) {
            viewModel.prepararParaEdicao(taskId)
        }
    }

    // Trata o botão de voltar do dispositivo
    BackHandler {
        viewModel.resetCompletamente()
        onBackClick()
    }

    // Observa o estado da UI para exibir feedbacks
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is AddDailieUiState.Success -> {
                Toast.makeText(context, mensagemSucesso, Toast.LENGTH_SHORT).show()
                viewModel.resetCompletamente()
                onBackClick()
            }

            is AddDailieUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetUiStateToIdle()
            }

            else -> {}
        }
    }

    AddDailieTaskContent(
        viewModel = viewModel,
        isEditMode = viewModel.isEditMode,
        onBackClick = {
            viewModel.resetCompletamente()
            onBackClick()
        },
        onSaveTask = { titulo, descricao, dificuldade, dataInicio, repeticao, frequencia, _ ->
            mensagemSucesso = "Diária salva com sucesso!"
            viewModel.salvarTarefaDailie(
                titulo,
                descricao,
                dificuldade,
                dataInicio,
                repeticao,
                frequencia
            )
        },
        onDeleteTask = {
            if (taskId != null) {
                mensagemSucesso = "Diária deletada com sucesso!"
                viewModel.deletarTarefaDailie(taskId)
            } else {
                viewModel.resetCompletamente()
                onBackClick()
            }
        }
    )
}

/**
 * Conteúdo visual do formulário de Missão Diária.
 * Inclui campos de texto, dificuldade, data de início e configurações de repetição.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDailieTaskContent(
    viewModel: AddDailieTaskViewModel = viewModel(),
    isEditMode: Boolean = false,
    onBackClick: () -> Unit,
    onSaveTask: (titulo: String, descricao: String, dificuldade: Dificulty, deadline: String, repeticao: Repetition, frequencia: Int, lembretes: List<String>) -> Unit = {_,_,_,_,_,_,_ ->},
    onDeleteTask: () -> Unit = {}
){
    // Estados locais dos campos do formulário
    var titulo by rememberSaveable { mutableStateOf("") }
    var descricao by rememberSaveable { mutableStateOf("") }
    var dificuldadeSelecionada by rememberSaveable { mutableStateOf(Dificulty.NONE) }
    var dataInicio by rememberSaveable { mutableStateOf("") }
    var repeticao by rememberSaveable { mutableStateOf(Repetition.NONE) }
    var frequencia by rememberSaveable { mutableIntStateOf(1) }
    var lembretes by rememberSaveable { mutableStateOf(listOf<String>()) }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    var menuExpandido by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Preenche os campos quando os dados são carregados para edição
    LaunchedEffect(uiState) {
        if (uiState is AddDailieUiState.Loaded) {
            val dailie = (uiState as AddDailieUiState.Loaded).dailie
            titulo = dailie.title
            descricao = dailie.description
            dificuldadeSelecionada = dailie.dificuldade
            dataInicio = dailie.dataInicio
            repeticao = dailie.repeticao
            frequencia = dailie.aCada

            viewModel.resetUiStateToIdle()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = if (isEditMode) "Editar Task Diária" else "Adicionar Task Diária") },
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
                            Icon(imageVector = Icons.Filled.Delete, contentDescription = "Deletar", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                    IconButton(
                        onClick = {
                            onSaveTask(titulo, descricao, dificuldadeSelecionada, dataInicio, repeticao, frequencia, lembretes)
                        }
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Salvar", tint = MaterialTheme.colorScheme.primary)
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
                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                value = titulo, onValueChange = { titulo = it },
                label = { Text("Título") },
            )
            // Campo: Descrição
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp).height(100.dp),
                value = descricao, onValueChange = { descricao = it },
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
                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Opções de Dificuldade
                DifficultyItem(label = "Trivial", icon = TaskSlayerIcons.trivialDificultyIcon, isSelected = dificuldadeSelecionada == Dificulty.TRIVIAL, onClick = { dificuldadeSelecionada = Dificulty.TRIVIAL })
                DifficultyItem(label = "Fácil", icon = TaskSlayerIcons.easyDificultyIcon, isSelected = dificuldadeSelecionada == Dificulty.FACIL, onClick = { dificuldadeSelecionada = Dificulty.FACIL })
                DifficultyItem(label = "Média", icon = TaskSlayerIcons.mediumDificultyIcon, isSelected = dificuldadeSelecionada == Dificulty.MEDIO, onClick = { dificuldadeSelecionada = Dificulty.MEDIO })
                DifficultyItem(label = "Difícil", icon = TaskSlayerIcons.hardDificultyIcon, isSelected = dificuldadeSelecionada == Dificulty.DIFICIL, onClick = { dificuldadeSelecionada = Dificulty.DIFICIL })
            }

            // Seção: Data de Início
            Text(
                text = "Data Inicial (Start)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 10.dp),
                textAlign = TextAlign.Center
            )
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp)) {
                OutlinedTextField(
                    value = dataInicio, onValueChange = {},
                    label = { Text("Selecione Uma Data...") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true, enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                Box(modifier = Modifier.matchParentSize().clickable { showDatePicker = true })
            }

            // Seção: Frequência e Repetição
            Text(
                text = "Frequência",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 10.dp),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Menu Suspenso para o Tipo de Repetição (Diário, Semanal, Mensal)
                ExposedDropdownMenuBox(
                    expanded = menuExpandido,
                    onExpandedChange = { menuExpandido = !menuExpandido },
                    modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 10.dp)
                ) {
                    OutlinedTextField(
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        readOnly = true,
                        value = if (repeticao == Repetition.NONE) "Selecione" else repeticao.name,
                        onValueChange = {},
                        label = { Text("Repetição") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuExpandido) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )

                    ExposedDropdownMenu(
                        expanded = menuExpandido,
                        onDismissRequest = { menuExpandido = false }
                    ) {
                        Repetition.entries.filter { it != Repetition.NONE }.forEach { opcao ->
                            DropdownMenuItem(
                                text = { Text(text = opcao.name, style = MaterialTheme.typography.bodyLarge) },
                                onClick = {
                                    repeticao = opcao
                                    menuExpandido = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }
                
                // Campo para definir o intervalo da repetição (ex: a cada 2 dias)
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 10.dp),
                    value = if (frequencia == 0) "" else frequencia.toString(),
                    onValueChange = { novoTexto ->
                        if (novoTexto.all { it.isDigit() }) {
                            frequencia = novoTexto.toIntOrNull() ?: 0
                        }
                    },
                    label = { Text("A cada") },
                    placeholder = { Text("Ex: 1, 2, 3...") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }
    }

    // Diálogo Seletor de Data
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val selectedDateMillis = datePickerState.selectedDateMillis
                    if (selectedDateMillis != null) {
                        val date = Instant.ofEpochMilli(selectedDateMillis).atZone(ZoneId.systemDefault()).toLocalDate()
                        val hoje = LocalDate.now()

                        if (date.isBefore(hoje)) {
                            Toast.makeText(
                                context,
                                "Não é permitido escolher uma data no passado, Guerreiro!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                            dataInicio = date.format(formatter)
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
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

/**
 * Componente para item de dificuldade.
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
fun AddDailieTaskContentPreview(){
    TaskSlayerTheme {
        AddDailieTaskContent(onBackClick = {})
    }
}
