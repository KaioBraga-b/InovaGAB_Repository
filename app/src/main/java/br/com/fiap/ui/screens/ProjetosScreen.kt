package br.com.fiap.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import br.com.fiap.ui.components.LiderBottomBar
import br.com.fiap.ui.navigation.Screens
import br.com.fiap.ui.theme.*
import br.com.fiap.viewmodel.InovacaoViewModel
import br.com.fiap.viewmodel.Projeto
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.model.Permissions

import br.com.fiap.viewmodel.AuthViewModel

@Composable
fun ProjetosScreen(
    navController: NavController, 
    userRole: String = "GESTOR",
    authViewModel: AuthViewModel = viewModel(),
    inovacaoViewModel: InovacaoViewModel = viewModel()
) {
    val projetos = inovacaoViewModel.projetos
    val canManage = Permissions.canManageProjects(userRole)
    
    val userData = authViewModel.userData
    val userName = (userData?.get("nome") ?: userData?.get("Nome"))?.toString() ?: ""
    val userSobrenome = (userData?.get("sobrenome") ?: userData?.get("Sobrenome"))?.toString() ?: ""
    val initials = if (userName.isNotEmpty()) {
        userName.take(1) + (if (userSobrenome.isNotEmpty()) userSobrenome.take(1) else "")
    } else {
        if (userRole == "GESTOR") "G" else "L"
    }

    LaunchedEffect(Unit) {
        inovacaoViewModel.fetchProjetos()
    }

    Scaffold(
        topBar = {
            if (userRole == "GESTOR") {
                br.com.fiap.ui.components.GestorTopBar(navController, initials)
            }
        },
        bottomBar = {
            if (userRole == "GESTOR") GestorBottomBar(navController)
            else LiderBottomBar(navController)
        },
        floatingActionButton = {
            if (canManage) {
                FloatingActionButton(
                    onClick = { navController.navigate(Screens.CriarProjeto.route) },
                    containerColor = Color(0xFF2563EB),
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Novo Projeto")
                }
            }
        }
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
            if (userRole != "GESTOR") {
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
            }

            Text(
                text = "Projetos em andamento · ${projetos.size} ativos",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (projetos.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nenhum projeto cadastrado no momento.",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(projetos) { projeto ->
                        ProjetoCard(projeto, navController, canEdit = canManage)
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

fun formatCurrencyDisplay(raw: String?): String {
    if (raw.isNullOrBlank()) return ""
    val cleaned = raw.trim()
    if (cleaned.startsWith("R$")) return cleaned
    val num = cleaned.replace("[^0-9,.]".toRegex(), "").replace(".", "").replace(",", ".").toDoubleOrNull()
    return if (num != null) {
        val format = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("pt", "BR"))
        format.format(num)
    } else {
        "R$ $cleaned"
    }
}

@Composable
fun ProjetoCard(projeto: Projeto, navController: NavController? = null, canEdit: Boolean = false) {
    val formattedInvestimento = formatCurrencyDisplay(projeto.investimento)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = tween(400)),
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
                        text = projeto.titulo ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E3A8A)
                    )
                    Text(
                        text = "${projeto.area} · ${projeto.periodo}" +
                               (if (formattedInvestimento.isNotBlank()) " · $formattedInvestimento" else ""),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    
                    if (!projeto.estrategiaTitulo.isNullOrBlank()) {
                        Text(
                            text = "Estratégia: ${projeto.estrategiaTitulo}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF2563EB), // Blue primary
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    if (!projeto.descricao.isNullOrBlank()) {
                        Text(
                            text = projeto.descricao,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF334155),
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }

                    if ((projeto.roi != null && projeto.roi > 0.0) || (projeto.lucroObtido != null && projeto.lucroObtido > 0.0)) {
                        Row(
                            modifier = Modifier.padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (projeto.roi != null && projeto.roi > 0.0) {
                                Surface(
                                    color = Color(0xFFDCFCE7),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "ROI: +${String.format(java.util.Locale("pt", "BR"), "%.1f", projeto.roi)}%",
                                        color = Color(0xFF15803D),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                            if (projeto.lucroObtido != null && projeto.lucroObtido > 0.0) {
                                Surface(
                                    color = Color(0xFFE0F2FE),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "Lucro: R$ ${String.format(java.util.Locale("pt", "BR"), "%,.2f", projeto.lucroObtido)}",
                                        color = Color(0xFF0369A1),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color(projeto.statusBg),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = projeto.status ?: "",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            color = Color(projeto.statusColor),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    if (canEdit) {
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = { 
                                navController?.navigate("${Screens.EditarProjeto.route}/${projeto.id}") 
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar",
                                tint = Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Timeline / Etapas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val etapas = listOf("Ideação", "Aprovação", "Execução", "Resultado")
                etapas.forEachIndexed { index, etapa ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(
                                    if (index <= projeto.etapaAtiva) Color(0xFF2563EB) else Color(0xFFE5E7EB),
                                    CircleShape
                                )
                        )
                        Text(
                            text = etapa,
                            fontSize = 10.sp,
                            color = if (index <= projeto.etapaAtiva) Color(0xFF2563EB) else Color.Gray,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Barra de Progresso
            var targetProgress by remember { mutableStateOf(0f) }
            LaunchedEffect(projeto.progresso) {
                targetProgress = projeto.progresso.toFloat()
            }
            
            val animatedProgress by animateFloatAsState(
                targetValue = targetProgress,
                animationSpec = tween(durationMillis = 1000),
                label = "progressAnimation"
            )

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = if (projeto.status == "Execução" || projeto.status == "Em execução") Color(0xFF2563EB) else Color(0xFFF59E0B),
                trackColor = Color(0xFFF3F4F6),
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = projeto.progressoTexto ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
                
                projeto.estMensal?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
            
            if (projeto.tarefas.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = Color(0xFFF3F4F6))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Checklist do Projeto:",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                projeto.tarefas.forEach { tarefa ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(if (tarefa.concluido) Color(0xFF16A34A) else Color(0xFFE5E7EB), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (tarefa.concluido) {
                                Icon(
                                    imageVector = Icons.Default.Add, // ideally a check icon, using Add or similar standard icon if check is unavailable, but material icons usually have Check.
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = tarefa.titulo,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (tarefa.concluido) Color.Gray else Color.Black,
                            textDecoration = if (tarefa.concluido) androidx.compose.ui.text.style.TextDecoration.LineThrough else androidx.compose.ui.text.style.TextDecoration.None
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProjetosGestorPreview() {
    ProjetosScreen(rememberNavController(), userRole = "GESTOR")
}

@Preview(showBackground = true)
@Composable
fun ProjetosLiderPreview() {
    ProjetosScreen(rememberNavController(), userRole = "LIDER")
}
