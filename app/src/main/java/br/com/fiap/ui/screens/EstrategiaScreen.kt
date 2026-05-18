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
import br.com.fiap.ui.theme.*

data class ObjetivoEstrategico(
    val id: String,
    val numero: String,
    val titulo: String,
    val descricao: String,
    val tags: List<String>,
    val cor: Color
)

@Composable
fun EstrategiaScreen(navController: NavController, userRole: String = "OPERADOR") {
    val objetivos = listOf(
        ObjetivoEstrategico(
            id = "1",
            numero = "OBJ 01",
            titulo = "Digitalização da Operação",
            descricao = "Reduzir processos manuais em 40% até dezembro de 2025. Foco em checklists digitais, apps de campo e integração de sistemas.",
            tags = listOf("Logística", "Passageiros"),
            cor = Color(0xFF2563EB)
        ),
        ObjetivoEstrategico(
            id = "2",
            numero = "OBJ 02",
            titulo = "Experiência do Colaborador",
            descricao = "Aumentar NPS interno de 62 para 80 pontos. Engajamento via plataforma InovaGAB e reconhecimento de talentos.",
            tags = listOf("RH", "Cultura"),
            cor = Color(0xFF10B981)
        ),
        ObjetivoEstrategico(
            id = "3",
            numero = "OBJ 03",
            titulo = "Eficiência de Custos",
            descricao = "Reduzir R$ 5M em custos operacionais via iniciativas de inovação com ROI mensurável em até 12 meses.",
            tags = listOf("Finanças", "Operação"),
            cor = Color(0xFFF59E0B)
        )
    )

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
                    onClick = { /* Criar novo objetivo */ },
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
                items(objetivos) { objetivo ->
                    ObjetivoCard(objetivo, canEdit = userRole == "LIDER")
                }
                item { Spacer(modifier = Modifier.height(20.dp)) }
            }
        }
    }
}

@Composable
fun ObjetivoCard(objetivo: ObjetivoEstrategico, canEdit: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            // Faixa lateral colorida
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(objetivo.cor)
            )
            
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = objetivo.numero,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = objetivo.cor
                    )
                    
                    if (canEdit) {
                        Row {
                            IconButton(onClick = { /* Editar */ }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.Gray, modifier = Modifier.size(16.dp))
                            }
                            IconButton(onClick = { /* Deletar */ }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Delete, contentDescription = "Deletar", tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                Text(
                    text = objetivo.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Text(
                    text = objetivo.descricao,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(top = 8.dp),
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    objetivo.tags.forEach { tag ->
                        Surface(
                            color = objetivo.cor.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = tag,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                color = objetivo.cor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
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
