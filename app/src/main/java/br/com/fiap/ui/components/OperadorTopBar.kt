package br.com.fiap.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import br.com.fiap.ui.navigation.Screens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OperadorTopBar(navController: NavController, initials: String) {
    var showToggleMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(
                text = buildAnnotatedString {
                    append("Inova")
                    withStyle(style = SpanStyle(color = Color(0xFF0284C7))) {
                        append("GAB")
                    }
                },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E3A8A)
            )
        },
        actions = {
            // Chip Perfil Operador
            Surface(
                color = Color(0xFFF0FDF4),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Operador",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    color = Color(0xFF16A34A),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.width(8.dp))

            // Avatar Perfil
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
                    Text(initials, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Botão Toggle Menu (Hambúrguer Estilizado)
            IconButton(
                onClick = { showToggleMenu = true },
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFEFF6FF), RoundedCornerShape(10.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu de Navegação",
                    tint = Color(0xFF2563EB)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFFF8F9FD)
        )
    )

    // Modal BottomSheet: Toggle Menu com Ícones de Navegação
    if (showToggleMenu) {
        ModalBottomSheet(
            onDismissRequest = { showToggleMenu = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Cabeçalho do Drawer/Toggle Menu
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color(0xFF2563EB), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(initials, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Navegação do Operador",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E3A8A)
                            )
                            Text(
                                text = "Inovação Operacional • Grupo Águia Branca",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }

                    IconButton(onClick = { showToggleMenu = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFF1F5F9))
                Spacer(modifier = Modifier.height(16.dp))

                // Seção 1: Meu Espaço de Inovação
                Text(
                    text = "MEU ESPAÇO DE INOVAÇÃO",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                ToggleMenuItem(
                    icon = Icons.Default.Home,
                    iconBg = Color(0xFFEFF6FF),
                    iconColor = Color(0xFF2563EB),
                    title = "Página Inicial",
                    subtitle = "Resumo das suas contribuições e impacto",
                    onClick = {
                        showToggleMenu = false
                        navController.navigate(Screens.OperadorHome.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )

                ToggleMenuItem(
                    icon = Icons.Default.Lightbulb,
                    iconBg = Color(0xFFFEF3C7),
                    iconColor = Color(0xFFD97706),
                    title = "Lançar Nova Ideia",
                    subtitle = "Compartilhe uma oportunidade ou melhoria",
                    onClick = {
                        showToggleMenu = false
                        navController.navigate(Screens.NovaIdeia.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )

                ToggleMenuItem(
                    icon = Icons.AutoMirrored.Filled.ListAlt,
                    iconBg = Color(0xFFF1F5F9),
                    iconColor = Color(0xFF475569),
                    title = "Minhas Ideias",
                    subtitle = "Histórico e status de aprovação das suas propostas",
                    onClick = {
                        showToggleMenu = false
                        navController.navigate(Screens.MinhasIdeias.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFF1F5F9))
                Spacer(modifier = Modifier.height(16.dp))

                // Seção 2: Estratégia
                Text(
                    text = "VISÃO & ESTRATÉGIA",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                ToggleMenuItem(
                    icon = Icons.Default.AdsClick,
                    iconBg = Color(0xFFECFDF5),
                    iconColor = Color(0xFF059669),
                    title = "Estratégias & Objetivos",
                    subtitle = "Metas anuais do Grupo Águia Branca",
                    onClick = {
                        showToggleMenu = false
                        navController.navigate("${Screens.Estrategia.route}/OPERADOR") {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFF1F5F9))
                Spacer(modifier = Modifier.height(16.dp))

                // Seção 3: Comunicação & Conta
                Text(
                    text = "COMUNICAÇÃO & CONTA",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                ToggleMenuItem(
                    icon = Icons.Default.Notifications,
                    iconBg = Color(0xFFFFFBEB),
                    iconColor = Color(0xFFD97706),
                    title = "Avisos & Comunicados",
                    subtitle = "Comunicados do time e atualizações",
                    onClick = {
                        showToggleMenu = false
                        navController.navigate(Screens.Notificacoes.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )

                ToggleMenuItem(
                    icon = Icons.Default.Person,
                    iconBg = Color(0xFFEFF6FF),
                    iconColor = Color(0xFF2563EB),
                    title = "Meu Perfil",
                    subtitle = "Informações cadastrais e credenciais",
                    onClick = {
                        showToggleMenu = false
                        navController.navigate(Screens.Profile.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
