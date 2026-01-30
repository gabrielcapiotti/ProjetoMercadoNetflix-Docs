# Horários de Funcionamento - Netflix Mercados

> Documentação pronta para produção com Controllers, Services, DTOs, validações, Swagger e regras de negócio para Spring Boot 3.2 (Java 21).

## 📋 Índice
1. [Endpoints](#endpoints)
2. [Enums](#enums)
3. [DTOs](#dtos)
4. [Validator](#validator)
5. [Helpers](#helpers)
6. [Services](#services)
7. [MercadoStatusChecker](#mercadostatuschecker)
8. [Controllers](#controllers)
9. [Swagger/OpenAPI](#swaggeropenapi)
10. [Regras de Negócio](#regras-de-negócio)
11. [Paginação e Soft Delete](#paginação-e-soft-delete)

---

## Endpoints
```
POST   /api/v1/mercados/{mercadoId}/horarios        # Criar horário
GET    /api/v1/mercados/{mercadoId}/horarios        # Listar horários (paginação)
PUT    /api/v1/horarios/{id}                        # Atualizar horário
DELETE /api/v1/horarios/{id}                        # Soft delete
GET    /api/v1/mercados/{mercadoId}/horarios/aberto # Verificar se mercado está aberto agora
GET    /api/v1/mercados/{mercadoId}/status          # Status do mercado (aberto/fechado + próxima abertura)
```

---

## Enums

### DiaSemana
```java
package com.netflix.mercados.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum DiaSemana {
    DOMINGO(0, "Domingo"),
    SEGUNDA(1, "Segunda"),
    TERCA(2, "Terça"),
    QUARTA(3, "Quarta"),
    QUINTA(4, "Quinta"),
    SEXTA(5, "Sexta"),
    SABADO(6, "Sábado");

    private final int code;
    private final String label;

    DiaSemana(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public static DiaSemana fromCode(Integer code) {
        if (code == null) {
            throw new IllegalArgumentException("DiaSemana é obrigatório");
        }
        return Arrays.stream(values())
                .filter(d -> d.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("DiaSemana inválido: " + code));
    }
}
```

---

## DTOs

### CreateHorarioRequest
```java
package com.netflix.mercados.dto.horario.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para criação/atualização de horário de funcionamento")
public class CreateHorarioRequest {

    @NotNull(message = "Dia da semana é obrigatório")
    @Schema(description = "Dia da semana (0-6, onde 0=Domingo)", example = "1")
    private Integer diaSemana;

    @NotBlank(message = "Horário de abertura é obrigatório")
    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "Formato de hora inválido (HH:mm)")
    @Schema(example = "08:00")
    private String abertura;

    @NotBlank(message = "Horário de fechamento é obrigatório")
    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "Formato de hora inválido (HH:mm)")
    @Schema(example = "12:00")
    private String fechamento;
}
```

### HorarioResponse
```java
package com.netflix.mercados.dto.horario.response;

import com.netflix.mercados.enums.DiaSemana;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de resposta de horário")
public class HorarioResponse {

    private Long id;
    private Long mercadoId;
    private DiaSemana diaSemana;

    @Schema(example = "08:00")
    private String abertura;

    @Schema(example = "12:00")
    private String fechamento;

    private Boolean ativo;
    private LocalDateTime dataCriacao;
}
```

### HorarioDetailResponse
```java
package com.netflix.mercados.dto.horario.response;

import com.netflix.mercados.enums.DiaSemana;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO detalhado de horário")
public class HorarioDetailResponse {

    private Long id;
    private Long mercadoId;
    private DiaSemana diaSemana;
    private String abertura;
    private String fechamento;
    private Boolean ativo;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private LocalDateTime dataExclusao;
}
```

### MercadoStatusResponse
```java
package com.netflix.mercados.dto.horario.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO com status do mercado (aberto/fechado)")
public class MercadoStatusResponse {

    private Long mercadoId;

    @Schema(description = "Indica se o mercado está aberto agora", example = "true")
    private Boolean abertoAgora;

    @Schema(description = "Status textual", example = "ABERTO")
    private String status;

    @Schema(description = "Próxima abertura em formato HH:mm", example = "14:00")
    private String proximaAbertura;

    @Schema(description = "Mensagem amigável", example = "Aberto agora. Fecha às 18:00")
    private String mensagem;
}
```

---

## Validator

### HorarioValidator
```java
package com.netflix.mercados.validation;

import com.netflix.mercados.enums.DiaSemana;
import com.netflix.mercados.exception.BusinessException;
import com.netflix.mercados.model.HorarioFuncionamento;
import com.netflix.mercados.util.HorarioUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;

@Slf4j
@Component
public class HorarioValidator {

    public void validarIntervalo(String abertura, String fechamento) {
        LocalTime start = HorarioUtils.parseTime(abertura);
        LocalTime end = HorarioUtils.parseTime(fechamento);

        if (!start.isBefore(end)) {
            throw new BusinessException("Horário de abertura deve ser menor que o de fechamento");
        }
    }

    public void validarDia(Integer diaSemana) {
        try {
            DiaSemana.fromCode(diaSemana);
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("Dia da semana inválido. Use 0-6 (Domingo a Sábado)");
        }
    }

    public void validarSemSobreposicao(List<HorarioFuncionamento> existentes, String abertura, String fechamento, Integer dia) {
        LocalTime novoInicio = HorarioUtils.parseTime(abertura);
        LocalTime novoFim = HorarioUtils.parseTime(fechamento);

        boolean overlap = existentes.stream()
                .filter(h -> h.getDiaSemana().getCode() == dia)
                .filter(h -> h.getAtivo() != null && h.getAtivo())
                .anyMatch(h -> {
                    LocalTime inicio = h.getAbertura();
                    LocalTime fim = h.getFechamento();
                    return novoInicio.isBefore(fim) && novoFim.isAfter(inicio);
                });

        if (overlap) {
            throw new BusinessException("Conflito de horário: existe sobreposição no mesmo dia");
        }
    }
}
```

---

## Helpers

### HorarioUtils
```java
package com.netflix.mercados.util;

import com.netflix.mercados.enums.DiaSemana;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public final class HorarioUtils {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private HorarioUtils() {}

    public static LocalTime parseTime(String value) {
        return LocalTime.parse(value, TIME_FORMAT);
    }

    public static String formatTime(LocalTime time) {
        return time.format(TIME_FORMAT);
    }

    public static boolean isToday(DiaSemana diaSemana) {
        return getDayOfWeek(LocalDate.now()) == diaSemana;
    }

    public static DiaSemana getDayOfWeek(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return switch (day) {
            case SUNDAY -> DiaSemana.DOMINGO;
            case MONDAY -> DiaSemana.SEGUNDA;
            case TUESDAY -> DiaSemana.TERCA;
            case WEDNESDAY -> DiaSemana.QUARTA;
            case THURSDAY -> DiaSemana.QUINTA;
            case FRIDAY -> DiaSemana.SEXTA;
            case SATURDAY -> DiaSemana.SABADO;
        };
    }
}
```

---

## Services

### HorarioService
```java
package com.netflix.mercados.service;

import com.netflix.mercados.dto.horario.request.CreateHorarioRequest;
import com.netflix.mercados.dto.horario.response.HorarioDetailResponse;
import com.netflix.mercados.dto.horario.response.HorarioResponse;
import com.netflix.mercados.dto.horario.response.MercadoStatusResponse;
import com.netflix.mercados.enums.DiaSemana;
import com.netflix.mercados.exception.NotFoundException;
import com.netflix.mercados.model.HorarioFuncionamento;
import com.netflix.mercados.model.Mercado;
import com.netflix.mercados.repository.HorarioFuncionamentoRepository;
import com.netflix.mercados.repository.MercadoRepository;
import com.netflix.mercados.util.HorarioUtils;
import com.netflix.mercados.validation.HorarioValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HorarioService {

    private final HorarioFuncionamentoRepository horarioRepository;
    private final MercadoRepository mercadoRepository;
    private final HorarioValidator validator;

    @Transactional
    public HorarioResponse criar(Long mercadoId, CreateHorarioRequest request) {
        validator.validarDia(request.getDiaSemana());
        validator.validarIntervalo(request.getAbertura(), request.getFechamento());

        Mercado mercado = mercadoRepository.findByIdAndDeletedAtIsNull(mercadoId)
                .orElseThrow(() -> new NotFoundException("Mercado não encontrado"));

        List<HorarioFuncionamento> existentes = horarioRepository
                .findByMercadoIdAndDeletedAtIsNull(mercadoId);

        validator.validarSemSobreposicao(existentes, request.getAbertura(), request.getFechamento(), request.getDiaSemana());

        HorarioFuncionamento horario = HorarioFuncionamento.builder()
                .mercado(mercado)
                .diaSemana(DiaSemana.fromCode(request.getDiaSemana()))
                .abertura(HorarioUtils.parseTime(request.getAbertura()))
                .fechamento(HorarioUtils.parseTime(request.getFechamento()))
                .ativo(true)
                .build();

        horarioRepository.save(horario);
        log.info("Horário criado: mercadoId={}, dia={}, {}-{}", mercadoId, request.getDiaSemana(), request.getAbertura(), request.getFechamento());
        return toResponse(horario);
    }

    @Transactional(readOnly = true)
    public Page<HorarioResponse> listar(Long mercadoId, Pageable pageable) {
        return horarioRepository.findByMercadoIdAndDeletedAtIsNull(mercadoId, pageable)
                .map(this::toResponse);
    }

    @Transactional
    public HorarioDetailResponse atualizar(Long id, CreateHorarioRequest request) {
        HorarioFuncionamento horario = horarioRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Horário não encontrado"));

        validator.validarDia(request.getDiaSemana());
        validator.validarIntervalo(request.getAbertura(), request.getFechamento());

        List<HorarioFuncionamento> existentes = horarioRepository
                .findByMercadoIdAndDeletedAtIsNull(horario.getMercado().getId());

        validator.validarSemSobreposicao(
                existentes.stream().filter(h -> !h.getId().equals(id)).toList(),
                request.getAbertura(), request.getFechamento(), request.getDiaSemana()
        );

        horario.setDiaSemana(DiaSemana.fromCode(request.getDiaSemana()));
        horario.setAbertura(HorarioUtils.parseTime(request.getAbertura()));
        horario.setFechamento(HorarioUtils.parseTime(request.getFechamento()));

        horarioRepository.save(horario);
        log.info("Horário atualizado: id={}", id);
        return toDetailResponse(horario);
    }

    @Transactional
    public void deletar(Long id) {
        HorarioFuncionamento horario = horarioRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new NotFoundException("Horário não encontrado"));

        horario.setAtivo(false);
        horario.softDelete();
        horarioRepository.save(horario);
        log.info("Horário deletado (soft): id={} ", id);
    }

    @Transactional(readOnly = true)
    public boolean verificarAberto(Long mercadoId) {
        List<HorarioFuncionamento> horarios = horarioRepository.findByMercadoIdAndDeletedAtIsNull(mercadoId);
        if (horarios.isEmpty()) {
            return false;
        }

        DiaSemana hoje = HorarioUtils.getDayOfWeek(LocalDate.now());
        LocalTime agora = LocalTime.now();

        return horarios.stream()
                .filter(h -> h.getDiaSemana() == hoje)
                .filter(h -> Boolean.TRUE.equals(h.getAtivo()))
                .anyMatch(h -> !agora.isBefore(h.getAbertura()) && agora.isBefore(h.getFechamento()));
    }

    @Transactional(readOnly = true)
    public String gerarProximaAbertura(Long mercadoId) {
        List<HorarioFuncionamento> horarios = horarioRepository.findByMercadoIdAndDeletedAtIsNull(mercadoId);
        if (horarios.isEmpty()) {
            return null;
        }

        DiaSemana hoje = HorarioUtils.getDayOfWeek(LocalDate.now());
        LocalTime agora = LocalTime.now();

        // 1) Ainda abre hoje?
        var hojeProximo = horarios.stream()
                .filter(h -> h.getDiaSemana() == hoje)
                .filter(h -> Boolean.TRUE.equals(h.getAtivo()))
                .filter(h -> h.getAbertura().isAfter(agora))
                .min(Comparator.comparing(HorarioFuncionamento::getAbertura));

        if (hojeProximo.isPresent()) {
            return HorarioUtils.formatTime(hojeProximo.get().getAbertura());
        }

        // 2) Próximo dia com horários
        for (int i = 1; i <= 7; i++) {
            DiaSemana dia = HorarioUtils.getDayOfWeek(LocalDate.now().plusDays(i));
            var proximo = horarios.stream()
                    .filter(h -> h.getDiaSemana() == dia)
                    .filter(h -> Boolean.TRUE.equals(h.getAtivo()))
                    .min(Comparator.comparing(HorarioFuncionamento::getAbertura));
            if (proximo.isPresent()) {
                return HorarioUtils.formatTime(proximo.get().getAbertura());
            }
        }
        return null;
    }

    public MercadoStatusResponse status(Long mercadoId) {
        boolean aberto = verificarAberto(mercadoId);
        String proxima = gerarProximaAbertura(mercadoId);

        String status = aberto ? "ABERTO" : "FECHADO";
        String mensagem = aberto
                ? "Aberto agora"
                : (proxima == null ? "Fechado sem previsão" : "Fechado. Próxima abertura às " + proxima);

        return MercadoStatusResponse.builder()
                .mercadoId(mercadoId)
                .abertoAgora(aberto)
                .status(status)
                .proximaAbertura(proxima)
                .mensagem(mensagem)
                .build();
    }

    private HorarioResponse toResponse(HorarioFuncionamento h) {
        return HorarioResponse.builder()
                .id(h.getId())
                .mercadoId(h.getMercado().getId())
                .diaSemana(h.getDiaSemana())
                .abertura(HorarioUtils.formatTime(h.getAbertura()))
                .fechamento(HorarioUtils.formatTime(h.getFechamento()))
                .ativo(h.getAtivo())
                .dataCriacao(h.getCreatedAt())
                .build();
    }

    private HorarioDetailResponse toDetailResponse(HorarioFuncionamento h) {
        return HorarioDetailResponse.builder()
                .id(h.getId())
                .mercadoId(h.getMercado().getId())
                .diaSemana(h.getDiaSemana())
                .abertura(HorarioUtils.formatTime(h.getAbertura()))
                .fechamento(HorarioUtils.formatTime(h.getFechamento()))
                .ativo(h.getAtivo())
                .dataCriacao(h.getCreatedAt())
                .dataAtualizacao(h.getUpdatedAt())
                .dataExclusao(h.getDeletedAt())
                .build();
    }
}
```

---

## MercadoStatusChecker
```java
package com.netflix.mercados.service;

import com.netflix.mercados.dto.horario.response.MercadoStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MercadoStatusChecker {

    private final HorarioService horarioService;

    public MercadoStatusResponse verificarStatus(Long mercadoId) {
        return horarioService.status(mercadoId);
    }
}
```

---

## Controllers

### HorarioController
```java
package com.netflix.mercados.controller;

import com.netflix.mercados.dto.horario.request.CreateHorarioRequest;
import com.netflix.mercados.dto.horario.response.HorarioDetailResponse;
import com.netflix.mercados.dto.horario.response.HorarioResponse;
import com.netflix.mercados.dto.horario.response.MercadoStatusResponse;
import com.netflix.mercados.dto.response.ApiResponse;
import com.netflix.mercados.dto.response.PageResponse;
import com.netflix.mercados.service.HorarioService;
import com.netflix.mercados.service.MercadoStatusChecker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Horários", description = "Horários de funcionamento do mercado")
public class HorarioController {

    private final HorarioService horarioService;
    private final MercadoStatusChecker statusChecker;

    @Operation(summary = "Criar horário de funcionamento")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Horário criado com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = HorarioResponse.class)))
    @PostMapping("/mercados/{mercadoId}/horarios")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MERCADO')")
    public ResponseEntity<ApiResponse<HorarioResponse>> criar(
            @PathVariable Long mercadoId,
            @Valid @RequestBody CreateHorarioRequest request) {

        HorarioResponse response = horarioService.criar(mercadoId, request);
        return ResponseEntity.status(201)
                .body(ApiResponse.success("Horário criado com sucesso", response));
    }

    @Operation(summary = "Listar horários do mercado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Horários listados com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class)))
    @GetMapping("/mercados/{mercadoId}/horarios")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MERCADO') or hasRole('USER')")
    public ResponseEntity<ApiResponse<PageResponse<HorarioResponse>>> listar(
            @PathVariable Long mercadoId,
            @ParameterObject Pageable pageable) {

        Page<HorarioResponse> page = horarioService.listar(mercadoId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Horários listados com sucesso", PageResponse.fromPage(page)));
    }

    @Operation(summary = "Atualizar horário")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Horário atualizado com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = HorarioDetailResponse.class)))
    @PutMapping("/horarios/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MERCADO')")
    public ResponseEntity<ApiResponse<HorarioDetailResponse>> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody CreateHorarioRequest request) {

        HorarioDetailResponse response = horarioService.atualizar(id, request);
        return ResponseEntity.ok(ApiResponse.success("Horário atualizado com sucesso", response));
    }

    @Operation(summary = "Deletar horário (soft delete)")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Horário deletado com sucesso")
    @DeleteMapping("/horarios/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MERCADO')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        horarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Verificar se o mercado está aberto agora")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Status do mercado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class)))
    @GetMapping("/mercados/{mercadoId}/horarios/aberto")
    public ResponseEntity<ApiResponse<Boolean>> abertoAgora(@PathVariable Long mercadoId) {
        boolean aberto = horarioService.verificarAberto(mercadoId);
        return ResponseEntity.ok(ApiResponse.success("Status consultado", aberto));
    }

    @Operation(summary = "Obter status do mercado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Status do mercado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MercadoStatusResponse.class)))
    @GetMapping("/mercados/{mercadoId}/status")
    public ResponseEntity<ApiResponse<MercadoStatusResponse>> status(@PathVariable Long mercadoId) {
        MercadoStatusResponse response = statusChecker.verificarStatus(mercadoId);
        return ResponseEntity.ok(ApiResponse.success("Status do mercado", response));
    }
}
```

---

## Swagger/OpenAPI

### OpenAPI - Anotações recomendadas
- `@Tag` em `HorarioController`
- `@Operation` para cada endpoint
- `@ApiResponses`/`@ApiResponse` com exemplos
- `@SecurityRequirement(name = "bearerAuth")`

---

## Regras de Negócio

1. **Validação de horário:** `abertura < fechamento`.
2. **Dias válidos:** somente `0-6`.
3. **Sem sobreposição:** não permitir intervalos sobrepostos no mesmo dia.
4. **Múltiplos períodos por dia:** permitido desde que não haja sobreposição (ex.: 08:00-12:00 e 14:00-18:00).
5. **Soft delete:** horários removidos não aparecem em listagens.
6. **Verificação de aberto agora:** considera horários do dia e intervalos ativos.
7. **Próxima abertura:** procura primeiro no dia atual; se não houver, busca nos próximos 7 dias.

---

## Paginação e Soft Delete

- **GET** `/mercados/{mercadoId}/horarios` retorna `PageResponse<HorarioResponse>`.
- Exclusões são **soft delete** com `deletedAt` preenchido e `ativo=false`.

---

## Exemplos de Requests

### Criar horário
```json
{
  "diaSemana": 1,
  "abertura": "08:00",
  "fechamento": "12:00"
}
```

### Criar segundo período no mesmo dia
```json
{
  "diaSemana": 1,
  "abertura": "14:00",
  "fechamento": "18:00"
}
```

### Status do mercado
```json
{
  "mercadoId": 10,
  "abertoAgora": false,
  "status": "FECHADO",
  "proximaAbertura": "14:00",
  "mensagem": "Fechado. Próxima abertura às 14:00"
}
```
