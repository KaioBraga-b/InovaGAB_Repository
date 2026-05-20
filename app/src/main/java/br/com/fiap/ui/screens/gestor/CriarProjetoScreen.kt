package br.com.fiap.ui.screens.gestor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import br.com.fiap.ui.components.GestorBottomBar
import br.com.fiap.ui.components.LiderBottomBar
import br.com.fiap.viewmodel.InovacaoViewModel
import br.com.fiap.viewmodel.Projeto
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.ui.screens.ProjetoCard
import br.com.fiap.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CriarProjetoScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(),
    inovacaoViewModel: InovacaoViewModel = viewModel()
) {
    val userData = authViewModel.userData
    val userRole = (userData?.get("role") ?: userData?.get("Role"))?.toString() ?: "GESTOR"

    var titulo by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var etapaSelecionada by remember { mutableIntStateOf(0) }
    val etapas = listOf("Ideação", "Aprovação", "Execução", "Resultado")

    val projetos = inovacaoViewModel.projetos

    Scaffold(
        bottomBar = { 
            if (userRole == "GESTOR") GestorBottomBar(navController)
            else LiderBottomBar(navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FD))
                .padding(innerPadding)
                .imePadding() // Suporte dinâmico ao teclado
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
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
                        color = if (userRole == "GESTOR") Color(0xFFECFDF5) else Color(0xFFF5F3FF),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (userRole == "GESTOR") "Gestor" else "Líder",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            color = if (userRole == "GESTOR") Color(0xFF10B981) else Color(0xFF8B5CF6),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                Text(
                    text = "Criação e Acompanhamento de Projetos",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Formulário de Criação
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Novo Projeto da Unidade",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E3A8A)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        OutlinedTextField(
                            value = titulo,
                            onValueChange = { titulo = it },
                            placeholder = { Text("Nome do projeto") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedBorderColor = Color(0xFF2563EB),
                                unfocusedBorderColor = Color(0xFFE5E7EB)
                            ),
                            singleLine = true
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Área Responsável",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray
                        )
                        OutlinedTextField(
                            value = area,
                            onValueChange = { area = it },
                            placeholder = { Text("Ex: Operação, RH...") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedBorderColor = Color(0xFF2563EB),
                                unfocusedBorderColor = Color(0xFFE5E7EB)
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Etapa Atual",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Seletor Interativo de Etapas
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
                                if (titulo.isNotBlank()) {
                                    val etapaNome = etapas[etapaSelecionada]
                                    inovacaoViewModel.adicionarProjeto(Projeto(
                                        titulo = titulo,
                                        status = etapaNome,
                                        statusColor = when(etapaSelecionada) {
                                            0 -> Color(0xFFD97706).toArgb()
                                            1 -> Color(0xFF2563EB).toArgb()
                                            2 -> Color(0xFF8B5CF6).toArgb()
                                            3 -> Color(0xFF16A34A).toArgb()
                                            else -> Color.Gray.toArgb()
                                        },
                                        statusBg = when(etapaSelecionada) {
                                            0 -> Color(0xFFFEF3C7).toArgb()
                                            1 -> Color(0xFFEFF6FF).toArgb()
                                            2 -> Color(0xFFF5F3FF).toArgb()
                                            3 -> Color(0xFFDCFCE7).toArgb()
                                            else -> Color(0xFFF3F4F6).toArgb()
                                        },
                                        area = area.ifBlank { "Geral" },
                                        periodo = "Iniciado agora",
                                        progresso = when(etapaSelecionada) {
                                            0 -> 0.1
                                            1 -> 0.3
                                            2 -> 0.6
                                            3 -> 1.0
                                            else -> 0.0
                                        },
                                        progressoTexto = when(etapaSelecionada) {
                                            0 -> "10% concluído"
                                            1 -> "30% concluído"
                                            2 -> "60% concluído"
                                            3 -> "100% concluído"
                                            else -> "A iniciar"
                                        },
                                        etapaAtiva = etapaSelecionada
                                    ))
                                    navController.popBackStack()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Lançar Projeto 🚀")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Projetos Recentes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E3A8A)
                )

                Spacer(modifier = Modifier.height(16.dp))

                projetos.take(3).forEach { projeto ->
                    ProjetoCard(projeto)
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CriarProjetoPreview() {
    CriarProjetoScreen(rememberNavController())
}
