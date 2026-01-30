package com.netflix.mercado.dto.mercado;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.netflix.mercado.dto.avaliacao.AvaliacaoResponse;
import com.netflix.mercado.dto.horario.HorarioResponse;
import com.netflix.mercado.dto.promocao.PromocaoResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Resposta detalhada com informações completas do mercado")
public class MercadoDetailResponse {

    @Schema(description = "ID do mercado", example = "1")
    private Long id;

    @Schema(description = "Nome do mercado", example = "Mercado Central")
    private String nome;

    @Schema(description = "Descrição do mercado", example = "Mercado com ótima variedade de produtos")
    private String descricao;

    @Schema(description = "CNPJ do mercado", example = "12.345.678/0001-90")
    private String cnpj;

    @Schema(description = "Email do mercado", example = "contato@mercadocentral.com.br")
    private String email;

    @Schema(description = "Telefone de contato", example = "(11) 99999-9999")
    private String telefone;

    @Schema(description = "Endereço completo", example = "Rua das Flores, 123, Centro")
    private String enderecoCompleto;

    @Schema(description = "Latitude", example = "-23.5505")
    private BigDecimal latitude;

    @Schema(description = "Longitude", example = "-46.6333")
    private BigDecimal longitude;

    @Schema(description = "URL da foto principal")
    @JsonProperty("fotoPrincipalUrl")
    private String fotoPrincipalUrl;

    @Schema(description = "Avaliação média (0-5)", example = "4.5")
    @JsonProperty("avaliacaoMedia")
    private BigDecimal avaliacaoMedia;

    @Schema(description = "Total de avaliações", example = "42")
    @JsonProperty("totalAvaliacoes")
    private Integer totalAvaliacoes;

    @Schema(description = "Lista de horários de funcionamento")
    private List<HorarioResponse> horarios;

    @Schema(description = "Lista de promoções ativas")
    private List<PromocaoResponse> promocoes;

    @Schema(description = "Lista de avaliações mais recentes")
    private List<AvaliacaoResponse> avaliacoes;

    @Schema(description = "Data de criação")
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @Schema(description = "Data de atualização")
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;
    public MercadoDetailResponse() {
    }

    public MercadoDetailResponse(Long id, String nome, String descricao, String cnpj, String email, String telefone, String enderecoCompleto, BigDecimal latitude, BigDecimal longitude, String fotoPrincipalUrl, BigDecimal avaliacaoMedia, Integer totalAvaliacoes, List<HorarioResponse> horarios, List<PromocaoResponse> promocoes, List<AvaliacaoResponse> avaliacoes, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.cnpj = cnpj;
        this.email = email;
        this.telefone = telefone;
        this.enderecoCompleto = enderecoCompleto;
        this.latitude = latitude;
        this.longitude = longitude;
        this.fotoPrincipalUrl = fotoPrincipalUrl;
        this.avaliacaoMedia = avaliacaoMedia;
        this.totalAvaliacoes = totalAvaliacoes;
        this.horarios = horarios;
        this.promocoes = promocoes;
        this.avaliacoes = avaliacoes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCnpj() {
        return this.cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return this.telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEnderecoCompleto() {
        return this.enderecoCompleto;
    }

    public void setEnderecoCompleto(String enderecoCompleto) {
        this.enderecoCompleto = enderecoCompleto;
    }

    public BigDecimal getLatitude() {
        return this.latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return this.longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public String getFotoPrincipalUrl() {
        return this.fotoPrincipalUrl;
    }

    public void setFotoPrincipalUrl(String fotoPrincipalUrl) {
        this.fotoPrincipalUrl = fotoPrincipalUrl;
    }

    public BigDecimal getAvaliacaoMedia() {
        return this.avaliacaoMedia;
    }

    public void setAvaliacaoMedia(BigDecimal avaliacaoMedia) {
        this.avaliacaoMedia = avaliacaoMedia;
    }

    public Integer getTotalAvaliacoes() {
        return this.totalAvaliacoes;
    }

    public void setTotalAvaliacoes(Integer totalAvaliacoes) {
        this.totalAvaliacoes = totalAvaliacoes;
    }

    public List<HorarioResponse> getHorarios() {
        return this.horarios;
    }

    public void setHorarios(List<HorarioResponse> horarios) {
        this.horarios = horarios;
    }

    public List<PromocaoResponse> getPromocoes() {
        return this.promocoes;
    }

    public void setPromocoes(List<PromocaoResponse> promocoes) {
        this.promocoes = promocoes;
    }

    public List<AvaliacaoResponse> getAvaliacoes() {
        return this.avaliacoes;
    }

    public void setAvaliacoes(List<AvaliacaoResponse> avaliacoes) {
        this.avaliacoes = avaliacoes;
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
