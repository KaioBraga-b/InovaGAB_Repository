package br.com.fiap.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import br.com.fiap.model.UserProfile

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = Firebase.firestore
    private var userListener: ListenerRegistration? = null
    
    var isLoading by mutableStateOf(false)
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set

    // Estado do usuário para persistência na UI com real-time sync
    var userData by mutableStateOf<Map<String, Any>?>(null)
        private set

    val currentUserId: String?
        get() = auth.currentUser?.uid

    init {
        // Verifica se já existe um usuário logado e inicia o listener em tempo real
        auth.addAuthStateListener { firebaseAuth ->
            val userId = firebaseAuth.currentUser?.uid
            if (userId != null) {
                startUserDataListener(userId)
            } else {
                stopUserDataListener()
                userData = null
            }
        }
    }

    private fun startUserDataListener(userId: String) {
        userListener?.remove()
        userListener = db.collection("usuarios").document(userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("AuthDebug", "Erro no listener de usuário", e)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    userData = snapshot.data
                    Log.d("AuthDebug", "Dados do usuário sincronizados em tempo real")
                }
            }
    }

    private fun stopUserDataListener() {
        userListener?.remove()
        userListener = null
    }

    override fun onCleared() {
        super.onCleared()
        stopUserDataListener()
    }

    fun signIn(email: String, password: String, selectedProfile: UserProfile, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "E-mail e senha são obrigatórios"
            return
        }

        isLoading = true
        errorMessage = null

        Log.d("AuthDebug", "Iniciando Login para: $email como ${selectedProfile.name}")

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    Log.d("AuthDebug", "Auth com sucesso. UID: $userId. Validando perfil...")
                    
                    if (userId != null) {
                        // Fazemos uma verificação pontual apenas para o login, 
                        // o listener cuidará da persistência do estado.
                        db.collection("usuarios").document(userId).get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    val storedRole = document.getString("role")
                                    if (storedRole == selectedProfile.name) {
                                        onSuccess()
                                    } else {
                                        auth.signOut()
                                        errorMessage = "Acesso negado: Seu perfil é $storedRole"
                                    }
                                } else {
                                    auth.signOut()
                                    errorMessage = "Perfil não configurado no sistema."
                                }
                                isLoading = false
                            }
                            .addOnFailureListener { e ->
                                auth.signOut()
                                errorMessage = "Erro de conexão com o banco de dados."
                                isLoading = false
                            }
                    }
                } else {
                    errorMessage = "E-mail ou senha inválidos."
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
        
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        val userMap = hashMapOf(
                            "email" to email,
                            "nome" to nome,
                            "sobrenome" to sobrenome,
                            "unidade" to unidade,
                            "role" to UserProfile.OPERADOR.name
                        )
                        db.collection("usuarios").document(userId).set(userMap)
                            .addOnSuccessListener {
                                onSuccess()
                                isLoading = false
                            }
                            .addOnFailureListener { e ->
                                errorMessage = "Erro ao criar perfil: ${e.message}"
                                isLoading = false
                            }
                    }
                } else {
                    errorMessage = task.exception?.message
                    isLoading = false
                }
            }
    }

    fun signOut() {
        auth.signOut()
        userData = null
        stopUserDataListener()
    }
}