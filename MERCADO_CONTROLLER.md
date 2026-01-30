# MercadoController - REST API

> Controller REST profissional para gerenciamento de Mercados com Java 21 e Spring Boot 3.2

## MercadoController.java

```java
package com.netflix.mercados.controller;

import com.netflix.mercados.dto.response.ApiResponse;
import com.netflix.mercados.dto.response.PageResponse;
import com.netflix.mercados.dto.mercado.request.CreateMercadoRequest;
import com.netflix.mercados.dto.mercado.request.UpdateMercadoRequest;
import com.netflix.mercados.dto.mercado.response.MercadoResponse;
import com.netflix.mercados.dto.mercado.response.MercadoDetailResponse;
import com.netflix.mercados.dto.mercado.response.HorarioFuncionamentoResponse;
import com.netflix.mercados.dto.mercado.response.RatingStatsResponse;
import com.netflix.mercados.dto.horario.request.CreateHorarioRequest;
import com.netflix.mercados.service.MercadoService;
import com.netflix.mercados.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/mercados")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Mercados", description = "API REST para gerenciamento de Mercados")
public class MercadoController {

    private final MercadoService mercadoService;

    // ==================== CREATE ====================

    @PostMapping
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @Operation(summary = "Criar novo mercado", description = "Cria um novo mercado no sistema")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Mercado criado com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MercadoResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Não autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ApiResponse<MercadoResponse>> criarMercado(
            @Valid @RequestBody CreateMercadoRequest request,
            Authentication authentication) {
        log.info("Iniciando criação de novo mercado: {}", request.getNome());
        
        MercadoResponse mercado = mercadoService.criarMercado(request, authentication.getName());
        
        log.info("Mercado criado com sucesso. ID: {}", mercado.getId());
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Mercado criado com sucesso", mercado));
    }

    // ==================== READ ====================

    @GetMapping
    @PreAuthorize("permitAll()")
    @Operation(summary = "Listar mercados com paginação e filtros", 
               description = "Retorna lista de mercados com suporte a paginação, ordenação e filtros")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Lista de mercados retornada com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Parâmetros inválidos")
    })
    public ResponseEntity<ApiResponse<PageResponse<MercadoResponse>>> listarMercados(
            @Parameter(description = "Número da página (começando em 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Campo para ordenação") @RequestParam(defaultValue = "nome") String sortBy,
            @Parameter(description = "Direção de ordenação (ASC/DESC)") @RequestParam(defaultValue = "ASC") Sort.Direction direction,
            @Parameter(description = "Filtro por nome") @RequestParam(required = false) String nome,
            @Parameter(description = "Filtro por tipo de mercado") @RequestParam(required = false) String tipoMercado,
            @Parameter(description = "Filtro por estado") @RequestParam(required = false) String estado,
            @Parameter(description = "Filtro por status de aprovação") @RequestParam(required = false) Boolean aprovado) {
        
        log.debug("Listando mercados. Page: {}, Size: {}, SortBy: {}, Direction: {}", 
                  page, size, sortBy, direction);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<MercadoResponse> mercados = mercadoService.listarMercados(
            pageable, nome, tipoMercado, estado, aprovado
        );
        
        PageResponse<MercadoResponse> pageResponse = PageResponse.fromPage(mercados);
        return ResponseEntity.ok(ApiResponse.success("Mercados listados com sucesso", pageResponse));
    }

    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Obter detalhes de um mercado", 
               description = "Retorna informações completas de um mercado específico")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Detalhes do mercado retornados com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MercadoDetailResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Mercado não encontrado")
    })
    public ResponseEntity<ApiResponse<MercadoDetailResponse>> obterDetalhes(
            @Parameter(description = "ID do mercado") @PathVariable Long id) {
        
        log.debug("Obtendo detalhes do mercado ID: {}", id);
        MercadoDetailResponse mercado = mercadoService.obterDetalhes(id);
        
        return ResponseEntity.ok(ApiResponse.success("Detalhes do mercado obtidos com sucesso", mercado));
    }

    // ==================== UPDATE ====================

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @Operation(summary = "Atualizar mercado", 
               description = "Atualiza informações de um mercado existente. Apenas o proprietário ou admin pode atualizar")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Mercado atualizado com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MercadoResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acesso negado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Mercado não encontrado")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ApiResponse<MercadoResponse>> atualizarMercado(
            @Parameter(description = "ID do mercado") @PathVariable Long id,
            @Valid @RequestBody UpdateMercadoRequest request,
            Authentication authentication) {
        
        log.info("Atualizando mercado ID: {}", id);
        MercadoResponse mercado = mercadoService.atualizarMercado(id, request, authentication.getName());
        
        log.info("Mercado ID: {} atualizado com sucesso", id);
        return ResponseEntity.ok(ApiResponse.success("Mercado atualizado com sucesso", mercado));
    }

    // ==================== DELETE ====================

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @Operation(summary = "Deletar mercado (soft delete)", 
               description = "Realiza soft delete de um mercado. Apenas o proprietário ou admin pode deletar")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204",
            description = "Mercado deletado com sucesso"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acesso negado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Mercado não encontrado")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Void> deletarMercado(
            @Parameter(description = "ID do mercado") @PathVariable Long id,
            Authentication authentication) {
        
        log.info("Deletando mercado ID: {}", id);
        mercadoService.deletarMercado(id, authentication.getName());
        
        log.info("Mercado ID: {} deletado com sucesso", id);
        return ResponseEntity.noContent().build();
    }

    // ==================== APPROVAL ====================

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Aprovar mercado", 
               description = "Aprova um mercado para operação. Apenas administradores podem aprovar")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Mercado aprovado com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MercadoResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acesso negado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Mercado não encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Mercado já foi aprovado")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ApiResponse<MercadoResponse>> aprovarMercado(
            @Parameter(description = "ID do mercado") @PathVariable Long id,
            Authentication authentication) {
        
        log.info("Aprovando mercado ID: {} por usuário: {}", id, authentication.getName());
        MercadoResponse mercado = mercadoService.aprovarMercado(id, authentication.getName());
        
        log.info("Mercado ID: {} aprovado com sucesso", id);
        return ResponseEntity.ok(ApiResponse.success("Mercado aprovado com sucesso", mercado));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Rejeitar mercado", 
               description = "Rejeita um mercado. Apenas administradores podem rejeitar")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Mercado rejeitado com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MercadoResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acesso negado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Mercado não encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Mercado já foi rejeitado")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ApiResponse<MercadoResponse>> rejeitarMercado(
            @Parameter(description = "ID do mercado") @PathVariable Long id,
            @Parameter(description = "Motivo da rejeição") @RequestParam String motivo,
            Authentication authentication) {
        
        log.info("Rejeitando mercado ID: {} com motivo: {}", id, motivo);
        MercadoResponse mercado = mercadoService.rejeitarMercado(id, motivo, authentication.getName());
        
        log.info("Mercado ID: {} rejeitado com sucesso", id);
        return ResponseEntity.ok(ApiResponse.success("Mercado rejeitado com sucesso", mercado));
    }

    // ==================== LOCATION ====================

    @GetMapping("/nearby")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Buscar mercados próximos", 
               description = "Retorna lista de mercados dentro de um raio especificado (em km) da localização fornecida")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Mercados próximos encontrados",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Coordenadas inválidas")
    })
    public ResponseEntity<ApiResponse<List<MercadoResponse>>> buscarProximos(
            @Parameter(description = "Latitude do usuário") @RequestParam Double latitude,
            @Parameter(description = "Longitude do usuário") @RequestParam Double longitude,
            @Parameter(description = "Raio de busca em quilômetros") @RequestParam(defaultValue = "5.0") Double raioKm) {
        
        log.debug("Buscando mercados próximos. Lat: {}, Lon: {}, Raio: {}km", latitude, longitude, raioKm);
        
        if (latitude == null || longitude == null) {
            log.warn("Latitude ou longitude nulas");
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Latitude e longitude são obrigatórias"));
        }
        
        List<MercadoResponse> mercados = mercadoService.buscarProximos(latitude, longitude, raioKm);
        
        return ResponseEntity.ok(ApiResponse.success("Mercados próximos encontrados", mercados));
    }

    // ==================== FAVORITES ====================

    @PostMapping("/{id}/favorite")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Adicionar mercado aos favoritos", 
               description = "Adiciona um mercado à lista de favoritos do usuário autenticado")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Mercado adicionado aos favoritos com sucesso"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Não autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Mercado não encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Mercado já está nos favoritos")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ApiResponse<String>> adicionarFavorito(
            @Parameter(description = "ID do mercado") @PathVariable Long id,
            Authentication authentication) {
        
        log.info("Usuário {} adicionando mercado {} aos favoritos", authentication.getName(), id);
        mercadoService.adicionarFavorito(id, authentication.getName());
        
        return ResponseEntity.ok(ApiResponse.success("Mercado adicionado aos favoritos com sucesso", null));
    }

    @DeleteMapping("/{id}/favorite")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Remover mercado dos favoritos", 
               description = "Remove um mercado da lista de favoritos do usuário autenticado")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204",
            description = "Mercado removido dos favoritos com sucesso"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Não autenticado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Mercado não encontrado ou não está nos favoritos")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Void> removerFavorito(
            @Parameter(description = "ID do mercado") @PathVariable Long id,
            Authentication authentication) {
        
        log.info("Usuário {} removendo mercado {} dos favoritos", authentication.getName(), id);
        mercadoService.removerFavorito(id, authentication.getName());
        
        return ResponseEntity.noContent().build();
    }

    // ==================== HOURS ====================

    @GetMapping("/{id}/hours")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Obter horários de funcionamento", 
               description = "Retorna os horários de funcionamento de um mercado específico")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Horários encontrados",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Mercado não encontrado")
    })
    public ResponseEntity<ApiResponse<List<HorarioFuncionamentoResponse>>> obterHorarios(
            @Parameter(description = "ID do mercado") @PathVariable Long id) {
        
        log.debug("Obtendo horários de funcionamento do mercado ID: {}", id);
        List<HorarioFuncionamentoResponse> horarios = mercadoService.obterHorarios(id);
        
        return ResponseEntity.ok(ApiResponse.success("Horários obtidos com sucesso", horarios));
    }

    @PostMapping("/{id}/hours")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @Operation(summary = "Adicionar horário de funcionamento", 
               description = "Adiciona um novo horário de funcionamento para o mercado")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Horário adicionado com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = HorarioFuncionamentoResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acesso negado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Mercado não encontrado")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<ApiResponse<HorarioFuncionamentoResponse>> adicionarHorario(
            @Parameter(description = "ID do mercado") @PathVariable Long id,
            @Valid @RequestBody CreateHorarioRequest request,
            Authentication authentication) {
        
        log.info("Adicionando horário de funcionamento ao mercado ID: {}", id);
        HorarioFuncionamentoResponse horario = mercadoService.adicionarHorario(id, request, authentication.getName());
        
        log.info("Horário adicionado ao mercado ID: {} com sucesso", id);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Horário adicionado com sucesso", horario));
    }
}
```

