package br.com.fiap.model

object Permissions {
    /**
     * Define as permissões de forma escalável.
     * GESTOR: Visualiza tudo, mas não gerencia estratégias.
     * LIDER: Responsável por gerenciar estratégias.
     * OPERADOR: Envia ideias e visualiza progresso.
     */

    fun canManageStrategies(role: String): Boolean {
        // Apenas Líder cria estratégias
        return role == UserProfile.LIDER.name
    }

    fun canEditStrategy(role: String): Boolean {
        // Apenas Líder edita estratégias
        return role == UserProfile.LIDER.name
    }

    fun canDeleteStrategy(role: String): Boolean {
        // Apenas Líder exclui estratégias
        return role == UserProfile.LIDER.name
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
