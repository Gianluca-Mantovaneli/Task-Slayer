package com.example.taskslayer.ui.auth.register

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taskslayer.R
import com.example.taskslayer.ui.theme.FonteDoTituloSlayer
import com.example.taskslayer.ui.theme.TaskSlayerTheme


@Composable
fun RegisterRoute(){
    RegisterContent()
}
@Composable
fun RegisterContent() {
    var nickname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var confirmarSenha by remember { mutableStateOf("") }

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
                text = stringResource(R.string.titulo_App),
                textAlign = TextAlign.Center,
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
            Text(
                text = stringResource(R.string.titulo_tela_cadastro),
                style = TextStyle(
                    fontSize = 30.sp,
                    color = MaterialTheme.colorScheme.primary,
                    shadow = Shadow(
                        color = Color.Black,
                        blurRadius = 10f,
                        offset = Offset(4.0f, 4.0f)
                    )
                ),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(20.dp)
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                label = { Text(stringResource(R.string.label_nickname)) },
                value = nickname,
                onValueChange = { nickname = it },
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                label = { Text(stringResource(R.string.label_email)) },
                value = email,
                onValueChange = { email = it },
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                label = { Text(stringResource(R.string.label_senha)) },
                value = senha,
                onValueChange = { senha = it },
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                label = { Text(stringResource(R.string.label_confirmar_senha)) },
                value = confirmarSenha,
                onValueChange = { confirmarSenha = it },
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun RegisterContentPreview(){
    TaskSlayerTheme {
        RegisterContent()
    }
}