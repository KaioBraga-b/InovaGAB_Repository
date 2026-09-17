package br.com.fiap.ui.screens.gestor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.text.input.KeyboardType
import br.com.fiap.api.Area

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CriarProjetoScreen(
    navController: NavController,
    inovacaoViewModel: InovacaoViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val userData = authViewModel.userData
    val userRole = (userData?.get("role") ?: userData?.get("Role"))?.toString() ?: "GESTOR"

    var titulo by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") } // manteremos para fallback
    var areaSelecionada by remember { mutableStateOf<Area?>(null) }
    var expandedArea by remember { mutableStateOf(false) }
    var showNewAreaDialog by remember { mutableStateOf(false) }
    var novaAreaNome by remember { mutableStateOf("") }
    
    var investimento by remember { mutableStateOf("") }
    var aumentoProdutividade by remember { mutableStateOf("") }
    var expandedEstrategia by remember { mutableStateOf(false) }
    var selectedEstrategia by remember { mutableStateOf<br.com.fiap.viewmodel.Estrategia?>(null) }
    var etapaSelecionada by remember { mutableIntStateOf(0) }
    val etapas = listOf("Ideação", "Aprovação", "Execução", "Resultado")

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }

    val projetos = inovacaoViewModel.projetos

    Scaffold(
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
                .imePadding() // Suporte dinâmico ao teclado
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imeNestedScroll()
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

                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInVertically(initialOffsetY = { 50 }, animationSpec = tween(500)) + fadeIn(animationSpec = tween(500))
                ) {
                    Column {
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
                        ExposedDropdownMenuBox(
                            expanded = expandedArea,
                            onExpandedChange = { expandedArea = !expandedArea }
                        ) {
                            OutlinedTextField(
                                value = areaSelecionada?.nome ?: "Selecione uma área",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Área Responsável") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedArea) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = if (areaSelecionada != null) Color(0xFF1E293B) else Color.Gray,
                                    unfocusedTextColor = if (areaSelecionada != null) Color(0xFF1E293B) else Color.Gray,
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

                        val investimentoReais = (investimento.filter { it.isDigit() }.toLongOrNull() ?: 0L) / 100.0
                        val isInvestimentoInvalido = investimentoReais > 1_000_000.00

                        Text(
                            text = "Investimento (R$)",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray
                        )
                        OutlinedTextField(
                            value = investimento,
                            onValueChange = { newValue ->
                                // Aceita apenas dígitos
                                investimento = newValue.filter { it.isDigit() }
                            },
                            label = { Text("Investimento") },
                            placeholder = { Text("0,00") },
                            prefix = { Text("R$ ", color = Color(0xFF2563EB), fontWeight = FontWeight.Bold) },
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
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                            visualTransformation = CurrencyVisualTransformation()
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

                        var dataInicio by remember { mutableStateOf("") }
                        var prazoFinal by remember { mutableStateOf("") }
                        var showDataInicioPicker by remember { mutableStateOf(false) }
                        var showPrazoPicker by remember { mutableStateOf(false) }
                        val dataInicioPickerState = rememberDatePickerState()
                        val prazoPickerState = rememberDatePickerState()

                        if (showDataInicioPicker) {
                            DatePickerDialog(
                                onDismissRequest = { showDataInicioPicker = false },
                                confirmButton = {
                                    TextButton(onClick = {
                                        val mills = dataInicioPickerState.selectedDateMillis
                                        if (mills != null) {
                                            dataInicio = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date(mills))
                                        }
                                        showDataInicioPicker = false
                                    }) { Text("OK") }
                                },
                                dismissButton = { TextButton(onClick = { showDataInicioPicker = false }) { Text("Cancelar") } }
                            ) { DatePicker(state = dataInicioPickerState) }
                        }

                        if (showPrazoPicker) {
                            DatePickerDialog(
                                onDismissRequest = { showPrazoPicker = false },
                                confirmButton = {
                                    TextButton(onClick = {
                                        val mills = prazoPickerState.selectedDateMillis
                                        if (mills != null) {
                                            prazoFinal = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date(mills))
                                        }
                                        showPrazoPicker = false
                                    }) { Text("OK") }
                                },
                                dismissButton = { TextButton(onClick = { showPrazoPicker = false }) { Text("Cancelar") } }
                            ) { DatePicker(state = prazoPickerState) }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = dataInicio.takeIf { it.isNotBlank() } ?: "Data Inicial",
                                onValueChange = {},
                                readOnly = true,
                                enabled = false,
                                modifier = Modifier.weight(1f).clickable { showDataInicioPicker = true },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledTextColor = Color.Black,
                                    disabledBorderColor = Color(0xFF2563EB),
                                    disabledContainerColor = Color.White
                                )
                            )
                            OutlinedTextField(
                                value = prazoFinal.takeIf { it.isNotBlank() } ?: "Prazo Final",
                                onValueChange = {},
                                readOnly = true,
                                enabled = false,
                                modifier = Modifier.weight(1f).clickable { showPrazoPicker = true },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledTextColor = Color.Black,
                                    disabledBorderColor = Color(0xFF2563EB),
                                    disabledContainerColor = Color.White
                                )
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

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Ganho Estimado de Produtividade (%)",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray
                        )
                        OutlinedTextField(
                            value = aumentoProdutividade,
                            onValueChange = { newValue ->
                                if (newValue.isEmpty() || newValue.matches(Regex("""^\d*[.,]?\d*$"""))) {
                                    aumentoProdutividade = newValue
                                }
                            },
                            label = { Text("Aumento de Produtividade (%)") },
                            placeholder = { Text("Ex: 15.5") },
                            trailingIcon = { Text("%", color = Color(0xFF2563EB), fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 12.dp)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color(0xFF1E293B),
                                unfocusedTextColor = Color(0xFF1E293B),
                                focusedBorderColor = Color(0xFF2563EB),
                                unfocusedBorderColor = Color(0xFFE5E7EB),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedLabelColor = Color(0xFF2563EB),
                                unfocusedLabelColor = Color(0xFF64748B)
                            )
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
                                if (titulo.isNotBlank() && !isInvestimentoInvalido) {
                                    val etapaNome = etapas[etapaSelecionada]
                                    val formattedInvestimento = if (investimento.isNotBlank()) {
                                        val v = (investimento.filter { it.isDigit() }.toLongOrNull() ?: 0L) / 100.0
                                        String.format(java.util.Locale("pt", "BR"), "R$ %,.2f", v)
                                    } else "R$ 0,00"
                                    val valorNum = (investimento.filter { it.isDigit() }.toLongOrNull() ?: 0L) / 100.0

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
                                        area = areaSelecionada?.nome ?: area.ifBlank { "Geral" },
                                        periodo = "Iniciado agora",
                                        progresso = when(etapaSelecionada) {
                                            0 -> 0.25
                                            1 -> 0.50
                                            2 -> 0.75
                                            3 -> 1.00
                                            else -> 0.0
                                        },
                                        progressoTexto = when(etapaSelecionada) {
                                            0 -> "25% concluído"
                                            1 -> "50% concluído"
                                            2 -> "75% concluído"
                                            3 -> "100% concluído"
                                            else -> "A iniciar"
                                        },
                                        etapaAtiva = etapaSelecionada,
                                        investimento = formattedInvestimento,
                                        valorMensal = if (valorNum > 0) valorNum / 12.0 else null,
                                        aumentoProdutividade = aumentoProdutividade.replace(",", ".").toDoubleOrNull(),
                                        dataInicio = dataInicio,
                                        prazo = prazoFinal,
                                        estrategiaId = selectedEstrategia?.id,
                                        estrategiaTitulo = selectedEstrategia?.titulo
                                    ))
                                    navController.popBackStack()
                                }
                            },
                            enabled = titulo.isNotBlank() && !isInvestimentoInvalido,
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
            } // close Column
        } // close AnimatedVisibility

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
                
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CriarProjetoPreview() {
    CriarProjetoScreen(rememberNavController())
}

class CurrencyVisualTransformation : androidx.compose.ui.text.input.VisualTransformation {
    override fun filter(text: androidx.compose.ui.text.AnnotatedString): androidx.compose.ui.text.input.TransformedText {
        val originalText = text.text.filter { it.isDigit() }
        if (originalText.isEmpty()) {
            return androidx.compose.ui.text.input.TransformedText(
                text,
                androidx.compose.ui.text.input.OffsetMapping.Identity
            )
        }
        val value = originalText.toLongOrNull() ?: 0L
        val intPart = value / 100
        val decPart = value % 100
        
        val intStr = String.format("%,d", intPart).replace(",", ".")
        val formatted = "$intStr,${String.format("%02d", decPart)}"
        
        return androidx.compose.ui.text.input.TransformedText(
            androidx.compose.ui.text.AnnotatedString(formatted),
            object : androidx.compose.ui.text.input.OffsetMapping {
                override fun originalToTransformed(offset: Int): Int = formatted.length
                override fun transformedToOriginal(offset: Int): Int = originalText.length
            }
        )
    }
}
