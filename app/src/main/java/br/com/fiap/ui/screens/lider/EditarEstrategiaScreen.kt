package br.com.fiap.ui.screens.lider

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import br.com.fiap.ui.theme.*
import br.com.fiap.viewmodel.InovacaoViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.viewmodel.AuthViewModel
import br.com.fiap.model.Permissions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarEstrategiaScreen(
    navController: NavController, 
    estrategiaId: String,
    authViewModel: AuthViewModel = viewModel(),
    inovacaoViewModel: InovacaoViewModel = viewModel()
) {
    val userData = authViewModel.userData
    val userRole = (userData?.get("role") ?: userData?.get("Role"))?.toString() ?: "LIDER"
    val isLider = userRole == "LIDER"
    
    val estrategia = inovacaoViewModel.estrategias.find { it.id == estrategiaId }
    
    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var progresso by remember { mutableDoubleStateOf(0.0) }
    var etapaSelecionada by remember { mutableIntStateOf(0) }
    var initialized by remember { mutableStateOf(false) }

    val etapas = listOf("Planejamento", "Em andamento", "Concluído")

    LaunchedEffect(estrategia) {
        if (!initialized && estrategia != null) {
            titulo = estrategia.titulo
            descricao = estrategia.descricao
            progresso = estrategia.progresso
            etapaSelecionada = when(estrategia.status) {
                "Planejamento" -> 0
                "Em andamento" -> 1
                "Concluído" -> 2
                else -> 0
            }
            initialized = true
        }
    }

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
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                
                // Header igual ao CriarEstrategia
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color(0xFF1E3A8A))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
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
                    }
                    
                    Surface(
                        color = if (userRole == "GESTOR") Color(0xFFECFDF5) else Color(0xFFEFF6FF),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (userRole == "GESTOR") "Gestor" else "Líder",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            color = if (userRole == "GESTOR") Color(0xFF10B981) else Color(0xFF2563EB),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                Text(
                    text = "Gestão de Estratégias",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp, start = 40.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Formulário em Card
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
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Editar Estratégia",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E3A8A)
                            )
                            
                            if (isLider) {
                                IconButton(onClick = {
                                    inovacaoViewModel.excluirEstrategia(estrategiaId)
                                    navController.popBackStack()
                                }, modifier = Modifier.size(32.dp)) {
                                    Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = Color.Red)
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        OutlinedTextField(
                            value = titulo,
                            onValueChange = { if(isLider) titulo = it },
                            label = { Text("Título da Estratégia") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            enabled = isLider,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedBorderColor = BlueSecondary,
                                unfocusedBorderColor = BorderGray
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        OutlinedTextField(
                            value = descricao,
                            onValueChange = { if(isLider) descricao = it },
                            label = { Text("Descrição detalhada") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            minLines = 3,
                            enabled = isLider,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedBorderColor = BlueSecondary,
                                unfocusedBorderColor = BorderGray
                            )
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Progresso e Status",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E3A8A)
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Slider(
                                value = progresso.toFloat(),
                                onValueChange = { if(isLider) progresso = it.toDouble() },
                                modifier = Modifier.weight(1f),
                                enabled = isLider,
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFF2563EB),
                                    activeTrackColor = Color(0xFF2563EB),
                                    inactiveTrackColor = Color(0xFFE5E7EB)
                                )
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "${(progresso * 100).toInt()}%",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2563EB)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Seletor de Etapas Ajustado para não cortar texto
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            etapas.forEachIndexed { index, etapa ->
                                val isSelected = etapaSelecionada == index
                                Button(
                                    onClick = { 
                                        if(isLider) {
                                            etapaSelecionada = index 
                                            // Atualiza progresso mínimo baseado na etapa
                                            if (progresso < index * 0.5) progresso = index * 0.5
                                        }
                                    },
                                    modifier = Modifier.weight(1f).height(42.dp),
                                    contentPadding = PaddingValues(horizontal = 2.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    enabled = isLider,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isSelected) Color(0xFF2563EB) else Color(0xFFF3F4F6),
                                        contentColor = if (isSelected) Color.White else Color.Gray,
                                        disabledContainerColor = if (isSelected) Color(0xFF2563EB).copy(alpha = 0.5f) else Color(0xFFF3F4F6).copy(alpha = 0.5f),
                                        disabledContentColor = if (isSelected) Color.White else Color.Gray
                                    )
                                ) {
                                    Text(
                                        text = etapa,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        maxLines = 1
                                    )
                                }
                            }
                        }

                        if (isLider) {
                            Spacer(modifier = Modifier.height(32.dp))
                            
                            Button(
                                onClick = { 
                                    if (titulo.isNotBlank()) {
                                        inovacaoViewModel.atualizarEstrategia(estrategiaId, titulo, descricao, progresso, etapaSelecionada)
                                        navController.popBackStack() 
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(52.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                            ) {
                                Text("Salvar Alterações", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditarEstrategiaPreview() {
    EditarEstrategiaScreen(rememberNavController(), "ID_TESTE")
}
