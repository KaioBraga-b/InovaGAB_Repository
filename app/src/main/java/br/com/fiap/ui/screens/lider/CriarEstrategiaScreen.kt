package br.com.fiap.ui.screens.lider

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
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
import br.com.fiap.ui.navigation.Screens
import br.com.fiap.viewmodel.InovacaoViewModel
import br.com.fiap.viewmodel.Estrategia
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CriarEstrategiaScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(),
    inovacaoViewModel: InovacaoViewModel = viewModel()
) {
    val userData = authViewModel.userData
    // Padrão GESTOR para evitar pular para fluxo de líder indevidamente
    val userRole = (userData?.get("role") ?: userData?.get("Role"))?.toString() ?: "GESTOR"

    var novaEstrategiaTitulo by remember { mutableStateOf("") }
    var novaEstrategiaDescricao by remember { mutableStateOf("") }
    var novaEstrategiaCategoria by remember { mutableStateOf("") }
    var novaEstrategiaCampanha by remember { mutableStateOf("") }
    var novaEstrategiaData by remember { mutableStateOf("") }
    var novaEstrategiaOrientacoes by remember { mutableStateOf("") }
    var etapaSelecionada by remember { mutableIntStateOf(0) }
    
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }
    val etapas = listOf("Planejamento", "Em andamento", "Concluído")

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
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
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
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedBorderColor = Color(0xFF2563EB),
                                unfocusedBorderColor = Color(0xFFE5E7EB)
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        
                        var expandedCategoria by remember { mutableStateOf(false) }
                        val categorias = listOf("Receita", "Custo", "Produtividade", "Qualidade", "Satisfação")
                        
                        ExposedDropdownMenuBox(
                            expanded = expandedCategoria,
                            onExpandedChange = { expandedCategoria = !expandedCategoria }
                        ) {
                            OutlinedTextField(
                                value = novaEstrategiaCategoria.ifBlank { "Selecione uma Categoria" },
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Categoria") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoria) },
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = if (novaEstrategiaCategoria.isNotBlank()) Color(0xFF1E293B) else Color.Gray,
                                    unfocusedTextColor = if (novaEstrategiaCategoria.isNotBlank()) Color(0xFF1E293B) else Color.Gray,
                                    focusedBorderColor = Color(0xFF2563EB),
                                    unfocusedBorderColor = Color(0xFFE5E7EB),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedLabelColor = Color(0xFF2563EB),
                                    unfocusedLabelColor = Color(0xFF64748B)
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = expandedCategoria,
                                onDismissRequest = { expandedCategoria = false }
                            ) {
                                categorias.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat, color = Color(0xFF1E293B), fontWeight = FontWeight.Medium) },
                                        onClick = {
                                            novaEstrategiaCategoria = cat
                                            expandedCategoria = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        
                        var expandedCampanha by remember { mutableStateOf(false) }
                        val campanhas = listOf("2025Q1", "2025Q2", "2025Q3", "2025Q4", "Inovação Anual")

                        ExposedDropdownMenuBox(
                            expanded = expandedCampanha,
                            onExpandedChange = { expandedCampanha = !expandedCampanha }
                        ) {
                            OutlinedTextField(
                                value = novaEstrategiaCampanha.ifBlank { "Selecione uma Campanha" },
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Campanha") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCampanha) },
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = if (novaEstrategiaCampanha.isNotBlank()) Color(0xFF1E293B) else Color.Gray,
                                    unfocusedTextColor = if (novaEstrategiaCampanha.isNotBlank()) Color(0xFF1E293B) else Color.Gray,
                                    focusedBorderColor = Color(0xFF2563EB),
                                    unfocusedBorderColor = Color(0xFFE5E7EB),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedLabelColor = Color(0xFF2563EB),
                                    unfocusedLabelColor = Color(0xFF64748B)
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = expandedCampanha,
                                onDismissRequest = { expandedCampanha = false }
                            ) {
                                campanhas.forEach { camp ->
                                    DropdownMenuItem(
                                        text = { Text(camp, color = Color(0xFF1E293B), fontWeight = FontWeight.Medium) },
                                        onClick = {
                                            novaEstrategiaCampanha = camp
                                            expandedCampanha = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        
                        var showDatePicker by remember { mutableStateOf(false) }
                        val datePickerState = rememberDatePickerState()

                        if (showDatePicker) {
                            DatePickerDialog(
                                onDismissRequest = { showDatePicker = false },
                                confirmButton = {
                                    TextButton(onClick = {
                                        val dataMills = datePickerState.selectedDateMillis
                                        if (dataMills != null) {
                                            val formatter = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                                            novaEstrategiaData = formatter.format(java.util.Date(dataMills))
                                        }
                                        showDatePicker = false
                                    }) {
                                        Text("OK")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showDatePicker = false }) {
                                        Text("Cancelar")
                                    }
                                }
                            ) {
                                DatePicker(state = datePickerState)
                            }
                        }

                        OutlinedTextField(
                            value = novaEstrategiaData.ifBlank { "Selecione a data de vigência" },
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            label = { Text("Data de Vigência") },
                            modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = if (novaEstrategiaData.isNotBlank()) Color(0xFF1E293B) else Color.Gray,
                                disabledBorderColor = Color(0xFFE5E7EB),
                                disabledContainerColor = Color.White,
                                disabledLabelColor = Color(0xFF64748B)
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = novaEstrategiaOrientacoes,
                            onValueChange = { novaEstrategiaOrientacoes = it },
                            placeholder = { Text("Orientações / TO-DOs") },
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
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
                        
                        var isLoading by remember { mutableStateOf(false) }

                        Button(
                            onClick = {
                                if (novaEstrategiaTitulo.isNotBlank() && !isLoading) {
                                    isLoading = true
                                    inovacaoViewModel.adicionarEstrategia(
                                        Estrategia(
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
                                            dataCriacao = "Criada agora",
                                            categoria = novaEstrategiaCategoria,
                                            campanha = novaEstrategiaCampanha,
                                            dataVigencia = novaEstrategiaData,
                                            orientacoes = novaEstrategiaOrientacoes
                                        )
                                    ) { success ->
                                        isLoading = false
                                        if (success) {
                                            navController.popBackStack()
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Cadastrar Estratégia")
                            }
                        }
                    }
                }
            } // close Column
        } // close AnimatedVisibility

        Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Estratégias Recentes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E3A8A)
                )

                Spacer(modifier = Modifier.height(16.dp))
                
                // Exibe as estratégias usando Column normal (sem item {})
                inovacaoViewModel.estrategias.take(3).forEach { estrategia ->
                    EstrategiaCard(estrategia, navController, userRole = userRole)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun EstrategiaCard(estrategia: Estrategia, navController: NavController? = null, userRole: String = "OPERADOR") {
    val canEdit = br.com.fiap.model.Permissions.canEditStrategy(userRole)
    
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
                            text = estrategia.titulo ?: "",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E3A8A)
                        )
                        Text(
                            text = estrategia.dataCriacao ?: "",
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
                            text = estrategia.status ?: "",
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
                text = estrategia.descricao ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            if (!estrategia.categoria.isNullOrBlank() || !estrategia.campanha.isNullOrBlank()) {
                Row(modifier = Modifier.padding(bottom = 12.dp)) {
                    if (!estrategia.categoria.isNullOrBlank()) {
                        Surface(
                            color = Color(0xFFF3F4F6),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Cat: ${estrategia.categoria}",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = Color(0xFF4B5563),
                                fontSize = 10.sp
                            )
                        }
                    }
                    if (!estrategia.categoria.isNullOrBlank() && !estrategia.campanha.isNullOrBlank()) {
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    if (!estrategia.campanha.isNullOrBlank()) {
                        Surface(
                            color = Color(0xFFF3F4F6),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Campanha: ${estrategia.campanha}",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = Color(0xFF4B5563),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            if (!estrategia.orientacoes.isNullOrBlank()) {
                Surface(
                    color = Color(0xFFFFFBEB),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Orientações/TO-DOs:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD97706)
                        )
                        Text(
                            text = estrategia.orientacoes,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                var targetProgress by remember { mutableStateOf(0f) }
                LaunchedEffect(estrategia.progresso) {
                    targetProgress = estrategia.progresso.toFloat()
                }
                
                val animatedProgress by animateFloatAsState(
                    targetValue = targetProgress,
                    animationSpec = tween(durationMillis = 1000),
                    label = "progressAnimation"
                )

                LinearProgressIndicator(
                    progress = { animatedProgress },
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
