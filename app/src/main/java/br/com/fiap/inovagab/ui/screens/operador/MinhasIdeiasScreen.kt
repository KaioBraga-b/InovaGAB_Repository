package br.com.fiap.inovagab.ui.screens.operador

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import br.com.fiap.inovagab.ui.components.OperadorBottomBar
import br.com.fiap.inovagab.ui.theme.*

data class Ideia(
    val titulo: String,
    val status: String,
    val statusColor: Color,
    val statusTextColor: Color,
    val area: String,
    val tempo: String,
    val progresso: Float,
    val etapa: String,
    val destaque: String? = null
)

@Composable
fun MinhasIdeiasScreen(navController: NavController) {
    val ideias = listOf(
        Ideia(
            titulo = "Rastreamento em tempo real das entregas",
            status = "Em análise",
            statusColor = Color(0xFFFEF3C7),
            statusTextColor = Color(0xFFD97706),
            area = "Logística",
            tempo = "Enviada há 2 dias",
            progresso = 0.4f,
            etapa = "Etapa: Curadoria do gestor"
        ),
        Ideia(
            titulo = "Rota inteligente via app do motorista",
            status = "Aprovada ✓",
            statusColor = Color(0xFFDCFCE7),
            statusTextColor = Color(0xFF16A34A),
            area = "Operação",
            tempo = "Aprovada há 15 dias",
            progresso = 1.0f,
            etapa = "",
            destaque = "🏆 Virou projeto! Economizou R$ 12k/mês"
        ),
        Ideia(
            titulo = "Digitalizar checklist de vistoria do ônibus",
            status = "Enviada",
            statusColor = Color(0xFFF3F4F6),
            statusTextColor = Color(0xFF6B7280),
            area = "Passageiros",
            tempo = "Enviada ontem",
            progresso = 0.1f,
            etapa = "Aguardando triagem inicial"
        )
    )

    Scaffold(
        bottomBar = { OperadorBottomBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FD))
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = buildAnnotatedString {
                        append("Inova")
                        withStyle(style = SpanStyle(color = Color(0xFF3B82F6))) {
                            append("GAB")
                        }
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E3A8A)
                )
                
                Surface(
                    color = Color(0xFFEFF6FF),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "3 ideias",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = Color(0xFF2563EB),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Text(
                text = "Minhas ideias registradas",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(ideias) { ideia ->
                    IdeiaCard(ideia)
                }
                item { Spacer(modifier = Modifier.height(20.dp)) }
            }
        }
    }
}

@Composable
fun IdeiaCard(ideia: Ideia) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = ideia.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    color = Color(0xFF1E3A8A)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = ideia.statusColor,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = ideia.status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = ideia.statusTextColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = "Área: ${ideia.area} · ${ideia.tempo}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            LinearProgressIndicator(
                progress = { ideia.progresso },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .padding(vertical = 4.dp),
                color = if (ideia.status.contains("Aprovada")) Color(0xFF10B981) else Color(0xFF2563EB),
                trackColor = Color(0xFFE5E7EB),
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )

            if (ideia.etapa.isNotEmpty()) {
                Text(
                    text = ideia.etapa,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            ideia.destaque?.let {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFF3F4F6))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF059669),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MinhasIdeiasPreview() {
    MinhasIdeiasScreen(rememberNavController())
}
