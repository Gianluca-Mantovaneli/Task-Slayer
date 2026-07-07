package com.example.taskslayer.home


import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.taskslayer.domain.model.Dificulty
import com.example.taskslayer.ui.theme.TaskSlayerIcons
import com.example.taskslayer.ui.theme.TaskSlayerTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@Composable
fun AddHabitTaskRoute(
    onBackClick: () -> Unit
) {
    BackHandler {
        onBackClick() // Captura quando o usuario clicar no botao de voltar
    }
    AddHabitTaskContent(
        isEditMode = false,
        onBackClick = onBackClick,
        onSaveTask = { titulo, descricao, dificuldade, efeitoHabito -> onBackClick() }, // voltando pra home TODO: mudar isso para a viewmodel
        onDeleteTask = { onBackClick() }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitTaskContent(
    isEditMode: Boolean = false,
    onBackClick: () -> Unit = {},
    onSaveTask: (titulo: String, descricao: String, dificuldade: Dificulty, efeitoHabito: Boolean) -> Unit = {_,_,_,_ ->},
    onDeleteTask: () -> Unit = {}
){
    var titulo by rememberSaveable { mutableStateOf("") }
    var descricao by rememberSaveable { mutableStateOf("") }
    var dificuldadeSelecionada by rememberSaveable { mutableStateOf(Dificulty.NONE) }
    var efeitoHabito by rememberSaveable { mutableStateOf(true) }


    // Para validar se o formulário está completo o sufuciente para o usuario salvar a tarefa
    val isFormValid =
        titulo.isNotBlank() &&
        dificuldadeSelecionada != Dificulty.NONE

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Adicionar Hábito")},
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
                    if(isEditMode){
                        IconButton(onClick = onDeleteTask) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Deletar",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    IconButton(onClick = {
                        onSaveTask(
                            titulo,
                            descricao,
                            dificuldadeSelecionada,
                            efeitoHabito

                        )
                    },
                        enabled = isFormValid
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
    ) {paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ){
            // titulo
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título") },
            )
            // descricao
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .height(100.dp),
                value = descricao,
                onValueChange = { descricao = it },
                label = { Text("Descrição") },
            )
            // dificuldade
            Text(
                text = "Dificuldade",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 10.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Botao dificuldade Trivial
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
                // Botao dificuldade Fácil
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
                // Botao dificuldade Médio
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
                // Botao dificuldade Difícil
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
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Text(
                    text = "Negativo",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
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
                ){
                    Icon(
                        painter = painterResource(id = TaskSlayerIcons.positiveHabitIcon),
                        contentDescription = "Efeito positivo",
                        tint = if (efeitoHabito) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                    )
                }
                IconButton(
                    onClick = { efeitoHabito = false },
                    modifier = Modifier.weight(1f)
                ){
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