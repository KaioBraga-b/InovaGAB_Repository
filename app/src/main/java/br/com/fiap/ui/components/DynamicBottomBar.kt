package br.com.fiap.ui.components

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import br.com.fiap.viewmodel.AuthViewModel

@Composable
fun DynamicBottomBar(navController: NavController, authViewModel: AuthViewModel) {
    val role = authViewModel.userData?.get("role")?.toString()?.uppercase() ?: "OPERADOR"
    when (role) {
        "GESTOR" -> GestorBottomBar(navController)
        "LIDER" -> LiderBottomBar(navController)
        else -> OperadorBottomBar(navController)
    }
}
