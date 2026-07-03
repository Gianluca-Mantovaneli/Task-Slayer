package com.example.taskslayer.home

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.taskslayer.domain.model.Dificulty
import com.example.taskslayer.home.components.DailieCard
import com.example.taskslayer.home.components.HabitCard
import com.example.taskslayer.tools.SoundEffectsManager
import com.example.taskslayer.ui.theme.TaskSlayerTheme

@Composable
fun HabitsRoute(soundManager: SoundEffectsManager?){
    HabitsContent(
        soundManager = soundManager
    )
}

@Composable
fun HabitsContent(
    soundManager: SoundEffectsManager?
){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ){
        LazyColumn() {
            items(
                count = 2,
            ) {
                HabitCard(
                    "Título grande pra testar essa porra haaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                    true,
                    Dificulty.TRIVIAL,
                    soundManager
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HabitsContentPreview(){
    TaskSlayerTheme {
        HabitsContent(
            soundManager = null
        )
    }
}