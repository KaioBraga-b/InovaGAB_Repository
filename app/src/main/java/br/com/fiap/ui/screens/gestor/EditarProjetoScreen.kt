package br.com.fiap.ui.screens.gestor

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
import androidx.compose.ui.text.font.FontWeight
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarProjetoScreen(
    navController: NavController, 
    projetoId: String,
    authViewModel: AuthViewModel = viewModel(),
    inovacaoViewModel: InovacaoViewModel = viewModel()
) {
    val userData = authViewModel.userData
    val userRole = (userData?.get("role") ?: userData?.get("Role"))?.toString() ?: "GESTOR"
    
    val projeto = inovacaoViewModel.projetos.find { it.id == projetoId }
    
    var titulo by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var progresso by remember { mutableDoubleStateOf(0.0) }
    var etapaSelecionada by remember { mutableIntStateOf(0) }
    var initialized by remember { mutableStateOf(false) }

    val etapas = listOf("Ideação", "Aprovação", "Execução", "Resultado")

    LaunchedEffect(projeto) {
        if (!initialized && projeto != null) {
            titulo = projeto.titulo
            area = projeto.area
            progresso = projeto.progresso
            etapaSelecionada = projeto.etapaAtiva
            initialized = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Projeto", fontWeight = FontWeight.Bold, color = BluePrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = BluePrimary)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        inovacaoViewModel.excluirProjeto(projetoId)
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = Color.Red)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
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
                    .padding(24.dp)
            ) {
                Text(
                    text = "Informações Básicas",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E3A8A)
                )
                
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Nome do Projeto") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor = BlueSecondary,
                        unfocusedBorderColor = BorderGray
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = area,
                    onValueChange = { area = it },
                    label = { Text("Área Responsável") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor = BlueSecondary,
                        unfocusedBorderColor = BorderGray
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Progresso Atual",
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
                    value = progresso.toFloat(),
                    onValueChange = { progresso = it.toDouble() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF2563EB),
                        activeTrackColor = Color(0xFF2563EB),
                        inactiveTrackColor = Color(0xFFE5E7EB)
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Mudar Etapa",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E3A8A)
                )
                
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    etapas.forEachIndexed { index, etapa ->
                        val isSelected = etapaSelecionada == index
                        Button(
                            onClick = { 
                                etapaSelecionada = index 
                                if (progresso < (index + 1) * 0.25) {
                                    progresso = (index + 1) * 0.25
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) Color(0xFF2563EB) else Color(0xFFF3F4F6),
                                contentColor = if (isSelected) Color.White else Color.Gray
                            ),
                            contentPadding = PaddingValues(horizontal = 2.dp)
                        ) {
                            Text(text = etapa, fontSize = 9.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = { 
                        if (titulo.isNotBlank()) {
                            inovacaoViewModel.atualizarProjeto(projetoId, titulo, area, progresso, etapaSelecionada)
                            navController.popBackStack() 
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                ) {
                    Text("Salvar Alterações", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditarProjetoPreview() {
    EditarProjetoScreen(rememberNavController(), "ID_TESTE")
}
