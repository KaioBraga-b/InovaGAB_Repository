package br.com.fiap.ui.screens.lider

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
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
import androidx.compose.material.icons.filled.Edit

import br.com.fiap.viewmodel.InovacaoViewModel
import br.com.fiap.viewmodel.Estrategia
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CriarEstrategiaScreen(
    navController: NavController,
    inovacaoViewModel: InovacaoViewModel = viewModel()
) {
    var novaEstrategiaTitulo by remember { mutableStateOf("") }
    var novaEstrategiaDescricao by remember { mutableStateOf("") }
    var etapaSelecionada by remember { mutableIntStateOf(0) }
    val etapas = listOf("Planejamento", "Em andamento", "Concluído")

    val estrategias = inovacaoViewModel.estrategias

    Scaffold(
        bottomBar = { LiderBottomBar(navController) }
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
                        text = "Liderança",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = Color(0xFF2563EB),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Text(
                text = "Gestão de Estratégias",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Formulário de Criação Rápida
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Cadastrar Nova Estratégia",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E3A8A)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedTextField(
                        value = novaEstrategiaTitulo,
                        onValueChange = { novaEstrategiaTitulo = it },
                        placeholder = { Text("Título da estratégia") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2563EB),
                            unfocusedBorderColor = Color(0xFFE5E7EB)
                        ),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = novaEstrategiaDescricao,
                        onValueChange = { novaEstrategiaDescricao = it },
                        placeholder = { Text("Breve descrição do objetivo...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2563EB),
                            unfocusedBorderColor = Color(0xFFE5E7EB)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Fase Estratégica",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        etapas.forEachIndexed { index, etapa ->
                            val isSelected = etapaSelecionada == index
                            Button(
                                onClick = { etapaSelecionada = index },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(horizontal = 4.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) Color(0xFF2563EB) else Color(0xFFF3F4F6),
                                    contentColor = if (isSelected) Color.White else Color.Gray
                                )
                            ) {
                                Text(
                                    text = etapa,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Button(
                        onClick = {
                            if (novaEstrategiaTitulo.isNotBlank()) {
                                inovacaoViewModel.adicionarEstrategia(Estrategia(
                                    titulo = novaEstrategiaTitulo,
                                    descricao = novaEstrategiaDescricao,
                                    status = etapas[etapaSelecionada],
                                    statusColor = when(etapaSelecionada) {
                                        0 -> Color(0xFFF3F4F6).toArgb()
                                        1 -> Color(0xFFEFF6FF).toArgb()
                                        2 -> Color(0xFFDCFCE7).toArgb()
                                        else -> Color.Gray.toArgb()
                                    },
                                    statusTextColor = when(etapaSelecionada) {
                                        0 -> Color(0xFF6B7280).toArgb()
                                        1 -> Color(0xFF2563EB).toArgb()
                                        2 -> Color(0xFF16A34A).toArgb()
                                        else -> Color.Black.toArgb()
                                    },
                                    progresso = when(etapaSelecionada) {
                                        0 -> 0.1
                                        1 -> 0.5
                                        2 -> 1.0
                                        else -> 0.0
                                    },
                                    dataCriacao = "Criada agora"
                                ))
                                novaEstrategiaTitulo = ""
                                novaEstrategiaDescricao = ""
                                etapaSelecionada = 0
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cadastrar Estratégia")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Estratégias Ativas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E3A8A)
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(estrategias) { estrategia ->
                    EstrategiaCard(estrategia, navController)
                }
                item { Spacer(modifier = Modifier.height(20.dp)) }
            }
        }
    }
}


@Composable
fun EstrategiaCard(estrategia: Estrategia, navController: NavController? = null, userRole: String = "OPERADOR") {
    val canEdit = br.com.fiap.model.Permissions.canEditStrategy(userRole)
    
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
                Row(modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = null,
                        tint = Color(0xFF2563EB),
                        modifier = Modifier.size(20.dp).padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = estrategia.titulo,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E3A8A)
                        )
                        Text(
                            text = estrategia.dataCriacao,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color(estrategia.statusColor),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = estrategia.status,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = Color(estrategia.statusTextColor),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    if (canEdit) {
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = { 
                                navController?.navigate("${Screens.EditarEstrategia.route}/${estrategia.id}") 
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

            Text(
                text = estrategia.descricao,
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = { estrategia.progresso.toFloat() },
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp),
                    color = Color(0xFF2563EB),
                    trackColor = Color(0xFFE5E7EB),
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "${(estrategia.progresso * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2563EB)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CriarEstrategiaPreview() {
    CriarEstrategiaScreen(rememberNavController())
}
