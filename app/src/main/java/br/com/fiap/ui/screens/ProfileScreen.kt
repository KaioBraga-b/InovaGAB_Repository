package br.com.fiap.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import br.com.fiap.ui.navigation.Screens

import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    // Observa os dados persistidos no ViewModel
    val userData = authViewModel.userData
    
    // Extração robusta de dados (checa minúsculo e maiúsculo para evitar erro de digitação no banco)
    val userName = (userData?.get("nome") ?: userData?.get("Nome"))?.toString() ?: "Usuário"
    val userSobrenome = (userData?.get("sobrenome") ?: userData?.get("Sobrenome"))?.toString() ?: ""
    val userUnidade = (userData?.get("unidade") ?: userData?.get("Unidade") ?: userData?.get("matriz") ?: userData?.get("Matriz"))?.toString() ?: "Matriz - São Paulo"
    val userEmail = (userData?.get("email") ?: userData?.get("Email"))?.toString() ?: "E-mail não cadastrado"
    val userRole = (userData?.get("role") ?: userData?.get("Role"))?.toString() ?: "OPERADOR"

    val fullDisplayName = if (userSobrenome.isNotBlank()) "$userName $userSobrenome" else userName

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meu Perfil", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FD))
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Foto de Perfil / Avatar
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color(0xFF2563EB), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userName.take(1),
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = fullDisplayName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E3A8A)
            )

            Surface(
                color = when(userRole) {
                    "GESTOR" -> Color(0xFFDCFCE7)
                    "LIDER" -> Color(0xFFF5F3FF)
                    else -> Color(0xFFEFF6FF)
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = userRole,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    color = when(userRole) {
                        "GESTOR" -> Color(0xFF16A34A)
                        "LIDER" -> Color(0xFF8B5CF6)
                        else -> Color(0xFF2563EB)
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Informações do Perfil
            ProfileInfoItem(icon = Icons.Default.Person, label = "Nome", value = userName)
            ProfileInfoItem(icon = Icons.Default.Person, label = "Sobrenome", value = userSobrenome)
            ProfileInfoItem(icon = Icons.Default.Email, label = "E-mail", value = userEmail)
            ProfileInfoItem(icon = Icons.Default.Badge, label = "Cargo", value = when(userRole) {
                "GESTOR" -> "Gestor de Inovação"
                "LIDER" -> "Líder de Estratégia"
                else -> "Operador de Campo"
            })
            ProfileInfoItem(icon = Icons.Default.Person, label = "Unidade", value = userUnidade)

            Spacer(modifier = Modifier.weight(1f))

            // Botão de Log Out
            Button(
                onClick = {
                    authViewModel.signOut()
                    navController.navigate(Screens.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFEE2E2),
                    contentColor = Color(0xFFEF4444)
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sair da Conta", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ProfileInfoItem(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = Color(0xFFF3F4F6),
            shape = CircleShape,
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, fontSize = 12.sp, color = Color.Gray)
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1E3A8A))
        }
    }
    HorizontalDivider(color = Color(0xFFF3F4F6))
}

@Preview(showBackground = true)
@Composable
fun ProfilePreview() {
    ProfileScreen(rememberNavController())
}
