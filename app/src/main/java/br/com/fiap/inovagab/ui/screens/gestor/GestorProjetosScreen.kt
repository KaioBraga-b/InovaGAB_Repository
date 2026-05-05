package br.com.fiap.inovagab.ui.screens.gestor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import br.com.fiap.inovagab.ui.components.GestorBottomBar
import br.com.fiap.inovagab.ui.theme.*

data class Projeto(
    val titulo: String,
    val status: String,
    val statusColor: Color,
    val statusBg: Color,
    val area: String,
    val periodo: String,
    val progresso: Float,
    val progressoTexto: String,
    val etapaAtiva: Int,
    val roiMensal: String? = null,
    val investimento: String? = null,
    val retorno: String? = null,
    val roiPercent: String? = null,
    val estMensal: String? = null
)

@Composable
fun GestorProjetosScreen(navController: NavController) {
    val projetos = listOf(
        Projeto(
            titulo = "Rota Inteligente GAB",
            status = "Em andamento",
            statusColor = Color(0xFF16A34A),
            statusBg = Color(0xFFDCFCE7),
            area = "Logística",
            periodo = "Jan 2025 → Jun 2025",
            progresso = 0.65f,
            progressoTexto = "65% concluído",
            etapaAtiva = 2,
            roiMensal = "ROI: R$ 12k/mês",
            investimento = "R$ 45k",
            retorno = "R$ 144k/a",
            roiPercent = "220%"
        ),
        Projeto(
            titulo = "App Vistoria Digital",
            status = "Planejamento",
            statusColor = Color(0xFFD97706),
            statusBg = Color(0xFFFEF3C7),
            area = "Passageiros",
            periodo = "Mar 2025 → Ago 2025",
            progresso = 0.25f,
            progressoTexto = "25% concluído",
            etapaAtiva = 1,
            estMensal = "Est: R$ 8k/mês"
        )
    )

    Scaffold(
        bottomBar = { GestorBottomBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Criar projeto */ },
                containerColor = Color(0xFF2563EB),
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Novo Projeto")
            }
        }
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

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color(0xFFEFF6FF),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Gestor",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            color = Color(0xFF2563EB),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFFEF4444), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("AP", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            Text(
                text = "Projetos em andamento · 4 ativos",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(projetos) { projeto ->
                    ProjetoCard(projeto)
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun ProjetoCard(projeto: Projeto) {
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
                    text = projeto.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E3A8A)
                )
                Surface(
                    color = projeto.statusBg,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = projeto.status,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        color = projeto.statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = "${projeto.area} · ${projeto.periodo}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Timeline / Etapas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val etapas = listOf("Ideação", "Aprovação", "Execução", "Resultado")
                etapas.forEachIndexed { index, etapa ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(
                                    if (index <= projeto.etapaAtiva) Color(0xFF2563EB) else Color(0xFFE5E7EB),
                                    CircleShape
                                )
                        )
                        Text(
                            text = etapa,
                            fontSize = 10.sp,
                            color = if (index <= projeto.etapaAtiva) Color(0xFF2563EB) else Color.Gray,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Barra de Progresso
            LinearProgressIndicator(
                progress = { projeto.progresso },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = if (projeto.status == "Em andamento") Color(0xFF2563EB) else Color(0xFFF59E0B),
                trackColor = Color(0xFFF3F4F6),
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = projeto.progressoTexto,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
                projeto.roiMensal?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF16A34A),
                        fontWeight = FontWeight.Bold
                    )
                }
                projeto.estMensal?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            if (projeto.investimento != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FinCard(label = "Invest.", value = projeto.investimento, modifier = Modifier.weight(1f))
                    FinCard(label = "Retorno", value = projeto.retorno!!, valueColor = Color(0xFF16A34A), modifier = Modifier.weight(1.2f))
                    FinCard(label = "ROI", value = projeto.roiPercent!!, valueColor = Color(0xFF2563EB), modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun FinCard(label: String, value: String, valueColor: Color = Color.Black, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = Color(0xFFF9FAFB),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 10.sp, color = Color.Gray)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = valueColor)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GestorProjetosPreview() {
    GestorProjetosScreen(rememberNavController())
}
