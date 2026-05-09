package br.com.fiap.ui.screens.gestor

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import br.com.fiap.ui.components.GestorBottomBar
import br.com.fiap.ui.theme.*

// 1. Modelo de dados único
data class GestorIdeiaData(
    val titulo: String,
    val autor: String,
    val area: String,
    val tempo: String,
    val descricao: String,
    val votos: Int,
    val impacto: String,
    val objetivo: String,
    val prioridade: String,
    val prioridadeColor: Color,
    val prioridadeBg: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestorHomeScreen(navController: NavController) {
    val ideias = remember {
        listOf(
            GestorIdeiaData(
                titulo = "Rastreamento em tempo real das entregas",
                autor = "João Motorista",
                area = "Logística",
                tempo = "há 2 dias",
                descricao = "Clientes ficam sem info durante o trajeto. Proposta: app com mapa ao vivo para acompanhar entrega...",
                votos = 14,
                impacto = "Médio",
                objetivo = "Digitalização",
                prioridade = "Alta ↑",
                prioridadeColor = Color(0xFF2563EB),
                prioridadeBg = Color(0xFFEFF6FF)
            ),
            GestorIdeiaData(
                titulo = "Digitalizar checklist de vistoria do ônibus",
                autor = "João Motorista",
                area = "Passageiros",
                tempo = "ontem",
                descricao = "Processo atual usa papel e gera retrabalho. Proposta: formulário digital com fotos...",
                votos = 7,
                impacto = "Alto",
                objetivo = "Digitalização",
                prioridade = "Média",
                prioridadeColor = Color(0xFFD97706),
                prioridadeBg = Color(0xFFFEF3C7)
            )
        )
    }

    Scaffold(
        bottomBar = { GestorBottomBar(navController) }
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
                text = "Curadoria de ideias · 8 pendentes",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Filtros estáveis usando Row + horizontalScroll
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = true,
                    onClick = { },
                    label = { Text("Pendentes (8)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFFEF3C7),
                        selectedLabelColor = Color(0xFFD97706)
                    )
                )
                FilterChip(
                    selected = false,
                    onClick = { },
                    label = { Text("✓ Aprovadas (12)") },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color(0xFFDCFCE7),
                        labelColor = Color(0xFF16A34A)
                    )
                )
                FilterChip(
                    selected = false,
                    onClick = { },
                    label = { Text("✕ Recusadas (3)") },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color(0xFFF3F4F6),
                        labelColor = Color.Gray
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(ideias) { ideia ->
                    GestorIdeiaCardItem(ideia)
                }
                item { Spacer(modifier = Modifier.height(20.dp)) }
            }
        }
    }
}

@Composable
fun GestorIdeiaCardItem(ideia: GestorIdeiaData) {
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
                    color = ideia.prioridadeBg,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = ideia.prioridade,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = ideia.prioridadeColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = "${ideia.autor} · ${ideia.area} · ${ideia.tempo}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Text(
                text = ideia.descricao,
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray,
                modifier = Modifier.padding(vertical = 8.dp),
                lineHeight = 18.sp
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color.Gray
                        )
                        Text(
                            text = "${ideia.votos} votos",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Impacto: ${ideia.impacto} · Obj: ${ideia.objetivo}",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botão Aprovar com padding reduzido para não cortar o texto
                Button(
                    onClick = { },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check, 
                        contentDescription = null, 
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Aprovar", 
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        maxLines = 1
                    )
                }
                
                // Botão Recusar seguindo o mesmo padrão
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text(
                        text = "✕ Recusar", 
                        color = Color.Gray, 
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        maxLines = 1
                    )
                }
                
                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .background(Color(0xFFF3F4F6), RoundedCornerShape(8.dp))
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}


