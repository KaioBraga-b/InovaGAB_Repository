package br.com.fiap.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.fiap.ui.screens.LoginScreen
import br.com.fiap.ui.screens.SignUpScreen
import br.com.fiap.ui.screens.operador.OperadorHomeScreen
import br.com.fiap.ui.screens.gestor.GestorHomeScreen
import br.com.fiap.ui.screens.lider.LiderHomeScreen
import br.com.fiap.ui.screens.operador.NovaIdeiaScreen
import br.com.fiap.ui.screens.operador.MinhasIdeiasScreen
import br.com.fiap.ui.screens.EstrategiaScreen
import br.com.fiap.ui.screens.ProjetosScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screens.Login.route) {
        composable(Screens.Login.route) {
            LoginScreen(navController)
        }
        composable(Screens.SignUp.route) {
            SignUpScreen(navController)
        }
        composable(Screens.OperadorHome.route) {
            OperadorHomeScreen(navController)
        }
        composable(Screens.NovaIdeia.route) {
            NovaIdeiaScreen(navController)
        }
        composable(Screens.MinhasIdeias.route) {
            MinhasIdeiasScreen(navController)
        }
        composable(Screens.GestorHome.route) {
            GestorHomeScreen(navController)
        }
        composable(Screens.GestorProjetos.route) {
            ProjetosScreen(navController, userRole = "GESTOR")
        }
        composable(Screens.LiderHome.route) {
            LiderHomeScreen(navController)
        }
        composable(Screens.LiderProjetos.route) {
            ProjetosScreen(navController, userRole = "LIDER")
        }
        
        // Rotas para Estratégia com diferentes permissões
        composable("${Screens.Estrategia.route}/{role}") { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "OPERADOR"
            EstrategiaScreen(navController, userRole = role)
        }
    }
}
