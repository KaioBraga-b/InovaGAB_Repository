package br.com.fiap.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.api.ApiClient
import br.com.fiap.api.LoginRequest
import br.com.fiap.api.RegisterRequest
import br.com.fiap.model.UserProfile
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    
    var isLoading by mutableStateOf(false)
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set

    var userData by mutableStateOf<Map<String, Any>?>(null)
        private set

    val currentUserId: String?
        get() = userData?.get("userId") as? String

    val userGroupId: String?
        get() = (userData?.get("groupId") as? String)?.takeIf { it.isNotBlank() }

    fun signIn(email: String, password: String, selectedProfile: UserProfile, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "E-mail e senha são obrigatórios"
            return
        }

        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.login(
                    LoginRequest(email, password, selectedProfile.name)
                )
                if (response.isSuccessful) {
                    val authData = response.body()
                    if (authData != null) {
                        ApiClient.authToken = authData.token
                        userData = mapOf(
                            "userId" to authData.userId,
                            "role" to authData.role,
                            "nome" to (authData.nome ?: ""),
                            "sobrenome" to (authData.sobrenome ?: ""),
                            "unidade" to (authData.unidade ?: ""),
                            "email" to (authData.email ?: ""),
                            "groupId" to (authData.groupId ?: "")
                        )
                        onSuccess()
                    }
                } else {
                    errorMessage = "E-mail, senha ou perfil inválidos."
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error in login", e)
                errorMessage = "Erro de conexão com o servidor."
            } finally {
                isLoading = false
            }
        }
    }

    fun signUp(email: String, password: String, nome: String, sobrenome: String, unidade: String, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank() || nome.isBlank() || sobrenome.isBlank() || unidade.isBlank()) {
            errorMessage = "Todos os campos são obrigatórios"
            return
        }

        isLoading = true
        errorMessage = null
        
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.register(
                    RegisterRequest(email, password, nome, sobrenome, unidade, UserProfile.OPERADOR.name)
                )
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    errorMessage = "Erro ao criar perfil."
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error in register", e)
                errorMessage = "Erro de conexão com o servidor."
            } finally {
                isLoading = false
            }
        }
    }

    fun signOut() {
        ApiClient.authToken = null
        userData = null
    }
}