## DTOs de Suporte

### MercadoResponse.java

```java
package com.netflix.mercados.dto.mercado.response;

import com.netflix.mercados.entity.Mercado;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MercadoResponse {

    private Long id;
    private String nome;
    private String descricao;
    private String telefone;
    private String email;
    private String cnpj;
    private String tipoMercado;
    private Double latitude;
    private Double longitude;
    private String logo;
    private String siteUrl;
    private Boolean aprovado;
    private Double ratingMedio;
    private Long totalAvaliacoes;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    public static MercadoResponse from(Mercado mercado) {
        return MercadoResponse.builder()
            .id(mercado.getId())
            .nome(mercado.getNome())
            .descricao(mercado.getDescricao())
            .telefone(mercado.getTelefone())
            .email(mercado.getEmail())
            .cnpj(mercado.getCnpj())
            .tipoMercado(mercado.getTipoMercado())
            .latitude(mercado.getLatitude())
            .longitude(mercado.getLongitude())
            .logo(mercado.getLogo())
            .siteUrl(mercado.getSiteUrl())
            .aprovado(mercado.getAprovado())
            .ratingMedio(mercado.getRatingMedio())
            .totalAvaliacoes(mercado.getTotalAvaliacoes())
            .criadoEm(mercado.getCreatedAt())
            .atualizadoEm(mercado.getUpdatedAt())
            .build();
    }
}
```

