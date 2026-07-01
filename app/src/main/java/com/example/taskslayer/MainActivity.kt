package com.example.taskslayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.taskslayer.home.HomeRoute
import com.example.taskslayer.ui.auth.login.LoginRoute
import com.example.taskslayer.ui.theme.TaskSlayerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaskSlayerTheme {
                HomeRoute()
            }
        }
    }
}


