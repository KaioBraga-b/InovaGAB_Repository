package br.com.fiap.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import br.com.fiap.model.UserProfile
import br.com.fiap.ui.navigation.Screens
import br.com.fiap.ui.theme.*
import br.com.fiap.viewmodel.AuthViewModel

@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var perfilSelecionado by remember { mutableStateOf(UserProfile.OPERADOR) }

    val isLoading = authViewModel.isLoading
    val errorMessage = authViewModel.errorMessage

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBlue)
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "InovaGAB",
                style = MaterialTheme.typography.displayMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Plataforma de Inovação Corporativa",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(40.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Entrar na plataforma", 
                        style = MaterialTheme.typography.headlineMedium, 
                        color = Color.Black
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Selecione seu perfil", 
                        style = MaterialTheme.typography.labelMedium, 
                        color = TextGray
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(), 
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PerfilButton("Operador", Icons.Default.Build, perfilSelecionado == UserProfile.OPERADOR, Modifier.weight(1f)) { perfilSelecionado = UserProfile.OPERADOR }
                        PerfilButton("Gestor", Icons.Default.Person, perfilSelecionado == UserProfile.GESTOR, Modifier.weight(1f)) { perfilSelecionado = UserProfile.GESTOR }
                        PerfilButton("Líder", Icons.Default.Star, perfilSelecionado == UserProfile.LIDER, Modifier.weight(1f)) { perfilSelecionado = UserProfile.LIDER }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("E-mail") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = BlueSecondary,
                            unfocusedBorderColor = BorderGray
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = senha,
                        onValueChange = { senha = it },
                        label = { Text("Senha") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = BlueSecondary,
                            unfocusedBorderColor = BorderGray
                        )
                    )

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!, 
                            color = MaterialTheme.colorScheme.error, 
                            style = MaterialTheme.typography.bodySmall, 
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { 
                            authViewModel.signIn(email, senha, perfilSelecionado) {
                                val route = when(perfilSelecionado) {
                                    UserProfile.OPERADOR -> Screens.OperadorHome.route
                                    UserProfile.GESTOR -> Screens.GestorHome.route
                                    UserProfile.LIDER -> Screens.LiderHome.route
                                }
                                navController.navigate(route) { popUpTo(Screens.Login.route) { inclusive = true } }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        enabled = !isLoading,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BlueSecondary,
                            disabledContainerColor = BlueSecondary.copy(alpha = 0.6f) // Garante que o botão não "suma"
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White, 
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 3.dp
                            )
                        } else {
                            Text("Entrar →", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                        }
                    }

                    TextButton(
                        onClick = { navController.navigate(Screens.SignUp.route) },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Não tem uma conta? Cadastre-se", color = BlueSecondary, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun PerfilButton(label: String, icon: ImageVector, isSelected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(72.dp),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 2.dp),
        border = androidx.compose.foundation.BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) BlueSecondary else BorderGray),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) BlueSecondary.copy(alpha = 0.05f) else Color.Transparent
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = icon, 
                contentDescription = null, 
                tint = if (isSelected) BlueSecondary else TextGray, 
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label, 
                color = if (isSelected) BlueSecondary else TextGray, 
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}
