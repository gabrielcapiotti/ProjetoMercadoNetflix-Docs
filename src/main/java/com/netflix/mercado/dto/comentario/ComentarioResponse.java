package com.netflix.mercado.dto.comentario;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.netflix.mercado.entity.Comentario;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Resposta com informações de comentário")
public class ComentarioResponse {

    @Schema(description = "ID do comentário", example = "1")
    private Long id;

    @Schema(description = "Conteúdo do comentário", example = "Concordo totalmente com essa avaliação!")
    private String conteudo;

    @Schema(description = "ID do usuário que comentou")
    @JsonProperty("usuarioId")
    private Long usuarioId;

    @Schema(description = "Username de quem comentou", example = "joao.silva")
    private String usuarioUsername;

    @Schema(description = "Total de curtidas", example = "5")
    private Integer curtidas;

    @Schema(description = "ID do comentário pai (para respostas)", example = "1")
    @JsonProperty("comentarioPaiId")
    private Long comentarioPaiId;

    @Schema(description = "Data de criação")
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @Schema(description = "Data de atualização")
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;
    public ComentarioResponse() {
    }

    public ComentarioResponse(Long id, String conteudo, Long usuarioId, String usuarioUsername, Integer curtidas, Long comentarioPaiId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.conteudo = conteudo;
        this.usuarioId = usuarioId;
        this.usuarioUsername = usuarioUsername;
        this.curtidas = curtidas;
        this.comentarioPaiId = comentarioPaiId;
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

    public Integer getCurtidas() {
        return this.curtidas;
    }

    public void setCurtidas(Integer curtidas) {
        this.curtidas = curtidas;
    }

    public Long getComentarioPaiId() {
        return this.comentarioPaiId;
    }

    public void setComentarioPaiId(Long comentarioPaiId) {
        this.comentarioPaiId = comentarioPaiId;
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

    // Método estático para converter Entity em DTO
    public static ComentarioResponse from(Comentario comentario) {
        if (comentario == null) {
            return null;
        }
        
        ComentarioResponse response = new ComentarioResponse();
        response.setId(comentario.getId());
        response.setConteudo(comentario.getConteudo());
        response.setUsuarioId(comentario.getUser() != null ? comentario.getUser().getId() : null);
        response.setUsuarioUsername(comentario.getUser() != null ? comentario.getUser().getEmail() : null);
        response.setCurtidas(comentario.getCurtidas() != null ? comentario.getCurtidas().intValue() : 0);
        response.setComentarioPaiId(comentario.getComentarioPai() != null ? comentario.getComentarioPai().getId() : null);
        response.setCreatedAt(comentario.getCreatedAt());
        response.setUpdatedAt(comentario.getUpdatedAt());
        return response;
    }

}
