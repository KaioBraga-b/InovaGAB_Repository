package br.com.fiap.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

data class Projeto(
    val id: String = "",
    val titulo: String = "",
    var status: String = "",
    var statusColor: Int = Color(0xFFD97706).toArgb(),
    var statusBg: Int = Color(0xFFFEF3C7).toArgb(),
    val area: String = "",
    val periodo: String = "",
    var progresso: Double = 0.0,
    var progressoTexto: String = "",
    var etapaAtiva: Int = 0,
    val estMensal: String? = null
)

data class Estrategia(
    val id: String = "",
    val titulo: String = "",
    val descricao: String = "",
    var status: String = "",
    var statusColor: Int = Color(0xFFEFF6FF).toArgb(),
    var statusTextColor: Int = Color(0xFF2563EB).toArgb(),
    var progresso: Double = 0.0,
    val dataCriacao: String = ""
)

data class Ideia(
    val id: String = "",
    val titulo: String = "",
    val descricao: String = "",
    val autor: String = "",
    val area: String = "",
    val status: String = "Enviada",
    val statusColor: Int = Color(0xFFF3F4F6).toArgb(),
    val statusTextColor: Int = Color(0xFF6B7280).toArgb(),
    val tempo: String = "Agora",
    var progresso: Double = 0.1,
    val etapa: String = "Aguardando triagem",
    val destaque: String? = null,
    val userId: String = "",
    val votos: Int = 0,
    val impacto: String = "Baixo",
    val objetivo: String = "Melhoria",
    val prioridade: String = "Média",
    val prioridadeColor: Int = Color(0xFFD97706).toArgb(),
    val prioridadeBg: Int = Color(0xFFFEF3C7).toArgb()
)

class InovacaoViewModel : ViewModel() {
    private val db = Firebase.firestore

    // Usando mutableStateOf com delegação para garantir recomposição instantânea
    var projetos by mutableStateOf<List<Projeto>>(emptyList())
        private set
    
    var estrategias by mutableStateOf<List<Estrategia>>(emptyList())
        private set
    
    var ideias by mutableStateOf<List<Ideia>>(emptyList())
        private set

    init {
        fetchProjetos()
        fetchEstrategias()
        fetchIdeias()
    }

