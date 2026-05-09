package br.com.fiap.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import br.com.fiap.model.UserProfile
import br.com.fiap.ui.navigation.Screens
import br.com.fiap.ui.theme.*
import br.com.fiap.viewmodel.AuthViewModel

@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var perfilSelecionado by remember { mutableStateOf(UserProfile.OPERADOR) }

    val isLoading by authViewModel.isLoading
    val errorMessage by authViewModel.errorMessage

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBlue)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Logo e Título
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
        Text(
            text = "Grupo Águia Branca",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Card de Login
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
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
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PerfilButton(
                        label = "Operador",
                        icon = Icons.Default.Build,
                        isSelected = perfilSelecionado == UserProfile.OPERADOR,
                        modifier = Modifier.weight(1f),
                        onClick = { perfilSelecionado = UserProfile.OPERADOR }
                    )
                    PerfilButton(
                        label = "Gestor",
                        icon = Icons.Default.Person,
                        isSelected = perfilSelecionado == UserProfile.GESTOR,
                        modifier = Modifier.weight(1f),
                        onClick = { perfilSelecionado = UserProfile.GESTOR }
                    )
                    PerfilButton(
                        label = "Líder",
                        icon = Icons.Default.Star,
                        isSelected = perfilSelecionado == UserProfile.LIDER,
                        modifier = Modifier.weight(1f),
                        onClick = { perfilSelecionado = UserProfile.LIDER }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "E-mail",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextGray
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BlueSecondary,
                        unfocusedBorderColor = BorderGray
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Senha",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextGray
                )
                OutlinedTextField(
                    value = senha,
                    onValueChange = { senha = it },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
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
                            navController.navigate(route) {
                                popUpTo(Screens.Login.route) { inclusive = true }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BlueSecondary)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = "Entrar →",
                            style = MaterialTheme.typography.labelLarge,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = { navController.navigate(Screens.SignUp.route) },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "Não tem uma conta? Cadastre-se",
                        color = BlueSecondary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Grupo Águia Branca - 80 anos",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun PerfilButton(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(70.dp),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(4.dp),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, BlueSecondary)
        } else {
            androidx.compose.foundation.BorderStroke(1.dp, BorderGray)
        },
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) BlueSecondary.copy(alpha = 0.05f) else Color.Transparent
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) BlueSecondary else TextGray,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = if (isSelected) BlueSecondary else TextGray,
                fontSize = 12.sp
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    InovaGABTheme {
        LoginScreen(rememberNavController())
    }
}
