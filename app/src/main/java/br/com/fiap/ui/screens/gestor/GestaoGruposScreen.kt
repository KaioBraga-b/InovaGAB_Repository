package br.com.fiap.ui.screens.gestor

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import br.com.fiap.api.GrupoResponse
import br.com.fiap.api.MembroGrupo
import br.com.fiap.ui.components.GestorBottomBar
import br.com.fiap.ui.navigation.Screens
import br.com.fiap.ui.theme.BluePrimary
import br.com.fiap.ui.theme.BlueSecondary
import br.com.fiap.viewmodel.AuthViewModel
import br.com.fiap.viewmodel.GrupoViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GestaoGruposScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(),
    grupoViewModel: GrupoViewModel = viewModel()
) {
    val context = LocalContext.current
    val clipboardManager = remember { context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }

    val grupos = grupoViewModel.grupos
    val usuariosDisponiveis = grupoViewModel.usuariosDisponiveis
    val isLoading = grupoViewModel.isLoading
    val errorMessage = grupoViewModel.errorMessage
    val successMessage = grupoViewModel.successMessage

    var showCreateDialog by remember { mutableStateOf(false) }
    var novoGrupoNome by remember { mutableStateOf("") }
    var novoGrupoDepartamento by remember { mutableStateOf("") }
    var novoGrupoDescricao by remember { mutableStateOf("") }

    var expandedGroupId by remember { mutableStateOf<String?>(null) }
    var showAddMembroDialogForGroup by remember { mutableStateOf<GrupoResponse?>(null) }
    var novoMembroEmail by remember { mutableStateOf("") }
    var novoMembroRole by remember { mutableStateOf("OPERADOR") }

    LaunchedEffect(successMessage) {
        if (successMessage != null) {
            Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show()
            grupoViewModel.limparMensagens()
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
                        text = "Grupos & Delegação",
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
                actions = {
                    IconButton(onClick = { grupoViewModel.carregarGrupos() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Atualizar",
                            tint = BluePrimary
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
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = BluePrimary,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Novo Grupo", fontWeight = FontWeight.Bold) }
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imeNestedScroll()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Banner Explicativo
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFF2563EB), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Group, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Controle de Acesso por Grupo (RBAC)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF1E3A8A)
                                )
                                Text(
                                    text = "Você determina quem acessa o quê e qual o cargo de cada membro.",
                                    fontSize = 12.sp,
                                    color = Color(0xFF475569)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "• Cada grupo gera um GroupId em hash exclusivo.\n" +
                                   "• Ao adicionar um colaborador por e-mail, seu papel (Operador, Líder ou Gestor) é delegado automaticamente.\n" +
                                   "• Projetos, Estratégias e Dashboard serão restritos aos membros deste GroupId.",
                            fontSize = 11.sp,
                            color = Color(0xFF334155),
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Ação Rápida: Buscar e Adicionar Membro com verificação prévia
                OutlinedButton(
                    onClick = { navController.navigate(Screens.AdicionarMembro.route) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, BluePrimary),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFEFF6FF), contentColor = BluePrimary),
                    contentPadding = PaddingValues(vertical = 12.dp, horizontal = 16.dp)
                ) {
                    Icon(Icons.Default.PersonSearch, contentDescription = null, modifier = Modifier.size(20.dp), tint = BluePrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Buscar & Adicionar Membro por E-mail",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = BluePrimary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Título de Seção
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Grupos Criados (${grupos.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E3A8A)
                    )
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = BluePrimary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (grupos.isEmpty() && !isLoading) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Group, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Nenhum grupo cadastrado ainda",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Clique em 'Novo Grupo' para criar o primeiro squad/departamento.",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { showCreateDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Criar Primeiro Grupo")
                            }
                        }
                    }
                } else {
                    grupos.forEach { grupo ->
                        val isExpanded = expandedGroupId == grupo.hashId

                        GrupoCard(
                            grupo = grupo,
                            isExpanded = isExpanded,
                            onToggleExpand = {
                                expandedGroupId = if (isExpanded) null else grupo.hashId
                            },
                            onCopyHash = { hash ->
                                clipboardManager.setPrimaryClip(ClipData.newPlainText("GroupId", hash))
                                Toast.makeText(context, "GroupId copiado: $hash", Toast.LENGTH_SHORT).show()
                            },
                            onOpenAddMembro = {
                                val target = if (grupo.hashId != null) {
                                    "${Screens.AdicionarMembro.route}?groupId=${grupo.hashId}"
                                } else {
                                    Screens.AdicionarMembro.route
                                }
                                navController.navigate(target)
                            },
                            onRemoverMembro = { email ->
                                grupo.hashId?.let { hash ->
                                    grupoViewModel.removerMembro(hash, email) {}
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // Modal: Criar Novo Grupo
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = {
                Text(
                    text = "Criar Novo Grupo 🏢",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E3A8A)
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Defina o nome e departamento deste grupo. Um GroupId único em hash será gerado automaticamente.",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = novoGrupoNome,
                        onValueChange = { novoGrupoNome = it },
                        label = { Text("Nome do Grupo *") },
                        placeholder = { Text("Ex: Squad Inovação Digital") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = novoGrupoDepartamento,
                        onValueChange = { novoGrupoDepartamento = it },
                        label = { Text("Departamento / Área *") },
                        placeholder = { Text("Ex: Operações, TI, Logística") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = novoGrupoDescricao,
                        onValueChange = { novoGrupoDescricao = it },
                        label = { Text("Descrição (Opcional)") },
                        placeholder = { Text("Objetivo ou escopo do grupo...") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (novoGrupoNome.isNotBlank() && novoGrupoDepartamento.isNotBlank()) {
                            grupoViewModel.criarGrupo(
                                nome = novoGrupoNome,
                                departamento = novoGrupoDepartamento,
                                descricao = novoGrupoDescricao
                            ) {
                                showCreateDialog = false
                                novoGrupoNome = ""
                                novoGrupoDepartamento = ""
                                novoGrupoDescricao = ""
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    enabled = novoGrupoNome.isNotBlank() && novoGrupoDepartamento.isNotBlank() && !isLoading
                ) {
                    Text("Criar Grupo")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Modal: Adicionar / Delegar Função a Membro
    if (showAddMembroDialogForGroup != null) {
        val grupo = showAddMembroDialogForGroup!!
        val roles = listOf("OPERADOR", "LIDER", "GESTOR")

        AlertDialog(
            onDismissRequest = { showAddMembroDialogForGroup = null },
            title = {
                Text(
                    text = "Delegar Função a Colaborador 👤",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E3A8A)
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Vincule o colaborador ao grupo \"${grupo.nome}\" e defina o papel que ele terá no sistema.",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = novoMembroEmail,
                        onValueChange = { novoMembroEmail = it },
                        label = { Text("E-mail do Colaborador *") },
                        placeholder = { Text("colaborador@aguabranca.com.br") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Sugestões rápidas de usuários do sistema
                    val sugestoes = usuariosDisponiveis.filter {
                        novoMembroEmail.isNotBlank() &&
                        it.email?.contains(novoMembroEmail, ignoreCase = true) == true &&
                        it.email != novoMembroEmail
                    }.take(3)

                    if (sugestoes.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Sugestões encontradas:", fontSize = 11.sp, color = Color.Gray)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            sugestoes.forEach { user ->
                                user.email?.let { email ->
                                    AssistChip(
                                        onClick = { novoMembroEmail = email },
                                        label = { Text(user.nome ?: email, fontSize = 11.sp) }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Selecione a Função Delegada (ROLE):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E3A8A)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        roles.forEach { role ->
                            val isSelected = novoMembroRole == role
                            val (bg, txt) = when (role) {
                                "GESTOR" -> Color(0xFFEFF6FF) to Color(0xFF2563EB)
                                "LIDER" -> Color(0xFFDCFCE7) to Color(0xFF15803D)
                                else -> Color(0xFFF1F5F9) to Color(0xFF475569)
                            }
                            FilterChip(
                                selected = isSelected,
                                onClick = { novoMembroRole = role },
                                label = {
                                    Text(
                                        text = role,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 11.sp
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = txt,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (novoMembroEmail.isNotBlank() && grupo.hashId != null) {
                            grupoViewModel.adicionarOuAtualizarMembro(
                                groupId = grupo.hashId,
                                email = novoMembroEmail,
                                role = novoMembroRole
                            ) {
                                showAddMembroDialogForGroup = null
                                novoMembroEmail = ""
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    enabled = novoMembroEmail.isNotBlank() && !isLoading
                ) {
                    Text("Delegar Função")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddMembroDialogForGroup = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun GrupoCard(
    grupo: GrupoResponse,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onCopyHash: (String) -> Unit,
    onOpenAddMembro: () -> Unit,
    onRemoverMembro: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top row: Nome e Departamento
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = grupo.nome ?: "Grupo sem nome",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E3A8A)
                    )
                    Text(
                        text = "Departamento: ${grupo.departamento ?: "Geral"}",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Surface(
                    color = Color(0xFFEFF6FF),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "${grupo.totalMembros} membro(s)",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        color = BluePrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (!grupo.descricao.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = grupo.descricao,
                    fontSize = 12.sp,
                    color = Color(0xFF334155)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Badge do GroupId Hash
            grupo.hashId?.let { hash ->
                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.clickable { onCopyHash(hash) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "GroupId: ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = hash,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF2563EB)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copiar Hash",
                            modifier = Modifier.size(14.dp),
                            tint = Color(0xFF2563EB)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(8.dp))

            // Ações: Ver Membros e Adicionar Membro
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onToggleExpand,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = BluePrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isExpanded) "Ocultar Membros" else "Ver Membros (${grupo.membros.size})",
                        color = BluePrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = onOpenAddMembro,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEFF6FF)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Delegar Função", color = BluePrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            // Lista expandida de membros
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    if (grupo.membros.isEmpty()) {
                        Text(
                            text = "Nenhum membro vinculado ainda. Clique em 'Delegar Função' para adicionar.",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        grupo.membros.forEach { membro ->
                            MembroItemRow(
                                membro = membro,
                                onRemover = { membro.email?.let { onRemoverMembro(it) } }
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MembroItemRow(
    membro: MembroGrupo,
    onRemover: () -> Unit
) {
    val roleColor = when (membro.role?.uppercase()) {
        "GESTOR" -> Color(0xFF2563EB) to Color(0xFFEFF6FF)
        "LIDER" -> Color(0xFF15803D) to Color(0xFFDCFCE7)
        else -> Color(0xFF475569) to Color(0xFFF1F5F9)
    }

    Surface(
        color = Color(0xFFF8FAFC),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = membro.nome.takeIf { !it.isNullOrBlank() } ?: "Colaborador",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = Color(0xFF1E293B)
                )
                Text(
                    text = membro.email ?: "",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = roleColor.second,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = membro.role ?: "OPERADOR",
                        color = roleColor.first,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                IconButton(
                    onClick = onRemover,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remover Membro",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
