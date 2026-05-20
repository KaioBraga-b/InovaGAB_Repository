package br.com.fiap.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.viewmodel.AuthViewModel
import br.com.fiap.viewmodel.InovacaoViewModel
import br.com.fiap.ui.screens.LoginScreen
import br.com.fiap.ui.screens.SignUpScreen
import br.com.fiap.ui.screens.operador.OperadorHomeScreen
import br.com.fiap.ui.screens.gestor.GestorHomeScreen
import br.com.fiap.ui.screens.lider.LiderHomeScreen
import br.com.fiap.ui.screens.operador.NovaIdeiaScreen
import br.com.fiap.ui.screens.operador.MinhasIdeiasScreen
import br.com.fiap.ui.screens.EstrategiaScreen
import br.com.fiap.ui.screens.ProjetosScreen
import br.com.fiap.ui.screens.lider.CriarEstrategiaScreen
import br.com.fiap.ui.screens.gestor.CriarProjetoScreen
import br.com.fiap.ui.screens.gestor.EditarProjetoScreen
import br.com.fiap.ui.screens.lider.EditarEstrategiaScreen
import br.com.fiap.ui.screens.ProfileScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val inovacaoViewModel: InovacaoViewModel = viewModel()

    NavHost(navController = navController, startDestination = Screens.Login.route) {
        composable(Screens.Login.route) {
            LoginScreen(navController, authViewModel)
        }
        composable(Screens.SignUp.route) {
            SignUpScreen(navController, authViewModel)
        }
        composable(Screens.OperadorHome.route) {
            OperadorHomeScreen(navController, authViewModel, inovacaoViewModel)
        }
        composable(Screens.NovaIdeia.route) {
            NovaIdeiaScreen(navController, inovacaoViewModel, authViewModel)
        }
        composable(Screens.MinhasIdeias.route) {
            MinhasIdeiasScreen(navController, inovacaoViewModel, authViewModel)
        }
        composable(Screens.GestorHome.route) {
            GestorHomeScreen(navController, authViewModel, inovacaoViewModel)
        }
        composable(Screens.GestorProjetos.route) {
            ProjetosScreen(navController, userRole = "GESTOR", authViewModel = authViewModel, inovacaoViewModel = inovacaoViewModel)
        }
        composable(Screens.LiderHome.route) {
            LiderHomeScreen(navController, authViewModel)
        }
        composable(Screens.LiderProjetos.route) {
            ProjetosScreen(navController, userRole = "LIDER", authViewModel = authViewModel, inovacaoViewModel = inovacaoViewModel)
        }
        
        // Rotas para Estratégia com diferentes permissões
        composable("${Screens.Estrategia.route}/{role}") { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "OPERADOR"
            EstrategiaScreen(navController, userRole = role, authViewModel = authViewModel, inovacaoViewModel = inovacaoViewModel)
        }
        composable(Screens.CriarEstrategia.route) {
            CriarEstrategiaScreen(navController, inovacaoViewModel = inovacaoViewModel)
        }
        composable(Screens.CriarProjeto.route) {
            CriarProjetoScreen(navController, inovacaoViewModel = inovacaoViewModel)
        }
        composable("${Screens.EditarProjeto.route}/{projetoId}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("projetoId") ?: ""
            EditarProjetoScreen(navController, id, inovacaoViewModel = inovacaoViewModel)
        }
        composable("${Screens.EditarEstrategia.route}/{estrategiaId}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("estrategiaId") ?: ""
            EditarEstrategiaScreen(navController, id, inovacaoViewModel = inovacaoViewModel)
        }
        composable(Screens.Profile.route) {
            ProfileScreen(navController, authViewModel)
        }
    }
}
