package br.com.fiap.inovagab.ui.screens.operador

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
import br.com.fiap.inovagab.ui.components.OperadorBottomBar
import br.com.fiap.inovagab.ui.navigation.Screens
import br.com.fiap.inovagab.ui.theme.*

@Composable
fun OperadorHomeScreen(navController: NavController) {
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
                    Box {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = Color.LightGray
                        )
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color.Red, CircleShape)
                                .align(Alignment.TopEnd)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFF2563EB), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("JM", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Bom dia, João Motorista 👋 • Operacional",
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
                        Text("3 ideias", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                        Text("registradas · 1 aprovada ✨", color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
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
            StrategyCard()

            Spacer(modifier = Modifier.height(24.dp))
            SectionTitle("DESTAQUE DA SEMANA")
            HighlightCard()
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
fun StrategyCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                    Text("🎯 Objetivo 2026", color = Color(0xFF2563EB), fontWeight = FontWeight
                        .Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(color = Color(0xFFEFF6FF), shape = RoundedCornerShape(4.dp)) {
                        Text("Novo", color = Color(0xFF2563EB), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("Digitalização da Operação", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 16.sp)
                Text("Reduzir processos manuais em 40% até dezembro — Ver todos os direcionamentos", color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun HighlightCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFFEF3C7), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("🏆", fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("Maria Silva foi reconhecida!", fontWeight = FontWeight.Bold, color = Color.Black)
                Text("Ideia aprovada: Rota inteligente via app...", color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OperadorHomePreview() {
    OperadorHomeScreen(rememberNavController())
}