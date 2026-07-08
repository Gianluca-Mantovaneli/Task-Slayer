package com.example.taskslayer.ui.home.habit

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskslayer.domain.model.Dificulty
import com.example.taskslayer.domain.model.Habit
import com.example.taskslayer.ui.home.components.HabitCard
import com.example.taskslayer.tools.SoundEffectsManager
import com.example.taskslayer.ui.theme.TaskSlayerTheme

@Composable
fun HabitsRoute(
    soundManager: SoundEffectsManager?,
    onHabitClick: (String) -> Unit,
    viewModel: HabitsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.carregarHabitos()
    }

    // Trata os estados visuais da tela de forma reativa
    when (val state = uiState) {
        is HabitsUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }

        is HabitsUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = state.message, color = MaterialTheme.colorScheme.error)
            }
        }

        is HabitsUiState.Success -> {
            HabitsContent(
                habits = state.habits,
                soundManager = soundManager,
                onHabitClick = onHabitClick
            )
        }
    }
}

@Composable
fun HabitsContent(
    habits: List<Habit>,
    soundManager: SoundEffectsManager?,
    onHabitClick: (String) -> Unit = {}
){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ){
        if (habits.isEmpty()) {
            Text(
                text = "Nenhum hábito cadastrado. \nComece sua jornada!",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        } else {
            LazyColumn {

                items(habits) { habit ->
                    HabitCard(
                        titulo = habit.title,
                        habitEffect = habit.impact,
                        dificuldade = habit.dificuldade,
                        soundManager = soundManager,
                        onHabitClick = { onHabitClick(habit.id) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HabitsContentPreview(){
    TaskSlayerTheme {
        HabitsContent(
            habits = listOf(
                Habit(
                    id = "1",
                    title = "Estudar Kotlin",
                    impact = true,
                    dificuldade = Dificulty.MEDIO
                ),
                Habit(
                    id = "2",
                    title = "Comer Fast Food",
                    impact = false,
                    dificuldade = Dificulty.TRIVIAL
                )
            ),
            onHabitClick = { _ -> },
            soundManager = null
        )
    }
}