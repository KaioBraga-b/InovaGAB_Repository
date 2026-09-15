package br.com.fiap.ui.screens.operador

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import br.com.fiap.ui.components.OperadorBottomBar
import br.com.fiap.ui.navigation.Screens
import br.com.fiap.ui.theme.*

import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.viewmodel.AuthViewModel
import br.com.fiap.viewmodel.InovacaoViewModel

@Composable
fun OperadorHomeScreen(
    navController: NavController, 
    authViewModel: AuthViewModel = viewModel(),
    inovacaoViewModel: InovacaoViewModel = viewModel()
) {
    val userData = authViewModel.userData
    val rawName = (userData?.get("nome") ?: userData?.get("Nome"))?.toString() ?: ""
    val userName = if (rawName.isNotBlank()) rawName else "Operador"
    
    val rawSobrenome = (userData?.get("sobrenome") ?: userData?.get("Sobrenome"))?.toString() ?: ""
    val userSobrenome = if (rawSobrenome.isNotBlank()) rawSobrenome else ""
    
    val initials = userName.take(1) + (if (userSobrenome.isNotEmpty()) userSobrenome.take(1) else "")
    val userId = authViewModel.currentUserId ?: ""
    val minhasIdeias = inovacaoViewModel.ideias.filter { it.userId == userId }
    val aprovadasCount = minhasIdeias.count { it.status?.contains("Aprovada") == true }

    Scaffold(
        bottomBar = { OperadorBottomBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FD))
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
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
                    IconButton(
                        onClick = { navController.navigate(Screens.Notificacoes.route) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Avisos e Notificações",
                                tint = Color(0xFF1E3A8A)
                            )
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color.Red, CircleShape)
                                    .align(Alignment.TopEnd)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    IconButton(
                        onClick = { navController.navigate(Screens.Profile.route) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFF2563EB), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(initials, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Bom dia, $userName 👋 • Operacional",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Impact Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFF1E3A8A), Color(0xFF2563EB))
                            )
                        )
                        .padding(20.dp)
                        .fillMaxWidth()
                ) {
                    Column {
                        Text("MEU IMPACTO", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("${minhasIdeias.size} ideias", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                        Text("registradas · $aprovadasCount aprovada(s) ✨", color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            SectionTitle("AÇÕES RÁPIDAS")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                QuickActionCard(
                    title = "Nova ideia",
                    subtitle = "Registrar problema ou sugestão",
                    icon = Icons.Default.Lightbulb,
                    iconColor = Color(0xFFFBBF24),
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Screens.NovaIdeia.route) }
                )
                QuickActionCard(
                    title = "Minhas ideias",
                    subtitle = "Acompanhar status",
                    icon = Icons.Default.ListAlt,
                    iconColor = Color(0xFF60A5FA),
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Screens.MinhasIdeias.route) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            SectionTitle("ESTRATÉGIA DO GRUPO")
            StrategyCard(navController, inovacaoViewModel)
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = Color.Gray,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
fun QuickActionCard(
    title: String, 
    subtitle: String, 
    icon: ImageVector, 
    iconColor: Color, 
    modifier: Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A), fontSize = 14.sp)
            Text(subtitle, color = Color.Gray, fontSize = 10.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}

@Composable
fun StrategyCard(navController: NavController, inovacaoViewModel: InovacaoViewModel) {
    val estrategia = inovacaoViewModel.estrategias.firstOrNull()
    
    Card(
        modifier = Modifier.fillMaxWidth().clickable { navController.navigate("${Screens.Estrategia.route}/OPERADOR") },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(80.dp)
                    .background(Color(0xFF2563EB), RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🎯 Estratégia em Destaque", color = Color(0xFF2563EB), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(color = Color(0xFFEFF6FF), shape = RoundedCornerShape(4.dp)) {
                        Text("Ativa", color = Color(0xFF2563EB), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                if (estrategia != null) {
                    Text(estrategia.titulo ?: "", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 16.sp, maxLines = 1)
                    Text(estrategia.descricao ?: "", color = Color.Gray, fontSize = 12.sp, maxLines = 2)
                } else {
                    Text("Nenhuma estratégia cadastrada", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 16.sp)
                    Text("Aguardando novas orientações do Gestor", color = Color.Gray, fontSize = 12.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OperadorHomePreview() {
    OperadorHomeScreen(rememberNavController())
}
