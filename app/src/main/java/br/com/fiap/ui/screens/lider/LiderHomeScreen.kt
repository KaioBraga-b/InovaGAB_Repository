package br.com.fiap.ui.screens.lider

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import br.com.fiap.ui.components.LiderBottomBar
import br.com.fiap.ui.navigation.Screens
import br.com.fiap.ui.theme.*
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.viewmodel.AuthViewModel

@Composable
fun LiderHomeScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    val userData by authViewModel.userData
    val userName = (userData?.get("nome") ?: userData?.get("Nome"))?.toString() ?: "Líder"
    val userSobrenome = (userData?.get("sobrenome") ?: userData?.get("Sobrenome"))?.toString() ?: ""
    val initials = userName.take(1) + (if (userSobrenome.isNotEmpty()) userSobrenome.take(1) else "R")

    Scaffold(
        bottomBar = { LiderBottomBar(navController) }
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
                    Surface(
                        color = Color(0xFFEFF6FF),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Líder",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            color = Color(0xFF2563EB),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    IconButton(
                        onClick = { navController.navigate(Screens.Profile.route) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFF8B5CF6), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(initials, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }

            Text(
                text = "Olá, $userName 👋 • Liderança",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Grid de Resumo
            Row(modifier = Modifier.fillMaxWidth()) {
                SummaryCard(
                    title = "ROI TOTAL",
                    value = "R$ 47k",
                    subtitle = "↑ +23% vs meta",
                    subtitleColor = Color(0xFF16A34A),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                SummaryCard(
                    title = "IDEIAS REGISTRADAS",
                    value = "148",
                    subtitle = "↑ +31 este mês",
                    subtitleColor = Color(0xFF2563EB),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                SummaryCard(
                    title = "PROJETOS ATIVOS",
                    value = "4",
                    subtitle = "2 no prazo",
                    subtitleColor = Color(0xFFD97706),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                SummaryCard(
                    title = "ENGAJAMENTO",
                    value = "68%",
                    subtitle = "colaboradores ativos",
                    subtitleColor = Color(0xFF16A34A),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "RESULTADOS POR PROJETO",
                style = MaterialTheme.typography.labelLarge,
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ProjectResultItem(
                        title = "Rota Inteligente GAB",
                        subtitle = "Logística · Jun 2025",
                        value = "R$ 12k/mês",
                        badge = "ROI 220%",
                        badgeColor = Color(0xFF16A34A),
                        badgeBg = Color(0xFFDCFCE7)
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF3F4F6))
                    ProjectResultItem(
                        title = "App Vistoria Digital",
                        subtitle = "Passageiros · Ago 2025",
                        value = "Em execução",
                        valueColor = Color(0xFFD97706),
                        bottomText = "Est: R$ 8k/mês"
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "GESTÃO ESTRATÉGICA",
                style = MaterialTheme.typography.labelLarge,
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Gerenciar orientações estratégicas",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E3A8A)
                        )
                        Text(
                            "CRUD completo de objetivos do grupo",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                    IconButton(onClick = { navController.navigate("${Screens.Estrategia.route}/LIDER") }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color(0xFF2563EB)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SummaryCard(title: String, value: String, subtitle: String, subtitleColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A))
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, fontSize = 11.sp, color = subtitleColor, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun ProjectResultItem(
    title: String,
    subtitle: String,
    value: String,
    valueColor: Color = Color(0xFF2563EB),
    badge: String? = null,
    badgeColor: Color = Color.White,
    badgeBg: Color = Color.White,
    bottomText: String? = null
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column {
            Text(title, fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A))
            Text(subtitle, color = Color.Gray, fontSize = 12.sp)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(value, fontWeight = FontWeight.Bold, color = valueColor)
            if (badge != null) {
                Surface(
                    color = badgeBg,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = badge,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        color = badgeColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            if (bottomText != null) {
                Text(bottomText, color = Color.Gray, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LiderHomePreview() {
    LiderHomeScreen(rememberNavController())
}
