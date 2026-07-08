package com.example.taskslayer.ui.home.todo

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.taskslayer.domain.model.Dificulty
import com.example.taskslayer.domain.model.Todo
import com.example.taskslayer.ui.home.components.TodoCard
import com.example.taskslayer.tools.SoundEffectsManager
import com.example.taskslayer.ui.theme.TaskSlayerTheme

@Composable
fun TodoRoute(
    soundManager: SoundEffectsManager?,
    onNavigateToEdit: (String) -> Unit = {},
    viewModel: TodoViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.carregarTarefasTodo()
    }

    when (val state = uiState) {
        is TodoUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }

        is TodoUiState.Success -> {
            TodoContent(
                tasks = state.tasks,
                soundManager = soundManager,
                onNavigateToEdit = onNavigateToEdit,
                onTaskCheckedChange = { todo, novoStatus ->
                    viewModel.atualizarStatusTodo(todo.id, novoStatus)
                }
            )
        }

        is TodoUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = state.message, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun TodoContent(
    tasks: List<Todo>,
    soundManager: SoundEffectsManager?,
    onNavigateToEdit: (String) -> Unit = {},
    onTaskCheckedChange: (Todo, Boolean) -> Unit = { _, _ -> }
){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ){
        if (tasks.isEmpty()) {
            Text(
                text = "Nenhuma tarefa pendente. \nDescanse, Samurai!",
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        } else {
            // Listando as tarefas do banco desse usuario
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(
                    items = tasks,
                    key = { todo -> todo.id }
                ) { todo ->
                    TodoCard(
                        titulo = todo.title,
                        deadline = todo.deadline,
                        dificuldade = todo.dificuldade,
                        soundManager = soundManager,
                        done = todo.done,
                        onTaskCheckedChange = { novoStatus ->
                            onTaskCheckedChange(todo, novoStatus)
                        },
                        onCardClick = {
                            onNavigateToEdit(todo.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
fun TodoContentPreview(){
    TaskSlayerTheme {
        TodoContent(
            tasks = listOf(
                Todo(id = "1", title = "Cortar lenha", dificuldade = Dificulty.TRIVIAL),
                Todo(id = "2", title = "Treinar a katana", dificuldade = Dificulty.DIFICIL)
            ),
            soundManager = null
        )
    }
}