package br.com.fiap.ui.screens.operador

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lightbulb
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovaIdeiaScreen(navController: NavController) {
    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("Operacional") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Nova Ideia", 
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
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // Header Icon
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color(0xFFFEF3C7), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = Color(0xFFFBBF24),
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "O que você quer sugerir?",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E3A8A)
            )
            
            Text(
                text = "Sua ideia pode melhorar o dia a dia de todos.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

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
            
            // Simulação de Seleção de Categoria
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
                onClick = { /* Lógica de envio */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BlueSecondary)
            ) {
                Text(
                    "Enviar ideia ✨",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NovaIdeiaPreview() {
    NovaIdeiaScreen(rememberNavController())
}