### MercadoDetailResponse.java

```java
package com.netflix.mercados.dto.mercado.response;

import com.netflix.mercados.entity.Mercado;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MercadoDetailResponse {

    private Long id;
    private String nome;
    private String descricao;
    private String telefone;
    private String email;
    private String cnpj;
    private String tipoMercado;
    
    // Endereço
    private String rua;
    private Integer numero;
    private String complemento;
    private String cep;
    private String cidade;
    private String estado;
    
    // Localização
    private Double latitude;
    private Double longitude;
    
    // Informações
    private String logo;
    private String siteUrl;
    private Boolean aprovado;
    private String motiveRejeicao;
    
    // Estatísticas
    private Double ratingMedio;
    private Long totalAvaliacoes;
    private Integer totalFavoritos;
    
    // Horários
    private List<HorarioFuncionamentoResponse> horarios;
    
    // Auditoria
    private String criadoPor;
    private LocalDateTime criadoEm;
    private String atualizadoPor;
    private LocalDateTime atualizadoEm;

    public static MercadoDetailResponse from(Mercado mercado) {
        return MercadoDetailResponse.builder()
            .id(mercado.getId())
            .nome(mercado.getNome())
            .descricao(mercado.getDescricao())
            .telefone(mercado.getTelefone())
            .email(mercado.getEmail())
            .cnpj(mercado.getCnpj())
            .tipoMercado(mercado.getTipoMercado())
            .rua(mercado.getRua())
            .numero(mercado.getNumero())
            .complemento(mercado.getComplemento())
            .cep(mercado.getCep())
            .cidade(mercado.getCidade())
            .estado(mercado.getEstado())
            .latitude(mercado.getLatitude())
            .longitude(mercado.getLongitude())
            .logo(mercado.getLogo())
            .siteUrl(mercado.getSiteUrl())
            .aprovado(mercado.getAprovado())
            .motiveRejeicao(mercado.getMotivoRejeicao())
            .ratingMedio(mercado.getRatingMedio())
            .totalAvaliacoes(mercado.getTotalAvaliacoes())
            .totalFavoritos(mercado.getTotalFavoritos())
            .criadoPor(mercado.getCreatedBy())
            .criadoEm(mercado.getCreatedAt())
            .atualizadoPor(mercado.getUpdatedBy())
            .atualizadoEm(mercado.getUpdatedAt())
            .build();
    }
}
```

