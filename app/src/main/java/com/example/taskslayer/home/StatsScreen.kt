package com.example.taskslayer.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.example.taskslayer.ui.theme.TaskSlayerTheme

@Composable
fun StatsRoute(){
    StatsContent()
}

@Composable
fun StatsContent(){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ){
        Text(
            text = "Stats",
            fontSize = 50.sp
        )
    }
}

@Composable
fun StatsContentPreview(){
    TaskSlayerTheme {
        StatsContent()
    }
}