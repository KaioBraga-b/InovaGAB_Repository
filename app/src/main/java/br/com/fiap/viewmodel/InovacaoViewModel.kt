package br.com.fiap.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.api.ApiClient
import br.com.fiap.api.Area
import kotlinx.coroutines.launch
import com.google.gson.annotations.SerializedName

data class RetornoPorEstrategia(
    val estrategiaId: String = "",
    val estrategiaTitulo: String = "",
    val totalProjetos: Int = 0,
    val investimentoTotal: Double = 0.0,
    val retornoTotal: Double = 0.0,
    val roi: Double = 0.0
)

data class DashboardResumoResponse(
    val roiTotalPercentual: Double = 0.0,
    val lucroObtidoTotal: Double = 0.0,
    val investimentoTotal: Double = 0.0,
    val projetosAtivos: Int = 0,
    val projetosNoPrazo: Int = 0,
    val ideiasRegistradas: Long = 0,
    val taxaEngajamento: Double = 0.0,
    val aumentoMedioProdutividade: Double = 0.0,
    val retornosPorEstrategia: List<RetornoPorEstrategia> = emptyList()
)

data class Projeto(
    val id: String? = "",
    val titulo: String? = "",
    var status: String? = "",
    var statusColor: Int = Color(0xFFD97706).toArgb(),
    var statusBg: Int = Color(0xFFFEF3C7).toArgb(),
    val area: String? = "",
    val periodo: String? = "",
    var progresso: Double = 0.0,
    var progressoTexto: String? = "",
    var etapaAtiva: Int = 0,
    val estMensal: String? = null,
    val resultadoValor: String? = "",
    val resultadoRoi: String? = "",
    val investimento: String? = "",
    val estrategiaId: String? = null,
    val estrategiaTitulo: String? = null,
    val lucroObtido: Double? = null,
    val aumentoProdutividade: Double? = null,
    val noPrazo: Boolean? = null
)

data class Estrategia(
    val id: String? = "",
    val titulo: String? = "",
    val descricao: String? = "",
    var status: String? = "",
    var statusColor: Int = Color(0xFFEFF6FF).toArgb(),
    var statusTextColor: Int = Color(0xFF2563EB).toArgb(),
    var progresso: Double = 0.0,
    val dataCriacao: String? = "",
    val categoria: String? = "",
    val campanha: String? = ""
)

data class Ideia(
    val id: String? = "",
    val titulo: String? = "",
    val descricao: String? = "",
    val autor: String? = "",
    val area: String? = "",
    val status: String? = "Enviada",
    val statusColor: Int = Color(0xFFF3F4F6).toArgb(),
    val statusTextColor: Int = Color(0xFF6B7280).toArgb(),
    val tempo: String? = "Agora",
    var progresso: Double = 0.1,
    val etapa: String? = "Aguardando triagem",
    val destaque: String? = null,
    val userId: String? = "",
    val votos: Int = 0,
    val impacto: String? = "Baixo",
    val objetivo: String? = "Melhoria",
    val prioridade: String? = "Média",
    val prioridadeColor: Int = Color(0xFFD97706).toArgb(),
    val prioridadeBg: Int = Color(0xFFFEF3C7).toArgb(),
    val estrategiaId: String? = null,
    val estrategiaTitulo: String? = null
)

class InovacaoViewModel : ViewModel() {

    var projetos by mutableStateOf<List<Projeto>>(emptyList())
        private set
    
    var estrategias by mutableStateOf<List<Estrategia>>(emptyList())
        private set
    
    var ideias by mutableStateOf<List<Ideia>>(emptyList())
        private set

    var areas by mutableStateOf<List<Area>>(emptyList())
        private set
        
    var dashboardResumo by mutableStateOf(DashboardResumoResponse())
        private set

    init {
        fetchProjetos()
        fetchEstrategias()
        fetchIdeias()
        fetchAreas()
        fetchDashboardResumo()
    }

