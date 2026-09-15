package br.com.fiap.ui.screens.gestor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
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
import br.com.fiap.ui.components.GestorBottomBar
import br.com.fiap.ui.navigation.Screens
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.viewmodel.AuthViewModel
import br.com.fiap.viewmodel.InovacaoViewModel
import kotlinx.coroutines.delay

@Composable
fun GestorDashboardScreen(
    navController: NavController, 
    authViewModel: AuthViewModel = viewModel(),
    inovacaoViewModel: InovacaoViewModel = viewModel()
) {
    val userData = authViewModel.userData
    val userName = (userData?.get("nome") ?: userData?.get("Nome"))?.toString()?.takeIf { it.isNotBlank() } ?: "Gestor"
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
        topBar = { br.com.fiap.ui.components.GestorTopBar(navController, initials) },
        bottomBar = { GestorBottomBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screens.NovaIdeia.route) },
                containerColor = Color(0xFF2563EB),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nova Ideia")
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
                text = "Olá, $userName 👋 • Gestão Geral",
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
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E3A8A)),
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
                                    Text("R$ ${String.format("%,.2f", dashboardData.lucroObtidoTotal)}", color = Color.White, fontWeight = FontWeight.SemiBold)
                                }
                                Column {
                                    Text("Investimento", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                                    Text("R$ ${String.format("%,.2f", dashboardData.investimentoTotal)}", color = Color.White, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Grid Secundário
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        DashboardMetricCard(
                            title = "Projetos Ativos",
                            value = dashboardData.projetosAtivos.toString(),
                            subtitle = "Em andamento",
                            modifier = Modifier.weight(1f)
                        )
                        DashboardMetricCard(
                            title = "No Prazo",
                            value = dashboardData.projetosNoPrazo.toString(),
                            subtitle = "Em dias",
                            color = Color(0xFF16A34A),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        DashboardMetricCard(
                            title = "Ideias",
                            value = dashboardData.ideiasRegistradas.toString(),
                            subtitle = "Banco total",
                            modifier = Modifier.weight(1f)
                        )
                        DashboardMetricCard(
                            title = "Produtividade",
                            value = "+${dashboardData.aumentoMedioProdutividade}%",
                            subtitle = "Aumento médio",
                            color = Color(0xFFD97706),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "RETORNOS POR ESTRATÉGIA",
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
                            Text("Nenhum dado por estratégia.", modifier = Modifier.padding(20.dp), color = Color.Gray)
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
                                            color = Color(0xFF1E3A8A),
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
                                                text = "R$ ${String.format(java.util.Locale("pt", "BR"), "%,.2f", ret.investimentoTotal)}",
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF1E293B),
                                                fontSize = 13.sp
                                            )
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("Lucro / Retorno", color = Color.Gray, fontSize = 11.sp)
                                            Text(
                                                text = "R$ ${String.format(java.util.Locale("pt", "BR"), "%,.2f", ret.retornoTotal)}",
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF16A34A),
                                                fontSize = 13.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Curadoria Link
                    Card(
                        modifier = Modifier.fillMaxWidth(),
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
                                Text("Gerenciar banco de ideias", fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A))
                                Text("Aprovar ou recusar ideias enviadas", color = Color.Gray, fontSize = 12.sp)
                            }
                            IconButton(onClick = { navController.navigate(Screens.GestorCuradoria.route) }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color(0xFF2563EB))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Registrar Investimento Link
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2563EB)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("💰 Registrar Investimento", fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Adicionar lucro, ROI e investimento por projeto", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                            }
                            IconButton(onClick = { navController.navigate(Screens.InvestimentoProjeto.route) }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White)
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun DashboardMetricCard(title: String, value: String, subtitle: String, modifier: Modifier = Modifier, color: Color = Color(0xFF2563EB)) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, color = Color.Gray, fontSize = 11.sp)
        }
    }
}