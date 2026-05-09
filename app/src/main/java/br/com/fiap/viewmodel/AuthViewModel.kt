package br.com.fiap.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    
    var isLoading = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)

    fun signIn(email: String, password: String, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            errorMessage.value = "E-mail e senha são obrigatórios"
            return
        }

        isLoading.value = true
        errorMessage.value = null

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                isLoading.value = false
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    errorMessage.value = task.exception?.message ?: "Erro ao fazer login"
                }
            }
    }

    fun signUp(email: String, password: String, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            errorMessage.value = "E-mail e senha são obrigatórios"
            return
        }

        isLoading.value = true
        errorMessage.value = null

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                isLoading.value = false
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    errorMessage.value = task.exception?.message ?: "Erro ao criar conta"
                }
            }
    }

    fun signOut() {
        auth.signOut()
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}
