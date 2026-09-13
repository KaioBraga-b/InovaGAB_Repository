package br.com.fiap.ui.screens.gestor

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import br.com.fiap.ui.components.GestorBottomBar
import br.com.fiap.ui.navigation.Screens
import br.com.fiap.viewmodel.AuthViewModel
import br.com.fiap.viewmodel.InovacaoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestorIdeiasAprovadasScreen(
    navController: NavController, 
    authViewModel: AuthViewModel = viewModel(),
    inovacaoViewModel: InovacaoViewModel = viewModel()
) {
    val userData = authViewModel.userData
    val userName = (userData?.get("nome") ?: userData?.get("Nome"))?.toString() ?: "Gestor"
    val userSobrenome = (userData?.get("sobrenome") ?: userData?.get("Sobrenome"))?.toString() ?: ""
    val initials = userName.take(1) + (if (userSobrenome.isNotEmpty()) userSobrenome.take(1) else "P")

    // Somente as aprovadas
    val ideias = inovacaoViewModel.ideias.filter { it.status?.contains("Aprovada") == true }

    Scaffold(
        topBar = { br.com.fiap.ui.components.GestorTopBar(navController, initials.uppercase()) },
        bottomBar = { GestorBottomBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FD))
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Header Removido pois usamos TopBar

            Text(
                text = "TELA APROVADOS",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = false,
                    onClick = { navController.navigate(Screens.GestorHome.route) },
                    label = { Text("Pendentes") },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color(0xFFFEF3C7),
                        labelColor = Color(0xFFD97706)
                    )
                )
                FilterChip(
                    selected = true,
                    onClick = { },
                    label = { Text("✓ Aprovadas (${ideias.size})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFDCFCE7),
                        selectedLabelColor = Color(0xFF16A34A)
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (ideias.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhuma ideia aprovada ainda.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(ideias) { ideia ->
                        GestorIdeiaCardItem(ideia, inovacaoViewModel)
                    }
                    item { Spacer(modifier = Modifier.height(20.dp)) }
                }
            }
        }
    }
}