    private fun fetchProjetos() {
        // addSnapshotListener garante a sincronização em tempo real (Real-Time)
        db.collection("projetos").addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("Firestore", "Erro ao buscar projetos", e)
                return@addSnapshotListener
            }
            val list = snapshot?.documents?.mapNotNull { it.toObject(Projeto::class.java)?.copy(id = it.id) } ?: emptyList()
            projetos = list
            Log.d("Firestore", "Projetos sincronizados: ${list.size}")
        }
    }

    private fun fetchEstrategias() {
        db.collection("estrategias").addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("Firestore", "Erro ao buscar estrategias", e)
                return@addSnapshotListener
            }
            val list = snapshot?.documents?.mapNotNull { it.toObject(Estrategia::class.java)?.copy(id = it.id) } ?: emptyList()
            estrategias = list
            Log.d("Firestore", "Estrategias sincronizadas: ${list.size}")
        }
    }

    private fun fetchIdeias() {
        db.collection("ideias").addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("Firestore", "Erro ao buscar ideias", e)
                return@addSnapshotListener
            }
            val list = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(Ideia::class.java)?.copy(id = doc.id)
            } ?: emptyList()
            ideias = list
            Log.d("Firestore", "Ideias sincronizadas: ${list.size}")
        }
    }

    // Funções para manipulação de Projetos
    fun adicionarProjeto(projeto: Projeto) {
        db.collection("projetos").add(projeto)
            .addOnSuccessListener { Log.d("Firestore", "Projeto adicionado") }
            .addOnFailureListener { e -> Log.e("Firestore", "Erro ao adicionar projeto", e) }
    }

    fun atualizarProjeto(id: String, titulo: String, area: String, novoProgresso: Double, novaEtapa: Int) {
        if (id.isBlank()) return
        val projeto = projetos.find { it.id == id }
        if (projeto == null) {
            Log.e("Firestore", "Projeto não encontrado para ID: $id")
            return
        }
        val novasEtapas = listOf("Ideação", "Aprovação", "Execução", "Resultado")
        
        val updates = mutableMapOf<String, Any>(
            "titulo" to titulo,
            "area" to area,
            "progresso" to novoProgresso,
            "etapaAtiva" to novaEtapa,
            "status" to novasEtapas[novaEtapa],
            "progressoTexto" to "${(novoProgresso * 100).toInt()}% concluído"
        )
        
        updates["statusColor"] = when(novaEtapa) {
            0 -> Color(0xFFD97706).toArgb()
            1 -> Color(0xFF2563EB).toArgb()
            2 -> Color(0xFF8B5CF6).toArgb()
            3 -> Color(0xFF16A34A).toArgb()
            else -> projeto.statusColor
        }
        updates["statusBg"] = when(novaEtapa) {
            0 -> Color(0xFFFEF3C7).toArgb()
            1 -> Color(0xFFEFF6FF).toArgb()
            2 -> Color(0xFFF5F3FF).toArgb()
            3 -> Color(0xFFDCFCE7).toArgb()
            else -> projeto.statusBg
        }

        db.collection("projetos").document(id).update(updates)
            .addOnSuccessListener { Log.d("Firestore", "Projeto $id atualizado") }
            .addOnFailureListener { e -> Log.e("Firestore", "Erro ao atualizar projeto $id", e) }
    }

    fun excluirProjeto(id: String) {
        if (id.isBlank()) return
        db.collection("projetos").document(id).delete()
            .addOnSuccessListener { Log.d("Firestore", "Projeto $id excluído") }
            .addOnFailureListener { e -> Log.e("Firestore", "Erro ao excluir projeto $id", e) }
    }

    // Funções para manipulação de Estrategias
    fun adicionarEstrategia(estrategia: Estrategia) {
        db.collection("estrategias").add(estrategia)
            .addOnSuccessListener { Log.d("Firestore", "Estrategia adicionada") }
            .addOnFailureListener { e -> Log.e("Firestore", "Erro ao adicionar estrategia", e) }
    }

    fun atualizarEstrategia(id: String, titulo: String, descricao: String, novoProgresso: Double, novaEtapa: Int) {
        if (id.isBlank()) return
        val estrategia = estrategias.find { it.id == id }
        if (estrategia == null) {
            Log.e("Firestore", "Estrategia não encontrada para ID: $id")
            return
        }
        val nomesEtapas = listOf("Planejamento", "Em andamento", "Concluído")
        
        val updates = mutableMapOf<String, Any>(
            "titulo" to titulo,
            "descricao" to descricao,
            "progresso" to novoProgresso,
            "status" to nomesEtapas[novaEtapa]
        )
        
        updates["statusColor"] = when(novaEtapa) {
            0 -> Color(0xFFF3F4F6).toArgb()
            1 -> Color(0xFFEFF6FF).toArgb()
            2 -> Color(0xFFDCFCE7).toArgb()
            else -> estrategia.statusColor
        }
        updates["statusTextColor"] = when(novaEtapa) {
            0 -> Color(0xFF6B7280).toArgb()
            1 -> Color(0xFF2563EB).toArgb()
            2 -> Color(0xFF16A34A).toArgb()
            else -> estrategia.statusTextColor
        }

        db.collection("estrategias").document(id).update(updates)
            .addOnSuccessListener { Log.d("Firestore", "Estrategia $id atualizada") }
            .addOnFailureListener { e -> Log.e("Firestore", "Erro ao atualizar estrategia $id", e) }
    }

    fun excluirEstrategia(id: String) {
        if (id.isBlank()) return
        db.collection("estrategias").document(id).delete()
            .addOnSuccessListener { Log.d("Firestore", "Estrategia $id excluída") }
            .addOnFailureListener { e -> Log.e("Firestore", "Erro ao excluir estrategia $id", e) }
    }

    // Funções para manipulação de Ideias
    fun adicionarIdeia(ideia: Ideia) {
        db.collection("ideias").add(ideia)
            .addOnSuccessListener { Log.d("Firestore", "Ideia adicionada") }
            .addOnFailureListener { e -> Log.e("Firestore", "Erro ao adicionar ideia", e) }
    }

    fun atualizarIdeia(id: String, titulo: String, descricao: String, area: String) {
        if (id.isBlank()) return
        val updates = mapOf(
            "titulo" to titulo,
            "descricao" to descricao,
            "area" to area
        )
        db.collection("ideias").document(id).update(updates)
            .addOnSuccessListener { Log.d("Firestore", "Ideia $id atualizada") }
            .addOnFailureListener { e -> Log.e("Firestore", "Erro ao atualizar ideia $id", e) }
    }

    fun atualizarStatusIdeia(id: String, novoStatus: String) {
        if (id.isBlank()) {
            Log.e("Firestore", "Tentativa de atualizar ideia com ID vazio")
            return
        }
        
        val updates = mutableMapOf<String, Any>(
            "status" to novoStatus,
            "statusColor" to when(novoStatus) {
                "Aprovada" -> Color(0xFFDCFCE7).toArgb()
                "Recusada" -> Color(0xFFFEE2E2).toArgb()
                else -> Color(0xFFFEF3C7).toArgb()
            },
            "statusTextColor" to when(novoStatus) {
                "Aprovada" -> Color(0xFF16A34A).toArgb()
                "Recusada" -> Color(0xFFEF4444).toArgb()
                else -> Color(0xFFD97706).toArgb()
            },
            "etapa" to when(novoStatus) {
                "Aprovada" -> "Aprovada pelo gestor"
                "Recusada" -> "Ideia arquivada"
                else -> "Em análise"
            },
            "progresso" to if (novoStatus == "Aprovada") 1.0 else 0.4
        )
        
        db.collection("ideias").document(id).update(updates)
            .addOnSuccessListener { Log.d("Firestore", "Ideia $id atualizada para $novoStatus") }
            .addOnFailureListener { e -> Log.e("Firestore", "Erro ao atualizar ideia $id", e) }
    }

    fun excluirIdeia(id: String) {
        if (id.isBlank()) return
        db.collection("ideias").document(id).delete()
            .addOnSuccessListener { Log.d("Firestore", "Ideia $id excluída") }
            .addOnFailureListener { e -> Log.e("Firestore", "Erro ao excluir ideia $id", e) }
    }
}
