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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import br.com.fiap.ui.components.GestorBottomBar
import br.com.fiap.ui.components.LiderBottomBar
import br.com.fiap.ui.theme.*
import br.com.fiap.viewmodel.InovacaoViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.viewmodel.AuthViewModel
import br.com.fiap.api.Area

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
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
    var area by remember { mutableStateOf("") } // fallback original
    var areaSelecionada by remember { mutableStateOf<Area?>(null) }
    var expandedArea by remember { mutableStateOf(false) }
    var showNewAreaDialog by remember { mutableStateOf(false) }
    var novaAreaNome by remember { mutableStateOf("") }
    var investimento by remember { mutableStateOf("") }
    var aumentoProdutividade by remember { mutableStateOf("") }
    var expandedEstrategia by remember { mutableStateOf(false) }
    var selectedEstrategia by remember { mutableStateOf<br.com.fiap.viewmodel.Estrategia?>(null) }
    var progresso by remember { mutableDoubleStateOf(0.0) }
    var etapaSelecionada by remember { mutableIntStateOf(0) }
    var resultadoValor by remember { mutableStateOf("") }
    var resultadoRoi by remember { mutableStateOf("") }
    var initialized by remember { mutableStateOf(false) }

    val etapas = listOf("Ideação", "Aprovação", "Execução", "Resultado")

    LaunchedEffect(projeto) {
        if (!initialized && projeto != null) {
            titulo = projeto.titulo ?: ""
            area = projeto.area ?: ""
            areaSelecionada = inovacaoViewModel.areas.find { it.nome == projeto.area }
            investimento = projeto.investimento ?: ""
            aumentoProdutividade = projeto.aumentoProdutividade?.let { if (it > 0) it.toString() else "" } ?: ""
            selectedEstrategia = inovacaoViewModel.estrategias.find { it.id == projeto.estrategiaId }
            progresso = projeto.progresso
            etapaSelecionada = projeto.etapaAtiva
            resultadoValor = projeto.resultadoValor ?: ""
            resultadoRoi = projeto.resultadoRoi ?: ""
            initialized = true
        }
    }
    
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
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
            if (!WindowInsets.isImeVisible) {
                if (userRole == "GESTOR") GestorBottomBar(navController)
                else LiderBottomBar(navController)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FD))
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imeNestedScroll()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInVertically(initialOffsetY = { 50 }, animationSpec = tween(500)) + fadeIn(animationSpec = tween(500))
                ) {
                    Column {
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

                Text(
                    text = "Área Responsável",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
                ExposedDropdownMenuBox(
                    expanded = expandedArea,
                    onExpandedChange = { expandedArea = !expandedArea }
                ) {
                    OutlinedTextField(
                        value = areaSelecionada?.nome ?: area.ifBlank { "Selecione uma área" },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Área Responsável") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedArea) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = if (areaSelecionada != null || area.isNotBlank()) Color(0xFF1E293B) else Color.Gray,
                            unfocusedTextColor = if (areaSelecionada != null || area.isNotBlank()) Color(0xFF1E293B) else Color.Gray,
                            focusedBorderColor = Color(0xFF2563EB),
                            unfocusedBorderColor = Color(0xFFE5E7EB),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedLabelColor = Color(0xFF2563EB),
                            unfocusedLabelColor = Color(0xFF64748B)
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedArea,
                        onDismissRequest = { expandedArea = false }
                    ) {
                        inovacaoViewModel.areas.forEach { a ->
                            DropdownMenuItem(
                                text = { Text(a.nome, color = Color(0xFF1E293B), fontWeight = FontWeight.Medium) },
                                onClick = {
                                    areaSelecionada = a
                                    expandedArea = false
                                }
                            )
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        DropdownMenuItem(
                            text = { 
                                Text("➕ Adicionar nova área", color = Color(0xFF2563EB), fontWeight = FontWeight.Bold) 
                            },
                            onClick = {
                                expandedArea = false
                                showNewAreaDialog = true
                            }
                        )
                    }
                }

                if (showNewAreaDialog) {
                    AlertDialog(
                        onDismissRequest = { showNewAreaDialog = false },
                        title = { Text("Nova Área Responsável") },
                        text = {
                            OutlinedTextField(
                                value = novaAreaNome,
                                onValueChange = { novaAreaNome = it },
                                label = { Text("Nome da Área") },
                                singleLine = true
                            )
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                if (novaAreaNome.isNotBlank()) {
                                    inovacaoViewModel.adicionarArea(novaAreaNome)
                                    areaSelecionada = Area(nome = novaAreaNome)
                                    novaAreaNome = ""
                                    showNewAreaDialog = false
                                }
                            }) { Text("Salvar") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showNewAreaDialog = false }) { Text("Cancelar") }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                val invDigits = investimento.filter { it.isDigit() }
                val invReais = (invDigits.toLongOrNull() ?: 0L) / 100.0
                val isInvestimentoInvalido = invReais > 1_000_000.00

                OutlinedTextField(
                    value = investimento,
                    onValueChange = { investimento = it },
                    label = { Text("Investimento") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    isError = isInvestimentoInvalido,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color(0xFF1E293B),
                        unfocusedTextColor = Color(0xFF1E293B),
                        focusedBorderColor = if (isInvestimentoInvalido) Color.Red else Color(0xFF2563EB),
                        unfocusedBorderColor = if (isInvestimentoInvalido) Color.Red else Color(0xFFE5E7EB),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedLabelColor = Color(0xFF2563EB),
                        unfocusedLabelColor = Color(0xFF64748B)
                    )
                )

                if (isInvestimentoInvalido) {
                    Text(
                        text = "O investimento único não pode ultrapassar R$ 1.000.000,00",
                        color = Color(0xFFEF4444),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Estratégia Vinculada",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
                ExposedDropdownMenuBox(
                    expanded = expandedEstrategia,
                    onExpandedChange = { expandedEstrategia = !expandedEstrategia }
                ) {
                    OutlinedTextField(
                        value = selectedEstrategia?.titulo ?: "Selecione uma estratégia",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Estratégia Vinculada") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEstrategia) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = if (selectedEstrategia != null) Color(0xFF1E293B) else Color.Gray,
                            unfocusedTextColor = if (selectedEstrategia != null) Color(0xFF1E293B) else Color.Gray,
                            focusedBorderColor = Color(0xFF2563EB),
                            unfocusedBorderColor = Color(0xFFE5E7EB),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedLabelColor = Color(0xFF2563EB),
                            unfocusedLabelColor = Color(0xFF64748B)
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedEstrategia,
                        onDismissRequest = { expandedEstrategia = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Nenhuma (Opcional)", color = Color.Gray) },
                            onClick = {
                                selectedEstrategia = null
                                expandedEstrategia = false
                            }
                        )
                        inovacaoViewModel.estrategias.forEach { est ->
                            DropdownMenuItem(
                                text = { Text(est.titulo ?: "", color = Color(0xFF1E293B), fontWeight = FontWeight.Medium) },
                                onClick = {
                                    selectedEstrategia = est
                                    expandedEstrategia = false
                                }
                            )
                        }
                    }
                }

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
                    onValueChange = { 
                        val p = it.toDouble().coerceIn(0.0, 1.0)
                        progresso = p
                        etapaSelecionada = when {
                            p <= 0.25 -> 0
                            p <= 0.50 -> 1
                            p <= 0.75 -> 2
                            else -> 3
                        }
                    },
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
                                progresso = (index + 1) * 0.25
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

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Resultados Alcançados (Opcional)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E3A8A)
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = resultadoValor,
                        onValueChange = { resultadoValor = it },
                        label = { Text("Valor Mensal (ex: R$ 12k)") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = BlueSecondary,
                            unfocusedBorderColor = BorderGray
                        )
                    )

                    OutlinedTextField(
                        value = resultadoRoi,
                        onValueChange = { resultadoRoi = it },
                        label = { Text("ROI (ex: 220%)") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = BlueSecondary,
                            unfocusedBorderColor = BorderGray
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = aumentoProdutividade,
                    onValueChange = { str ->
                        if (str.isEmpty() || str.matches(Regex("""^\d*[.,]?\d*$"""))) {
                            aumentoProdutividade = str
                        }
                    },
                    label = { Text("Ganho de Produtividade (%)") },
                    placeholder = { Text("Ex: 15.5") },
                    trailingIcon = { Text("%", color = BluePrimary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 12.dp)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor = BlueSecondary,
                        unfocusedBorderColor = BorderGray
                    )
                )

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = {
                        if (titulo.isNotBlank() && !isInvestimentoInvalido) {
                            inovacaoViewModel.atualizarProjeto(
                                id = projetoId,
                                titulo = titulo,
                                area = areaSelecionada?.nome ?: area.ifBlank { "Geral" },
                                novoProgresso = progresso,
                                novaEtapa = etapaSelecionada,
                                resultadoValor = resultadoValor.takeIf { it.isNotBlank() },
                                resultadoRoi = resultadoRoi.takeIf { it.isNotBlank() },
                                investimento = investimento,
                                estrategiaId = selectedEstrategia?.id,
                                estrategiaTitulo = selectedEstrategia?.titulo,
                                aumentoProdutividade = aumentoProdutividade.replace(",", ".").toDoubleOrNull()
                            )
                            navController.popBackStack()
                        }
                    },
                    enabled = titulo.isNotBlank() && !isInvestimentoInvalido,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                ) {
                    Text("Salvar Alterações", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(modifier = Modifier.height(48.dp))
            } // Column 124
        } // AnimatedVisibility 120
    } // Column 114
    } // Column 107
    } // Scaffold 106
} // fun EditarProjetoScreen

@Preview(showBackground = true)
@Composable
fun EditarProjetoPreview() {
    EditarProjetoScreen(rememberNavController(), "ID_TESTE")
}
