package br.com.fiap.ui.screens.operador

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
import br.com.fiap.ui.components.OperadorBottomBar
import br.com.fiap.ui.theme.*
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.viewmodel.InovacaoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarIdeiaScreen(
    navController: NavController,
    ideiaId: String,
    inovacaoViewModel: InovacaoViewModel = viewModel()
) {
    val ideia = inovacaoViewModel.ideias.find { it.id == ideiaId }
    
    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var initialized by remember { mutableStateOf(false) }

    LaunchedEffect(ideia) {
        if (!initialized && ideia != null) {
            titulo = ideia.titulo
            descricao = ideia.descricao
            categoria = ideia.area
            initialized = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Editar Ideia", 
                        fontWeight = FontWeight.Bold,
                        color = BluePrimary
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Voltar",
                            tint = BluePrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        inovacaoViewModel.excluirIdeia(ideiaId)
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = Color.Red)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = { OperadorBottomBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FD))
                .padding(innerPadding)
                .imePadding() // Suporte ao teclado
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                // Formulário
                Text(
                    text = "Título da ideia",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFF1E3A8A),
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ex: Novo suporte para ferramentas") },
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

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Descrição detalhada",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFF1E3A8A),
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    placeholder = { Text("Descreva o problema e como sua ideia ajuda a resolver...") },
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

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Categoria",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFF1E3A8A),
                    fontWeight = FontWeight.Bold
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Operacional", "Segurança", "Digital").forEach { cat ->
                        val isSelected = categoria == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { categoria = cat },
                            label = { Text(cat) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BlueSecondary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = { 
                        if (titulo.isNotBlank() && descricao.isNotBlank()) {
                            inovacaoViewModel.atualizarIdeia(ideiaId, titulo, descricao, categoria)
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BlueSecondary)
                ) {
                    Text(
                        "Salvar Alterações ✨",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditarIdeiaPreview() {
    EditarIdeiaScreen(rememberNavController(), "TEST_ID")
}
