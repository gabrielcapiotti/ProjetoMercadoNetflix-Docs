package com.netflix.mercado.dto.comentario;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.netflix.mercado.dto.auth.UserResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Resposta detalhada com informações completas de comentário")
public class ComentarioDetailResponse {

    @Schema(description = "ID do comentário", example = "1")
    private Long id;

    @Schema(description = "Conteúdo do comentário", example = "Concordo totalmente com essa avaliação!")
    private String conteudo;

    @Schema(description = "Total de curtidas", example = "5")
    private Integer curtidas;

    @Schema(description = "Informações do usuário que comentou")
    private UserResponse usuario;

    @Schema(description = "ID do comentário pai (para respostas)", example = "1")
    @JsonProperty("comentarioPaiId")
    private Long comentarioPaiId;

    @Schema(description = "Lista de respostas aninhadas")
    private List<ComentarioDetailResponse> respostas;

    @Schema(description = "Data de criação")
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @Schema(description = "Data de atualização")
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;
    public ComentarioDetailResponse() {
    }

    public ComentarioDetailResponse(Long id, String conteudo, Integer curtidas, UserResponse usuario, Long comentarioPaiId, List<ComentarioDetailResponse> respostas, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.conteudo = conteudo;
        this.curtidas = curtidas;
        this.usuario = usuario;
        this.comentarioPaiId = comentarioPaiId;
        this.respostas = respostas;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConteudo() {
        return this.conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
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

    public Long getComentarioPaiId() {
        return this.comentarioPaiId;
    }

    public void setComentarioPaiId(Long comentarioPaiId) {
        this.comentarioPaiId = comentarioPaiId;
    }

    public List<ComentarioDetailResponse> getRespostas() {
        return this.respostas;
    }

    public void setRespostas(List<ComentarioDetailResponse> respostas) {
        this.respostas = respostas;
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
