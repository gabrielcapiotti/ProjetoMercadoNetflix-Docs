# DTOs, Services e Converters - Mercado e Avaliação

> Estrutura completa de DTOs, Services, Specifications e Converters para sistemas de Mercados e Avaliações usando Spring Boot 3.2 com Java 21.

## Índice
1. [DTOs de Mercado](#dtos-de-mercado)
2. [DTOs de Avaliação](#dtos-de-avaliação)
3. [DTOs de Horário](#dtos-de-horário)
4. [Services](#services)
5. [Specifications](#specifications)
6. [Converters](#converters)
7. [Entities de Suporte](#entities-de-suporte)

---

## DTOs de Mercado

### 1. CreateMercadoRequest

```java
package com.mercadonetflix.dtos.mercado.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMercadoRequest {

    @NotBlank(message = "Nome do mercado é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String nome;

    @NotBlank(message = "Descrição é obrigatória")
    @Size(min = 10, max = 500, message = "Descrição deve ter entre 10 e 500 caracteres")
    private String descricao;

    @NotBlank(message = "Telefone é obrigatório")
    @Pattern(regexp = "^\\d{10,11}$", message = "Telefone inválido")
    private String telefone;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "CEP é obrigatório")
    @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "CEP inválido")
    private String cep;

    @NotBlank(message = "Rua é obrigatória")
    private String rua;

    @NotNull(message = "Número é obrigatório")
    @Positive(message = "Número deve ser positivo")
    private Integer numero;

    @NotBlank(message = "Cidade é obrigatória")
    private String cidade;

    @NotBlank(message = "Estado é obrigatório")
    @Size(min = 2, max = 2, message = "Estado deve ter 2 caracteres")
    private String estado;

    private String complemento;

    @NotNull(message = "Latitude é obrigatória")
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private Double latitude;

    @NotNull(message = "Longitude é obrigatória")
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private Double longitude;

    @NotBlank(message = "CNPJ é obrigatório")
    @Pattern(regexp = "^\\d{14}$", message = "CNPJ inválido")
    private String cnpj;

    @NotNull(message = "Tipo de mercado é obrigatório")
    private String tipoMercado; // "SUPERMERCADO", "MERCEARIA", "AÇOUGUE"

    private String logo;

    private String siteUrl;
}
```

### 2. UpdateMercadoRequest

```java
package com.mercadonetflix.dtos.mercado.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMercadoRequest {

    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String nome;

    @Size(min = 10, max = 500, message = "Descrição deve ter entre 10 e 500 caracteres")
    private String descricao;

    @Pattern(regexp = "^\\d{10,11}$", message = "Telefone inválido")
    private String telefone;

    @Email(message = "Email inválido")
    private String email;

    @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "CEP inválido")
    private String cep;

    private String rua;

    @Positive(message = "Número deve ser positivo")
    private Integer numero;

    private String cidade;

    @Size(min = 2, max = 2, message = "Estado deve ter 2 caracteres")
    private String estado;

    private String complemento;

    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private Double latitude;

    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private Double longitude;

    private String logo;

    private String siteUrl;

    @Positive(message = "Tempo de funcionamento deve ser positivo")
    private Integer tempoFuncionamento;
}
```

### 3. MercadoResponse

```java
package com.mercadonetflix.dtos.mercado.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.math.BigDecimal;

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
    private String cidade;
    private String estado;
    private Double latitude;
    private Double longitude;
    private String tipoMercado;
    private BigDecimal avaliacaoMedia;
    private Long totalAvaliacoes;
    private Boolean ativo;
    private Boolean aprovado;
    private String logo;
    private LocalDateTime dataCriacao;
}
```

### 4. MercadoDetailResponse

```java
package com.mercadonetflix.dtos.mercado.response;

import com.mercadonetflix.dtos.horario.response.HorarioResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.math.BigDecimal;
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
    private String cep;
    private String rua;
    private Integer numero;
    private String complemento;
    private String cidade;
    private String estado;
    private Double latitude;
    private Double longitude;
    private String cnpj;
    private String tipoMercado;
    private String logo;
    private String siteUrl;
    private BigDecimal avaliacaoMedia;
    private Long totalAvaliacoes;
    private Integer tempoFuncionamento;
    private Boolean ativo;
    private Boolean aprovado;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private LocalDateTime dataAprovacao;

    @Builder.Default
    private List<HorarioResponse> horarios = List.of();

    private Integer distanciaKm;
}
```

### 5. MercadoSearchFilterRequest

```java
package com.mercadonetflix.dtos.mercado.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MercadoSearchFilterRequest {

    private String nome;
    private String cidade;
    private String estado;
    private String tipoMercado;
    private Boolean ativo;
    private Boolean aprovado;
    private Double avaliacaoMinima; // Média de avaliação mínima
    private Long totalAvaliacaoMinima;

    @Builder.Default
    private Integer page = 0;

    @Builder.Default
    private Integer size = 20;

    @Builder.Default
    private String sortBy = "dataCriacao";

    @Builder.Default
    private Sort.Direction direction = Sort.Direction.DESC;
}
```

### 6. MercadoNearbyRequest

```java
package com.mercadonetflix.dtos.mercado.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MercadoNearbyRequest {

    @NotNull(message = "Latitude é obrigatória")
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private Double latitude;

    @NotNull(message = "Longitude é obrigatória")
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private Double longitude;

    @NotNull(message = "Raio é obrigatório")
    @Positive(message = "Raio deve ser positivo")
    private Double raioKm;

    @Builder.Default
    private Integer page = 0;

    @Builder.Default
    private Integer size = 20;
}
```

---

## DTOs de Avaliação

### 1. CreateAvaliacaoRequest

```java
package com.mercadonetflix.dtos.avaliacao.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAvaliacaoRequest {

    @NotNull(message = "ID do mercado é obrigatório")
    @Positive(message = "ID do mercado deve ser positivo")
    private Long mercadoId;

    @NotNull(message = "Número de estrelas é obrigatório")
    @Min(value = 1, message = "Mínimo 1 estrela")
    @Max(value = 5, message = "Máximo 5 estrelas")
    private Integer estrelas;

    @NotBlank(message = "Comentário é obrigatório")
    @Size(min = 5, max = 500, message = "Comentário deve ter entre 5 e 500 caracteres")
    private String comentario;

    @Size(max = 5, message = "Máximo 5 imagens")
    private java.util.List<String> imagensUrls;

    @Builder.Default
    private Boolean anonimo = false;
}
```

### 2. UpdateAvaliacaoRequest

```java
package com.mercadonetflix.dtos.avaliacao.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAvaliacaoRequest {

    @Min(value = 1, message = "Mínimo 1 estrela")
    @Max(value = 5, message = "Máximo 5 estrelas")
    private Integer estrelas;

    @Size(min = 5, max = 500, message = "Comentário deve ter entre 5 e 500 caracteres")
    private String comentario;

    @Size(max = 5, message = "Máximo 5 imagens")
    private java.util.List<String> imagensUrls;
}
```

### 3. AvaliacaoResponse

```java
package com.mercadonetflix.dtos.avaliacao.response;

import com.mercadonetflix.dtos.mercado.response.MercadoResponse;
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
public class AvaliacaoResponse {

    private Long id;
    private Long mercadoId;
    private String usuarioNome;
    private Integer estrelas;
    private String comentario;
    private Integer totalAjudas;
    private Integer totalCriticas;
    private Boolean anonimo;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    @Builder.Default
    private List<String> imagensUrls = List.of();
}
```

### 4. AvaliacaoDetailResponse

```java
package com.mercadonetflix.dtos.avaliacao.response;

import com.mercadonetflix.dtos.mercado.response.MercadoResponse;
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
public class AvaliacaoDetailResponse {

    private Long id;
    private Long mercadoId;
    private MercadoResponse mercado;
    private Long usuarioId;
    private String usuarioNome;
    private String usuarioFoto;
    private Integer estrelas;
    private String comentario;
    private Integer totalAjudas;
    private Integer totalCriticas;
    private Boolean anonimo;
    private Boolean verificado; // Se a compra foi verificada
    private Integer tempoCompraEmDias;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    @Builder.Default
    private List<String> imagensUrls = List.of();
}
```

### 5. RatingStatsResponse

```java
package com.mercadonetflix.dtos.avaliacao.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingStatsResponse {

    private Long mercadoId;
    private BigDecimal mediaAvaliacoes;
    private Long totalAvaliacoes;
    private Integer percentualEstrela5;
    private Integer percentualEstrela4;
    private Integer percentualEstrela3;
    private Integer percentualEstrela2;
    private Integer percentualEstrela1;
    private Long totalEstrela5;
    private Long totalEstrela4;
    private Long totalEstrela3;
    private Long totalEstrela2;
    private Long totalEstrela1;
}
```

---

## DTOs de Horário

### 1. HorarioFuncionamentoRequest

```java
package com.mercadonetflix.dtos.horario.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HorarioFuncionamentoRequest {

    @NotNull(message = "Dia da semana é obrigatório")
    private String diaSemana; // "SEGUNDA", "TERCA", etc

    @NotNull(message = "Horário de abertura é obrigatório")
    private LocalTime horarioAbertura;

    @NotNull(message = "Horário de fechamento é obrigatório")
    private LocalTime horarioFechamento;

    @Builder.Default
    private Boolean aberto = true;

    private LocalTime horarioAberturaAlmoco;
    private LocalTime horarioFechamentoAlmoco;
}
```

### 2. HorarioResponse

```java
package com.mercadonetflix.dtos.horario.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HorarioResponse {

    private Long id;
    private String diaSemana;
    private LocalTime horarioAbertura;
    private LocalTime horarioFechamento;
    private Boolean aberto;
    private LocalTime horarioAberturaAlmoco;
    private LocalTime horarioFechamentoAlmoco;
    private String descricao; // Ex: "Aberto de 7h às 21h (intervalo de 12h-13h)"
}
```

---

## Services

### 1. MercadoService

```java
package com.mercadonetflix.services.mercado;

import com.mercadonetflix.dtos.mercado.request.CreateMercadoRequest;
import com.mercadonetflix.dtos.mercado.request.UpdateMercadoRequest;
import com.mercadonetflix.dtos.mercado.request.MercadoSearchFilterRequest;
import com.mercadonetflix.dtos.mercado.request.MercadoNearbyRequest;
import com.mercadonetflix.dtos.mercado.response.MercadoResponse;
import com.mercadonetflix.dtos.mercado.response.MercadoDetailResponse;
import com.mercadonetflix.entities.Mercado;
import com.mercadonetflix.repositories.MercadoRepository;
import com.mercadonetflix.repositories.specifications.MercadoSpecification;
import com.mercadonetflix.converters.MercadoConverter;
import com.mercadonetflix.exceptions.ResourceNotFoundException;
import com.mercadonetflix.exceptions.BadRequestException;
import com.mercadonetflix.utils.GeolocationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MercadoService {

    private final MercadoRepository mercadoRepository;
    private final MercadoConverter mercadoConverter;
    private final GeolocationUtils geolocationUtils;

    @Transactional
    public MercadoResponse criar(CreateMercadoRequest request) {
        log.info("Criando novo mercado: {}", request.getNome());

        validarDuplicacaoCnpj(request.getCnpj());

        Mercado mercado = mercadoConverter.toEntity(request);
        mercado.setAtivo(true);
        mercado.setAprovado(false);

        Mercado saved = mercadoRepository.save(mercado);
        log.info("Mercado criado com sucesso. ID: {}", saved.getId());

        return mercadoConverter.toResponse(saved);
    }

    @Transactional
    public MercadoResponse atualizar(Long id, UpdateMercadoRequest request) {
        log.info("Atualizando mercado ID: {}", id);

        Mercado mercado = buscarPorIdOuLancarExcecao(id);
        mercadoConverter.updateEntityFromRequest(request, mercado);

        Mercado updated = mercadoRepository.save(mercado);
        log.info("Mercado atualizado com sucesso. ID: {}", id);

        return mercadoConverter.toResponse(updated);
    }

    @Transactional
    public void deletar(Long id) {
        log.info("Deletando mercado ID: {}", id);

        Mercado mercado = buscarPorIdOuLancarExcecao(id);
        
        // Soft Delete
        mercado.setAtivo(false);
        mercadoRepository.save(mercado);
        
        log.info("Mercado deletado (soft delete). ID: {}", id);
    }

    @Transactional(readOnly = true)
    public MercadoResponse buscarPorId(Long id) {
        log.debug("Buscando mercado ID: {}", id);
        Mercado mercado = buscarPorIdOuLancarExcecao(id);
        return mercadoConverter.toResponse(mercado);
    }

    @Transactional(readOnly = true)
    public MercadoDetailResponse buscarDetalhePorId(Long id) {
        log.debug("Buscando detalhes do mercado ID: {}", id);
        Mercado mercado = buscarPorIdOuLancarExcecao(id);
        return mercadoConverter.toDetailResponse(mercado);
    }

    @Transactional(readOnly = true)
    public Page<MercadoResponse> buscarComFiltros(MercadoSearchFilterRequest filterRequest) {
        log.debug("Buscando mercados com filtros: {}", filterRequest);

        Pageable pageable = PageRequest.of(
            filterRequest.getPage(),
            filterRequest.getSize(),
            Sort.by(filterRequest.getDirection(), filterRequest.getSortBy())
        );

        Specification<Mercado> spec = MercadoSpecification.comFiltros(filterRequest);
        Page<Mercado> mercados = mercadoRepository.findAll(spec, pageable);

        return mercados.map(mercadoConverter::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<MercadoResponse> buscarProximos(MercadoNearbyRequest request) {
        log.debug("Buscando mercados próximos. Lat: {}, Lon: {}, Raio: {}km",
            request.getLatitude(), request.getLongitude(), request.getRaioKm());

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        
        // Buscar todos os mercados ativos e aprovados
        List<Mercado> mercados = mercadoRepository.findAllAtivoEAprovado();

        // Filtrar por distância usando Haversine
        List<MercadoResponse> proximosFiltrados = mercados.stream()
            .filter(m -> {
                double distancia = geolocationUtils.calcularDistanciaHaversine(
                    request.getLatitude(), request.getLongitude(),
                    m.getLatitude(), m.getLongitude()
                );
                return distancia <= request.getRaioKm();
            })
            .map(m -> {
                MercadoResponse response = mercadoConverter.toResponse(m);
                double distancia = geolocationUtils.calcularDistanciaHaversine(
                    request.getLatitude(), request.getLongitude(),
                    m.getLatitude(), m.getLongitude()
                );
                response.setDistanciaKm((int) Math.round(distancia));
                return response;
            })
            .sorted((m1, m2) -> m1.getDistanciaKm().compareTo(m2.getDistanciaKm()))
            .skip((long) request.getPage() * request.getSize())
            .limit(request.getSize())
            .collect(Collectors.toList());

        return new org.springframework.data.domain.PageImpl<>(
            proximosFiltrados,
            pageable,
            mercados.size()
        );
    }

    @Transactional(readOnly = true)
    public List<MercadoResponse> listarTodos() {
        log.debug("Listando todos os mercados ativos");
        return mercadoRepository.findAllAtivoEAprovado()
            .stream()
            .map(mercadoConverter::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public void aprovarMercado(Long id) {
        log.info("Aprovando mercado ID: {}", id);
        Mercado mercado = buscarPorIdOuLancarExcecao(id);
        
        mercado.setAprovado(true);
        mercado.setDataAprovacao(java.time.LocalDateTime.now());
        mercadoRepository.save(mercado);
        
        log.info("Mercado aprovado com sucesso. ID: {}", id);
    }

    @Transactional
    public void rejeitarMercado(Long id, String motivo) {
        log.info("Rejeitando mercado ID: {}. Motivo: {}", id, motivo);
        Mercado mercado = buscarPorIdOuLancarExcecao(id);
        
        mercado.setAprovado(false);
        mercadoRepository.save(mercado);
        
        log.info("Mercado rejeitado. ID: {}", id);
    }

    @Transactional(readOnly = true)
    public List<MercadoResponse> buscarPorCidade(String cidade) {
        log.debug("Buscando mercados pela cidade: {}", cidade);
        return mercadoRepository.findByCidadeAndAtivoAndAprovado(cidade, true, true)
            .stream()
            .map(mercadoConverter::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MercadoResponse> buscarPorEstado(String estado) {
        log.debug("Buscando mercados pelo estado: {}", estado);
        return mercadoRepository.findByEstadoAndAtivoAndAprovado(estado, true, true)
            .stream()
            .map(mercadoConverter::toResponse)
            .collect(Collectors.toList());
    }

    // Métodos auxiliares

    private Mercado buscarPorIdOuLancarExcecao(Long id) {
        return mercadoRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Mercado não encontrado. ID: {}", id);
                return new ResourceNotFoundException("Mercado não encontrado com ID: " + id);
            });
    }

    private void validarDuplicacaoCnpj(String cnpj) {
        if (mercadoRepository.existsByCnpj(cnpj)) {
            log.warn("Tentativa de criar mercado com CNPJ duplicado: {}", cnpj);
            throw new BadRequestException("Já existe um mercado registrado com este CNPJ");
        }
    }
}
```

### 2. AvaliacaoService

```java
package com.mercadonetflix.services.avaliacao;

import com.mercadonetflix.dtos.avaliacao.request.CreateAvaliacaoRequest;
import com.mercadonetflix.dtos.avaliacao.request.UpdateAvaliacaoRequest;
import com.mercadonetflix.dtos.avaliacao.response.AvaliacaoResponse;
import com.mercadonetflix.dtos.avaliacao.response.AvaliacaoDetailResponse;
import com.mercadonetflix.dtos.avaliacao.response.RatingStatsResponse;
import com.mercadonetflix.entities.Avaliacao;
import com.mercadonetflix.entities.Mercado;
import com.mercadonetflix.repositories.AvaliacaoRepository;
import com.mercadonetflix.repositories.MercadoRepository;
import com.mercadonetflix.repositories.specifications.AvaliacaoSpecification;
import com.mercadonetflix.converters.AvaliacaoConverter;
import com.mercadonetflix.exceptions.ResourceNotFoundException;
import com.mercadonetflix.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final MercadoRepository mercadoRepository;
    private final AvaliacaoConverter avaliacaoConverter;

    @Transactional
    public AvaliacaoResponse criar(CreateAvaliacaoRequest request, Long usuarioId) {
        log.info("Criando nova avaliação para mercado ID: {}", request.getMercadoId());

        // Validar se mercado existe
        Mercado mercado = mercadoRepository.findById(request.getMercadoId())
            .orElseThrow(() -> new ResourceNotFoundException("Mercado não encontrado"));

        // Validar duplicação
        validarDuplicacao(request.getMercadoId(), usuarioId);

        Avaliacao avaliacao = avaliacaoConverter.toEntity(request);
        avaliacao.setUsuarioId(usuarioId);
        avaliacao.setMercado(mercado);

        Avaliacao saved = avaliacaoRepository.save(avaliacao);
        
        // Atualizar avaliação média do mercado
        atualizarAvaliacaoMercado(request.getMercadoId());

        log.info("Avaliação criada com sucesso. ID: {}", saved.getId());
        return avaliacaoConverter.toResponse(saved);
    }

    @Transactional
    public AvaliacaoResponse atualizar(Long id, UpdateAvaliacaoRequest request) {
        log.info("Atualizando avaliação ID: {}", id);

        Avaliacao avaliacao = buscarPorIdOuLancarExcecao(id);
        avaliacaoConverter.updateEntityFromRequest(request, avaliacao);

        Avaliacao updated = avaliacaoRepository.save(avaliacao);
        
        // Atualizar avaliação média do mercado
        atualizarAvaliacaoMercado(avaliacao.getMercado().getId());

        log.info("Avaliação atualizada com sucesso. ID: {}", id);
        return avaliacaoConverter.toResponse(updated);
    }

    @Transactional
    public void deletar(Long id) {
        log.info("Deletando avaliação ID: {}", id);

        Avaliacao avaliacao = buscarPorIdOuLancarExcecao(id);
        Long mercadoId = avaliacao.getMercado().getId();

        // Soft Delete
        avaliacao.setAtivo(false);
        avaliacaoRepository.save(avaliacao);

        // Atualizar avaliação média do mercado
        atualizarAvaliacaoMercado(mercadoId);

        log.info("Avaliação deletada (soft delete). ID: {}", id);
    }

    @Transactional(readOnly = true)
    public AvaliacaoResponse buscarPorId(Long id) {
        log.debug("Buscando avaliação ID: {}", id);
        Avaliacao avaliacao = buscarPorIdOuLancarExcecao(id);
        return avaliacaoConverter.toResponse(avaliacao);
    }

    @Transactional(readOnly = true)
    public AvaliacaoDetailResponse buscarDetalhePorId(Long id) {
        log.debug("Buscando detalhes da avaliação ID: {}", id);
        Avaliacao avaliacao = buscarPorIdOuLancarExcecao(id);
        return avaliacaoConverter.toDetailResponse(avaliacao);
    }

    @Transactional(readOnly = true)
    public Page<AvaliacaoResponse> buscarPorMercado(Long mercadoId, Integer page, Integer size) {
        log.debug("Buscando avaliações do mercado ID: {}", mercadoId);

        validarMercadoExiste(mercadoId);

        Pageable pageable = PageRequest.of(
            page,
            size,
            Sort.by(Sort.Direction.DESC, "dataCriacao")
        );

        Page<Avaliacao> avaliacoes = avaliacaoRepository
            .findByMercadoIdAndAtivo(mercadoId, true, pageable);

        return avaliacoes.map(avaliacaoConverter::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<AvaliacaoResponse> buscarPorUsuario(Long usuarioId, Integer page, Integer size) {
        log.debug("Buscando avaliações do usuário ID: {}", usuarioId);

        Pageable pageable = PageRequest.of(
            page,
            size,
            Sort.by(Sort.Direction.DESC, "dataCriacao")
        );

        Page<Avaliacao> avaliacoes = avaliacaoRepository
            .findByUsuarioIdAndAtivo(usuarioId, true, pageable);

        return avaliacoes.map(avaliacaoConverter::toResponse);
    }

    @Transactional(readOnly = true)
    public RatingStatsResponse obterEstatisticas(Long mercadoId) {
        log.debug("Obtendo estatísticas de avaliação para mercado ID: {}", mercadoId);

        validarMercadoExiste(mercadoId);

        List<Avaliacao> avaliacoes = avaliacaoRepository
            .findByMercadoIdAndAtivo(mercadoId, true);

        if (avaliacoes.isEmpty()) {
            return RatingStatsResponse.builder()
                .mercadoId(mercadoId)
                .mediaAvaliacoes(BigDecimal.ZERO)
                .totalAvaliacoes(0L)
                .percentualEstrela5(0)
                .percentualEstrela4(0)
                .percentualEstrela3(0)
                .percentualEstrela2(0)
                .percentualEstrela1(0)
                .build();
        }

        long total = avaliacoes.size();
        long total5 = avaliacoes.stream().filter(a -> a.getEstrelas() == 5).count();
        long total4 = avaliacoes.stream().filter(a -> a.getEstrelas() == 4).count();
        long total3 = avaliacoes.stream().filter(a -> a.getEstrelas() == 3).count();
        long total2 = avaliacoes.stream().filter(a -> a.getEstrelas() == 2).count();
        long total1 = avaliacoes.stream().filter(a -> a.getEstrelas() == 1).count();

        BigDecimal media = avaliacoes.stream()
            .map(a -> BigDecimal.valueOf(a.getEstrelas()))
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);

        return RatingStatsResponse.builder()
            .mercadoId(mercadoId)
            .mediaAvaliacoes(media)
            .totalAvaliacoes(total)
            .totalEstrela5(total5)
            .percentualEstrela5((int) ((total5 * 100) / total))
            .totalEstrela4(total4)
            .percentualEstrela4((int) ((total4 * 100) / total))
            .totalEstrela3(total3)
            .percentualEstrela3((int) ((total3 * 100) / total))
            .totalEstrela2(total2)
            .percentualEstrela2((int) ((total2 * 100) / total))
            .totalEstrela1(total1)
            .percentualEstrela1((int) ((total1 * 100) / total))
            .build();
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularMedia(Long mercadoId) {
        log.debug("Calculando média de avaliação para mercado ID: {}", mercadoId);

        List<Avaliacao> avaliacoes = avaliacaoRepository
            .findByMercadoIdAndAtivo(mercadoId, true);

        if (avaliacoes.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return avaliacoes.stream()
            .map(a -> BigDecimal.valueOf(a.getEstrelas()))
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(BigDecimal.valueOf(avaliacoes.size()), 2, RoundingMode.HALF_UP);
    }

    // Métodos auxiliares

    private Avaliacao buscarPorIdOuLancarExcecao(Long id) {
        return avaliacaoRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Avaliação não encontrada. ID: {}", id);
                return new ResourceNotFoundException("Avaliação não encontrada com ID: " + id);
            });
    }

    private void validarDuplicacao(Long mercadoId, Long usuarioId) {
        if (avaliacaoRepository.existsByMercadoIdAndUsuarioId(mercadoId, usuarioId)) {
            log.warn("Usuário {} já avaliou o mercado {}", usuarioId, mercadoId);
            throw new BadRequestException("Você já avaliou este mercado");
        }
    }

    private void validarMercadoExiste(Long mercadoId) {
        if (!mercadoRepository.existsById(mercadoId)) {
            throw new ResourceNotFoundException("Mercado não encontrado com ID: " + mercadoId);
        }
    }

    private void atualizarAvaliacaoMercado(Long mercadoId) {
        Mercado mercado = mercadoRepository.findById(mercadoId)
            .orElseThrow(() -> new ResourceNotFoundException("Mercado não encontrado"));

        BigDecimal novaMedia = calcularMedia(mercadoId);
        Long totalAvaliacoes = avaliacaoRepository.countByMercadoIdAndAtivo(mercadoId, true);

        mercado.setAvaliacaoMedia(novaMedia);
        mercado.setTotalAvaliacoes(totalAvaliacoes);
        mercadoRepository.save(mercado);

        log.debug("Avaliação do mercado {} atualizada. Média: {}", mercadoId, novaMedia);
    }
}
```

---

## Specifications

### 1. MercadoSpecification

```java
package com.mercadonetflix.repositories.specifications;

import com.mercadonetflix.dtos.mercado.request.MercadoSearchFilterRequest;
import com.mercadonetflix.entities.Mercado;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class MercadoSpecification {

    public static Specification<Mercado> comFiltros(MercadoSearchFilterRequest filter) {
        return (root, query, criteriaBuilder) -> {
            var predicates = new java.util.ArrayList<>();

            // Nome (ILIKE)
            if (filter.getNome() != null && !filter.getNome().isBlank()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("nome")),
                    "%" + filter.getNome().toLowerCase() + "%"
                ));
            }

            // Cidade
            if (filter.getCidade() != null && !filter.getCidade().isBlank()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("cidade")),
                    "%" + filter.getCidade().toLowerCase() + "%"
                ));
            }

            // Estado
            if (filter.getEstado() != null && !filter.getEstado().isBlank()) {
                predicates.add(criteriaBuilder.equal(
                    criteriaBuilder.upper(root.get("estado")),
                    filter.getEstado().toUpperCase()
                ));
            }

            // Tipo de Mercado
            if (filter.getTipoMercado() != null && !filter.getTipoMercado().isBlank()) {
                predicates.add(criteriaBuilder.equal(
                    root.get("tipoMercado"),
                    filter.getTipoMercado()
                ));
            }

            // Status Ativo
            if (filter.getAtivo() != null) {
                predicates.add(criteriaBuilder.equal(
                    root.get("ativo"),
                    filter.getAtivo()
                ));
            } else {
                // Por padrão, buscar apenas ativos
                predicates.add(criteriaBuilder.equal(
                    root.get("ativo"),
                    true
                ));
            }

            // Status Aprovado
            if (filter.getAprovado() != null) {
                predicates.add(criteriaBuilder.equal(
                    root.get("aprovado"),
                    filter.getAprovado()
                ));
            }

            // Avaliação Mínima
            if (filter.getAvaliacaoMinima() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("avaliacaoMedia"),
                    filter.getAvaliacaoMinima()
                ));
            }

            // Total de Avaliações Mínimo
            if (filter.getTotalAvaliacaoMinima() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("totalAvaliacoes"),
                    filter.getTotalAvaliacaoMinima()
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new javax.persistence.criteria.Predicate[0]));
        };
    }

    public static Specification<Mercado> ativoEAprovado() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(
            criteriaBuilder.equal(root.get("ativo"), true),
            criteriaBuilder.equal(root.get("aprovado"), true)
        );
    }

    public static Specification<Mercado> porCidade(String cidade) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(
            criteriaBuilder.lower(root.get("cidade")),
            "%" + cidade.toLowerCase() + "%"
        );
    }
}
```

### 2. AvaliacaoSpecification

```java
package com.mercadonetflix.repositories.specifications;

import com.mercadonetflix.entities.Avaliacao;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class AvaliacaoSpecification {

    public static Specification<Avaliacao> porMercado(Long mercadoId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(
            root.get("mercado").get("id"),
            mercadoId
        );
    }

    public static Specification<Avaliacao> porUsuario(Long usuarioId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(
            root.get("usuarioId"),
            usuarioId
        );
    }

    public static Specification<Avaliacao> porEstrelas(Integer estrelas) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(
            root.get("estrelas"),
            estrelas
        );
    }

    public static Specification<Avaliacao> ativa() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(
            root.get("ativo"),
            true
        );
    }

    public static Specification<Avaliacao> comComentario() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isNotNull(
            root.get("comentario")
        );
    }

    public static Specification<Avaliacao> comImagens() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isNotNull(
            root.get("imagensUrls")
        );
    }
}
```

---

## Converters

### 1. MercadoConverter

```java
package com.mercadonetflix.converters;

import com.mercadonetflix.dtos.mercado.request.CreateMercadoRequest;
import com.mercadonetflix.dtos.mercado.request.UpdateMercadoRequest;
import com.mercadonetflix.dtos.mercado.response.MercadoResponse;
import com.mercadonetflix.dtos.mercado.response.MercadoDetailResponse;
import com.mercadonetflix.entities.Mercado;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MercadoConverter {

    private final HorarioConverter horarioConverter;

    public Mercado toEntity(CreateMercadoRequest request) {
        return Mercado.builder()
            .nome(request.getNome())
            .descricao(request.getDescricao())
            .telefone(request.getTelefone())
            .email(request.getEmail())
            .cep(request.getCep())
            .rua(request.getRua())
            .numero(request.getNumero())
            .complemento(request.getComplemento())
            .cidade(request.getCidade())
            .estado(request.getEstado())
            .latitude(request.getLatitude())
            .longitude(request.getLongitude())
            .cnpj(request.getCnpj())
            .tipoMercado(request.getTipoMercado())
            .logo(request.getLogo())
            .siteUrl(request.getSiteUrl())
            .build();
    }

    public void updateEntityFromRequest(UpdateMercadoRequest request, Mercado mercado) {
        if (request.getNome() != null) {
            mercado.setNome(request.getNome());
        }
        if (request.getDescricao() != null) {
            mercado.setDescricao(request.getDescricao());
        }
        if (request.getTelefone() != null) {
            mercado.setTelefone(request.getTelefone());
        }
        if (request.getEmail() != null) {
            mercado.setEmail(request.getEmail());
        }
        if (request.getCep() != null) {
            mercado.setCep(request.getCep());
        }
        if (request.getRua() != null) {
            mercado.setRua(request.getRua());
        }
        if (request.getNumero() != null) {
            mercado.setNumero(request.getNumero());
        }
        if (request.getComplemento() != null) {
            mercado.setComplemento(request.getComplemento());
        }
        if (request.getCidade() != null) {
            mercado.setCidade(request.getCidade());
        }
        if (request.getEstado() != null) {
            mercado.setEstado(request.getEstado());
        }
        if (request.getLatitude() != null) {
            mercado.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            mercado.setLongitude(request.getLongitude());
        }
        if (request.getLogo() != null) {
            mercado.setLogo(request.getLogo());
        }
        if (request.getSiteUrl() != null) {
            mercado.setSiteUrl(request.getSiteUrl());
        }
        if (request.getTempoFuncionamento() != null) {
            mercado.setTempoFuncionamento(request.getTempoFuncionamento());
        }
    }

    public MercadoResponse toResponse(Mercado mercado) {
        return MercadoResponse.builder()
            .id(mercado.getId())
            .nome(mercado.getNome())
            .descricao(mercado.getDescricao())
            .telefone(mercado.getTelefone())
            .email(mercado.getEmail())
            .cidade(mercado.getCidade())
            .estado(mercado.getEstado())
            .latitude(mercado.getLatitude())
            .longitude(mercado.getLongitude())
            .tipoMercado(mercado.getTipoMercado())
            .avaliacaoMedia(mercado.getAvaliacaoMedia())
            .totalAvaliacoes(mercado.getTotalAvaliacoes())
            .ativo(mercado.getAtivo())
            .aprovado(mercado.getAprovado())
            .logo(mercado.getLogo())
            .dataCriacao(mercado.getDataCriacao())
            .build();
    }

    public MercadoDetailResponse toDetailResponse(Mercado mercado) {
        return MercadoDetailResponse.builder()
            .id(mercado.getId())
            .nome(mercado.getNome())
            .descricao(mercado.getDescricao())
            .telefone(mercado.getTelefone())
            .email(mercado.getEmail())
            .cep(mercado.getCep())
            .rua(mercado.getRua())
            .numero(mercado.getNumero())
            .complemento(mercado.getComplemento())
            .cidade(mercado.getCidade())
            .estado(mercado.getEstado())
            .latitude(mercado.getLatitude())
            .longitude(mercado.getLongitude())
            .cnpj(mercado.getCnpj())
            .tipoMercado(mercado.getTipoMercado())
            .logo(mercado.getLogo())
            .siteUrl(mercado.getSiteUrl())
            .avaliacaoMedia(mercado.getAvaliacaoMedia())
            .totalAvaliacoes(mercado.getTotalAvaliacoes())
            .tempoFuncionamento(mercado.getTempoFuncionamento())
            .ativo(mercado.getAtivo())
            .aprovado(mercado.getAprovado())
            .dataCriacao(mercado.getDataCriacao())
            .dataAtualizacao(mercado.getDataAtualizacao())
            .dataAprovacao(mercado.getDataAprovacao())
            .horarios(mercado.getHorarios() != null ?
                mercado.getHorarios().stream()
                    .map(horarioConverter::toResponse)
                    .toList() : java.util.List.of())
            .build();
    }
}
```

### 2. AvaliacaoConverter

```java
package com.mercadonetflix.converters;

import com.mercadonetflix.dtos.avaliacao.request.CreateAvaliacaoRequest;
import com.mercadonetflix.dtos.avaliacao.request.UpdateAvaliacaoRequest;
import com.mercadonetflix.dtos.avaliacao.response.AvaliacaoResponse;
import com.mercadonetflix.dtos.avaliacao.response.AvaliacaoDetailResponse;
import com.mercadonetflix.entities.Avaliacao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AvaliacaoConverter {

    private final MercadoConverter mercadoConverter;

    public Avaliacao toEntity(CreateAvaliacaoRequest request) {
        return Avaliacao.builder()
            .estrelas(request.getEstrelas())
            .comentario(request.getComentario())
            .imagensUrls(request.getImagensUrls())
            .anonimo(request.getAnonimo())
            .build();
    }

    public void updateEntityFromRequest(UpdateAvaliacaoRequest request, Avaliacao avaliacao) {
        if (request.getEstrelas() != null) {
            avaliacao.setEstrelas(request.getEstrelas());
        }
        if (request.getComentario() != null) {
            avaliacao.setComentario(request.getComentario());
        }
        if (request.getImagensUrls() != null) {
            avaliacao.setImagensUrls(request.getImagensUrls());
        }
    }

    public AvaliacaoResponse toResponse(Avaliacao avaliacao) {
        return AvaliacaoResponse.builder()
            .id(avaliacao.getId())
            .mercadoId(avaliacao.getMercado().getId())
            .usuarioNome(avaliacao.getAnonimo() ? "Anônimo" : avaliacao.getUsuarioNome())
            .estrelas(avaliacao.getEstrelas())
            .comentario(avaliacao.getComentario())
            .totalAjudas(avaliacao.getTotalAjudas())
            .totalCriticas(avaliacao.getTotalCriticas())
            .anonimo(avaliacao.getAnonimo())
            .imagensUrls(avaliacao.getImagensUrls() != null ? 
                avaliacao.getImagensUrls() : java.util.List.of())
            .dataCriacao(avaliacao.getDataCriacao())
            .dataAtualizacao(avaliacao.getDataAtualizacao())
            .build();
    }

    public AvaliacaoDetailResponse toDetailResponse(Avaliacao avaliacao) {
        return AvaliacaoDetailResponse.builder()
            .id(avaliacao.getId())
            .mercadoId(avaliacao.getMercado().getId())
            .mercado(avaliacao.getMercado() != null ?
                mercadoConverter.toResponse(avaliacao.getMercado()) : null)
            .usuarioId(avaliacao.getUsuarioId())
            .usuarioNome(avaliacao.getAnonimo() ? "Anônimo" : avaliacao.getUsuarioNome())
            .usuarioFoto(avaliacao.getUsuarioFoto())
            .estrelas(avaliacao.getEstrelas())
            .comentario(avaliacao.getComentario())
            .totalAjudas(avaliacao.getTotalAjudas())
            .totalCriticas(avaliacao.getTotalCriticas())
            .anonimo(avaliacao.getAnonimo())
            .verificado(avaliacao.getVerificado())
            .imagensUrls(avaliacao.getImagensUrls() != null ? 
                avaliacao.getImagensUrls() : java.util.List.of())
            .dataCriacao(avaliacao.getDataCriacao())
            .dataAtualizacao(avaliacao.getDataAtualizacao())
            .build();
    }
}
```

### 3. HorarioConverter

```java
package com.mercadonetflix.converters;

import com.mercadonetflix.dtos.horario.request.HorarioFuncionamentoRequest;
import com.mercadonetflix.dtos.horario.response.HorarioResponse;
import com.mercadonetflix.entities.HorarioFuncionamento;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class HorarioConverter {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public HorarioFuncionamento toEntity(HorarioFuncionamentoRequest request) {
        return HorarioFuncionamento.builder()
            .diaSemana(request.getDiaSemana())
            .horarioAbertura(request.getHorarioAbertura())
            .horarioFechamento(request.getHorarioFechamento())
            .aberto(request.getAberto())
            .horarioAberturaAlmoco(request.getHorarioAberturaAlmoco())
            .horarioFechamentoAlmoco(request.getHorarioFechamentoAlmoco())
            .build();
    }

    public void updateEntityFromRequest(HorarioFuncionamentoRequest request, HorarioFuncionamento horario) {
        if (request.getHorarioAbertura() != null) {
            horario.setHorarioAbertura(request.getHorarioAbertura());
        }
        if (request.getHorarioFechamento() != null) {
            horario.setHorarioFechamento(request.getHorarioFechamento());
        }
        if (request.getAberto() != null) {
            horario.setAberto(request.getAberto());
        }
        if (request.getHorarioAberturaAlmoco() != null) {
            horario.setHorarioAberturaAlmoco(request.getHorarioAberturaAlmoco());
        }
        if (request.getHorarioFechamentoAlmoco() != null) {
            horario.setHorarioFechamentoAlmoco(request.getHorarioFechamentoAlmoco());
        }
    }

    public HorarioResponse toResponse(HorarioFuncionamento horario) {
        String descricao = montarDescricao(horario);

        return HorarioResponse.builder()
            .id(horario.getId())
            .diaSemana(horario.getDiaSemana())
            .horarioAbertura(horario.getHorarioAbertura())
            .horarioFechamento(horario.getHorarioFechamento())
            .aberto(horario.getAberto())
            .horarioAberturaAlmoco(horario.getHorarioAberturaAlmoco())
            .horarioFechamentoAlmoco(horario.getHorarioFechamentoAlmoco())
            .descricao(descricao)
            .build();
    }

    private String montarDescricao(HorarioFuncionamento horario) {
        if (Boolean.FALSE.equals(horario.getAberto())) {
            return "Fechado";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Aberto de ")
            .append(horario.getHorarioAbertura().format(FORMATTER))
            .append(" às ")
            .append(horario.getHorarioFechamento().format(FORMATTER));

        if (horario.getHorarioAberturaAlmoco() != null &&
            horario.getHorarioFechamentoAlmoco() != null) {
            sb.append(" (intervalo de ")
                .append(horario.getHorarioAberturaAlmoco().format(FORMATTER))
                .append("-")
                .append(horario.getHorarioFechamentoAlmoco().format(FORMATTER))
                .append(")");
        }

        return sb.toString();
    }
}
```

---

## Entities de Suporte

### 1. Mercado Entity

```java
package com.mercadonetflix.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "mercados")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Mercado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 500)
    private String descricao;

    @Column(nullable = false, length = 20)
    private String telefone;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 10)
    private String cep;

    @Column(nullable = false, length = 100)
    private String rua;

    @Column(nullable = false)
    private Integer numero;

    @Column(length = 100)
    private String complemento;

    @Column(nullable = false, length = 50)
    private String cidade;

    @Column(nullable = false, length = 2)
    private String estado;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false, unique = true, length = 14)
    private String cnpj;

    @Column(nullable = false, length = 50)
    private String tipoMercado;

    @Column(length = 500)
    private String logo;

    @Column(length = 200)
    private String siteUrl;

    @Column(precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal avaliacaoMedia = BigDecimal.ZERO;

    @Builder.Default
    private Long totalAvaliacoes = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean aprovado = false;

    private Integer tempoFuncionamento;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @UpdateTimestamp
    private LocalDateTime dataAtualizacao;

    private LocalDateTime dataAprovacao;

    @OneToMany(mappedBy = "mercado", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<HorarioFuncionamento> horarios = List.of();

    @OneToMany(mappedBy = "mercado", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Avaliacao> avaliacoes = List.of();
}
```

### 2. Avaliacao Entity

```java
package com.mercadonetflix.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "avaliacoes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mercado_id", nullable = false)
    private Mercado mercado;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(length = 100)
    private String usuarioNome;

    @Column(length = 500)
    private String usuarioFoto;

    @Column(nullable = false)
    private Integer estrelas;

    @Column(nullable = false, length = 500)
    private String comentario;

    @ElementCollection
    @CollectionTable(name = "avaliacao_imagens", joinColumns = @JoinColumn(name = "avaliacao_id"))
    @Column(name = "imagem_url", length = 500)
    @Builder.Default
    private List<String> imagensUrls = List.of();

    @Builder.Default
    private Integer totalAjudas = 0;

    @Builder.Default
    private Integer totalCriticas = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean anonimo = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean verificado = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @UpdateTimestamp
    private LocalDateTime dataAtualizacao;
}
```

### 3. HorarioFuncionamento Entity

```java
package com.mercadonetflix.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Table(name = "horarios_funcionamento")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HorarioFuncionamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mercado_id", nullable = false)
    private Mercado mercado;

    @Column(nullable = false, length = 20)
    private String diaSemana;

    @Column(nullable = false)
    private LocalTime horarioAbertura;

    @Column(nullable = false)
    private LocalTime horarioFechamento;

    @Column(nullable = false)
    @Builder.Default
    private Boolean aberto = true;

    private LocalTime horarioAberturaAlmoco;

    private LocalTime horarioFechamentoAlmoco;
}
```

---

## Repositories

### MercadoRepository

```java
package com.mercadonetflix.repositories;

import com.mercadonetflix.entities.Mercado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MercadoRepository extends JpaRepository<Mercado, Long>, JpaSpecificationExecutor<Mercado> {

    boolean existsByCnpj(String cnpj);

    @Query("SELECT m FROM Mercado m WHERE m.ativo = true AND m.aprovado = true")
    List<Mercado> findAllAtivoEAprovado();

    List<Mercado> findByCidadeAndAtivoAndAprovado(String cidade, Boolean ativo, Boolean aprovado);

    List<Mercado> findByEstadoAndAtivoAndAprovado(String estado, Boolean ativo, Boolean aprovado);

    @Query("SELECT m FROM Mercado m WHERE m.ativo = true AND m.aprovado = false")
    List<Mercado> findPendingApproval();

    @Query("SELECT m FROM Mercado m WHERE LOWER(m.nome) LIKE LOWER(CONCAT('%', :nome, '%')) " +
           "AND m.ativo = true AND m.aprovado = true")
    List<Mercado> searchByNome(@Param("nome") String nome);
}
```

### AvaliacaoRepository

```java
package com.mercadonetflix.repositories;

import com.mercadonetflix.entities.Avaliacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long>, JpaSpecificationExecutor<Avaliacao> {

    boolean existsByMercadoIdAndUsuarioId(Long mercadoId, Long usuarioId);

    Page<Avaliacao> findByMercadoIdAndAtivo(Long mercadoId, Boolean ativo, Pageable pageable);

    List<Avaliacao> findByMercadoIdAndAtivo(Long mercadoId, Boolean ativo);

    Page<Avaliacao> findByUsuarioIdAndAtivo(Long usuarioId, Boolean ativo, Pageable pageable);

    Long countByMercadoIdAndAtivo(Long mercadoId, Boolean ativo);

    @Query("SELECT a FROM Avaliacao a WHERE a.mercado.id = :mercadoId AND a.ativo = true " +
           "AND a.estrelas >= :minEstrelas")
    List<Avaliacao> findAvaliacoesComMinimoEstrelas(@Param("mercadoId") Long mercadoId,
                                                     @Param("minEstrelas") Integer minEstrelas);

    @Query("SELECT a FROM Avaliacao a WHERE a.mercado.id = :mercadoId AND a.ativo = true " +
           "ORDER BY a.totalAjudas DESC, a.dataCriacao DESC LIMIT 10")
    List<Avaliacao> findTopAvaliacoes(@Param("mercadoId") Long mercadoId);
}
```

---

## Utilitário: GeolocationUtils

```java
package com.mercadonetflix.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class GeolocationUtils {

    private static final double RAIO_TERRA_KM = 6371.0;

    /**
     * Calcula a distância entre dois pontos usando a fórmula de Haversine
     * @param lat1 Latitude do primeiro ponto
     * @param lon1 Longitude do primeiro ponto
     * @param lat2 Latitude do segundo ponto
     * @param lon2 Longitude do segundo ponto
     * @return Distância em quilômetros
     */
    public static double calcularDistanciaHaversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.asin(Math.sqrt(a));

        return RAIO_TERRA_KM * c;
    }

    /**
     * Valida coordenadas geográficas
     */
    public static boolean validarCoordenadas(Double latitude, Double longitude) {
        return latitude != null && longitude != null &&
               latitude >= -90 && latitude <= 90 &&
               longitude >= -180 && longitude <= 180;
    }
}
```

---

## Exceções Customizadas

```java
package com.mercadonetflix.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

```java
package com.mercadonetflix.exceptions;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

---

## Dependências pom.xml

Adicione ao seu `pom.xml`:

```xml
<!-- Spring Boot Web -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Spring Data JPA -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- Bean Validation -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<!-- Lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>

<!-- Database (escolha uma) -->
<!-- PostgreSQL -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- ou H2 (para testes) -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

---

## Boas Práticas Implementadas ✅

✅ **Validações com Bean Validation** - Todas as DTOs usam anotações de validação  
✅ **Transações com @Transactional** - Métodos críticos protegidos com transações  
✅ **Logging com @Slf4j** - Rastreamento completo das operações  
✅ **Geolocalização com Haversine** - Cálculo de distância preciso  
✅ **Soft Delete** - Dados nunca são realmente deletados  
✅ **Paginação e Ordenação** - Suporte completo a Page e Sort  
✅ **Specifications para Filtros** - Queries dinâmicas e reutilizáveis  
✅ **Converters Dedicados** - Separação clara entre entidades e DTOs  
✅ **Tratamento de Exceções** - Exceções customizadas específicas  
✅ **Lazy Loading** - Relacionamentos otimizados com FetchType.LAZY  

