package com.example.taskslayer.home

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.taskslayer.domain.model.Dificulty
import com.example.taskslayer.home.components.TodoCard
import com.example.taskslayer.tools.SoundEffectsManager
import com.example.taskslayer.ui.theme.TaskSlayerTheme

@Composable
fun TodoRoute(){
    TodoContent(
        soundManager = null
    )
}

@Composable
fun TodoContent(
    soundManager: SoundEffectsManager?
){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ){
        LazyColumn() {
            item {
                TodoCard(
                    "Título grande pra testar essa porra haaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                    "10/10/2023",
                    Dificulty.TRIVIAL,
                    soundManager
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
fun TodoContentPreview(){
    TaskSlayerTheme {
        TodoContent(
            soundManager = null
        )
    }
}