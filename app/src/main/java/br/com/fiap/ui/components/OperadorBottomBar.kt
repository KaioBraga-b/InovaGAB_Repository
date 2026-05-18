package br.com.fiap.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.AdsClick
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import br.com.fiap.ui.navigation.Screens

@Composable
fun OperadorBottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Início") },
            selected = currentRoute == Screens.OperadorHome.route,
            onClick = {
                navController.navigate(Screens.OperadorHome.route) {
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
            icon = { Icon(Icons.Default.Lightbulb, contentDescription = null) },
            label = { Text("Ideia") },
            selected = currentRoute == Screens.NovaIdeia.route,
            onClick = {
                navController.navigate(Screens.NovaIdeia.route) {
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
            icon = { Icon(Icons.AutoMirrored.Filled.ListAlt, contentDescription = null) },
            label = { Text("Minhas") },
            selected = currentRoute == Screens.MinhasIdeias.route,
            onClick = {
                navController.navigate(Screens.MinhasIdeias.route) {
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
                navController.navigate("${Screens.Estrategia.route}/OPERADOR") {
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
