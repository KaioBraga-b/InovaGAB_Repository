package br.com.fiap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import br.com.fiap.ui.navigation.AppNavigation
import br.com.fiap.ui.theme.InovaGABTheme

import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.viewmodel.AuthViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InovaGABTheme {
                val authViewModel: AuthViewModel = viewModel()
                
                // Força o logout se o app abrir e não tivermos o perfil no banco (segurança extra)
                LaunchedEffect(Unit) {
                    val user = Firebase.auth.currentUser
                    if (user != null) {
                        // Se já está logado, vamos garantir que ele tem o perfil no Firestore
                        authViewModel.signOut() // Desloga por segurança para forçar o re-login com validação de cargo
                    }
                }

                AppNavigation()
            }
        }
    }
}
