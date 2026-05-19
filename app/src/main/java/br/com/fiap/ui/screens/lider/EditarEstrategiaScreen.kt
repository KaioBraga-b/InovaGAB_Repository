package br.com.fiap.ui.screens.lider

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import br.com.fiap.ui.components.LiderBottomBar

import br.com.fiap.viewmodel.InovacaoViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarEstrategiaScreen(
    navController: NavController, 
    estrategiaTitulo: String,
    inovacaoViewModel: InovacaoViewModel = viewModel()
) {
    val estrategia = inovacaoViewModel.estrategias.find { it.titulo == estrategiaTitulo }
    
    var progresso by remember { mutableFloatStateOf(estrategia?.progresso ?: 0f) }
    var etapaSelecionada by remember { mutableIntStateOf(
        when(estrategia?.status) {
            "Planejamento" -> 0
            "Em andamento" -> 1
            "Concluído" -> 2
            else -> 0
        }
    ) }
    val etapas = listOf("Planejamento", "Em andamento", "Concluído")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Estratégia", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = { LiderBottomBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FD))
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFFEFF6FF), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Flag, contentDescription = null, tint = Color(0xFF2563EB))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = estrategiaTitulo.ifBlank { "Título da Estratégia" },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E3A8A)
                        )
                        Text(
                            text = "Ajuste o foco estratégico",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Progresso do Objetivo",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E3A8A)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "${(progresso * 100).toInt()}%",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF2563EB)
                )
            }

            Slider(
                value = progresso,
                onValueChange = { progresso = it },
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF2563EB),
                    activeTrackColor = Color(0xFF2563EB),
                    inactiveTrackColor = Color(0xFFE5E7EB)
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Status da Estratégia",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E3A8A)
            )
            
            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                etapas.forEachIndexed { index, etapa ->
                    val isSelected = etapaSelecionada == index
                    Surface(
                        onClick = { 
                            etapaSelecionada = index 
                            progresso = when(index) {
                                0 -> 0.1f
                                1 -> 0.5f
                                2 -> 1.0f
                                else -> progresso
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Color(0xFFEFF6FF) else Color.White,
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF2563EB)) else null
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = etapa,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color(0xFF2563EB) else Color.Black
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { 
                    inovacaoViewModel.atualizarEstrategia(estrategiaTitulo, progresso, etapaSelecionada)
                    navController.popBackStack() 
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
            ) {
                Text("Confirmar Atualização", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditarEstrategiaPreview() {
    EditarEstrategiaScreen(rememberNavController(), "Expansão de Malha Regional")
}
