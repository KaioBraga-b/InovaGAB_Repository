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
import androidx.compose.runtime.getValue
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
import br.com.fiap.ui.navigation.Screens
import br.com.fiap.ui.theme.*
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.viewmodel.AuthViewModel
import br.com.fiap.viewmodel.InovacaoViewModel
import br.com.fiap.viewmodel.Ideia

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestorCuradoriaScreen(
    navController: NavController, 
    authViewModel: AuthViewModel = viewModel(),
    inovacaoViewModel: InovacaoViewModel = viewModel()
) {
    val userData = authViewModel.userData
    val rawName = (userData?.get("nome") ?: userData?.get("Nome"))?.toString() ?: ""
    val userName = if (rawName.isNotBlank()) rawName else "Gestor"
    
    val rawSobrenome = (userData?.get("sobrenome") ?: userData?.get("Sobrenome"))?.toString() ?: ""
    val userSobrenome = if (rawSobrenome.isNotBlank()) rawSobrenome else ""
    
    val initials = userName.take(1) + (if (userSobrenome.isNotEmpty()) userSobrenome.take(1) else "")

    val todasIdeias = inovacaoViewModel.ideias
    var selectedTab by remember { mutableStateOf(0) }
    
    val ideiasPendentes = todasIdeias
        .filter { it.status != "Aprovada" && it.status != "Recusada" }
        .sortedByDescending { it.votos }
    val ideiasRecusadas = todasIdeias
        .filter { it.status == "Recusada" }
        .sortedByDescending { it.votos }
    
    val ideias = if (selectedTab == 0) ideiasPendentes else ideiasRecusadas

    LaunchedEffect(Unit) { inovacaoViewModel.fetchIdeias() }

    Scaffold(
        topBar = { br.com.fiap.ui.components.GestorTopBar(navController, initials) },
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

            // Header Removido pois usamos TopBar

            Text(
                text = "Olá, $userName 👋 • Gestão",
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
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    label = { Text("Pendentes (${ideiasPendentes.size})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFFEF3C7),
                        selectedLabelColor = Color(0xFFD97706)
                    )
                )
                FilterChip(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    label = { Text("✕ Recusadas (${ideiasRecusadas.size})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFFEE2E2),
                        selectedLabelColor = Color(0xFFEF4444)
                    )
                )
                FilterChip(
                    selected = false,
                    onClick = { navController.navigate(Screens.GestorIdeiasAprovadas.route) },
                    label = { Text("✓ Aprovadas (${todasIdeias.count { it.status?.contains("Aprovada") == true }})") },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color(0xFFDCFCE7),
                        labelColor = Color(0xFF16A34A)
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (ideias.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhuma ideia cadastrada.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(ideias) { ideia ->
                        GestorIdeiaCardItem(ideia, inovacaoViewModel)
                    }
                    item { Spacer(modifier = Modifier.height(20.dp)) }
                }
            }
        }
    }
}

@Composable
fun GestorIdeiaCardItem(ideia: Ideia, inovacaoViewModel: InovacaoViewModel) {
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
                    text = ideia.titulo ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    color = Color(0xFF1E3A8A)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color(0xFFEFF6FF),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ThumbUp,
                                contentDescription = null,
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${ideia.votos} voto(s)",
                                fontSize = 11.sp,
                                color = Color(0xFF2563EB),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = Color(ideia.prioridadeBg ?: 0xFFF3F4F6.toInt()),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = ideia.prioridade ?: "Média",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = Color(ideia.prioridadeColor ?: 0xFF6B7280.toInt()),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Text(
                text = "${ideia.autor ?: "Colaborador"} • ${ideia.area ?: "Sem Área"} • ${ideia.tempo ?: "Recente"}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            if (!ideia.estrategiaTitulo.isNullOrBlank()) {
                Text(
                    text = "Estratégia: ${ideia.estrategiaTitulo}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF2563EB),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            Text(
                text = ideia.descricao ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF334155),
                modifier = Modifier.padding(vertical = 8.dp),
                lineHeight = 20.sp
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Impacto: ${ideia.impacto ?: "Médio"} · Obj: ${ideia.objetivo ?: "Geral"}",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = Color(0xFF4B5563)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (ideia.status == "Aprovada" || ideia.status == "Recusada") {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(ideia.statusColor).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (ideia.status == "Aprovada") Icons.Default.CheckCircle else Icons.Default.Cancel,
                            contentDescription = null,
                            tint = Color(ideia.statusTextColor),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ideia ${ideia.status}",
                            color = Color(ideia.statusTextColor),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { inovacaoViewModel.atualizarStatusIdeia(ideia.id ?: "", "Aprovada") },
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
                    
                    OutlinedButton(
                        onClick = { inovacaoViewModel.atualizarStatusIdeia(ideia.id ?: "", "Recusada") },
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
                        onClick = { if (!ideia.id.isNullOrBlank()) inovacaoViewModel.votarIdeia(ideia.id) },
                        modifier = Modifier
                            .background(Color(0xFFEFF6FF), RoundedCornerShape(8.dp))
                            .size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ThumbUp,
                            contentDescription = "Votar",
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(18.dp)
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
}

@Preview(showBackground = true)
@Composable
fun GestorCuradoriaPreview() {
    GestorCuradoriaScreen(rememberNavController())
}
