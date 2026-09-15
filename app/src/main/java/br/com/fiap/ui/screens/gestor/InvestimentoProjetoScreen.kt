package br.com.fiap.ui.screens.gestor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import br.com.fiap.viewmodel.InovacaoViewModel
import br.com.fiap.viewmodel.Projeto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestimentoProjetoScreen(
    navController: NavController,
    inovacaoViewModel: InovacaoViewModel = viewModel()
) {
    val projetos = inovacaoViewModel.projetos

    var expandedProjeto by remember { mutableStateOf(false) }
    var selectedProjeto by remember { mutableStateOf<Projeto?>(null) }
    
    var valorMensal by remember { mutableStateOf("") }
    var roi by remember { mutableStateOf("") }
    var lucroObtido by remember { mutableStateOf("") }
    var duracao by remember { mutableStateOf("") }
    var resultados by remember { mutableStateOf("") }
    
    var showError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Investimento", fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color(0xFF1E3A8A))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF8F9FD))
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FD))
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            
            Text(
                text = "Selecione o Projeto",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )
            ExposedDropdownMenuBox(
                expanded = expandedProjeto,
                onExpandedChange = { expandedProjeto = !expandedProjeto }
            ) {
                OutlinedTextField(
                    value = selectedProjeto?.titulo ?: "Selecione um projeto existente",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Projeto") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedProjeto) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = if (selectedProjeto != null) Color(0xFF1E293B) else Color.Gray,
                        unfocusedTextColor = if (selectedProjeto != null) Color(0xFF1E293B) else Color.Gray,
                        focusedBorderColor = Color(0xFF2563EB),
                        unfocusedBorderColor = Color(0xFFE5E7EB),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedLabelColor = Color(0xFF2563EB),
                        unfocusedLabelColor = Color(0xFF64748B)
                    )
                )
                ExposedDropdownMenu(
                    expanded = expandedProjeto,
                    onDismissRequest = { expandedProjeto = false }
                ) {
                    projetos.forEach { proj ->
                        DropdownMenuItem(
                            text = { Text(proj.titulo ?: "Sem título", color = Color(0xFF1E293B), fontWeight = FontWeight.Medium) },
                            onClick = {
                                selectedProjeto = proj
                                expandedProjeto = false
                                // Preencher se já houver dados
                                valorMensal = proj.valorMensal?.toString() ?: ""
                                roi = proj.roi?.toString() ?: ""
                                lucroObtido = proj.lucroObtido?.toString() ?: ""
                                duracao = proj.duracao ?: ""
                                resultados = proj.resultadosAlcancados ?: ""
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val valorNum = valorMensal.replace(",", ".").toDoubleOrNull() ?: 0.0
            val lucroNum = lucroObtido.replace(",", ".").toDoubleOrNull() ?: 0.0
            val isExcedido = valorNum > 1_000_000.00 || lucroNum > 1_000_000.00

            Text("Duração do Projeto (ex: 6 meses)", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            OutlinedTextField(
                value = duracao,
                onValueChange = { duracao = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF1E293B),
                    unfocusedTextColor = Color(0xFF1E293B),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color(0xFF2563EB),
                    unfocusedBorderColor = Color(0xFFE5E7EB)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Valor Mensal do Investimento (Obrigatório)", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            OutlinedTextField(
                value = valorMensal,
                onValueChange = { valorMensal = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                placeholder = { Text("Ex: 15000.00") },
                isError = (showError && valorMensal.isBlank()) || valorNum > 1_000_000.00,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF1E293B),
                    unfocusedTextColor = Color(0xFF1E293B),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color(0xFF2563EB),
                    unfocusedBorderColor = Color(0xFFE5E7EB)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("ROI Esperado/Obtido (%) (Obrigatório)", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            OutlinedTextField(
                value = roi,
                onValueChange = { roi = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                placeholder = { Text("Ex: 150.5") },
                isError = showError && roi.isBlank(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF1E293B),
                    unfocusedTextColor = Color(0xFF1E293B),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color(0xFF2563EB),
                    unfocusedBorderColor = Color(0xFFE5E7EB)
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            Text("Lucro / Receita Esperada ou Obtida (R$)", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            OutlinedTextField(
                value = lucroObtido,
                onValueChange = { lucroObtido = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                placeholder = { Text("Ex: 50000.00") },
                isError = lucroNum > 1_000_000.00,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF1E293B),
                    unfocusedTextColor = Color(0xFF1E293B),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color(0xFF2563EB),
                    unfocusedBorderColor = Color(0xFFE5E7EB)
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            Text("Resultados Alcançados", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            OutlinedTextField(
                value = resultados,
                onValueChange = { resultados = it },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                shape = RoundedCornerShape(12.dp),
                placeholder = { Text("Descreva os resultados...") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF1E293B),
                    unfocusedTextColor = Color(0xFF1E293B),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color(0xFF2563EB),
                    unfocusedBorderColor = Color(0xFFE5E7EB)
                )
            )

            if (isExcedido) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("O investimento ou receita/lucro único não pode ultrapassar R$ 1.000.000,00", color = Color.Red, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            } else if (showError) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Preencha o projeto, valor mensal e ROI.", color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (selectedProjeto != null && valorMensal.isNotBlank() && roi.isNotBlank() && !isExcedido) {
                        val valor = valorMensal.replace(",", ".").toDoubleOrNull() ?: 0.0
                        val r = roi.replace(",", ".").toDoubleOrNull() ?: 0.0
                        val lucro = lucroObtido.replace(",", ".").toDoubleOrNull() ?: 0.0
                        inovacaoViewModel.atualizarInvestimentoProjeto(
                            id = selectedProjeto!!.id!!,
                            duracao = duracao,
                            valorMensal = valor,
                            roi = r,
                            lucroObtido = lucro,
                            resultadosAlcancados = resultados
                        )
                        navController.popBackStack()
                    } else {
                        showError = true
                    }
                },
                enabled = !isExcedido,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Salvar Investimento", fontWeight = FontWeight.Bold)
            }
        }
    }
}
