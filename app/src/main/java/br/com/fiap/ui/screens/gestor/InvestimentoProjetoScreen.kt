package br.com.fiap.ui.screens.gestor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import br.com.fiap.api.TransacaoFinanceira
import br.com.fiap.viewmodel.InovacaoViewModel
import br.com.fiap.viewmodel.Projeto
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun InvestimentoProjetoScreen(
    navController: NavController,
    inovacaoViewModel: InovacaoViewModel = viewModel()
) {
    val projetos = inovacaoViewModel.projetos
    val transacoes = inovacaoViewModel.transacoesFinanceiras

    var currentTab by remember { mutableIntStateOf(0) } // 0: Registrar, 1: Histórico
    var tipoOperacao by remember { mutableStateOf("INVESTIMENTO") } // "INVESTIMENTO", "RECEITA", "DESPESA"

    var expandedProjeto by remember { mutableStateOf(false) }
    var selectedProjeto by remember { mutableStateOf<Projeto?>(null) }

    // Helpers de formatação de moeda brasileira (R$)
    fun doubleToCentsString(value: Double?): String {
        if (value == null || value <= 0.0) return ""
        val cents = Math.round(value * 100.0)
        return cents.toString()
    }

    fun formatCentsToCurrency(centsRaw: String): String {
        val digits = centsRaw.filter { it.isDigit() }
        if (digits.isEmpty()) return ""
        val value = digits.toDouble() / 100.0
        val nf = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"))
        return nf.format(value)
    }

    fun parseCurrencyToDouble(centsRaw: String): Double {
        val digits = centsRaw.filter { it.isDigit() }
        if (digits.isEmpty()) return 0.0
        return digits.toDouble() / 100.0
    }

    // Campos do formulário com armazenamento em dígitos de centavos para máscara
    var valorMensalCents by remember { mutableStateOf("") }
    var roiRaw by remember { mutableStateOf("") }
    var lucroCents by remember { mutableStateOf("") }
    var duracaoSelecionada by remember { mutableStateOf("") }
    var expandedDuracao by remember { mutableStateOf(false) }
    var resultados by remember { mutableStateOf("") }

    // Campos específicos para Lançamento Avulso (Receita / Despesa)
    var transacaoDescricao by remember { mutableStateOf("") }
    var transacaoValorCents by remember { mutableStateOf("") }
    var transacaoCategoria by remember { mutableStateOf("Operacional") }

    var showError by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val opcoesDuracao = listOf(
        "1 mês", "2 meses", "3 meses", "6 meses",
        "10 meses", "12 meses", "18 meses", "24 meses", "36 meses"
    )

    val categoriasTransacao = listOf("Operacional", "Tecnologia", "RH/Treinamento", "Infraestrutura", "Marketing", "Outros")

    LaunchedEffect(Unit) {
        inovacaoViewModel.fetchProjetos()
        inovacaoViewModel.fetchTransacoes()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Gestão Financeira", fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A), fontSize = 18.sp)
                        Text("Investimentos, receitas e despesas", fontSize = 12.sp, color = Color.Gray)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color(0xFF1E3A8A))
                    }
                },
                actions = {
                    IconButton(onClick = {
                        inovacaoViewModel.fetchProjetos()
                        inovacaoViewModel.fetchTransacoes()
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Atualizar", tint = Color(0xFF2563EB))
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
                .consumeWindowInsets(innerPadding)
                .imePadding()
        ) {
            // Abas Superiores: Registrar vs Histórico
            TabRow(
                selectedTabIndex = currentTab,
                containerColor = Color.White,
                contentColor = Color(0xFF2563EB)
            ) {
                Tab(
                    selected = currentTab == 0,
                    onClick = { currentTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AddCircleOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Registrar", fontWeight = FontWeight.Bold)
                        }
                    }
                )
                Tab(
                    selected = currentTab == 1,
                    onClick = { currentTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Histórico (${transacoes.size})", fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }

            if (currentTab == 0) {
                // FORMULÁRIO DE REGISTRO
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .imeNestedScroll()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    // Seleção do tipo de registro: Investimento Completo, Receita ou Despesa
                    Text("O que deseja registrar?", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = tipoOperacao == "INVESTIMENTO",
                            onClick = { tipoOperacao = "INVESTIMENTO" },
                            label = { Text("Métricas do Projeto", fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF2563EB),
                                selectedLabelColor = Color.White,
                                selectedLeadingIconColor = Color.White
                            ),
                            modifier = Modifier.weight(1.3f)
                        )
                        FilterChip(
                            selected = tipoOperacao == "RECEITA",
                            onClick = { tipoOperacao = "RECEITA" },
                            label = { Text("Receita 💰", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF16A34A),
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = tipoOperacao == "DESPESA",
                            onClick = { tipoOperacao = "DESPESA" },
                            label = { Text("Despesa 📉", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFDC2626),
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // SELEÇÃO DO PROJETO (Comum a todos os tipos)
                    Text("Selecione o Projeto *", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
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
                            isError = showError && selectedProjeto == null,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = if (selectedProjeto != null) Color(0xFF1E293B) else Color.Gray,
                                unfocusedTextColor = if (selectedProjeto != null) Color(0xFF1E293B) else Color.Gray,
                                focusedBorderColor = Color(0xFF2563EB),
                                unfocusedBorderColor = Color(0xFFE5E7EB),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
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
                                        // Preencher com dados existentes do projeto
                                        valorMensalCents = doubleToCentsString(proj.valorMensal)
                                        roiRaw = proj.roi?.let { String.format(Locale.US, "%.1f", it) } ?: ""
                                        lucroCents = doubleToCentsString(proj.lucroObtido)
                                        duracaoSelecionada = proj.duracao ?: ""
                                        resultados = proj.resultadosAlcancados ?: ""
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (tipoOperacao == "INVESTIMENTO") {
                        // FORMULÁRIO DE INVESTIMENTO COMPLETO
                        
                        // DURAÇÃO DO PROJETO EM LISTA (Não aceita digitação livre)
                        Text("Duração do Projeto *", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                        ExposedDropdownMenuBox(
                            expanded = expandedDuracao,
                            onExpandedChange = { expandedDuracao = !expandedDuracao }
                        ) {
                            OutlinedTextField(
                                value = if (duracaoSelecionada.isNotBlank()) duracaoSelecionada else "Selecione a duração...",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDuracao) },
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = if (duracaoSelecionada.isNotBlank()) Color(0xFF1E293B) else Color.Gray,
                                    unfocusedTextColor = if (duracaoSelecionada.isNotBlank()) Color(0xFF1E293B) else Color.Gray,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = Color(0xFF2563EB),
                                    unfocusedBorderColor = Color(0xFFE5E7EB)
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = expandedDuracao,
                                onDismissRequest = { expandedDuracao = false }
                            ) {
                                opcoesDuracao.forEach { dur ->
                                    DropdownMenuItem(
                                        text = { Text(dur, color = Color(0xFF1E293B), fontWeight = FontWeight.Medium) },
                                        onClick = {
                                            duracaoSelecionada = dur
                                            expandedDuracao = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // VALOR MENSAL DO INVESTIMENTO (COM MÁSCARA MONETÁRIA R$)
                        val valorMensalNum = parseCurrencyToDouble(valorMensalCents)
                        val isValorExcedido = valorMensalNum > 1_000_000.00

                        Text("Valor Mensal do Investimento (Obrigatório)", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                        OutlinedTextField(
                            value = formatCentsToCurrency(valorMensalCents),
                            onValueChange = { input ->
                                val digits = input.filter { it.isDigit() }
                                if (digits.length <= 11) {
                                    valorMensalCents = digits
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            placeholder = { Text("R$ 0,00") },
                            isError = (showError && valorMensalCents.isBlank()) || isValorExcedido,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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

                        // ROI ESPERADO / OBTIDO (COM SUFIXO E FORMATAÇÃO %)
                        Text("ROI Esperado/Obtido (%) (Obrigatório)", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                        OutlinedTextField(
                            value = roiRaw,
                            onValueChange = { input ->
                                val filtered = input.replace(',', '.').filter { it.isDigit() || it == '.' }
                                if (filtered.count { it == '.' } <= 1 && filtered.length <= 6) {
                                    roiRaw = filtered
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            placeholder = { Text("Ex: 150.5") },
                            trailingIcon = {
                                Text(
                                    text = "%",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2563EB),
                                    modifier = Modifier.padding(end = 12.dp),
                                    fontSize = 16.sp
                                )
                            },
                            isError = showError && roiRaw.isBlank(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
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

                        // LUCRO / RECEITA ESPERADA OU OBTIDA (COM MÁSCARA MONETÁRIA R$)
                        val lucroNum = parseCurrencyToDouble(lucroCents)
                        val isLucroExcedido = lucroNum > 1_000_000.00

                        Text("Lucro / Receita Esperada ou Obtida (R$)", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                        OutlinedTextField(
                            value = formatCentsToCurrency(lucroCents),
                            onValueChange = { input ->
                                val digits = input.filter { it.isDigit() }
                                if (digits.length <= 11) {
                                    lucroCents = digits
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            placeholder = { Text("R$ 0,00") },
                            isError = isLucroExcedido,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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

                        // RESULTADOS ALCANÇADOS
                        Text("Resultados Alcançados", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                        OutlinedTextField(
                            value = resultados,
                            onValueChange = { resultados = it },
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                            shape = RoundedCornerShape(12.dp),
                            placeholder = { Text("Descreva os resultados alcançados ou metas...") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color(0xFF1E293B),
                                unfocusedTextColor = Color(0xFF1E293B),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = Color(0xFF2563EB),
                                unfocusedBorderColor = Color(0xFFE5E7EB)
                            )
                        )

                        if (isValorExcedido || isLucroExcedido) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("O investimento ou receita/lucro único não pode ultrapassar R$ 1.000.000,00", color = Color.Red, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        } else if (showError) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Preencha o projeto, duração, valor mensal e ROI.", color = Color.Red, fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        Button(
                            onClick = {
                                if (selectedProjeto != null && valorMensalCents.isNotBlank() && roiRaw.isNotBlank() && !isValorExcedido && !isLucroExcedido) {
                                    val valor = parseCurrencyToDouble(valorMensalCents)
                                    val r = roiRaw.toDoubleOrNull() ?: 0.0
                                    val lucro = parseCurrencyToDouble(lucroCents)
                                    
                                    inovacaoViewModel.atualizarInvestimentoProjeto(
                                        id = selectedProjeto!!.id!!,
                                        duracao = duracaoSelecionada,
                                        valorMensal = valor,
                                        roi = r,
                                        lucroObtido = lucro,
                                        resultadosAlcancados = resultados
                                    )
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Investimento e Receita salvos com sucesso!")
                                    }
                                    currentTab = 1 // Direciona para o Histórico para ver o lançamento
                                } else {
                                    showError = true
                                }
                            },
                            enabled = !isValorExcedido && !isLucroExcedido,
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Salvar Investimento & Receita", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }

                    } else {
                        // FORMULÁRIO DE LANÇAMENTO ESPECÍFICO: RECEITA OU DESPESA
                        val isReceita = tipoOperacao == "RECEITA"
                        val temaColor = if (isReceita) Color(0xFF16A34A) else Color(0xFFDC2626)

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = if (isReceita) Color(0xFFF0FDF4) else Color(0xFFFEF2F2)),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isReceita) Color(0xFFBBF7D0) else Color(0xFFFECACA))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = if (isReceita) "Novo Lançamento de Receita 💰" else "Novo Lançamento de Despesa 📉",
                                    fontWeight = FontWeight.Bold,
                                    color = temaColor,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = if (isReceita) "Adiciona receita obtida ou prevista ao histórico do projeto." else "Registra custo, aquisição ou despesa operacional do projeto.",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Descrição do Lançamento *", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                        OutlinedTextField(
                            value = transacaoDescricao,
                            onValueChange = { transacaoDescricao = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            placeholder = { Text(if (isReceita) "Ex: Economia de diesel no mês / Venda de sucata" else "Ex: Aquisição de sensores IoT / Treinamento") },
                            isError = showError && transacaoDescricao.isBlank(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color(0xFF1E293B),
                                unfocusedTextColor = Color(0xFF1E293B),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = temaColor,
                                unfocusedBorderColor = Color(0xFFE5E7EB)
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Valor do Lançamento (R$) *", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                        val transacaoValorNum = parseCurrencyToDouble(transacaoValorCents)
                        OutlinedTextField(
                            value = formatCentsToCurrency(transacaoValorCents),
                            onValueChange = { input ->
                                val digits = input.filter { it.isDigit() }
                                if (digits.length <= 11) {
                                    transacaoValorCents = digits
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            placeholder = { Text("R$ 0,00") },
                            isError = (showError && transacaoValorCents.isBlank()) || transacaoValorNum > 1_000_000.00,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color(0xFF1E293B),
                                unfocusedTextColor = Color(0xFF1E293B),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = temaColor,
                                unfocusedBorderColor = Color(0xFFE5E7EB)
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Categoria", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            categoriasTransacao.forEach { cat ->
                                FilterChip(
                                    selected = transacaoCategoria == cat,
                                    onClick = { transacaoCategoria = cat },
                                    label = { Text(cat, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = temaColor,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        if (showError && (selectedProjeto == null || transacaoDescricao.isBlank() || transacaoValorCents.isBlank())) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Preencha o projeto, descrição e o valor do lançamento.", color = Color.Red, fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        Button(
                            onClick = {
                                if (selectedProjeto != null && transacaoDescricao.isNotBlank() && transacaoValorCents.isNotBlank()) {
                                    val valor = parseCurrencyToDouble(transacaoValorCents)
                                    inovacaoViewModel.registrarTransacao(
                                        projetoId = selectedProjeto!!.id ?: "",
                                        projetoTitulo = selectedProjeto!!.titulo ?: "Projeto",
                                        tipo = tipoOperacao,
                                        descricao = transacaoDescricao,
                                        valor = valor,
                                        categoria = transacaoCategoria,
                                        responsavel = "Gestor"
                                    )
                                    // Limpar campos
                                    transacaoDescricao = ""
                                    transacaoValorCents = ""
                                    showError = false
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            if (isReceita) "Receita adicionada ao histórico com sucesso!" else "Despesa adicionada ao histórico com sucesso!"
                                        )
                                    }
                                    currentTab = 1 // Muda para a aba de histórico para ver o item
                                } else {
                                    showError = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = temaColor)
                        ) {
                            Icon(if (isReceita) Icons.Default.TrendingUp else Icons.Default.TrendingDown, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isReceita) "Confirmar Registro de Receita" else "Confirmar Registro de Despesa",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(48.dp))
                    }
                }
            } else {
                // ABA HISTÓRICO DE RECEITAS E DESPESAS
                var filtroTipo by remember { mutableStateOf("TODOS") } // "TODOS", "RECEITA", "DESPESA"
                var filtroProjetoId by remember { mutableStateOf<String?>(null) }

                val transacoesFiltradas = transacoes.filter { t ->
                    val matchTipo = when (filtroTipo) {
                        "RECEITA" -> t.tipo.equals("RECEITA", ignoreCase = true)
                        "DESPESA" -> t.tipo.equals("DESPESA", ignoreCase = true)
                        else -> true
                    }
                    val matchProj = if (filtroProjetoId == null) true else t.projetoId == filtroProjetoId
                    matchTipo && matchProj
                }

                val totalReceitas = transacoes.filter { it.tipo.equals("RECEITA", ignoreCase = true) }.sumOf { it.valor }
                val totalDespesas = transacoes.filter { it.tipo.equals("DESPESA", ignoreCase = true) }.sumOf { it.valor }
                val saldoLiquido = totalReceitas - totalDespesas

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    // Resumo Financeiro Consolidado
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E3A8A)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text("Balanço do Histórico", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "R$ ${String.format(Locale.forLanguageTag("pt-BR"), "%,.2f", saldoLiquido)}",
                                    color = if (saldoLiquido >= 0) Color(0xFF4ADE80) else Color(0xFFF87171),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Surface(
                                    color = Color.White.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "${transacoes.size} registros",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
                            Spacer(modifier = Modifier.height(14.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text("Total Receitas (+)", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                                    Text(
                                        "R$ ${String.format(Locale.forLanguageTag("pt-BR"), "%,.2f", totalReceitas)}",
                                        color = Color(0xFF4ADE80),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Total Despesas (-)", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                                    Text(
                                        "R$ ${String.format(Locale.forLanguageTag("pt-BR"), "%,.2f", totalDespesas)}",
                                        color = Color(0xFFFCA5A5),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Filtros por Tipo
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = filtroTipo == "TODOS",
                            onClick = { filtroTipo = "TODOS" },
                            label = { Text("Todos (${transacoes.size})", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF2563EB),
                                selectedLabelColor = Color.White
                            )
                        )
                        FilterChip(
                            selected = filtroTipo == "RECEITA",
                            onClick = { filtroTipo = "RECEITA" },
                            label = { Text("Receitas 💰", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF16A34A),
                                selectedLabelColor = Color.White
                            )
                        )
                        FilterChip(
                            selected = filtroTipo == "DESPESA",
                            onClick = { filtroTipo = "DESPESA" },
                            label = { Text("Despesas 📉", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFDC2626),
                                selectedLabelColor = Color.White
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (transacoesFiltradas.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(32.dp).fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ReceiptLong,
                                    contentDescription = null,
                                    tint = Color.LightGray,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Nenhum lançamento encontrado",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B),
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Use a aba 'Registrar' para lançar receitas, despesas ou métricas de projetos.",
                                    color = Color.Gray,
                                    fontSize = 12.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "Lançamentos Realizados (${transacoesFiltradas.size})",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            transacoesFiltradas.forEach { item ->
                                val isRec = item.tipo.equals("RECEITA", ignoreCase = true)
                                val itemColor = if (isRec) Color(0xFF16A34A) else Color(0xFFDC2626)
                                val itemBg = if (isRec) Color(0xFFF0FDF4) else Color(0xFFFEF2F2)

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(14.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp).fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            modifier = Modifier.weight(1f),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .background(itemBg, CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = if (isRec) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                                    contentDescription = null,
                                                    tint = itemColor,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(12.dp))

                                            Column {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Surface(
                                                        color = itemBg,
                                                        shape = RoundedCornerShape(6.dp)
                                                    ) {
                                                        Text(
                                                            text = if (isRec) "RECEITA" else "DESPESA",
                                                            color = itemColor,
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                        )
                                                    }
                                                    if (!item.categoria.isNullOrBlank()) {
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text(
                                                            text = "· ${item.categoria}",
                                                            color = Color.Gray,
                                                            fontSize = 11.sp
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = item.descricao,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = Color(0xFF1E293B),
                                                    fontSize = 13.sp
                                                )
                                                Text(
                                                    text = "Projeto: ${item.projetoTitulo ?: "Sem projeto"} · ${item.dataHora ?: ""}",
                                                    color = Color(0xFF64748B),
                                                    fontSize = 11.sp
                                                )
                                            }
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = "${if (isRec) "+" else "-"} R$ ${String.format(Locale.forLanguageTag("pt-BR"), "%,.2f", item.valor)}",
                                                fontWeight = FontWeight.Bold,
                                                color = itemColor,
                                                fontSize = 14.sp
                                            )
                                            if (!item.id.isNullOrBlank()) {
                                                IconButton(
                                                    onClick = {
                                                        inovacaoViewModel.excluirTransacao(item.id)
                                                        scope.launch {
                                                            snackbarHostState.showSnackbar("Lançamento removido")
                                                        }
                                                    },
                                                    modifier = Modifier.size(28.dp)
                                                ) {
                                                    Icon(
                                                        Icons.Default.DeleteOutline,
                                                        contentDescription = "Excluir",
                                                        tint = Color.LightGray,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
