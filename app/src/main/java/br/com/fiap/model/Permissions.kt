package br.com.fiap.model

object Permissions {
    /**
     * Define as permissões de forma escalável.
     * GESTOR: Visualiza tudo, mas não gerencia estratégias.
     * LIDER: Responsável por gerenciar estratégias.
     * OPERADOR: Envia ideias e visualiza progresso.
     */

    fun canManageStrategies(role: String): Boolean {
        // Líder e Gestor gerenciam estratégias
        return role == UserProfile.LIDER.name || role == UserProfile.GESTOR.name
    }

    fun canEditStrategy(role: String): Boolean {
        // Líder e Gestor editam estratégias
        return role == UserProfile.LIDER.name || role == UserProfile.GESTOR.name
    }

    fun canDeleteStrategy(role: String): Boolean {
        // Líder e Gestor excluem estratégias
        return role == UserProfile.LIDER.name || role == UserProfile.GESTOR.name
    }

    fun canManageProjects(role: String): Boolean {
        return role == UserProfile.GESTOR.name
    }

    fun canSubmitIdea(role: String): Boolean {
        return true
    }
    
    fun canEditIdea(role: String): Boolean {
        return role == UserProfile.GESTOR.name
    }
}
