package com.example.taskslayer.ui.auth.register

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskslayer.R
import com.example.taskslayer.ui.auth.AuthUiState
import com.example.taskslayer.ui.theme.FonteDoTituloSlayer
import com.example.taskslayer.ui.theme.TaskSlayerTheme
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.focus.FocusDirection

@Composable
fun RegisterRoute(
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit,
    viewModel: RegisterViewModel = viewModel()
){
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState) {
        when(uiState) {
            is AuthUiState.Success -> {
                Toast.makeText(context, "Guerreiro registrado com sucesso!", Toast.LENGTH_SHORT).show()
                onRegisterSuccess()
                viewModel.resetState()
            }
            is AuthUiState.Error -> {
                Toast.makeText(context, (uiState as AuthUiState.Error).theMessage, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    RegisterContent(
        uiState = uiState,
        onRegisterClick = { nick, email, senha, conf -> viewModel.cadastrarUsuario(nick, email, senha, conf) },
        onBackToLoginClick = onBackToLogin
    )
}

@Composable
fun RegisterContent(
    uiState: AuthUiState = AuthUiState.Idle,
    onRegisterClick: (String, String, String, String) -> Unit = { _, _, _, _ -> },
    onBackToLoginClick: () -> Unit = {}
) {
    var nickname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var confirmarSenha by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)){
        Column(
            modifier = Modifier.fillMaxSize().imePadding().padding(16.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.titulo_App),
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 60.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = FonteDoTituloSlayer,
                    shadow = Shadow(color = Color.Black, blurRadius = 10f, offset = Offset(4.0f, 4.0f))
                )
            )
            Text(
                text = stringResource(R.string.titulo_tela_cadastro),
                style = TextStyle(fontSize = 28.sp, color = MaterialTheme.colorScheme.primary),
                modifier = Modifier.padding(12.dp)
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                label = { Text(stringResource(R.string.label_nickname)) },
                value = nickname, onValueChange = { nickname = it },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                label = { Text(stringResource(R.string.label_email)) },
                value = email, onValueChange = { email = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                label = { Text(stringResource(R.string.label_senha)) },
                value = senha, onValueChange = { senha = it },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                label = { Text(stringResource(R.string.label_confirmar_senha)) },
                value = confirmarSenha, onValueChange = { confirmarSenha = it },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        onRegisterClick(nickname, email, senha, confirmarSenha) // Já tenta registrar ao dar enter no último
                    }
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onRegisterClick(nickname, email, senha, confirmarSenha) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                enabled = uiState !is AuthUiState.Loading
            ) {
                if (uiState is AuthUiState.Loading) {
                    CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                } else {
                    Text("FORJAR MINHA CONTA", fontFamily = FonteDoTituloSlayer, fontSize = 18.sp, color = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(
                    text = "Já possui uma conta? ",
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                )
                Text(
                    text = "Voltar ao Login",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                    modifier = Modifier
                        .clickable { onBackToLoginClick() }
                )
            }

        }
    }
}

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun RegisterContentPreview(){
    TaskSlayerTheme { RegisterContent() }
}