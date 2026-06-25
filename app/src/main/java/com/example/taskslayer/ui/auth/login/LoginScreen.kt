package com.example.taskslayer.ui.auth.login

import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taskslayer.R
import com.example.taskslayer.ui.theme.FonteDoTituloSlayer
import com.example.taskslayer.ui.theme.TaskSlayerIcons
import com.example.taskslayer.ui.theme.TaskSlayerTheme


@Composable
fun LoginRoute(){
    LoginContent()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginContent(){

    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ){

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .verticalScroll(rememberScrollState()), // Serve para fazer o scroll na tela se o teclado estiver aberto
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "TaskSlayer",
                style = TextStyle(
                    fontSize = 70.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = FonteDoTituloSlayer,
                    shadow = Shadow(
                        color = Color.Black,
                        blurRadius = 10f,
                        offset = Offset(4.0f, 4.0f)
                    )
                )
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentAlignment = Alignment.CenterStart
            ){
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter
                ){
                    // Sombra das katanas
                    Icon(
                        modifier = Modifier
                            .size(200.dp)
                            .align(Alignment.BottomCenter)
                            .offset(x = 4.dp, y = 4.dp),
                        painter = painterResource(id = TaskSlayerIcons.SamuraiCrossedKatana),
                        tint = Color.Black.copy(alpha = 0.7f),
                        contentDescription = null
                    )
                    // Katanas
                    Icon(
                        modifier = Modifier.size(200.dp),
                        painter = painterResource(id = TaskSlayerIcons.SamuraiCrossedKatana),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        contentDescription = "Samurai Crossed Katana"
                    )
                }
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ){
                    // Sobra do capacete
                    Icon(
                        modifier = Modifier
                            .size(200.dp)
                            .align(Alignment.TopCenter)
                            .offset(x = 4.dp, y = 4.dp),
                        painter = painterResource(id = TaskSlayerIcons.SamuraiLoginHelmet),
                        tint = Color.Black.copy(alpha = 0.8f),
                        contentDescription = null
                    )
                    // Capacete
                    Icon(
                        modifier = Modifier.size(200.dp),
                        painter = painterResource(id = TaskSlayerIcons.SamuraiLoginHelmet),
                        tint = MaterialTheme.colorScheme.primary ,
                        contentDescription = "Samurai Login Helmet",
                    )
                }
            }

            Text(
                text = "Login",
                style = MaterialTheme.typography.headlineLarge
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(R.string.label_email)) }
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                value = senha,
                onValueChange = { senha = it },
                label = { Text(stringResource(R.string.label_senha)) }
            )

        }
    }
}


@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LoginContentPreview(){
    TaskSlayerTheme() {
        LoginContent()
    }
}