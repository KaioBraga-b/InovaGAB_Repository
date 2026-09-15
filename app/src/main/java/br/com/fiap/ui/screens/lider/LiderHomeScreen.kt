package br.com.fiap.ui.screens.lider

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import br.com.fiap.ui.components.LiderBottomBar
import br.com.fiap.ui.navigation.Screens
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.viewmodel.AuthViewModel
import br.com.fiap.viewmodel.InovacaoViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiderHomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(),
    inovacaoViewModel: InovacaoViewModel = viewModel()
) {
    val userData = authViewModel.userData
    val userName = (userData?.get("nome") ?: userData?.get("Nome"))?.toString()?.takeIf { it.isNotBlank() } ?: "Líder"
    val userSobrenome = (userData?.get("sobrenome") ?: userData?.get("Sobrenome"))?.toString() ?: ""
    val initials = userName.take(1) + (if (userSobrenome.isNotEmpty()) userSobrenome.take(1) else "")

    val dashboardData = inovacaoViewModel.dashboardResumo
    
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        inovacaoViewModel.fetchDashboardResumo()
        delay(100)
        isVisible = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Liderança", fontWeight = FontWeight.Bold) },
                actions = {
                    Surface(
                        color = Color(0xFFE0E7FF),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Text(
                            text = initials,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            color = Color(0xFF4338CA),
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = { LiderBottomBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screens.CriarEstrategia.route) },
                containerColor = Color(0xFF4338CA),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nova Estratégia")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FD))
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Text(
                text = "Olá, $userName 👋",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { 50 }
            ) {
                Column {
                    // Métricas Financeiras Principais
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF4338CA)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text("Retorno Total (ROI)", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                            
                            val animatedRoi by animateFloatAsState(
                                targetValue = if (isVisible) dashboardData.roiTotalPercentual.toFloat() else 0f,
                                animationSpec = tween(1500)
                            )
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = String.format("%.1f", animatedRoi) + "%",
                                    color = Color.White,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color(0xFF4ADE80), modifier = Modifier.padding(bottom = 6.dp))
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text("Lucro Obtido", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                                    Text("R$ ${String.format(java.util.Locale.forLanguageTag("pt-BR"), "%,.2f", dashboardData.lucroObtidoTotal)}", color = Color.White, fontWeight = FontWeight.SemiBold)
                                }
                                Column {
                                    Text("Investimento", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                                    Text("R$ ${String.format(java.util.Locale.forLanguageTag("pt-BR"), "%,.2f", dashboardData.investimentoTotal)}", color = Color.White, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Grid Secundário
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        br.com.fiap.ui.screens.gestor.DashboardMetricCard(
                            title = "Projetos Ativos",
                            value = dashboardData.projetosAtivos.toString(),
                            subtitle = "Em andamento",
                            color = Color(0xFF4338CA),
                            modifier = Modifier.weight(1f)
                        )
                        br.com.fiap.ui.screens.gestor.DashboardMetricCard(
                            title = "No Prazo",
                            value = dashboardData.projetosNoPrazo.toString(),
                            subtitle = "Em dias",
                            color = Color(0xFF16A34A),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "RESUMO ESTRATÉGICO",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (dashboardData.retornosPorEstrategia.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Text("Nenhuma estratégia com projetos vinculados.", modifier = Modifier.padding(20.dp), color = Color.Gray)
                        }
                    } else {
                        dashboardData.retornosPorEstrategia.forEach { ret ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = ret.estrategiaTitulo,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF4338CA),
                                            style = MaterialTheme.typography.titleMedium,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Surface(
                                            color = if (ret.roi > 0.0) Color(0xFFDCFCE7) else Color(0xFFF3F4F6),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = if (ret.roi > 0.0) "+${String.format(java.util.Locale("pt", "BR"), "%.1f", ret.roi)}% ROI"
                                                       else if (ret.totalProjetos == 0) "Sem projetos"
                                                       else "0,0% ROI",
                                                color = if (ret.roi > 0.0) Color(0xFF16A34A) else Color.Gray,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${ret.totalProjetos} ${if (ret.totalProjetos == 1) "projeto vinculado" else "projetos vinculados"}",
                                        fontSize = 12.sp,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    HorizontalDivider(color = Color(0xFFF1F5F9))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text("Investimento", color = Color.Gray, fontSize = 11.sp)
                                            Text(
                                                text = "R$ ${String.format(java.util.Locale.forLanguageTag("pt-BR"), "%,.2f", ret.investimentoTotal)}",
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF1E293B),
                                                fontSize = 13.sp
                                            )
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("Lucro / Retorno", color = Color.Gray, fontSize = 11.sp)
                                            Text(
                                                text = "R$ ${String.format(java.util.Locale.forLanguageTag("pt-BR"), "%,.2f", ret.retornoTotal)}",
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF10B981),
                                                fontSize = 13.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Atalho para Votação de Ideias para o Líder
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate(Screens.MinhasIdeias.route) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("🗳️ Votação de Ideias", fontWeight = FontWeight.Bold, color = Color(0xFF4338CA))
                                Text("Vote nas ideias prioritárias antes da curadoria", color = Color.Gray, fontSize = 12.sp)
                            }
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color(0xFF4338CA))
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}