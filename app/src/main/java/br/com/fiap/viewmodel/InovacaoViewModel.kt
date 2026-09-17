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
import br.com.fiap.api.TransacaoFinanceira
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
    var tarefas: List<ChecklistItem> = emptyList(),
    val groupId: String? = null
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
    val orientacoes: String? = null,
    val groupId: String? = null
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
    val comentarios: List<Comentario> = emptyList(),
    val groupId: String? = null
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

    var transacoesFinanceiras by mutableStateOf<List<TransacaoFinanceira>>(emptyList())
        private set

    init {
        fetchProjetos()
        fetchEstrategias()
        fetchIdeias()
        fetchAreas()
        fetchDashboardResumo()
        fetchTransacoes()
        loadDefaultNotificacoes("TODOS")
    }

    fun parseInvestimento(proj: Projeto): Double {
        if (proj.valorMensal != null && proj.valorMensal > 0.0) {
            val meses = proj.duracao?.filter { it.isDigit() }?.toDoubleOrNull() ?: 1.0
            return proj.valorMensal * meses
        }
        if (!proj.investimento.isNullOrBlank()) {
            val clean = proj.investimento
                .replace("R$", "")
                .replace("/mês", "")
                .replace("/mes", "")
                .trim()
            val numStr = clean.replace("[^0-9,.]".toRegex(), "")
            return when {
                numStr.contains(",") && numStr.contains(".") -> numStr.replace(".", "").replace(",", ".").toDoubleOrNull() ?: 0.0
                numStr.contains(",") -> numStr.replace(",", ".").toDoubleOrNull() ?: 0.0
                numStr.contains(".") -> numStr.toDoubleOrNull() ?: 0.0
                else -> numStr.toDoubleOrNull() ?: 0.0
            }
        }
        return 0.0
    }

    fun recalcularDashboardResumo() {
        var somaInvestimento = 0.0
        var somaLucro = 0.0
        var countAtivos = 0
        var countNoPrazo = 0

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

        // 3. Somar transações avulsas (sem projeto vinculado) para refletir no saldo global
        transacoesFinanceiras.filter { it.projetoId.isNullOrBlank() }.forEach { t ->
            if (t.tipo.equals("DESPESA", ignoreCase = true)) {
                somaInvestimento += t.valor
            } else if (t.tipo.equals("RECEITA", ignoreCase = true)) {
                somaLucro += t.valor
            }
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
                if (response.isSuccessful && response.body() != null) {
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
        estrategiaTitulo: String? = null,
        aumentoProdutividade: Double? = null
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
            aumentoProdutividade = if (aumentoProdutividade != null) aumentoProdutividade else projeto.aumentoProdutividade,
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

        // Se houver lucro/receita informada, registrar no histórico de receitas
        if (lucroObtido > 0.0) {
            registrarReceita(
                projetoId = id,
                projetoTitulo = projeto.titulo ?: "Projeto",
                descricao = if (resultadosAlcancados.isNotBlank()) resultadosAlcancados else "Receita / Lucro registrado",
                valor = lucroObtido,
                categoria = "Receita de Projeto"
            )
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

    // Gestão de Transações Financeiras (Receitas e Despesas)
    fun fetchTransacoes(projetoId: String? = null, tipo: String? = null) {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getTransacoes(projetoId, tipo)
                if (response.isSuccessful && response.body() != null) {
                    transacoesFinanceiras = response.body()!!
                    Log.d("API", "Transações carregadas da API: ${transacoesFinanceiras.size}")
                }
            } catch (e: Exception) {
                Log.d("API", "Sem transações remotas: ${e.message}")
            }
        }
    }

    fun registrarTransacao(
        projetoId: String,
        projetoTitulo: String,
        tipo: String, // "RECEITA" ou "DESPESA"
        descricao: String,
        valor: Double,
        categoria: String = "Geral",
        responsavel: String = "Gestor"
    ) {
        val dataFormatada = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
        val nova = TransacaoFinanceira(
            id = "trans-${System.currentTimeMillis()}",
            projetoId = projetoId,
            projetoTitulo = projetoTitulo,
            tipo = tipo.uppercase(),
            descricao = descricao,
            valor = valor,
            categoria = categoria,
            dataHora = dataFormatada,
            responsavel = responsavel
        )

        // Optimistic UI update no histórico
        transacoesFinanceiras = listOf(nova) + transacoesFinanceiras

        // Atualizar projeto localmente
        val projeto = projetos.find { it.id == projetoId }
        if (projeto != null) {
            if (tipo.equals("RECEITA", ignoreCase = true)) {
                val novoLucro = (projeto.lucroObtido ?: 0.0) + valor
                val atualizado = projeto.copy(lucroObtido = novoLucro)
                projetos = projetos.map { if (it.id == projetoId) atualizado else it }
            } else if (tipo.equals("DESPESA", ignoreCase = true)) {
                val invAtual = parseInvestimento(projeto)
                val novoInv = invAtual + valor
                val atualizado = projeto.copy(
                    investimento = "R$ ${String.format(java.util.Locale.forLanguageTag("pt-BR"), "%,.2f", novoInv)}"
                )
                projetos = projetos.map { if (it.id == projetoId) atualizado else it }
            }
            recalcularDashboardResumo()
            val tipoNome = if (tipo.equals("RECEITA", ignoreCase = true)) "Receita" else "Despesa"
            adicionarNotificacaoLocal("$tipoNome de R$ ${String.format(java.util.Locale.forLanguageTag("pt-BR"), "%,.2f", valor)} registrada no projeto \"${projetoTitulo}\"")
        }

        // Chamar API
        viewModelScope.launch {
            try {
                val response = if (tipo.equals("RECEITA", ignoreCase = true)) {
                    ApiClient.apiService.addReceita(nova)
                } else {
                    ApiClient.apiService.addDespesa(nova)
                }
                if (response.isSuccessful) {
                    fetchTransacoes()
                    fetchProjetos()
                }
            } catch (e: Exception) {
                Log.e("API", "Erro ao registrar transação na API", e)
            }
        }
    }

    fun registrarReceita(
        projetoId: String,
        projetoTitulo: String,
        descricao: String,
        valor: Double,
        categoria: String = "Receita Obtida",
        responsavel: String = "Gestor"
    ) {
        registrarTransacao(projetoId, projetoTitulo, "RECEITA", descricao, valor, categoria, responsavel)
    }

    fun registrarDespesa(
        projetoId: String,
        projetoTitulo: String,
        descricao: String,
        valor: Double,
        categoria: String = "Despesa Operacional",
        responsavel: String = "Gestor"
    ) {
        registrarTransacao(projetoId, projetoTitulo, "DESPESA", descricao, valor, categoria, responsavel)
    }

    fun excluirTransacao(id: String) {
        val trans = transacoesFinanceiras.find { it.id == id }
        transacoesFinanceiras = transacoesFinanceiras.filter { it.id != id }

        // Reverter efeito no projeto se for receita ou despesa
        if (trans != null && trans.projetoId != null) {
            val projeto = projetos.find { it.id == trans.projetoId }
            if (projeto != null) {
                if (trans.tipo.equals("RECEITA", ignoreCase = true) && projeto.lucroObtido != null) {
                    val novoLucro = maxOf(0.0, projeto.lucroObtido - trans.valor)
                    projetos = projetos.map { if (it.id == trans.projetoId) it.copy(lucroObtido = novoLucro) else it }
                } else if (trans.tipo.equals("DESPESA", ignoreCase = true)) {
                    val invAtual = parseInvestimento(projeto)
                    val novoInv = maxOf(0.0, invAtual - trans.valor)
                    val atualizado = projeto.copy(
                        investimento = "R$ ${String.format(java.util.Locale.forLanguageTag("pt-BR"), "%,.2f", novoInv)}"
                    )
                    projetos = projetos.map { if (it.id == trans.projetoId) atualizado else it }
                }
                recalcularDashboardResumo()
            }
        }

        viewModelScope.launch {
            try {
                ApiClient.apiService.deleteTransacao(id)
                fetchTransacoes()
                fetchProjetos()
            } catch (e: Exception) {
                Log.e("API", "Erro ao excluir transação", e)
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
    fun adicionarIdeia(ideia: Ideia, onComplete: ((Boolean) -> Unit)? = null) {
        val payload = ideia.copy(
            id = if (ideia.id.isNullOrBlank() || ideia.id.startsWith("local-")) null else ideia.id,
            area = if (ideia.area.isNullOrBlank()) "Geral" else ideia.area
        )
        val novaIdeia = if (ideia.id.isNullOrBlank()) ideia.copy(id = "local-${System.currentTimeMillis()}") else ideia
        ideias = (listOf(novaIdeia) + ideias).sortedByDescending { it.votos }
        adicionarNotificacaoLocal("Nova ideia submetida: \"${ideia.titulo}\"")

        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.addIdeia(payload)
                if (response.isSuccessful && response.body() != null) {
                    val salva = response.body()!!
                    ideias = ideias.map { if (it.id == novaIdeia.id) salva else it }.sortedByDescending { it.votos }
                    fetchIdeias()
                    onComplete?.invoke(true)
                } else {
                    fetchIdeias()
                    onComplete?.invoke(true)
                }
            } catch (e: Exception) {
                Log.e("API", "Erro ao adicionar ideia na API (mantida localmente)", e)
                onComplete?.invoke(true)
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

    fun vincularIdeiaAoGrupo(ideiaId: String, groupId: String, onComplete: ((Boolean, String) -> Unit)? = null) {
        if (ideiaId.isBlank() || groupId.isBlank()) {
            onComplete?.invoke(false, "ID da ideia ou do grupo inválido.")
            return
        }
        val ideia = ideias.find { it.id == ideiaId }
        if (ideia == null) {
            onComplete?.invoke(false, "Ideia não encontrada.")
            return
        }

        val atualizada = ideia.copy(
            groupId = groupId,
            etapa = "Enviada para o Grupo"
        )

        // Optimistic UI update
        ideias = ideias.map { if (it.id == ideiaId) atualizada else it }
        adicionarNotificacaoLocal("Ideia \"${ideia.titulo}\" vinculada ao grupo de inovação!")

        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.updateIdeia(ideiaId, atualizada)
                if (response.isSuccessful) {
                    fetchIdeias()
                    onComplete?.invoke(true, "Ideia vinculada ao seu grupo com sucesso! 🚀")
                } else {
                    fetchIdeias()
                    onComplete?.invoke(true, "Ideia vinculada ao grupo!")
                }
            } catch (e: Exception) {
                Log.e("API", "Erro ao vincular ideia ao grupo", e)
                onComplete?.invoke(true, "Ideia vinculada localmente ao grupo.")
            }
        }
    }

    fun atualizarStatusIdeia(id: String, novoStatus: String, onFeedback: ((Boolean, String) -> Unit)? = null) {
        if (id.isBlank()) return
        val ideia = ideias.find { it.id == id }
        if (ideia == null) {
            onFeedback?.invoke(false, "Ideia não encontrada localmente")
            return
        }
        
        val statusColor = when {
            novoStatus.equals("Aprovada", ignoreCase = true) -> Color(0xFFDCFCE7).toArgb()
            novoStatus.equals("Recusada", ignoreCase = true) -> Color(0xFFFEE2E2).toArgb()
            else -> Color(0xFFFEF3C7).toArgb()
        }
        val statusTextColor = when {
            novoStatus.equals("Aprovada", ignoreCase = true) -> Color(0xFF16A34A).toArgb()
            novoStatus.equals("Recusada", ignoreCase = true) -> Color(0xFFEF4444).toArgb()
            else -> Color(0xFFD97706).toArgb()
        }
        val etapa = when {
            novoStatus.equals("Aprovada", ignoreCase = true) -> "Aprovada pelo gestor"
            novoStatus.equals("Recusada", ignoreCase = true) -> "Ideia arquivada"
            else -> "Em análise"
        }
        val progresso = if (novoStatus.equals("Aprovada", ignoreCase = true)) 1.0 else 0.4

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

        val msg = "A ideia \"${ideia.titulo}\" foi ${if (novoStatus.equals("Aprovada", ignoreCase = true)) "Aprovada ✅" else "Recusada ❌"}"
        adicionarNotificacaoLocal(msg)
        onFeedback?.invoke(true, msg)

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

    var votosRealizados by mutableStateOf<Map<String, Int>>(emptyMap())
        private set

    val maxVotosPermitidos = 5

    fun getVotosGastos(userId: String? = null): Int {
        val uid = userId?.takeIf { it.isNotBlank() } ?: "currentUser"
        return votosRealizados[uid] ?: 0
    }

    fun getVotosRestantes(userId: String? = null): Int {
        val gastos = getVotosGastos(userId)
        return maxOf(0, maxVotosPermitidos - gastos)
    }

    fun podeVotar(ideia: Ideia, userId: String? = null): Boolean {
        val status = ideia.status?.trim()?.uppercase() ?: ""
        val isFinalizada = status.contains("APROVAD") || status.contains("RECUSAD")
        if (isFinalizada) return false
        val gastos = getVotosGastos(userId)
        return gastos < maxVotosPermitidos
    }

    fun carregarVotosLocais(context: android.content.Context, userId: String?) {
        val uid = userId?.takeIf { it.isNotBlank() } ?: "currentUser"
        val prefs = context.getSharedPreferences("inovagab_votos", android.content.Context.MODE_PRIVATE)
        val gastos = prefs.getInt("votos_$uid", 0)
        val mapa = votosRealizados.toMutableMap()
        mapa[uid] = gastos
        votosRealizados = mapa
    }

    fun persistirVotoLocal(context: android.content.Context, userId: String?) {
        val uid = userId?.takeIf { it.isNotBlank() } ?: "currentUser"
        val prefs = context.getSharedPreferences("inovagab_votos", android.content.Context.MODE_PRIVATE)
        val atual = votosRealizados[uid] ?: 0
        prefs.edit().putInt("votos_$uid", atual).apply()
    }

    fun votarIdeia(id: String, userId: String? = null, onFeedback: ((String) -> Unit)? = null) {
        if (id.isBlank()) return
        val ideia = ideias.find { it.id == id } ?: return

        val status = ideia.status?.trim()?.uppercase() ?: ""
        if (status.contains("APROVAD") || status.contains("RECUSAD")) {
            val msg = "A ideia já está ${ideia.status} e não pode mais receber votos."
            adicionarNotificacaoLocal(msg)
            onFeedback?.invoke(msg)
            return
        }

        val uid = userId?.takeIf { it.isNotBlank() } ?: "currentUser"
        val votosAtuais = getVotosGastos(uid)
        if (votosAtuais >= maxVotosPermitidos) {
            val msg = "Você atingiu o limite de $maxVotosPermitidos votos individuais!"
            adicionarNotificacaoLocal(msg)
            onFeedback?.invoke(msg)
            return
        }

        // Incrementa contagem de votos do usuário
        val novoMapa = votosRealizados.toMutableMap()
        novoMapa[uid] = votosAtuais + 1
        votosRealizados = novoMapa

        // Optimistic UI Update - mais votadas sempre em cima
        val atualizada = ideia.copy(votos = ideia.votos + 1)
        val tempIdeias = ideias.toMutableList()
        val index = tempIdeias.indexOfFirst { it.id == id }
        if (index != -1) {
            tempIdeias[index] = atualizada
            ideias = tempIdeias.sortedByDescending { it.votos }
        }

        val restantes = maxVotosPermitidos - (votosAtuais + 1)
        val sucessoMsg = "Voto registrado na ideia \"${ideia.titulo}\"! (Restam $restantes voto(s))"
        adicionarNotificacaoLocal(sucessoMsg)
        onFeedback?.invoke(sucessoMsg)

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
