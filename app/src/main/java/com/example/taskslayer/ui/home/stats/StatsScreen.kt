package com.example.taskslayer.ui.home.stats

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.taskslayer.domain.model.User
import com.example.taskslayer.tools.SoundEffectsManager
import com.example.taskslayer.ui.theme.FonteDoTituloSlayer
import com.example.taskslayer.ui.theme.TaskSlayerTheme

@Composable
fun StatsRoute(
    soundManager: SoundEffectsManager?,
    viewModel: StatsViewModel = viewModel()
) {
    // Escuta o estado do ViewModel que expõe o objeto User
    val userState by viewModel.uiState.collectAsState()

    StatsContent(soundManager = soundManager, user = userState)
}

@Composable
fun StatsContent(soundManager: SoundEffectsManager?, user: User) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // 1. Foto de Perfil Redonda usando AsyncImage (Coil)
        AsyncImage(
            model = user.imagenPerfilURL,
            contentDescription = "Foto de perfil de ${user.nome}",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Nome do Guerreiro / Usuário
        Text(
            text = user.nome,
            fontFamily = FonteDoTituloSlayer,
            fontSize = 28.sp,
            color = MaterialTheme.colorScheme.primary
        )

        // Exemplo usando o statusAtual como se fosse o Nível do RPG
        Text(
            text = "Nível ${user.statusAtual} Slayer",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 3. Cabeçalho dos Atributos
        Text(
            text = "ESTATÍSTICAS DE BATALHA",
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 8.dp)
        )

        // 4. Painel de Atributos (Cards com os dados do seu modelo User)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val tasksConcluidas = user.tasksConcluidas
                val totalDeTasks = user.tasksCriadas
                val textoPorcentagem = if (totalDeTasks > 0) {
                    val porcentagem = (tasksConcluidas.toFloat() / totalDeTasks.toFloat()) * 100
                    String.format("%.0f%%", porcentagem)
                } else {
                    "0%"
                }

                ItemLinhaStats(label = "Tasks Criadas", valor = totalDeTasks.toString())
                ItemLinhaStats(label = "Tasks Concluídas", valor = tasksConcluidas.toString())
                ItemLinhaStats(label = "Tasks Perdidas", valor = user.tasksPerdidas.toString())
                ItemLinhaStats(label = "Hábitos Ativos", valor = user.habitosAtivos.toString())
                ItemLinhaStats(label = "Aproveitamento", valor = textoPorcentagem)
            }
        }
    }
}

@Composable
fun ItemLinhaStats(label: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = valor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            fontFamily = FonteDoTituloSlayer
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun StatsContentPreview() {
    TaskSlayerTheme {
        StatsContent(
            soundManager = null,
            user = User(
                nome = "Fulano",
                statusAtual = 50,
                imagenPerfilURL = "https://rlv.zcache.com.br/adesivo_redondo_foto_de_cao_cachorro_pet_personalizado-r9b188fed564a4ae4a4d26ece493e043a_zg2qos_644.webp?rlvnet=1",
                tasksCriadas = 50,
                tasksConcluidas = 45,
                tasksPerdidas = 5,
                habitosAtivos = 2
            )
        )
    }
}