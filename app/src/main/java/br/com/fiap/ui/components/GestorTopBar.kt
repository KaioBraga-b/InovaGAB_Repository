package br.com.fiap.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import br.com.fiap.ui.navigation.Screens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestorTopBar(navController: NavController, initials: String) {
    var expanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(
                text = buildAnnotatedString {
                    append("Inova")
                    withStyle(style = SpanStyle(color = Color(0xFF3B82F6))) {
                        append("GAB")
                    }
                },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E3A8A)
            )
        },
        actions = {
            Surface(
                color = Color(0xFFEFF6FF),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Gestor",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    color = Color(0xFF2563EB),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(12.dp))
            
            IconButton(
                onClick = { navController.navigate(Screens.Profile.route) },
                modifier = Modifier.size(36.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFEF4444), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(initials, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Mais opções")
            }

                DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Dashboard") },
                    onClick = {
                        expanded = false
                        navController.navigate(Screens.GestorHome.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                DropdownMenuItem(
                    text = { Text("Estratégias") },
                    onClick = {
                        expanded = false
                        navController.navigate("${Screens.Estrategia.route}/GESTOR") {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                DropdownMenuItem(
                    text = { Text("Curadoria") },
                    onClick = {
                        expanded = false
                        navController.navigate(Screens.GestorCuradoria.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                DropdownMenuItem(
                    text = { Text("Projetos") },
                    onClick = {
                        expanded = false
                        navController.navigate(Screens.GestorProjetos.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                DropdownMenuItem(
                    text = { Text("Ideias Aprovadas") },
                    onClick = {
                        expanded = false
                        navController.navigate(Screens.GestorIdeiasAprovadas.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                DropdownMenuItem(
                    text = { Text("Nova Ideia") },
                    onClick = {
                        expanded = false
                        navController.navigate(Screens.NovaIdeia.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                DropdownMenuItem(
                    text = { Text("Minhas Ideias") },
                    onClick = {
                        expanded = false
                        navController.navigate(Screens.MinhasIdeias.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFFF8F9FD)
        )
    )
}
