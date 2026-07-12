package com.example.taskslayer.ui.auth.login

import com.example.taskslayer.R
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskslayer.ui.auth.AuthUiState
import com.example.taskslayer.ui.theme.FonteDoTituloSlayer
import com.example.taskslayer.ui.theme.TaskSlayerIcons
import com.example.taskslayer.ui.theme.TaskSlayerTheme
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.focus.FocusDirection

/**
 * Função de rota para a tela de Login.
 * Gerencia o estado da UI e a navegação baseada nos eventos do ViewModel.
 */
@Composable
fun LoginRoute(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: LoginViewModel = viewModel()
){
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Efeito disparado sempre que o estado da UI muda
    LaunchedEffect(uiState) {
        when(uiState) {
            is AuthUiState.Success -> {
                onLoginSuccess()
                viewModel.resetState()
            }
            is AuthUiState.Error -> {
                // Exibe mensagem de erro via Toast
                Toast.makeText(context, (uiState as AuthUiState.Error).theMessage, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    LoginContent(
        uiState = uiState,
        onLoginClick = { email, senha -> viewModel.logarUsuario(email, senha) },
        onRegisterClick = onNavigateToRegister,
        onForgotPasswordClick = { emailInformado, onResultado ->
            viewModel.enviarEmailRecuperacao(emailInformado) { sucesso, mensagem ->
                onResultado(sucesso, mensagem)
            }
        }
    )
}

/**
 * Conteúdo visual da tela de Login.
 * Responsável pelo layout, campos de entrada e botões.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginContent(
    uiState: AuthUiState = AuthUiState.Idle,
    onLoginClick: (String, String) -> Unit = { _, _ -> },
    onRegisterClick: () -> Unit = {},
    onForgotPasswordClick: (String, (Boolean, String) -> Unit) -> Unit = { _, _ -> }
){
    // Estados locais para os campos de entrada
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }

    // Estados para o diálogo de recuperação de senha
    var mostrarDialogoRecuperacao by remember { mutableStateOf(false) }
    var emailRecuperacao by remember { mutableStateOf("") }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    Box(modifier = Modifier.fillMaxSize().imePadding().background(MaterialTheme.colorScheme.background)){
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Título do Aplicativo
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

            // Seção de Ícones das Katanas e Capacete com efeito de sombra/sobreposição
            Box(modifier = Modifier.fillMaxWidth().height(180.dp), contentAlignment = Alignment.Center){
                Icon(
                    modifier = Modifier.size(160.dp).offset(x = 4.dp, y = 4.dp),
                    painter = painterResource(id = TaskSlayerIcons.SamuraiCrossedKatana),
                    tint = Color.Black.copy(alpha = 0.5f), contentDescription = null
                )
                Icon(
                    modifier = Modifier.size(160.dp),
                    painter = painterResource(id = TaskSlayerIcons.SamuraiCrossedKatana),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), contentDescription = null
                )
                Icon(
                    modifier = Modifier.size(160.dp).offset(x = 4.dp, y = 4.dp),
                    painter = painterResource(id = TaskSlayerIcons.SamuraiLoginHelmet),
                    tint = Color.Black.copy(alpha = 0.6f), contentDescription = null
                )
                Icon(
                    modifier = Modifier.size(160.dp),
                    painter = painterResource(id = TaskSlayerIcons.SamuraiLoginHelmet),
                    tint = MaterialTheme.colorScheme.primary, contentDescription = null
                )
            }

            // Título da tela de Login
            Text(
                text = stringResource(R.string.titulo_login),
                style = TextStyle(fontSize = 28.sp, color = MaterialTheme.colorScheme.primary)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de entrada de E-mail
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(R.string.label_email)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            // Campo de entrada de Senha
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                value = senha,
                onValueChange = { senha = it },
                label = { Text(stringResource(R.string.label_senha)) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        onLoginClick(email, senha)
                    }
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Botão de Login
            Button(
                onClick = { onLoginClick(email, senha) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                enabled = uiState !is AuthUiState.Loading
            ) {
                if (uiState is AuthUiState.Loading) {
                    CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                } else {
                    Text("ENTRAR NA BATALHA", fontFamily = FonteDoTituloSlayer, fontSize = 18.sp, color = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Link para Cadastro
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(text = stringResource(R.string.text_nao_tem_conta) + " ", color = MaterialTheme.colorScheme.secondary)
                Text(
                    text = stringResource(R.string.text_cadastre_se),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    modifier = Modifier.clickable { onRegisterClick() }
                )
            }

            // Link para Recuperação de Senha
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(text = stringResource(R.string.text_esqueceu_a_senha) + " ", color = MaterialTheme.colorScheme.secondary)
                Text(
                    text = stringResource(R.string.text_redefina),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    modifier = Modifier.clickable {
                        emailRecuperacao = email
                        mostrarDialogoRecuperacao = true
                    }
                )
            }
        }
    }

    // Diálogo de Recuperação de Senha
    if (mostrarDialogoRecuperacao) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoRecuperacao = false },
            title = {
                Text(
                    text = "Recuperar Senha",
                    fontFamily = FonteDoTituloSlayer,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            text = {
                Column {
                    Text(
                        text = "Informe o e-mail da sua conta para enviarmos as instruções de redefinição.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    OutlinedTextField(
                        value = emailRecuperacao,
                        onValueChange = { emailRecuperacao = it },
                        label = { Text("E-mail cadastrado") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onForgotPasswordClick(emailRecuperacao) { sucesso, mensagem ->
                            Toast.makeText(context, mensagem, Toast.LENGTH_LONG).show()
                            if (sucesso) {
                                mostrarDialogoRecuperacao = false
                            }
                        }
                    }
                ) {
                    Text("ENVIAR", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoRecuperacao = false }) {
                    Text("CANCELAR", color = MaterialTheme.colorScheme.secondary)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}


@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LoginContentPreview(){
    TaskSlayerTheme { LoginContent() }
}
