package br.com.fiap.ui.navigation

sealed class Screens(val route: String) {
    object Login : Screens("login")
    object SignUp : Screens("signup")
    object OperadorHome : Screens("operador_home")
    object NovaIdeia : Screens("nova_ideia")
    object MinhasIdeias : Screens("minhas_ideias")
    object GestorHome : Screens("gestor_home")
    object GestorProjetos : Screens("gestor_projetos")
    object Estrategia : Screens("estrategia")
    object LiderHome : Screens("lider_home")
    object LiderProjetos : Screens("lider_projetos")
}
