package br.com.fiap.inovagab.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdsClick
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import br.com.fiap.inovagab.ui.navigation.Screens

@Composable
fun LiderBottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.BarChart, contentDescription = null) },
            label = { Text("Dashboard") },
            selected = currentRoute == Screens.LiderHome.route,
            onClick = {
                navController.navigate(Screens.LiderHome.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF2563EB),
                selectedTextColor = Color(0xFF2563EB)
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Folder, contentDescription = null) },
            label = { Text("Projetos") },
            selected = currentRoute == Screens.LiderProjetos.route,
            onClick = {
                navController.navigate(Screens.LiderProjetos.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF2563EB),
                selectedTextColor = Color(0xFF2563EB)
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.AdsClick, contentDescription = null) },
            label = { Text("Estratégia") },
            selected = currentRoute?.startsWith(Screens.Estrategia.route) == true,
            onClick = {
                navController.navigate("${Screens.Estrategia.route}/LIDER") {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF2563EB),
                selectedTextColor = Color(0xFF2563EB)
            )
        )
    }
}
