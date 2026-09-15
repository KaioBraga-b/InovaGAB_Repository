package br.com.fiap.api

import br.com.fiap.model.UserProfile
import br.com.fiap.viewmodel.Estrategia
import br.com.fiap.viewmodel.Ideia
import br.com.fiap.viewmodel.Projeto
import retrofit2.Response
import retrofit2.http.*
import com.google.gson.annotations.SerializedName

data class Area(
    val id: String? = null,
    val nome: String
)

data class Notificacao(
    val id: String? = null,
    val mensagem: String,
    val lida: Boolean = false,
    val dataCriacao: String? = null,
    val destinatarioRole: String,
    val ideiaId: String? = null,
    val tipo: String? = null
)

data class LoginRequest(
    val email: String,
    val password: String,
    val selectedProfile: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val nome: String,
    val sobrenome: String,
    val unidade: String,
    val role: String
)

data class AuthResponse(
    val token: String,
    val userId: String,
    val role: String,
    val nome: String?,
    val sobrenome: String?,
    val unidade: String?,
    val email: String?
)

interface InovaGabApi {
    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("/api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<Unit>

    // Projetos
    @GET("/api/inovacao/projetos")
    suspend fun getProjetos(): Response<List<Projeto>>

    @POST("/api/inovacao/projetos")
    suspend fun addProjeto(@Body projeto: Projeto): Response<Projeto>

    @PUT("/api/inovacao/projetos/{id}")
    suspend fun updateProjeto(@Path("id") id: String, @Body projeto: Projeto): Response<Projeto>

    @DELETE("/api/inovacao/projetos/{id}")
    suspend fun deleteProjeto(@Path("id") id: String): Response<Unit>

    // Estrategias
    @GET("/api/inovacao/estrategias")
    suspend fun getEstrategias(): Response<List<Estrategia>>

    @POST("/api/inovacao/estrategias")
    suspend fun addEstrategia(@Body estrategia: Estrategia): Response<Estrategia>

    @PUT("/api/inovacao/estrategias/{id}")
    suspend fun updateEstrategia(@Path("id") id: String, @Body estrategia: Estrategia): Response<Estrategia>

    @DELETE("/api/inovacao/estrategias/{id}")
    suspend fun deleteEstrategia(@Path("id") id: String): Response<Unit>

    // Ideias
    @GET("/api/inovacao/ideias")
    suspend fun getIdeias(): Response<List<Ideia>>

    @POST("/api/inovacao/ideias")
    suspend fun addIdeia(@Body ideia: Ideia): Response<Ideia>

    @PUT("/api/inovacao/ideias/{id}")
    suspend fun updateIdeia(@Path("id") id: String, @Body ideia: Ideia): Response<Ideia>

    @DELETE("/api/inovacao/ideias/{id}")
    suspend fun deleteIdeia(@Path("id") id: String): Response<Unit>

    @POST("/api/inovacao/ideias/{id}/votar")
    suspend fun votarIdeia(@Path("id") id: String): Response<Ideia>

    @POST("/api/inovacao/ideias/{id}/comentar")
    suspend fun comentarIdeia(@Path("id") id: String, @Body comentario: br.com.fiap.viewmodel.Comentario): Response<Ideia>

    // Areas
    @GET("/api/inovacao/areas")
    suspend fun getAreas(): Response<List<Area>>

    @POST("/api/inovacao/areas")
    suspend fun addArea(@Body area: Area): Response<Area>

    // Dashboard
    @GET("/api/inovacao/dashboard")
    suspend fun getDashboardResumo(): Response<br.com.fiap.viewmodel.DashboardResumoResponse>

    // Notificacoes
    @GET("/api/notificacoes/{role}")
    suspend fun getNotificacoes(@Path("role") role: String): Response<List<Notificacao>>

    @PUT("/api/notificacoes/{id}/lida")
    suspend fun marcarNotificacaoLida(@Path("id") id: String): Response<Notificacao>
}