### HorarioFuncionamentoResponse.java

```java
package com.netflix.mercados.dto.mercado.response;

import com.netflix.mercados.entity.HorarioFuncionamento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HorarioFuncionamentoResponse {

    private Long id;
    private String diaSemana; // MONDAY, TUESDAY, etc.
    private LocalTime horarioAbertura;
    private LocalTime horarioFechamento;
    private Boolean aberto;
    private String observacoes;

    public static HorarioFuncionamentoResponse from(HorarioFuncionamento horario) {
        return HorarioFuncionamentoResponse.builder()
            .id(horario.getId())
            .diaSemana(horario.getDiaSemana())
            .horarioAbertura(horario.getHorarioAbertura())
            .horarioFechamento(horario.getHorarioFechamento())
            .aberto(horario.getAberto())
            .observacoes(horario.getObservacoes())
            .build();
    }
}
```

### RatingStatsResponse.java

```java
package com.netflix.mercados.dto.mercado.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingStatsResponse {

    private Long mercadoId;
    private String mercadoNome;
    private Double ratingMedio;
    private Long totalAvaliacoes;
    private Long avaliacoes5Estrelas;
    private Long avaliacoes4Estrelas;
    private Long avaliacoes3Estrelas;
    private Long avaliacoes2Estrelas;
    private Long avaliacoes1Estrela;
    private Double percentualAprovacao;

    public void calcularPercentualAprovacao() {
        if (this.totalAvaliacoes == 0) {
            this.percentualAprovacao = 0.0;
            return;
        }
        long avaliacoesPositivas = this.avaliacoes5Estrelas + this.avaliacoes4Estrelas;
        this.percentualAprovacao = (double) avaliacoesPositivas / this.totalAvaliacoes * 100;
    }
}
```

## Configuração CORS

```java
package com.netflix.mercados.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("*")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .maxAge(3600)
            .allowCredentials(false);

        registry.addMapping("/api/v1/mercados/**")
            .allowedOrigins("*")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
            .allowedHeaders("*", "Authorization", "Content-Type")
            .maxAge(3600)
            .allowCredentials(false);
    }
}
```

## Documentação no application.yml

```yaml
springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    enabled: true
    operations-sorter: method
    tags-sorter: alpha
  
  swagger-ui:
    urls:
      - name: Mercados API
        url: /v3/api-docs?group=mercados
      - name: Avaliações API
        url: /v3/api-docs?group=avaliacoes

springdoc.packages-to-scan: com.netflix.mercados.controller
```
