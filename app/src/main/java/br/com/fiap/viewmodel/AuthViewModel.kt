package br.com.fiap.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import br.com.fiap.model.UserProfile

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = Firebase.firestore
    
    var isLoading = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)

    fun signIn(email: String, password: String, selectedProfile: UserProfile, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            errorMessage.value = "E-mail e senha são obrigatórios"
            return
        }

        isLoading.value = true
        errorMessage.value = null

        Log.d("AuthDebug", "Iniciando Login para: $email como ${selectedProfile.name}")

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    Log.d("AuthDebug", "Auth com sucesso. UID: $userId. Buscando no Firestore...")
                    
                    if (userId != null) {
                        db.collection("usuarios").document(userId).get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    val storedRole = document.getString("role")
                                    Log.d("AuthDebug", "Perfil encontrado no banco: $storedRole")
                                    
                                    if (storedRole == selectedProfile.name) {
                                        Log.d("AuthDebug", "Sucesso! Perfil bate.")
                                        onSuccess()
                                    } else {
                                        Log.w("AuthDebug", "Bloqueado! Perfil no banco ($storedRole) != Selecionado (${selectedProfile.name})")
                                        auth.signOut()
                                        errorMessage.value = "Acesso negado: Seu perfil é $storedRole"
                                    }
                                } else {
                                    Log.e("AuthDebug", "ERRO: Usuário existe no Auth mas não tem documento no Firestore!")
                                    auth.signOut()
                                    errorMessage.value = "Perfil não configurado no sistema."
                                }
                                isLoading.value = false
                            }
                            .addOnFailureListener { e ->
                                Log.e("AuthDebug", "Erro ao ler Firestore", e)
                                auth.signOut()
                                errorMessage.value = "Erro de conexão com o banco de dados."
                                isLoading.value = false
                            }
                    }
                } else {
                    Log.e("AuthDebug", "Falha no Auth: ${task.exception?.message}")
                    errorMessage.value = "E-mail ou senha inválidos."
                    isLoading.value = false
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
        
        Log.d("AuthDebug", "Iniciando Cadastro para: $email")

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    Log.d("AuthDebug", "Conta criada no Auth. UID: $userId. Criando perfil no Firestore...")
                    
                    if (userId != null) {
                        val userMap = hashMapOf(
                            "email" to email,
                            "role" to UserProfile.OPERADOR.name
                        )
                        db.collection("usuarios").document(userId).set(userMap)
                            .addOnSuccessListener {
                                Log.d("AuthDebug", "Perfil de OPERADOR salvo com sucesso no Firestore!")
                                onSuccess()
                                isLoading.value = false
                            }
                            .addOnFailureListener { e ->
                                Log.e("AuthDebug", "Erro ao salvar no Firestore", e)
                                errorMessage.value = "Erro ao criar perfil no banco: ${e.message}"
                                isLoading.value = false
                            }
                    }
                } else {
                    Log.e("AuthDebug", "Erro ao criar conta no Auth: ${task.exception?.message}")
                    errorMessage.value = task.exception?.message
                    isLoading.value = false
                }
            }
    }

    fun signOut() {
        auth.signOut()
    }
}