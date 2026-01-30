package com.netflix.mercado.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "mercados", indexes = {
        @Index(name = "idx_mercado_email", columnList = "email", unique = true),
        @Index(name = "idx_mercado_cnpj", columnList = "cnpj", unique = true),
        @Index(name = "idx_mercado_avaliacao", columnList = "avaliacao_media"),
        @Index(name = "idx_mercado_coordenadas", columnList = "latitude,longitude"),
        @Index(name = "idx_mercado_active", columnList = "active")
})
public class Mercado extends BaseEntity {

    @NotBlank(message = "O nome do mercado é obrigatório")
    @Size(min = 3, max = 150, message = "O nome deve ter entre 3 e 150 caracteres")
    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @NotBlank(message = "A descrição é obrigatória")
    @Size(min = 10, max = 1000, message = "A descrição deve ter entre 10 e 1000 caracteres")
    @Column(name = "descricao", nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Pattern(regexp = "\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}",
            message = "O CNPJ deve estar no formato XX.XXX.XXX/XXXX-XX")
    @Column(name = "cnpj", unique = true, length = 18)
    private String cnpj;

    @Email(message = "O email deve ser válido")
    @Column(name = "email", unique = true, length = 150)
    private String email;

    @Column(name = "telefone", length = 20)
    private String telefone;

    @NotBlank(message = "O endereço é obrigatório")
    @Column(name = "endereco", nullable = false, length = 255)
    private String endereco;

    @Column(name = "numero", length = 20)
    private String numero;

    @Column(name = "complemento", length = 255)
    private String complemento;

    @NotBlank(message = "O bairro é obrigatório")
    @Column(name = "bairro", nullable = false, length = 100)
    private String bairro;

    @NotBlank(message = "A cidade é obrigatória")
    @Column(name = "cidade", nullable = false, length = 100)
    private String cidade;

    @NotBlank(message = "O estado é obrigatório")
    @Column(name = "estado", nullable = false, length = 2)
    private String estado;

    @NotBlank(message = "O CEP é obrigatório")
    @Pattern(regexp = "\\d{5}-\\d{3}", message = "O CEP deve estar no formato XXXXX-XXX")
    @Column(name = "cep", nullable = false, length = 9)
    private String cep;

    @NotNull(message = "A latitude é obrigatória")
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    @Column(name = "latitude", nullable = false, precision = 10, scale = 8)
    private BigDecimal latitude;

    @NotNull(message = "A longitude é obrigatória")
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    @Column(name = "longitude", nullable = false, precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(name = "foto_principal_url", length = 500)
    private String fotoPrincipalUrl;

    @DecimalMin("0.0")
    @DecimalMax("5.0")
    @Column(name = "avaliacao_media", nullable = false, precision = 3, scale = 2)
    private BigDecimal avaliacaoMedia = BigDecimal.ZERO;

    @Column(name = "total_avaliacoes", nullable = false)
    private Long totalAvaliacoes = 0L;

    @OneToMany(mappedBy = "mercado", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Avaliacao> avaliacoes = new HashSet<>();

    @OneToMany(mappedBy = "mercado", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Favorito> favoritos = new HashSet<>();

    @OneToMany(mappedBy = "mercado", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Promocao> promocoes = new HashSet<>();

    @OneToMany(mappedBy = "mercado", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<HorarioFuncionamento> horariosFuncionamento = new HashSet<>();

    public void atualizarAvaliacaoMedia(BigDecimal novaAvaliacao) {
        if (this.totalAvaliacoes == 0) {
            this.avaliacaoMedia = novaAvaliacao;
        } else {
            BigDecimal soma = this.avaliacaoMedia.multiply(BigDecimal.valueOf(this.totalAvaliacoes));
            soma = soma.add(novaAvaliacao);
            this.avaliacaoMedia = soma.divide(BigDecimal.valueOf(this.totalAvaliacoes + 1), 2, RoundingMode.HALF_UP);
        }
        this.totalAvaliacoes++;
    }

    public double calcularDistancia(BigDecimal latitude2, BigDecimal longitude2) {
        double lat1 = this.latitude.doubleValue();
        double lon1 = this.longitude.doubleValue();
        double lat2 = latitude2.doubleValue();
        double lon2 = longitude2.doubleValue();

        double r = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return r * c;
    }
    public Mercado() {
    }

    public Mercado(String nome, String descricao, String cnpj, String email, String telefone, String endereco, String numero, String complemento, String bairro, String cidade, String estado, String cep, BigDecimal latitude, BigDecimal longitude, String fotoPrincipalUrl, BigDecimal avaliacaoMedia, Long totalAvaliacoes, Set<Avaliacao> avaliacoes, Set<Favorito> favoritos, Set<Promocao> promocoes, Set<HorarioFuncionamento> horariosFuncionamento) {
        this.nome = nome;
        this.descricao = descricao;
        this.cnpj = cnpj;
        this.email = email;
        this.telefone = telefone;
        this.endereco = endereco;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.cep = cep;
        this.latitude = latitude;
        this.longitude = longitude;
        this.fotoPrincipalUrl = fotoPrincipalUrl;
        this.avaliacaoMedia = avaliacaoMedia;
        this.totalAvaliacoes = totalAvaliacoes;
        this.avaliacoes = avaliacoes;
        this.favoritos = favoritos;
        this.promocoes = promocoes;
        this.horariosFuncionamento = horariosFuncionamento;
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

    public String getEndereco() {
        return this.endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getNumero() {
        return this.numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return this.complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return this.bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return this.cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return this.estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCep() {
        return this.cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
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

    public Long getTotalAvaliacoes() {
        return this.totalAvaliacoes;
    }

    public void setTotalAvaliacoes(Long totalAvaliacoes) {
        this.totalAvaliacoes = totalAvaliacoes;
    }

    public Set<Avaliacao> getAvaliacoes() {
        return this.avaliacoes;
    }

    public void setAvaliacoes(Set<Avaliacao> avaliacoes) {
        this.avaliacoes = avaliacoes;
    }

    public Set<Favorito> getFavoritos() {
        return this.favoritos;
    }

    public void setFavoritos(Set<Favorito> favoritos) {
        this.favoritos = favoritos;
    }

    public Set<Promocao> getPromocoes() {
        return this.promocoes;
    }

    public void setPromocoes(Set<Promocao> promocoes) {
        this.promocoes = promocoes;
    }

    public Set<HorarioFuncionamento> getHorariosFuncionamento() {
        return this.horariosFuncionamento;
    }

    public void setHorariosFuncionamento(Set<HorarioFuncionamento> horariosFuncionamento) {
        this.horariosFuncionamento = horariosFuncionamento;
    }

}
