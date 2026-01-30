package com.netflix.mercado.dto.avaliacao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.netflix.mercado.dto.auth.UserResponse;
import com.netflix.mercado.dto.comentario.ComentarioDetailResponse;
import com.netflix.mercado.dto.mercado.MercadoResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Resposta detalhada com informações completas de avaliação")
public class AvaliacaoDetailResponse {

    @Schema(description = "ID da avaliação", example = "1")
    private Long id;

    @Schema(description = "Número de estrelas (1-5)", example = "4")
    private Integer estrelas;

    @Schema(description = "Comentário da avaliação", example = "Ótimo atendimento e produtos frescos!")
    private String comentario;

    @Schema(description = "Total de curtidas", example = "12")
    private Integer curtidas;

    @Schema(description = "Informações do usuário que avaliou")
    private UserResponse usuario;

    @Schema(description = "Informações básicas do mercado")
    private MercadoResponse mercado;

    @Schema(description = "Lista de comentários sobre a avaliação")
    private List<ComentarioDetailResponse> comentarios;

    @Schema(description = "Data de criação")
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @Schema(description = "Data de atualização")
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;
    public AvaliacaoDetailResponse() {
    }

    public AvaliacaoDetailResponse(Long id, Integer estrelas, String comentario, Integer curtidas, UserResponse usuario, MercadoResponse mercado, List<ComentarioDetailResponse> comentarios, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.estrelas = estrelas;
        this.comentario = comentario;
        this.curtidas = curtidas;
        this.usuario = usuario;
        this.mercado = mercado;
        this.comentarios = comentarios;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getEstrelas() {
        return this.estrelas;
    }

    public void setEstrelas(Integer estrelas) {
        this.estrelas = estrelas;
    }

    public String getComentario() {
        return this.comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Integer getCurtidas() {
        return this.curtidas;
    }

    public void setCurtidas(Integer curtidas) {
        this.curtidas = curtidas;
    }

    public UserResponse getUsuario() {
        return this.usuario;
    }

    public void setUsuario(UserResponse usuario) {
        this.usuario = usuario;
    }

    public MercadoResponse getMercado() {
        return this.mercado;
    }

    public void setMercado(MercadoResponse mercado) {
        this.mercado = mercado;
    }

    public List<ComentarioDetailResponse> getComentarios() {
        return this.comentarios;
    }

    public void setComentarios(List<ComentarioDetailResponse> comentarios) {
        this.comentarios = comentarios;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

}
