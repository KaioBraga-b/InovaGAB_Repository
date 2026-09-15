package br.com.fiap.ui.screens.operador

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ThumbUp
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
import br.com.fiap.ui.components.DynamicBottomBar
import br.com.fiap.ui.theme.*
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.viewmodel.InovacaoViewModel
import br.com.fiap.viewmodel.AuthViewModel
import br.com.fiap.viewmodel.Ideia
import br.com.fiap.ui.navigation.Screens

@Composable
fun MinhasIdeiasScreen(
    navController: NavController,
    inovacaoViewModel: InovacaoViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val userId = authViewModel.currentUserId ?: ""
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        inovacaoViewModel.fetchIdeias()
    }

    val todasIdeias = inovacaoViewModel.ideias
    val ideiasParaVotacao = todasIdeias
        .filter { it.status != "Aprovada" && it.status != "Recusada" }
        .sortedByDescending { it.votos }
    val minhasIdeias = todasIdeias
        .filter { it.userId == userId }
        .sortedByDescending { it.votos }
    val ideias = if (selectedTab == 0) ideiasParaVotacao else minhasIdeias

    Scaffold(
        bottomBar = { DynamicBottomBar(navController, authViewModel) }
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
                        text = "${ideias.size} ideias",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = Color(0xFF2563EB),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Text(
                text = "Votação e Acompanhamento de Ideias",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tabs de Filtro
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    label = { Text("🗳️ Em Votação (${ideiasParaVotacao.size})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFEFF6FF),
                        selectedLabelColor = Color(0xFF2563EB)
                    )
                )
                FilterChip(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    label = { Text("👤 Minhas Ideias (${minhasIdeias.size})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFEFF6FF),
                        selectedLabelColor = Color(0xFF2563EB)
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (ideias.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        if (selectedTab == 0) "Nenhuma ideia aberta para votação no momento."
                        else "Você ainda não cadastrou nenhuma ideia.",
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(ideias, key = { it.id ?: it.titulo ?: "" }) { ideia ->
                        IdeiaCard(
                            ideia = ideia,
                            onEditClick = {
                                if (ideia.userId == userId) {
                                    navController.navigate("${Screens.EditarIdeia.route}/${ideia.id}")
                                }
                            },
                            onVoteClick = {
                                if (!ideia.id.isNullOrBlank()) {
                                    inovacaoViewModel.votarIdeia(ideia.id)
                                }
                            }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(20.dp)) }
                }
            }
        }
    }
}

@Composable
fun IdeiaCard(
    ideia: Ideia, 
    onEditClick: () -> Unit,
    onVoteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEditClick() },
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = ideia.titulo ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E3A8A)
                    )
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color(ideia.statusColor),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = ideia.status ?: "",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = Color(ideia.statusTextColor),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Text(
                text = "${ideia.autor ?: "Colaborador"} • Área: ${ideia.area ?: "Sem Área"} • ${ideia.tempo ?: "Recente"}",
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

            // Descrição detalhada preenchida
            if (!ideia.descricao.isNullOrBlank()) {
                Text(
                    text = ideia.descricao,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF334155),
                    modifier = Modifier.padding(vertical = 6.dp),
                    lineHeight = 20.sp
                )
            }

            // Badges de Impacto e Objetivo
            if (!ideia.impacto.isNullOrBlank() || !ideia.objetivo.isNullOrBlank()) {
                Row(
                    modifier = Modifier.padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!ideia.impacto.isNullOrBlank()) {
                        Surface(
                            color = Color(0xFFEFF6FF),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Impacto: ${ideia.impacto}",
                                color = Color(0xFF2563EB),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    if (!ideia.objetivo.isNullOrBlank()) {
                        Surface(
                            color = Color(0xFFF3F4F6),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Obj: ${ideia.objetivo}",
                                color = Color(0xFF4B5563),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            LinearProgressIndicator(
                progress = { ideia.progresso.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .padding(vertical = 4.dp),
                color = if (ideia.status?.contains("Aprovada") == true) Color(0xFF10B981) else Color(0xFF2563EB),
                trackColor = Color(0xFFE5E7EB),
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )

            if (!ideia.etapa.isNullOrEmpty()) {
                Text(
                    text = ideia.etapa ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            // Barra de Votação Interativa
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFFEFF6FF),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ThumbUp,
                            contentDescription = null,
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${ideia.votos} voto(s)",
                            fontSize = 13.sp,
                            color = Color(0xFF2563EB),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Button(
                    onClick = { onVoteClick() },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = "Votar",
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Votar", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MinhasIdeiasPreview() {
    MinhasIdeiasScreen(rememberNavController())
}
