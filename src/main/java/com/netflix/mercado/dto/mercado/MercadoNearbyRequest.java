package com.netflix.mercado.dto.mercado;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Requisição para buscar mercados próximos por localização")
public class MercadoNearbyRequest {

    @NotNull(message = "Latitude não pode ser nula")
    @DecimalMin(value = "-90.0", message = "Latitude deve estar entre -90 e 90")
    @DecimalMax(value = "90.0", message = "Latitude deve estar entre -90 e 90")
    @Schema(description = "Latitude da localização", example = "-23.5505")
    private BigDecimal latitude;

    @NotNull(message = "Longitude não pode ser nula")
    @DecimalMin(value = "-180.0", message = "Longitude deve estar entre -180 e 180")
    @DecimalMax(value = "180.0", message = "Longitude deve estar entre -180 e 180")
    @Schema(description = "Longitude da localização", example = "-46.6333")
    private BigDecimal longitude;

    @NotNull(message = "Raio não pode ser nulo")
    @Min(value = 1, message = "Raio deve ser no mínimo 1 km")
    @Schema(description = "Raio de busca em quilômetros", example = "5")
    private BigDecimal raio;

    @Min(value = 0, message = "Página deve ser no mínimo 0")
    @Schema(description = "Número da página (0-indexed)", example = "0")
    private Integer page;

    @Min(value = 1, message = "Tamanho deve ser no mínimo 1")
    @Schema(description = "Quantidade de resultados por página", example = "20")
    private Integer size;
    public MercadoNearbyRequest() {
    }

    public MercadoNearbyRequest(BigDecimal latitude, BigDecimal longitude, BigDecimal raio, Integer page, Integer size) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.raio = raio;
        this.page = page;
        this.size = size;
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

    public BigDecimal getRaio() {
        return this.raio;
    }

    public void setRaio(BigDecimal raio) {
        this.raio = raio;
    }

    public Integer getPage() {
        return this.page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return this.size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

}
