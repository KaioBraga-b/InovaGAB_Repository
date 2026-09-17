package br.com.fiap.ui.screens.operador

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import br.com.fiap.ui.components.OperadorBottomBar
import br.com.fiap.ui.theme.*
import br.com.fiap.viewmodel.InovacaoViewModel
import br.com.fiap.viewmodel.Ideia
import br.com.fiap.viewmodel.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.api.Area
import br.com.fiap.viewmodel.Estrategia

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NovaIdeiaScreen(
    navController: NavController,
    inovacaoViewModel: InovacaoViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var impactoSelecionado by remember { mutableStateOf("Baixo") }
    
    val userData = authViewModel.userData
    val userName = (userData?.get("nome") ?: userData?.get("Nome"))?.toString() ?: "Colaborador"
    val userId = authViewModel.currentUserId ?: ""
    val userGroupId = authViewModel.userGroupId ?: (userData?.get("groupId") ?: userData?.get("GroupId"))?.toString()?.takeIf { it.isNotBlank() }
    val context = androidx.compose.ui.platform.LocalContext.current
    var isSubmitting by remember { mutableStateOf(false) }
    
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }

    Scaffold(
        bottomBar = {
            if (!WindowInsets.isImeVisible) {
                OperadorBottomBar(navController)
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
                Text(
                    text = "Lançar Nova Ideia",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E3A8A)
                )
                Text(
                    text = "Sua contribuição é fundamental para o Grupo Águia Branca.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(24.dp))

                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInVertically(initialOffsetY = { 50 }, animationSpec = tween(500)) + fadeIn(animationSpec = tween(500))
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            
                            var expandedEstrategia by remember { mutableStateOf(false) }
                            var estrategiaSelecionada by remember { mutableStateOf<Estrategia?>(null) }
                            
                            if (userGroupId.isNullOrBlank()) {
                                // Usuário sem grupo não acessa estratégias da empresa: ideia é registrada como PESSOAL
                                Surface(
                                    color = Color(0xFFEFF6FF),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = Color(0xFF2563EB),
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = "💡 Ideia Pessoal (Sem Grupo Vinculado)",
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1E3A8A),
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = "Você ainda não foi atribuído a um grupo/squad. Esta ideia será salva no seu perfil pessoal e você poderá adicioná-la a um grupo assim que for incluído por um Gestor.",
                                                color = Color(0xFF1E40AF),
                                                fontSize = 11.sp,
                                                lineHeight = 16.sp
                                            )
                                        }
                                    }
                                }
                            } else {
                                ExposedDropdownMenuBox(
                                    expanded = expandedEstrategia,
                                    onExpandedChange = { expandedEstrategia = !expandedEstrategia }
                                ) {
                                    OutlinedTextField(
                                        value = estrategiaSelecionada?.titulo ?: "Vincular a uma Estratégia do Grupo (Opcional)",
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Estratégia do Grupo") },
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEstrategia) },
                                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = if (estrategiaSelecionada != null) Color(0xFF1E293B) else Color.Gray,
                                            unfocusedTextColor = if (estrategiaSelecionada != null) Color(0xFF1E293B) else Color.Gray,
                                            focusedBorderColor = Color(0xFF2563EB),
                                            unfocusedBorderColor = Color(0xFFE5E7EB),
                                            focusedContainerColor = Color.White,
                                            unfocusedContainerColor = Color.White
                                        )
                                    )
                                    ExposedDropdownMenu(
                                        expanded = expandedEstrategia,
                                        onDismissRequest = { expandedEstrategia = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("✕ Nenhuma estratégia (Avulsa)", color = Color.Gray) },
                                            onClick = {
                                                estrategiaSelecionada = null
                                                expandedEstrategia = false
                                            }
                                        )
                                        inovacaoViewModel.estrategias.forEach { est ->
                                            DropdownMenuItem(
                                                text = { Text(est.titulo ?: "", color = Color(0xFF1E293B), fontWeight = FontWeight.Medium) },
                                                onClick = {
                                                    estrategiaSelecionada = est
                                                    expandedEstrategia = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            
                            OutlinedTextField(
                                value = titulo,
                                onValueChange = { titulo = it },
                                label = { Text("Título da Ideia") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color(0xFF1E293B),
                                    unfocusedTextColor = Color(0xFF1E293B),
                                    focusedBorderColor = BlueSecondary,
                                    unfocusedBorderColor = BorderGray,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            var expandedArea by remember { mutableStateOf(false) }
                            var areaSelecionada by remember { mutableStateOf<Area?>(null) }
                            var showNewAreaDialog by remember { mutableStateOf(false) }
                            var novaAreaNome by remember { mutableStateOf("") }

                            ExposedDropdownMenuBox(
                                expanded = expandedArea,
                                onExpandedChange = { expandedArea = !expandedArea }
                            ) {
                                OutlinedTextField(
                                    value = areaSelecionada?.nome ?: area.takeIf { it.isNotBlank() } ?: "Selecione uma área",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Área de Aplicação") },
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
                                        unfocusedContainerColor = Color.White
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
                                                area = a.nome
                                                expandedArea = false
                                            }
                                        )
                                    }
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                    DropdownMenuItem(
                                        text = { Text("➕ Adicionar nova área", color = Color(0xFF2563EB), fontWeight = FontWeight.Bold) },
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
                                    title = { Text("Nova Área") },
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
                                                val novaA = Area(nome = novaAreaNome)
                                                areaSelecionada = novaA
                                                area = novaA.nome
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

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = descricao,
                                onValueChange = { descricao = it },
                                label = { Text("Explique sua ideia detalhadamente") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 4,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black,
                                    focusedBorderColor = BlueSecondary,
                                    unfocusedBorderColor = BorderGray,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = "Impacto Estimado",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("Baixo", "Médio", "Alto").forEach { impacto ->
                                    FilterChip(
                                        selected = impactoSelecionado == impacto,
                                        onClick = { impactoSelecionado = impacto },
                                        label = { Text(impacto) },
                                        modifier = Modifier.weight(1f),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = BlueSecondary,
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            Button(
                                onClick = {
                                    if (titulo.isBlank() || descricao.isBlank()) {
                                        android.widget.Toast.makeText(context, "Por favor, preencha o título e a descrição da sua ideia.", android.widget.Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    if (!isSubmitting) {
                                        isSubmitting = true
                                        val areaFinal = if (area.isNotBlank()) area else "Geral"
                                        val groupId = userGroupId

                                        inovacaoViewModel.adicionarIdeia(Ideia(
                                            id = null,
                                            titulo = titulo.trim(),
                                            descricao = descricao.trim(),
                                            autor = userName,
                                            area = areaFinal,
                                            impacto = impactoSelecionado,
                                            userId = userId,
                                            estrategiaId = if (groupId != null) estrategiaSelecionada?.id else null,
                                            estrategiaTitulo = if (groupId != null) estrategiaSelecionada?.titulo else null,
                                            groupId = groupId
                                        )) {
                                            val feedbackMsg = if (groupId != null) {
                                                "Ideia enviada para o seu grupo com sucesso! 🚀"
                                            } else {
                                                "Ideia pessoal salva com sucesso! 💡"
                                            }
                                            android.widget.Toast.makeText(context, feedbackMsg, android.widget.Toast.LENGTH_SHORT).show()
                                            navController.popBackStack()
                                        }
                                    }
                                },
                                enabled = !isSubmitting,
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BlueSecondary)
                            ) {
                                if (isSubmitting) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                                } else {
                                    Icon(Icons.Default.Send, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Enviar Ideia para Análise", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}
