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
import androidx.compose.runtime.Composable
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

@Composable
fun ProjetosScreen(
    navController: NavController, 
    userRole: String = "GESTOR",
    inovacaoViewModel: InovacaoViewModel = viewModel()
) {
    val projetos = inovacaoViewModel.projetos

    Scaffold(
        bottomBar = {
            if (userRole == "GESTOR") GestorBottomBar(navController)
            else LiderBottomBar(navController)
        },
        floatingActionButton = {
            if (userRole == "GESTOR") {
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
                            text = if (userRole == "GESTOR") "Gestor" else "Líder",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            color = Color(0xFF2563EB),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(if (userRole == "GESTOR") Color(0xFFEF4444) else Color(0xFF8B5CF6), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(if (userRole == "GESTOR") "AP" else "DR", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
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

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(projetos) { projeto ->
                    ProjetoCard(projeto, navController, canEdit = userRole == "GESTOR")
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun ProjetoCard(projeto: Projeto, navController: NavController? = null, canEdit: Boolean = false) {
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = projeto.titulo,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E3A8A)
                    )
                    Text(
                        text = "${projeto.area} · ${projeto.periodo}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = projeto.statusBg,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = projeto.status,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            color = projeto.statusColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    if (canEdit) {
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = { 
                                navController?.navigate("${Screens.EditarProjeto.route}/${projeto.titulo}") 
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
            LinearProgressIndicator(
                progress = { projeto.progresso },
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
                    text = projeto.progressoTexto,
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
