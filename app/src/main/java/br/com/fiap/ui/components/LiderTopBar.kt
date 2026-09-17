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
fun LiderTopBar(navController: NavController, initials: String) {
    var showToggleMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(
                text = buildAnnotatedString {
                    append("Inova")
                    withStyle(style = SpanStyle(color = Color(0xFF6366F1))) {
                        append("GAB")
                    }
                },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF312E81)
            )
        },
        actions = {
            // Chip Perfil Líder
            Surface(
                color = Color(0xFFEEF2FF),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Líder",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    color = Color(0xFF4338CA),
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
                        .background(Color(0xFF4338CA), CircleShape),
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
                    .background(Color(0xFFEEF2FF), RoundedCornerShape(10.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu de Navegação",
                    tint = Color(0xFF4338CA)
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
                                .background(Color(0xFF4338CA), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(initials, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Navegação do Líder",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF312E81)
                            )
                            Text(
                                text = "Gestão de Squad • Grupo Águia Branca",
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

                // Seção 1: Gestão & Painéis
                Text(
                    text = "PAINEL & ESTRATÉGIA",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                ToggleMenuItem(
                    icon = Icons.Default.BarChart,
                    iconBg = Color(0xFFEEF2FF),
                    iconColor = Color(0xFF4338CA),
                    title = "Dashboard da Liderança",
                    subtitle = "Métricas da equipe, progresso e indicadores",
                    onClick = {
                        showToggleMenu = false
                        navController.navigate(Screens.LiderHome.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )

                ToggleMenuItem(
                    icon = Icons.Default.Folder,
                    iconBg = Color(0xFFEFF6FF),
                    iconColor = Color(0xFF2563EB),
                    title = "Projetos em Andamento",
                    subtitle = "Acompanhamento das iniciativas do squad",
                    onClick = {
                        showToggleMenu = false
                        navController.navigate(Screens.LiderProjetos.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )

                ToggleMenuItem(
                    icon = Icons.Default.AdsClick,
                    iconBg = Color(0xFFECFDF5),
                    iconColor = Color(0xFF059669),
                    title = "Estratégias & Metas",
                    subtitle = "Pilares estratégicos e objetivos da unidade",
                    onClick = {
                        showToggleMenu = false
                        navController.navigate("${Screens.Estrategia.route}/LIDER") {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )

                ToggleMenuItem(
                    icon = Icons.Default.AddCircle,
                    iconBg = Color(0xFFF5F3FF),
                    iconColor = Color(0xFF7C3AED),
                    title = "Criar Nova Estratégia",
                    subtitle = "Definir novo objetivo anual e pilares",
                    onClick = {
                        showToggleMenu = false
                        navController.navigate(Screens.CriarEstrategia.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFF1F5F9))
                Spacer(modifier = Modifier.height(16.dp))

                // Seção 2: Inovação & Ideias
                Text(
                    text = "INOVAÇÃO & IDEIAS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                ToggleMenuItem(
                    icon = Icons.Default.Lightbulb,
                    iconBg = Color(0xFFFEF3C7),
                    iconColor = Color(0xFFD97706),
                    title = "Ideias Aprovadas",
                    subtitle = "Ideias validadas e prontas para virar projetos",
                    onClick = {
                        showToggleMenu = false
                        navController.navigate(Screens.GestorIdeiasAprovadas.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )

                ToggleMenuItem(
                    icon = Icons.Default.Add,
                    iconBg = Color(0xFFEEF2FF),
                    iconColor = Color(0xFF4338CA),
                    title = "Lançar Nova Ideia",
                    subtitle = "Cadastrar uma sugestão para avaliação",
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
                    subtitle = "Histórico de contribuições pessoais",
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

                // Seção 3: Comunicação & Conta
                Text(
                    text = "COMUNICAÇÃO & PERFIL",
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
                    title = "Avisos & Notificações",
                    subtitle = "Comunicados do sistema e atualizações",
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
                    iconBg = Color(0xFFEEF2FF),
                    iconColor = Color(0xFF4338CA),
                    title = "Meu Perfil",
                    subtitle = "Dados cadastrais e credenciais de acesso",
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
