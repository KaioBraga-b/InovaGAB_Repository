package br.com.fiap.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import br.com.fiap.ui.components.OperadorBottomBar
import br.com.fiap.ui.navigation.Screens
import br.com.fiap.ui.theme.*

import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.viewmodel.InovacaoViewModel
import br.com.fiap.viewmodel.Estrategia
import br.com.fiap.ui.screens.lider.EstrategiaCard

@Composable
fun EstrategiaScreen(
    navController: NavController, 
    userRole: String = "OPERADOR",
    inovacaoViewModel: InovacaoViewModel = viewModel()
) {
    val estrategias = inovacaoViewModel.estrategias

    Scaffold(
        bottomBar = {
            when (userRole) {
                "OPERADOR" -> OperadorBottomBar(navController)
                "GESTOR" -> GestorBottomBar(navController)
                "LIDER" -> LiderBottomBar(navController)
                else -> {}
            }
        },
        floatingActionButton = {
            if (userRole == "LIDER") {
                FloatingActionButton(
                    onClick = { navController.navigate(Screens.CriarEstrategia.route) },
                    containerColor = Color(0xFF2563EB),
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Novo Objetivo")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FD))
                .padding(innerPadding)
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
                    color = Color(0xFFEFF6FF),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "2025",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = Color(0xFF2563EB),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Text(
                text = "Direcionamentos estratégicos do Grupo",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Info Box
            Surface(
                color = Color(0xFFEFF6FF).copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFF2563EB),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Esses objetivos guiam as ideias prioritárias para o Grupo GAB em 2025.",
                        fontSize = 12.sp,
                        color = Color(0xFF1E3A8A)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(estrategias) { estrategia ->
                    EstrategiaCard(estrategia, navController)
                }
                item { Spacer(modifier = Modifier.height(20.dp)) }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EstrategiaPreview() {
    EstrategiaScreen(rememberNavController())
}

@Preview(showBackground = true)
@Composable
fun EstrategiaLiderPreview() {
    EstrategiaScreen(rememberNavController(), userRole = "LIDER")
}
