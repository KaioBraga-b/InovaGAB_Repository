package br.com.fiap.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel

data class Projeto(
    val titulo: String,
    var status: String,
    var statusColor: Color,
    var statusBg: Color,
    val area: String,
    val periodo: String,
    var progresso: Float,
    var progressoTexto: String,
    var etapaAtiva: Int,
    val estMensal: String? = null
)

data class Estrategia(
    val id: String,
    val titulo: String,
    val descricao: String,
    var status: String,
    var statusColor: Color,
    var statusTextColor: Color,
    var progresso: Float,
    val dataCriacao: String
)

class InovacaoViewModel : ViewModel() {
    // Listas globais compartilhadas entre as telas
    val projetos = mutableStateListOf(
        Projeto(
            titulo = "Otimização de Almoxarifado",
            status = "Em análise",
            statusColor = Color(0xFFD97706),
            statusBg = Color(0xFFFEF3C7),
            area = "Suprimentos",
            periodo = "Mai 2025 → Set 2025",
            progresso = 0.1f,
            progressoTexto = "10% concluído",
            etapaAtiva = 0,
            estMensal = "Est: R$ 5k/mês"
        ),
        Projeto(
            titulo = "Treinamento VR Motoristas",
            status = "Aprovado",
            statusColor = Color(0xFF16A34A),
            statusBg = Color(0xFFDCFCE7),
            area = "Treinamento",
            periodo = "Jun 2025 → Dez 2025",
            progresso = 0.0f,
            progressoTexto = "A iniciar",
            etapaAtiva = 1,
            estMensal = "Est: R$ 15k/mês"
        )
    )

    val estrategias = mutableStateListOf(
        Estrategia(
            id = "1",
            titulo = "Expansão de Malha Regional",
            descricao = "Aumentar a cobertura em 15% no interior do estado.",
            status = "Em andamento",
            statusColor = Color(0xFFEFF6FF),
            statusTextColor = Color(0xFF2563EB),
            progresso = 0.35f,
            dataCriacao = "Criada há 5 dias"
        ),
        Estrategia(
            id = "2",
            titulo = "Eficiência Energética 2025",
            descricao = "Reduzir consumo de diesel em 10% via telemetria avançada.",
            status = "Planejado",
            statusColor = Color(0xFFF3F4F6),
            statusTextColor = Color(0xFF6B7280),
            progresso = 0.05f,
            dataCriacao = "Criada ontem"
        )
    )

    // Funções para manipulação
    fun adicionarProjeto(projeto: Projeto) {
        projetos.add(0, projeto)
    }

    fun atualizarProjeto(titulo: String, novoProgresso: Float, novaEtapa: Int) {
        val index = projetos.indexOfFirst { it.titulo == titulo }
        if (index != -1) {
            val projeto = projetos[index]
            val novasEtapas = listOf("Ideação", "Aprovação", "Execução", "Resultado")
            
            projetos[index] = projeto.copy(
                progresso = novoProgresso,
                etapaAtiva = novaEtapa,
                status = novasEtapas[novaEtapa],
                progressoTexto = "${(novoProgresso * 100).toInt()}% concluído",
                statusColor = when(novaEtapa) {
                    0 -> Color(0xFFD97706)
                    1 -> Color(0xFF2563EB)
                    2 -> Color(0xFF8B5CF6)
                    3 -> Color(0xFF16A34A)
                    else -> projeto.statusColor
                },
                statusBg = when(novaEtapa) {
                    0 -> Color(0xFFFEF3C7)
                    1 -> Color(0xFFEFF6FF)
                    2 -> Color(0xFFF5F3FF)
                    3 -> Color(0xFFDCFCE7)
                    else -> projeto.statusBg
                }
            )
        }
    }

    fun adicionarEstrategia(estrategia: Estrategia) {
        estrategias.add(0, estrategia)
    }

    fun atualizarEstrategia(titulo: String, novoProgresso: Float, novaEtapa: Int) {
        val index = estrategias.indexOfFirst { it.titulo == titulo }
        if (index != -1) {
            val estrategia = estrategias[index]
            val nomesEtapas = listOf("Planejamento", "Em andamento", "Concluído")
            
            estrategias[index] = estrategia.copy(
                progresso = novoProgresso,
                status = nomesEtapas[novaEtapa],
                statusColor = when(novaEtapa) {
                    0 -> Color(0xFFF3F4F6)
                    1 -> Color(0xFFEFF6FF)
                    2 -> Color(0xFFDCFCE7)
                    else -> estrategia.statusColor
                },
                statusTextColor = when(novaEtapa) {
                    0 -> Color(0xFF6B7280)
                    1 -> Color(0xFF2563EB)
                    2 -> Color(0xFF16A34A)
                    else -> estrategia.statusTextColor
                }
            )
        }
    }
}
