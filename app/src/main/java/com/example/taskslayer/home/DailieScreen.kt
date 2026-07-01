package com.example.taskslayer.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.example.taskslayer.ui.theme.TaskSlayerTheme

@Composable
fun DailieRoute(){
    DailieContent()
}

@Composable
fun DailieContent(){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ){
        Text(
            text = "Dailies",
            fontSize = 50.sp
        )
    }
}

@Composable
fun DailieContentPreview(){
    TaskSlayerTheme {
        DailieContent()
    }
}