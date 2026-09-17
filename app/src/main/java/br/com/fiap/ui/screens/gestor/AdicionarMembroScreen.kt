package br.com.fiap.ui.screens.gestor

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import br.com.fiap.api.GrupoResponse
import br.com.fiap.ui.components.GestorBottomBar
import br.com.fiap.ui.theme.BluePrimary
import br.com.fiap.viewmodel.AuthViewModel
import br.com.fiap.viewmodel.GrupoViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AdicionarMembroScreen(
    navController: NavController,
    preSelectedGroupId: String? = null,
    authViewModel: AuthViewModel = viewModel(),
    grupoViewModel: GrupoViewModel = viewModel()
) {
    val context = LocalContext.current

    val grupos = grupoViewModel.grupos
    val usuarioVerificado = grupoViewModel.usuarioVerificado
    val isVerificando = grupoViewModel.isVerificandoUsuario
    val buscaRealizada = grupoViewModel.buscaRealizada
    val isLoading = grupoViewModel.isLoading
    val errorMessage = grupoViewModel.errorMessage
    val successMessage = grupoViewModel.successMessage

    var selectedGroup by remember {
        mutableStateOf(
            grupos.find { it.hashId == preSelectedGroupId } ?: grupos.firstOrNull()
        )
    }

    LaunchedEffect(grupos) {
        if (selectedGroup == null && grupos.isNotEmpty()) {
            selectedGroup = grupos.find { it.hashId == preSelectedGroupId } ?: grupos.first()
        }
    }

    var emailInput by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("OPERADOR") }
    var expandedGroupDropdown by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        grupoViewModel.limparVerificacao()
        grupoViewModel.carregarGrupos()
        grupoViewModel.buscarUsuarios()
    }

    LaunchedEffect(successMessage) {
        if (successMessage != null) {
            Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show()
            grupoViewModel.limparMensagens()
            navController.popBackStack()
        }
    }

    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            grupoViewModel.limparMensagens()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Vincular Membro ao GroupId",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E3A8A)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color(0xFF1E3A8A)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            if (!WindowInsets.isImeVisible) {
                GestorBottomBar(navController)
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
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Card Informativo
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFF2563EB), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PersonSearch, contentDescription = null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Verificação de Cadastro Prévia",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF1E3A8A)
                            )
                            Text(
                                text = "O sistema verifica se o e-mail já existe na base de dados antes de associá-lo ao seu GroupId e delegar a ROLE.",
                                fontSize = 11.sp,
                                color = Color(0xFF475569)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 1. Seletor de Grupo do Gestor
                Text(
                    text = "1. Selecione o Grupo / GroupId de Destino:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E3A8A)
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (grupos.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7))
                    ) {
                        Text(
                            text = "⚠️ Você ainda não possui nenhum grupo criado. Crie um grupo primeiro na tela de Gestão de Grupos.",
                            modifier = Modifier.padding(16.dp),
                            fontSize = 12.sp,
                            color = Color(0xFFB45309)
                        )
                    }
                } else {
                    ExposedDropdownMenuBox(
                        expanded = expandedGroupDropdown,
                        onExpandedChange = { expandedGroupDropdown = !expandedGroupDropdown }
                    ) {
                        OutlinedTextField(
                            value = selectedGroup?.let { "${it.nome} (${it.hashId})" } ?: "Selecione um grupo",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Grupo Selecionado") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGroupDropdown) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BluePrimary,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = expandedGroupDropdown,
                            onDismissRequest = { expandedGroupDropdown = false }
                        ) {
                            grupos.forEach { g ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(g.nome ?: "Grupo", fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                                            Text("Hash: ${g.hashId} · Depto: ${g.departamento}", fontSize = 11.sp, color = Color.Gray)
                                        }
                                    },
                                    onClick = {
                                        selectedGroup = g
                                        expandedGroupDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 2. Busca e Verificação de E-mail
                Text(
                    text = "2. E-mail do Colaborador:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E3A8A)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = {
                            emailInput = it
                            grupoViewModel.limparVerificacao()
                        },
                        label = { Text("E-mail corporativo *") },
                        placeholder = { Text("exemplo@aguabranca.com.br") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = { grupoViewModel.verificarEmailUsuario(emailInput) }
                        ),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = BluePrimary,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    Button(
                        onClick = { grupoViewModel.verificarEmailUsuario(emailInput) },
                        enabled = emailInput.isNotBlank() && !isVerificando,
                        modifier = Modifier.height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                    ) {
                        if (isVerificando) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Search, contentDescription = "Verificar")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Verificar")
                        }
                    }
                }

                // Resultado da Verificação
                if (buscaRealizada) {
                    Spacer(modifier = Modifier.height(16.dp))

                    if (usuarioVerificado != null) {
                        // SUCESSO: Usuário Encontrado na base
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFF86EFAC), RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .background(Color(0xFF16A34A), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = (usuarioVerificado.nome?.take(1) ?: "U").uppercase(),
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "${usuarioVerificado.nome ?: ""} ${usuarioVerificado.sobrenome ?: ""}".trim().ifBlank { "Colaborador" },
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = Color(0xFF166534)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Confirmado",
                                                tint = Color(0xFF16A34A),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        Text(
                                            text = usuarioVerificado.email ?: "",
                                            fontSize = 12.sp,
                                            color = Color(0xFF15803D)
                                        )
                                        if (!usuarioVerificado.unidade.isNullOrBlank()) {
                                            Text(
                                                text = "Unidade: ${usuarioVerificado.unidade}",
                                                fontSize = 11.sp,
                                                color = Color(0xFF4B5563)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = Color(0xFFDCFCE7))
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Cargo Atual no Sistema:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF166534)
                                    )
                                    Surface(
                                        color = Color(0xFFDCFCE7),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = usuarioVerificado.role ?: "OPERADOR",
                                            color = Color(0xFF15803D),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                if (usuarioVerificado.groupId != null) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "ℹ️ Atualmente no grupo: ${usuarioVerificado.groupId} (será transferido para o novo grupo)",
                                        fontSize = 11.sp,
                                        color = Color(0xFFB45309)
                                    )
                                } else {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "✓ Sem grupo vinculado no momento. Pronto para ativação!",
                                        fontSize = 11.sp,
                                        color = Color(0xFF15803D)
                                    )
                                }
                            }
                        }
                    } else {
                        // ERRO: Usuário Não Encontrado
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFFFCA5A5), RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = Color(0xFFDC2626),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Colaborador não encontrado!",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color(0xFF991B1B)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Nenhum usuário cadastrado com o e-mail \"$emailInput\".\n\nPara que o colaborador possa ser associado ao seu GroupId, ele precisa primeiro criar uma conta no InovaGAB através da opção 'Cadastre-se' na tela de Login.",
                                            fontSize = 11.sp,
                                            color = Color(0xFF7F1D1D),
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 3. Seleção de Cargo / Função (Aparece se usuário foi verificado)
                AnimatedVisibility(
                    visible = usuarioVerificado != null,
                    enter = fadeIn() + slideInVertically { 30 }
                ) {
                    Column(modifier = Modifier.padding(top = 24.dp)) {
                        Text(
                            text = "3. Delegar Nova Função (ROLE) para o Colaborador:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E3A8A)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        val roles = listOf(
                            Triple("OPERADOR", "Operacional", "Envia ideias, comenta e visualiza iniciativas."),
                            Triple("LIDER", "Liderança", "Define objetivos estratégicos e analisa ROI do grupo."),
                            Triple("GESTOR", "Gestor", "Curadoria e aprovação de ideias, gestão de projetos.")
                        )

                        roles.forEach { (role, label, desc) ->
                            val isSelected = selectedRole == role
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { selectedRole = role },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) Color(0xFFEFF6FF) else Color.White,
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, BluePrimary) else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedRole = role },
                                        colors = RadioButtonDefaults.colors(selectedColor = BluePrimary)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "$role ($label)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (isSelected) BluePrimary else Color(0xFF1E293B)
                                        )
                                        Text(
                                            text = desc,
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        // Botão de Confirmação Final
                        Button(
                            onClick = {
                                val grupoHash = selectedGroup?.hashId
                                val emailConfirmado = usuarioVerificado?.email ?: emailInput
                                if (!grupoHash.isNullOrBlank() && emailConfirmado.isNotBlank()) {
                                    grupoViewModel.adicionarOuAtualizarMembro(
                                        groupId = grupoHash,
                                        email = emailConfirmado,
                                        role = selectedRole
                                    ) {
                                        // Sucesso disparado no LaunchedEffect
                                    }
                                }
                            },
                            enabled = selectedGroup != null && usuarioVerificado != null && !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.GroupAdd, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Associar ao GroupId & Delegar Papel",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}