    fun fetchDashboardResumo() {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getDashboardResumo()
                if (response.isSuccessful) {
                    dashboardResumo = response.body() ?: DashboardResumoResponse()
                }
            } catch (e: Exception) {
                Log.e("API", "Erro ao buscar dashboard", e)
            }
        }
    }

    fun fetchProjetos() {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getProjetos()
                if (response.isSuccessful) {
                    projetos = response.body() ?: emptyList()
                    Log.d("API", "Projetos carregados: ${projetos.size}")
                }
            } catch (e: Exception) {
                Log.e("API", "Erro ao buscar projetos", e)
            }
        }
    }

    fun fetchEstrategias() {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getEstrategias()
                if (response.isSuccessful) {
                    estrategias = response.body() ?: emptyList()
                    Log.d("API", "Estrategias carregadas: ${estrategias.size}")
                }
            } catch (e: Exception) {
                Log.e("API", "Erro ao buscar estrategias", e)
            }
        }
    }

    fun fetchIdeias() {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getIdeias()
                if (response.isSuccessful) {
                    ideias = response.body() ?: emptyList()
                    Log.d("API", "Ideias carregadas: ${ideias.size}")
                }
            } catch (e: Exception) {
                Log.e("API", "Erro ao buscar ideias", e)
            }
        }
    }

    fun fetchAreas() {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getAreas()
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    areas = response.body() ?: emptyList()
                    Log.d("API", "Áreas carregadas da API: ${areas.size}")
                } else {
                    // Fallback local se a API não estiver pronta ou retornar vazio
                    loadDefaultAreas()
                }
            } catch (e: Exception) {
                Log.e("API", "Erro ao buscar áreas (usando fallback local)", e)
                loadDefaultAreas()
            }
        }
    }

    private fun loadDefaultAreas() {
        if (areas.isEmpty()) {
            areas = listOf(
                Area(nome = "Operação"),
                Area(nome = "Produção"),
                Area(nome = "RH"),
                Area(nome = "TI"),
                Area(nome = "Comercial"),
                Area(nome = "Financeiro"),
                Area(nome = "Marketing")
            )
        }
    }

    fun adicionarArea(nome: String) {
        if (nome.isBlank()) return
        
        // Optimistic Update
        val novaArea = Area(nome = nome)
        areas = areas + novaArea

        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.addArea(novaArea)
                if (response.isSuccessful) {
                    fetchAreas()
                }
            } catch (e: Exception) {
                Log.e("API", "Erro ao adicionar área na API", e)
                // Mantém o optimistic update caso a API não exista (para demonstração)
            }
        }
    }

    // Projetos
    fun adicionarProjeto(projeto: Projeto) {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.addProjeto(projeto)
                if (response.isSuccessful) {
                    fetchProjetos() // Refresh
                }
            } catch (e: Exception) {
                Log.e("API", "Erro ao adicionar projeto", e)
            }
        }
    }

    fun atualizarProjeto(
        id: String, 
        titulo: String, 
        area: String, 
        novoProgresso: Double, 
        novaEtapa: Int,
        resultadoValor: String? = null,
        resultadoRoi: String? = null,
        investimento: String? = null,
        estrategiaId: String? = null,
        estrategiaTitulo: String? = null
    ) {
        if (id.isBlank()) return
        val projeto = projetos.find { it.id == id } ?: return
        
        val novasEtapas = listOf("Ideação", "Aprovação", "Execução", "Resultado")
        val statusColor = when(novaEtapa) {
            0 -> Color(0xFFD97706).toArgb()
            1 -> Color(0xFF2563EB).toArgb()
            2 -> Color(0xFF8B5CF6).toArgb()
            3 -> Color(0xFF16A34A).toArgb()
            else -> projeto.statusColor
        }
        val statusBg = when(novaEtapa) {
            0 -> Color(0xFFFEF3C7).toArgb()
            1 -> Color(0xFFEFF6FF).toArgb()
            2 -> Color(0xFFF5F3FF).toArgb()
            3 -> Color(0xFFDCFCE7).toArgb()
            else -> projeto.statusBg
        }

        val atualizado = projeto.copy(
            titulo = titulo,
            area = area,
            progresso = novoProgresso,
            etapaAtiva = novaEtapa,
            status = novasEtapas[novaEtapa],
            progressoTexto = "${(novoProgresso * 100).toInt()}% concluído",
            statusColor = statusColor,
            statusBg = statusBg,
            resultadoValor = resultadoValor ?: projeto.resultadoValor,
            resultadoRoi = resultadoRoi ?: projeto.resultadoRoi,
            investimento = investimento ?: projeto.investimento,
            estrategiaId = estrategiaId ?: projeto.estrategiaId,
            estrategiaTitulo = estrategiaTitulo ?: projeto.estrategiaTitulo,
            lucroObtido = projeto.lucroObtido,
            aumentoProdutividade = projeto.aumentoProdutividade,
            noPrazo = projeto.noPrazo
        )

        // Optimistic UI Update
        val tempProjetos = projetos.toMutableList()
        val index = tempProjetos.indexOfFirst { it.id == id }
        if (index != -1) {
            tempProjetos[index] = atualizado
            projetos = tempProjetos
        }

        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.updateProjeto(id, atualizado)
                if (response.isSuccessful) {
                    fetchProjetos()
                } else {
                    fetchProjetos() // rollback
                }
            } catch (e: Exception) {
                Log.e("API", "Erro ao atualizar projeto", e)
                fetchProjetos() // rollback
            }
        }
    }

    fun excluirProjeto(id: String) {
        if (id.isBlank()) return
        
        // Optimistic UI Update
        val tempProjetos = projetos.toMutableList()
        tempProjetos.removeAll { it.id == id }
        projetos = tempProjetos

        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.deleteProjeto(id)
                if (response.isSuccessful) {
                    fetchProjetos()
                } else {
                    fetchProjetos() // rollback
                }
            } catch (e: Exception) {
                Log.e("API", "Erro ao excluir projeto", e)
                fetchProjetos() // rollback
            }
        }
    }

    fun adicionarEstrategia(estrategia: Estrategia, onComplete: ((Boolean) -> Unit)? = null) {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.addEstrategia(estrategia)
                if (response.isSuccessful) {
                    fetchEstrategias()
                    onComplete?.invoke(true)
                } else {
                    onComplete?.invoke(false)
                }
            } catch (e: Exception) {
                Log.e("API", "Erro ao adicionar estrategia", e)
                onComplete?.invoke(false)
            }
        }
    }

    fun atualizarEstrategia(
        id: String, 
        titulo: String, 
        descricao: String, 
        novoProgresso: Double, 
        novaEtapa: Int,
        categoria: String? = null,
        campanha: String? = null
    ) {
        if (id.isBlank()) return
        val estrategia = estrategias.find { it.id == id } ?: return
        val nomesEtapas = listOf("Planejamento", "Em andamento", "Concluído")
        
        val statusColor = when(novaEtapa) {
            0 -> Color(0xFFF3F4F6).toArgb()
            1 -> Color(0xFFEFF6FF).toArgb()
            2 -> Color(0xFFDCFCE7).toArgb()
            else -> estrategia.statusColor
        }
        val statusTextColor = when(novaEtapa) {
            0 -> Color(0xFF6B7280).toArgb()
            1 -> Color(0xFF2563EB).toArgb()
            2 -> Color(0xFF16A34A).toArgb()
            else -> estrategia.statusTextColor
        }

        val atualizada = estrategia.copy(
            titulo = titulo,
            descricao = descricao,
            progresso = novoProgresso,
            status = nomesEtapas[novaEtapa],
            statusColor = statusColor,
            statusTextColor = statusTextColor,
            categoria = categoria ?: estrategia.categoria,
            campanha = campanha ?: estrategia.campanha
        )

        // Optimistic UI Update
        val tempEstrategias = estrategias.toMutableList()
        val index = tempEstrategias.indexOfFirst { it.id == id }
        if (index != -1) {
            tempEstrategias[index] = atualizada
            estrategias = tempEstrategias
        }

        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.updateEstrategia(id, atualizada)
                if (response.isSuccessful) {
                    fetchEstrategias()
                } else {
                    fetchEstrategias() // rollback
                }
            } catch (e: Exception) {
                Log.e("API", "Erro ao atualizar estrategia", e)
                fetchEstrategias() // rollback
            }
        }
    }

    fun excluirEstrategia(id: String) {
        if (id.isBlank()) return
        
        // Optimistic UI Update
        val tempEstrategias = estrategias.toMutableList()
        tempEstrategias.removeAll { it.id == id }
        estrategias = tempEstrategias

        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.deleteEstrategia(id)
                if (response.isSuccessful) {
                    fetchEstrategias()
                } else {
                    fetchEstrategias() // rollback
                }
            } catch (e: Exception) {
                Log.e("API", "Erro ao excluir estrategia", e)
                fetchEstrategias() // rollback
            }
        }
    }

    // Ideias
    fun adicionarIdeia(ideia: Ideia) {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.addIdeia(ideia)
                if (response.isSuccessful) {
                    fetchIdeias()
                }
            } catch (e: Exception) {
                Log.e("API", "Erro ao adicionar ideia", e)
            }
        }
    }

    fun atualizarIdeia(id: String, titulo: String, descricao: String, area: String) {
        if (id.isBlank()) return
        val ideia = ideias.find { it.id == id } ?: return
        
        val atualizada = ideia.copy(
            titulo = titulo,
            descricao = descricao,
            area = area
        )

        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.updateIdeia(id, atualizada)
                if (response.isSuccessful) {
                    fetchIdeias()
                }
            } catch (e: Exception) {
                Log.e("API", "Erro ao atualizar ideia", e)
            }
        }
    }

    fun atualizarStatusIdeia(id: String, novoStatus: String) {
        if (id.isBlank()) return
        val ideia = ideias.find { it.id == id } ?: return
        
        val statusColor = when(novoStatus) {
            "Aprovada" -> Color(0xFFDCFCE7).toArgb()
            "Recusada" -> Color(0xFFFEE2E2).toArgb()
            else -> Color(0xFFFEF3C7).toArgb()
        }
        val statusTextColor = when(novoStatus) {
            "Aprovada" -> Color(0xFF16A34A).toArgb()
            "Recusada" -> Color(0xFFEF4444).toArgb()
            else -> Color(0xFFD97706).toArgb()
        }
        val etapa = when(novoStatus) {
            "Aprovada" -> "Aprovada pelo gestor"
            "Recusada" -> "Ideia arquivada"
            else -> "Em análise"
        }
        val progresso = if (novoStatus == "Aprovada") 1.0 else 0.4

        val atualizada = ideia.copy(
            status = novoStatus,
            statusColor = statusColor,
            statusTextColor = statusTextColor,
            etapa = etapa,
            progresso = progresso
        )

        // Optimistic UI Update
        val tempIdeias = ideias.toMutableList()
        val index = tempIdeias.indexOfFirst { it.id == id }
        if (index != -1) {
            tempIdeias[index] = atualizada
            ideias = tempIdeias
        }

        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.updateIdeia(id, atualizada)
                if (response.isSuccessful) {
                    fetchIdeias()
                }
            } catch (e: Exception) {
                Log.e("API", "Erro ao atualizar status da ideia", e)
            }
        }
    }

    fun excluirIdeia(id: String) {
        if (id.isBlank()) return
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.deleteIdeia(id)
                if (response.isSuccessful) {
                    fetchIdeias()
                }
            } catch (e: Exception) {
                Log.e("API", "Erro ao excluir ideia", e)
            }
        }
    }
}
