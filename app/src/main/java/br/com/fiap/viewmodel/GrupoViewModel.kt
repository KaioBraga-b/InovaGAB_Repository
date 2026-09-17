package br.com.fiap.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.api.ApiClient
import br.com.fiap.api.GrupoRequest
import br.com.fiap.api.GrupoResponse
import br.com.fiap.api.MembroRequest
import br.com.fiap.api.UserResponse
import kotlinx.coroutines.launch

class GrupoViewModel : ViewModel() {

    var grupos by mutableStateOf<List<GrupoResponse>>(emptyList())
        private set

    var grupoSelecionado by mutableStateOf<GrupoResponse?>(null)
        private set

    var usuariosDisponiveis by mutableStateOf<List<UserResponse>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var successMessage by mutableStateOf<String?>(null)
        private set

    var usuarioVerificado by mutableStateOf<UserResponse?>(null)
        private set

    var isVerificandoUsuario by mutableStateOf(false)
        private set

    var buscaRealizada by mutableStateOf(false)
        private set

    init {
        carregarGrupos()
        buscarUsuarios()
    }

    fun carregarGrupos() {
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.listarGrupos()
                if (response.isSuccessful && response.body() != null) {
                    grupos = response.body()!!
                    // Atualiza grupoSelecionado se ainda existir
                    if (grupoSelecionado != null) {
                        grupoSelecionado = grupos.find { it.hashId == grupoSelecionado?.hashId }
                    }
                } else {
                    val err = response.errorBody()?.string()
                    Log.w("GrupoViewModel", "Falha ao listar grupos: $err")
                }
            } catch (e: Exception) {
                Log.e("GrupoViewModel", "Erro ao carregar grupos", e)
                errorMessage = "Falha de conexão ao carregar grupos."
            } finally {
                isLoading = false
            }
        }
    }

    fun selecionarGrupo(grupo: GrupoResponse?) {
        grupoSelecionado = grupo
    }

    fun criarGrupo(
        nome: String,
        departamento: String,
        descricao: String? = null,
        membrosIniciais: List<MembroRequest> = emptyList(),
        onSuccess: (GrupoResponse) -> Unit
    ) {
        if (nome.isBlank() || departamento.isBlank()) {
            errorMessage = "Nome e departamento são obrigatórios."
            return
        }

        isLoading = true
        errorMessage = null
        successMessage = null

        viewModelScope.launch {
            try {
                val request = GrupoRequest(
                    nome = nome.trim(),
                    departamento = departamento.trim(),
                    descricao = descricao?.trim(),
                    membros = membrosIniciais
                )
                val response = ApiClient.apiService.criarGrupo(request)
                if (response.isSuccessful && response.body() != null) {
                    val novoGrupo = response.body()!!
                    grupos = listOf(novoGrupo) + grupos.filter { it.id != novoGrupo.id }
                    successMessage = "Grupo \"${novoGrupo.nome}\" criado com sucesso!"
                    onSuccess(novoGrupo)
                    buscarUsuarios() // Atualiza lista de usuários com novos groupIds
                } else {
                    val err = response.errorBody()?.string() ?: "Erro ao criar grupo"
                    errorMessage = err
                }
            } catch (e: Exception) {
                Log.e("GrupoViewModel", "Erro ao criar grupo", e)
                errorMessage = "Erro de conexão ao criar grupo."
            } finally {
                isLoading = false
            }
        }
    }

    fun adicionarOuAtualizarMembro(
        groupId: String,
        email: String,
        role: String,
        onSuccess: (GrupoResponse) -> Unit
    ) {
        if (groupId.isBlank() || email.isBlank()) {
            errorMessage = "E-mail e identificador do grupo são obrigatórios."
            return
        }

        isLoading = true
        errorMessage = null
        successMessage = null

        viewModelScope.launch {
            try {
                val request = MembroRequest(email = email.trim(), role = role)
                val response = ApiClient.apiService.adicionarOuAtualizarMembro(groupId, request)
                if (response.isSuccessful && response.body() != null) {
                    val atualizado = response.body()!!
                    grupos = grupos.map { if (it.hashId == groupId) atualizado else it }
                    if (grupoSelecionado?.hashId == groupId) {
                        grupoSelecionado = atualizado
                    }
                    successMessage = "Colaborador $email delegado como $role com sucesso!"
                    onSuccess(atualizado)
                    buscarUsuarios()
                } else {
                    val err = response.errorBody()?.string() ?: "Erro ao delegar membro"
                    errorMessage = err
                }
            } catch (e: Exception) {
                Log.e("GrupoViewModel", "Erro ao adicionar membro", e)
                errorMessage = "Erro de conexão ao vincular membro."
            } finally {
                isLoading = false
            }
        }
    }

    fun removerMembro(
        groupId: String,
        email: String,
        onSuccess: (GrupoResponse) -> Unit
    ) {
        if (groupId.isBlank() || email.isBlank()) return

        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.removerMembro(groupId, email)
                if (response.isSuccessful && response.body() != null) {
                    val atualizado = response.body()!!
                    grupos = grupos.map { if (it.hashId == groupId) atualizado else it }
                    if (grupoSelecionado?.hashId == groupId) {
                        grupoSelecionado = atualizado
                    }
                    successMessage = "Membro $email removido do grupo."
                    onSuccess(atualizado)
                    buscarUsuarios()
                } else {
                    errorMessage = response.errorBody()?.string() ?: "Erro ao remover membro."
                }
            } catch (e: Exception) {
                Log.e("GrupoViewModel", "Erro ao remover membro", e)
                errorMessage = "Erro de conexão ao remover membro."
            } finally {
                isLoading = false
            }
        }
    }

    fun buscarUsuarios(busca: String? = null) {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.listarUsuariosDisponiveis(busca)
                if (response.isSuccessful && response.body() != null) {
                    usuariosDisponiveis = response.body()!!
                }
            } catch (e: Exception) {
                Log.e("GrupoViewModel", "Erro ao listar usuários disponíveis", e)
            }
        }
    }

    fun verificarEmailUsuario(email: String) {
        val emailTratado = email.trim()
        if (emailTratado.isBlank()) {
            usuarioVerificado = null
            buscaRealizada = false
            return
        }

        isVerificandoUsuario = true
        buscaRealizada = false
        usuarioVerificado = null

        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.listarUsuariosDisponiveis(emailTratado)
                if (response.isSuccessful && response.body() != null) {
                    val lista = response.body()!!
                    // Match exato do email ignorando maiúsculas
                    usuarioVerificado = lista.find { it.email?.equals(emailTratado, ignoreCase = true) == true }
                        ?: lista.firstOrNull { it.email?.contains(emailTratado, ignoreCase = true) == true }
                }
            } catch (e: Exception) {
                Log.e("GrupoViewModel", "Erro ao verificar e-mail do usuário", e)
            } finally {
                buscaRealizada = true
                isVerificandoUsuario = false
            }
        }
    }

    fun limparVerificacao() {
        usuarioVerificado = null
        buscaRealizada = false
        isVerificandoUsuario = false
    }

    fun limparMensagens() {
        errorMessage = null
        successMessage = null
    }
}
