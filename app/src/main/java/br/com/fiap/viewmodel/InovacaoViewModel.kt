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
import br.com.fiap.api.Notificacao
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

data class DashboardData(
    val lucroObtidoTotal: Double = 0.0,
    val investimentoTotal: Double = 0.0,
    val roiTotalPercentual: Double = 0.0,
    val totalIdeias: Int = 0,
    val totalProjetos: Int = 0,
    val retornosPorEstrategia: List<RetornoPorEstrategia> = emptyList()
)

data class ChecklistItem(
    val id: String? = null,
    val titulo: String = "",
    var concluido: Boolean = false
)

data class Comentario(
    val autor: String = "",
    val texto: String = "",
    val dataHora: String? = null
)

data class Projeto(
    val id: String? = "",
    val titulo: String? = "",
    val descricao: String? = "",
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
    val noPrazo: Boolean? = null,
    val duracao: String? = "",
    val valorMensal: Double? = 0.0,
    val roi: Double? = 0.0,
    val resultadosAlcancados: String? = "",
    val dataInicio: String? = null,
    val prazo: String? = null,
    var tarefas: List<ChecklistItem> = emptyList()
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
    val campanha: String? = "",
    val dataVigencia: String? = null,
    val orientacoes: String? = null
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
    val prioridade: String? = "",
    val prioridadeColor: Int? = null,
    val prioridadeBg: Int? = null,
    val estrategiaId: String? = "",
    val estrategiaTitulo: String? = "",
    val comentarios: List<Comentario> = emptyList()
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

    var notificacoes by mutableStateOf<List<Notificacao>>(emptyList())
        private set

    init {
        fetchProjetos()
        fetchEstrategias()
        fetchIdeias()
        fetchAreas()
        fetchDashboardResumo()
        loadDefaultNotificacoes("TODOS")
    }

    fun recalcularDashboardResumo() {
        var somaInvestimento = 0.0
        var somaLucro = 0.0
        var countAtivos = 0
        var countNoPrazo = 0

        fun parseInvestimento(proj: Projeto): Double {
            if (proj.valorMensal != null && proj.valorMensal > 0.0) {
                val meses = proj.duracao?.filter { it.isDigit() }?.toDoubleOrNull() ?: 12.0
                return proj.valorMensal * meses
            }
            if (!proj.investimento.isNullOrBlank()) {
                val clean = proj.investimento.replace("R$", "").trim()
                if (clean.contains(",") && clean.contains(".")) {
                    val numStr = clean.replace(".", "").replace(",", ".")
                    return numStr.toDoubleOrNull() ?: 0.0
                } else if (clean.contains(",")) {
                    val numStr = clean.replace(",", ".")
                    return numStr.toDoubleOrNull() ?: 0.0
                } else {
                    val digits = clean.filter { it.isDigit() }
                    val d = digits.toDoubleOrNull() ?: 0.0
                    return if (digits.length > 5) d / 100.0 else d
                }
            }
            return 0.0
        }

        val mapaEstrategias = mutableMapOf<String, RetornoPorEstrategia>()

        // 1. Processar todas as estratégias cadastradas da API
        estrategias.forEach { est ->
            val estId = est.id ?: est.titulo ?: "estrategia"
            val projsDaEst = projetos.filter { proj ->
                (proj.estrategiaId != null && proj.estrategiaId == est.id) ||
                (!proj.estrategiaTitulo.isNullOrBlank() && proj.estrategiaTitulo.equals(est.titulo, ignoreCase = true))
            }

            var estInv = 0.0
            var estLucro = 0.0
            val estRois = mutableListOf<Double>()

            projsDaEst.forEach { p ->
                val pInv = parseInvestimento(p)
                val pLucro = when {
                    p.lucroObtido != null && p.lucroObtido > 0.0 -> p.lucroObtido
                    p.roi != null && p.roi > 0.0 && pInv > 0.0 -> pInv * (p.roi / 100.0)
                    else -> 0.0
                }
                val pRoi = when {
                    p.roi != null && p.roi > 0.0 -> p.roi
                    pInv > 0.0 && pLucro > 0.0 -> {
                        if (pLucro >= pInv) ((pLucro - pInv) / pInv) * 100.0
                        else (pLucro / pInv) * 100.0
                    }
                    else -> 0.0
                }

                estInv += pInv
                estLucro += pLucro
                if (pRoi > 0.0) estRois.add(pRoi)
            }

            val finalRoi = when {
                estRois.isNotEmpty() -> estRois.average()
                estInv > 0.0 && estLucro > 0.0 -> {
                    if (estLucro >= estInv) ((estLucro - estInv) / estInv) * 100.0
                    else (estLucro / estInv) * 100.0
                }
                else -> 0.0
            }

            mapaEstrategias[estId] = RetornoPorEstrategia(
                estrategiaId = estId,
                estrategiaTitulo = est.titulo ?: "Estratégia",
                totalProjetos = projsDaEst.size,
                investimentoTotal = estInv,
                retornoTotal = estLucro,
                roi = finalRoi
            )
        }

        // 2. Acumular totais gerais dos projetos reais
        projetos.forEach { proj ->
            val inv = parseInvestimento(proj)
            val lucro = when {
                proj.lucroObtido != null && proj.lucroObtido > 0.0 -> proj.lucroObtido
                proj.roi != null && proj.roi > 0.0 && inv > 0.0 -> inv * (proj.roi / 100.0)
                else -> 0.0
            }

            somaInvestimento += inv
            somaLucro += lucro

            if (proj.status != "Concluído") countAtivos++
            if (proj.status != "Atrasado") countNoPrazo++
        }

        val roiGeral = when {
            somaInvestimento > 0.0 && somaLucro > 0.0 -> {
                if (somaLucro >= somaInvestimento) ((somaLucro - somaInvestimento) / somaInvestimento) * 100.0
                else (somaLucro / somaInvestimento) * 100.0
            }
            somaLucro > 0.0 -> 100.0
            else -> {
                val validRois = projetos.mapNotNull { it.roi }.filter { it > 0.0 }
                if (validRois.isNotEmpty()) validRois.average() else 0.0
            }
        }

        val listaRetornos = mapaEstrategias.values.toList()
        val mediaProdutividade = projetos.mapNotNull { it.aumentoProdutividade }.filter { it > 0.0 }
        val aumentoProd = if (mediaProdutividade.isNotEmpty()) mediaProdutividade.average() else 0.0

        dashboardResumo = DashboardResumoResponse(
            roiTotalPercentual = roiGeral,
            lucroObtidoTotal = somaLucro,
            investimentoTotal = somaInvestimento,
            projetosAtivos = countAtivos,
            projetosNoPrazo = countNoPrazo,
            ideiasRegistradas = ideias.size.toLong(),
            taxaEngajamento = if (countAtivos > 0) (ideias.size.toDouble() / countAtivos) * 10.0 else 0.0,
            aumentoMedioProdutividade = aumentoProd,
            retornosPorEstrategia = listaRetornos
        )
    }

    fun fetchDashboardResumo() {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getDashboardResumo()
                if (response.isSuccessful && response.body() != null && response.body()!!.investimentoTotal > 0.0) {
                    dashboardResumo = response.body()!!
                } else {
                    recalcularDashboardResumo()
                }
            } catch (e: Exception) {
                recalcularDashboardResumo()
            }
        }
    }

    fun loadDefaultNotificacoes(role: String) {
        // Notificações geradas pelas ações reais dos usuários no aplicativo
    }

    fun adicionarNotificacaoLocal(mensagem: String, tipo: String = "GERAL", role: String = "TODOS") {
        val hoje = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
        val nova = Notificacao(
            id = "local-${System.currentTimeMillis()}",
            mensagem = mensagem,
            tipo = tipo,
            destinatarioRole = role,
            lida = false,
            dataCriacao = hoje
        )
        notificacoes = listOf(nova) + notificacoes
    }

    fun fetchNotificacoes(role: String) {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getNotificacoes(role)
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    notificacoes = response.body()!!
                }
            } catch (e: Exception) {
                Log.d("API", "Sem notificações remotas na API: ${e.message}")
            }
        }
    }

    fun marcarNotificacaoLida(id: String) {
        notificacoes = notificacoes.map {
            if (it.id == id) it.copy(lida = true) else it
        }
        viewModelScope.launch {
            try {
                ApiClient.apiService.marcarNotificacaoLida(id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchProjetos() {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getProjetos()
                if (response.isSuccessful) {
                    projetos = response.body() ?: emptyList()
                    Log.d("API", "Projetos carregados da API: ${projetos.size}")
                    recalcularDashboardResumo()
                } else {
                    Log.e("API", "Erro ao buscar projetos: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("API", "Erro de conexão ao buscar projetos", e)
            }
        }
    }

    fun fetchEstrategias() {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getEstrategias()
                if (response.isSuccessful) {
                    estrategias = response.body() ?: emptyList()
                    Log.d("API", "Estratégias carregadas da API: ${estrategias.size}")
                    recalcularDashboardResumo()
                } else {
                    Log.e("API", "Erro ao buscar estratégias: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("API", "Erro de conexão ao buscar estratégias", e)
            }
        }
    }

    fun fetchIdeias() {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getIdeias()
                if (response.isSuccessful) {
                    ideias = (response.body() ?: emptyList()).sortedByDescending { it.votos }
                    Log.d("API", "Ideias carregadas da API: ${ideias.size}")
                    recalcularDashboardResumo()
                } else {
                    Log.e("API", "Erro ao buscar ideias: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("API", "Erro de conexão ao buscar ideias", e)
            }
        }
    }

    fun fetchAreas() {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getAreas()
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    areas = response.body()!!
                    Log.d("API", "Áreas carregadas da API: ${areas.size}")
                } else {
                    loadDefaultAreas()
                }
            } catch (e: Exception) {
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
        val novo = if (projeto.id.isNullOrBlank()) projeto.copy(id = "proj-${System.currentTimeMillis()}") else projeto
        projetos = listOf(novo) + projetos
        recalcularDashboardResumo()
        adicionarNotificacaoLocal("Novo projeto criado: \"${projeto.titulo}\"")

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
            recalcularDashboardResumo()
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

    fun atualizarInvestimentoProjeto(
        id: String,
        duracao: String,
        valorMensal: Double,
        roi: Double,
        lucroObtido: Double,
        resultadosAlcancados: String
    ) {
        if (id.isBlank()) return
        val projeto = projetos.find { it.id == id } ?: return
        
        val atualizado = projeto.copy(
            duracao = duracao,
            valorMensal = valorMensal,
            roi = roi,
            lucroObtido = lucroObtido,
            resultadosAlcancados = resultadosAlcancados,
            investimento = "R$ ${String.format("%,.2f", valorMensal)}/mês" // Atualizar UI
        )
        
        val tempProjetos = projetos.toMutableList()
        val index = tempProjetos.indexOfFirst { it.id == id }
        if (index != -1) {
            tempProjetos[index] = atualizado
            projetos = tempProjetos
            recalcularDashboardResumo()
            adicionarNotificacaoLocal("Investimento registrado no projeto \"${projeto.titulo}\": R$ ${String.format("%,.2f", valorMensal)}/mês (ROI: ${String.format("%.1f", roi)}%)")
        }
        
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.updateProjeto(id, atualizado)
                if (response.isSuccessful) {
                    fetchProjetos()
                }
            } catch (e: Exception) {
                Log.e("API", "Erro ao atualizar investimento", e)
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
        campanha: String? = null,
        dataVigencia: String? = null,
        orientacoes: String? = null
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
        val novaIdeia = if (ideia.id.isNullOrBlank()) ideia.copy(id = "local-${System.currentTimeMillis()}") else ideia
        ideias = (listOf(novaIdeia) + ideias).sortedByDescending { it.votos }
        adicionarNotificacaoLocal("Nova ideia submetida: \"${ideia.titulo}\"")

        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.addIdeia(ideia)
                if (response.isSuccessful) {
                    fetchIdeias()
                }
            } catch (e: Exception) {
                Log.e("API", "Erro ao adicionar ideia na API (mantida localmente)", e)
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
            ideias = tempIdeias.sortedByDescending { it.votos }
        }

        adicionarNotificacaoLocal("A ideia \"${ideia.titulo}\" foi ${if (novoStatus == "Aprovada") "Aprovada ✅" else "Recusada ❌"}")

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

    fun votarIdeia(id: String) {
        if (id.isBlank()) return
        
        // Optimistic UI Update - mais votadas sempre em cima
        val ideia = ideias.find { it.id == id } ?: return
        val atualizada = ideia.copy(votos = ideia.votos + 1)
        val tempIdeias = ideias.toMutableList()
        val index = tempIdeias.indexOfFirst { it.id == id }
        if (index != -1) {
            tempIdeias[index] = atualizada
            ideias = tempIdeias.sortedByDescending { it.votos }
        }

        adicionarNotificacaoLocal("Você votou na ideia \"${ideia.titulo}\" (+1 voto)")

        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.votarIdeia(id)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    val updated = ideias.map { if (it.id == id) body else it }.sortedByDescending { it.votos }
                    ideias = updated
                }
            } catch (e: Exception) {
                Log.e("API", "Erro ao registrar voto no backend (mantido localmente)", e)
            }
        }
    }

    fun comentarIdeia(id: String, texto: String, autor: String) {
        if (id.isBlank() || texto.isBlank()) return
        
        val comentario = Comentario(autor = autor, texto = texto)
        
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.comentarIdeia(id, comentario)
                if (response.isSuccessful) {
                    fetchIdeias()
                }
            } catch (e: Exception) {
                Log.e("API", "Erro ao comentar", e)
            }
        }
    }
}
