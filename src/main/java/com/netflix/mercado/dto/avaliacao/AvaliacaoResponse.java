package com.netflix.mercado.dto.avaliacao;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Resposta com informações de avaliação")
public class AvaliacaoResponse {

    @Schema(description = "ID da avaliação", example = "1")
    private Long id;

    @Schema(description = "ID do usuário que avaliou")
    @JsonProperty("usuarioId")
    private Long usuarioId;

    @Schema(description = "Username de quem avaliou", example = "joao.silva")
    private String usuarioUsername;

    @Schema(description = "ID do mercado avaliado")
    @JsonProperty("mercadoId")
    private Long mercadoId;

    @Schema(description = "Nome do mercado avaliado", example = "Mercado Central")
    private String mercadoNome;

    @Schema(description = "Número de estrelas (1-5)", example = "4")
    private Integer estrelas;

    @Schema(description = "Comentário da avaliação", example = "Ótimo atendimento e produtos frescos!")
    private String comentario;

    @Schema(description = "Total de curtidas", example = "12")
    private Integer curtidas;

    @Schema(description = "Data de criação")
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @Schema(description = "Data de atualização")
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;
    public AvaliacaoResponse() {
    }

    public AvaliacaoResponse(Long id, Long usuarioId, String usuarioUsername, Long mercadoId, String mercadoNome, Integer estrelas, String comentario, Integer curtidas, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.usuarioUsername = usuarioUsername;
        this.mercadoId = mercadoId;
        this.mercadoNome = mercadoNome;
        this.estrelas = estrelas;
        this.comentario = comentario;
        this.curtidas = curtidas;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return this.usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsuarioUsername() {
        return this.usuarioUsername;
    }

    public void setUsuarioUsername(String usuarioUsername) {
        this.usuarioUsername = usuarioUsername;
    }

    public Long getMercadoId() {
        return this.mercadoId;
    }

    public void setMercadoId(Long mercadoId) {
        this.mercadoId = mercadoId;
    }

    public String getMercadoNome() {
        return this.mercadoNome;
    }

    public void setMercadoNome(String mercadoNome) {
        this.mercadoNome = mercadoNome;
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

    /**
     * Método factory para criar AvaliacaoResponse a partir de Avaliacao
     */
    public static AvaliacaoResponse from(com.netflix.mercado.entity.Avaliacao avaliacao) {
        return new AvaliacaoResponse(
            avaliacao.getId(),
            avaliacao.getUser() != null ? avaliacao.getUser().getId() : null,
            avaliacao.getUser() != null ? avaliacao.getUser().getEmail() : null,
            avaliacao.getMercado() != null ? avaliacao.getMercado().getId() : null,
            avaliacao.getMercado() != null ? avaliacao.getMercado().getNome() : null,
            avaliacao.getEstrelas(),
            avaliacao.getComentario(),
            avaliacao.getUteis().intValue(),
            avaliacao.getCreatedAt(),
            avaliacao.getUpdatedAt()
        );
    }

}
