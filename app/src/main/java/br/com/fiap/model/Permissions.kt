package br.com.fiap.model

object Permissions {
    /**
     * Define as permissões de forma escalável.
     * GESTOR: Admin total.
     * LIDER: Pode gerenciar estratégias e ver projetos.
     * OPERADOR: Pode enviar ideias e ver estratégias/projetos (sem editar).
     */

    fun canManageStrategies(role: String): Boolean {
        return role == UserProfile.GESTOR.name || role == UserProfile.LIDER.name
    }

    fun canEditStrategy(role: String): Boolean {
        return role == UserProfile.GESTOR.name || role == UserProfile.LIDER.name
    }

    fun canManageProjects(role: String): Boolean {
        return role == UserProfile.GESTOR.name
    }

    fun canSubmitIdea(role: String): Boolean {
        return true // Todos podem enviar ideias
    }
    
    fun canEditIdea(role: String): Boolean {
        // Regra solicitada: Operador não pode alterar ideias (apenas enviar)
        return role == UserProfile.GESTOR.name
    }
}
