package com.netflix.mercados.controller;

import com.netflix.mercados.dto.mercado.request.CreateMercadoRequest;
import com.netflix.mercados.dto.mercado.request.UpdateMercadoRequest;
import com.netflix.mercados.dto.mercado.request.CreateHorarioRequest;
import com.netflix.mercados.dto.mercado.response.MercadoResponse;
import com.netflix.mercados.dto.mercado.response.MercadoDetailResponse;
import com.netflix.mercados.dto.horario.response.HorarioFuncionamentoResponse;
import com.netflix.mercados.entity.User;
import com.netflix.mercados.security.UserPrincipal;
import com.netflix.mercados.service.MercadoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mercados")
@Slf4j
@RequiredArgsConstructor
@Transactional
@Tag(name = "Mercados", description = "Gerenciamento de mercados/lojas")
public class MercadoController {

    private final MercadoService mercadoService;

    /**
     * Cria um novo mercado (apenas SELLER)
     */
    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Criar novo mercado",
        description = "Cria um novo mercado. Requer role SELLER"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Mercado criado com sucesso",
            content = @Content(schema = @Schema(implementation = MercadoResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "409", description = "CNPJ ou email já existe"),
        @ApiResponse(responseCode = "403", description = "Usuário sem permissão")
    })
    public ResponseEntity<MercadoResponse> createMercado(
            @Valid @RequestBody CreateMercadoRequest request) {
        try {
            User user = getCurrentUser();
            log.info("Criando novo mercado para usuário: {}", user.getId());
            MercadoResponse response = mercadoService.createMercado(request, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Erro ao criar mercado", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Lista todos os mercados com paginação e filtros
     */
    @GetMapping
    @Operation(
        summary = "Listar mercados",
        description = "Retorna lista paginada de mercados aprovados"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista retornada com sucesso",
            content = @Content(schema = @Schema(implementation = Page.class))
        ),
        @ApiResponse(responseCode = "400", description = "Parâmetros de paginação inválidos")
    })
    public ResponseEntity<Page<MercadoResponse>> listMercados(
            @Parameter(description = "Número da página (começando de 0)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Filtro por nome")
            @RequestParam(required = false) String nome,
            @Parameter(description = "Filtro por tipo")
            @RequestParam(required = false) String tipo,
            @Parameter(description = "Filtro por cidade")
            @RequestParam(required = false) String cidade) {
        try {
            log.debug("Listando mercados - page: {}, size: {}", page, size);
            Pageable pageable = PageRequest.of(page, size);
            Page<MercadoResponse> response = mercadoService.listMercados(
                    pageable, nome, tipo, cidade);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao listar mercados", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Obtém detalhes de um mercado específico
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Obter detalhes do mercado",
        description = "Retorna informações completas de um mercado"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Detalhes retornados com sucesso",
            content = @Content(schema = @Schema(implementation = MercadoDetailResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Mercado não encontrado")
    })
    public ResponseEntity<MercadoDetailResponse> getMercadoById(
            @Parameter(description = "ID do mercado")
            @PathVariable Long id) {
        try {
            log.debug("Obtendo mercado: {}", id);
            MercadoDetailResponse response = mercadoService.getMercadoById(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao obter mercado", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Atualiza um mercado (apenas owner ou admin)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Atualizar mercado",
        description = "Atualiza informações de um mercado. Apenas owner ou ADMIN"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Mercado atualizado com sucesso",
            content = @Content(schema = @Schema(implementation = MercadoResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Mercado não encontrado"),
        @ApiResponse(responseCode = "403", description = "Sem permissão para atualizar")
    })
    public ResponseEntity<MercadoResponse> updateMercado(
            @Parameter(description = "ID do mercado")
            @PathVariable Long id,
            @Valid @RequestBody UpdateMercadoRequest request) {
        try {
            User user = getCurrentUser();
            log.info("Atualizando mercado: {} por usuário: {}", id, user.getId());
            MercadoResponse response = mercadoService.updateMercado(id, request, user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao atualizar mercado", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Deleta um mercado (soft delete)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Deletar mercado",
        description = "Realiza soft delete de um mercado"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Mercado deletado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Mercado não encontrado"),
        @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    public ResponseEntity<Void> deleteMercado(
            @Parameter(description = "ID do mercado")
            @PathVariable Long id) {
        try {
            User user = getCurrentUser();
            log.info("Deletando mercado: {} por usuário: {}", id, user.getId());
            mercadoService.deleteMercado(id, user);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Erro ao deletar mercado", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Aprova um mercado (apenas ADMIN)
     */
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Aprovar mercado",
        description = "Aprova um mercado pendente. Apenas ADMIN"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Mercado aprovado com sucesso",
            content = @Content(schema = @Schema(implementation = MercadoResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Mercado não encontrado"),
        @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    public ResponseEntity<MercadoResponse> approveMercado(
            @Parameter(description = "ID do mercado")
            @PathVariable Long id) {
        try {
            log.info("Aprovando mercado: {}", id);
            MercadoResponse response = mercadoService.approveMercado(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao aprovar mercado", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Rejeita um mercado (apenas ADMIN)
     */
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Rejeitar mercado",
        description = "Rejeita um mercado pendente. Apenas ADMIN"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Mercado rejeitado com sucesso",
            content = @Content(schema = @Schema(implementation = MercadoResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Mercado não encontrado"),
        @ApiResponse(responseCode = "403", description = "Sem permissão")
    })
    public ResponseEntity<MercadoResponse> rejectMercado(
            @Parameter(description = "ID do mercado")
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        try {
            log.info("Rejeitando mercado: {}", id);
            MercadoResponse response = mercadoService.rejectMercado(id, reason);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao rejeitar mercado", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Busca mercados próximos por coordenadas
     */
    @GetMapping("/nearby")
    @Operation(
        summary = "Buscar mercados próximos",
        description = "Busca mercados na proximidade de uma localização (raio em km)"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Mercados encontrados",
            content = @Content(schema = @Schema(implementation = Page.class))
        ),
        @ApiResponse(responseCode = "400", description = "Coordenadas inválidas")
    })
    public ResponseEntity<Page<MercadoResponse>> findNearby(
            @Parameter(description = "Latitude da busca")
            @RequestParam Double latitude,
            @Parameter(description = "Longitude da busca")
            @RequestParam Double longitude,
            @Parameter(description = "Raio de busca em km")
            @RequestParam(defaultValue = "5") Double raioKm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            log.debug("Buscando mercados próximos: lat={}, lon={}, raio={}", 
                    latitude, longitude, raioKm);
            Pageable pageable = PageRequest.of(page, size);
            Page<MercadoResponse> response = mercadoService.findNearby(
                    latitude, longitude, raioKm, pageable);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao buscar mercados próximos", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Adiciona um mercado aos favoritos do usuário
     */
    @PostMapping("/{id}/favorite")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Adicionar aos favoritos",
        description = "Marca um mercado como favorito do usuário autenticado"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Adicionado aos favoritos"),
        @ApiResponse(responseCode = "404", description = "Mercado não encontrado"),
        @ApiResponse(responseCode = "409", description = "Já é favorito")
    })
    public ResponseEntity<Void> addToFavorites(
            @Parameter(description = "ID do mercado")
            @PathVariable Long id) {
        try {
            User user = getCurrentUser();
            log.info("Adicionando mercado {} aos favoritos do usuário: {}", id, user.getId());
            mercadoService.addToFavorites(id, user);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Erro ao adicionar aos favoritos", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Remove um mercado dos favoritos
     */
    @DeleteMapping("/{id}/favorite")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Remover dos favoritos",
        description = "Remove um mercado dos favoritos do usuário autenticado"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Removido dos favoritos"),
        @ApiResponse(responseCode = "404", description = "Mercado não está nos favoritos")
    })
    public ResponseEntity<Void> removeFromFavorites(
            @Parameter(description = "ID do mercado")
            @PathVariable Long id) {
        try {
            User user = getCurrentUser();
            log.info("Removendo mercado {} dos favoritos do usuário: {}", id, user.getId());
            mercadoService.removeFromFavorites(id, user);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Erro ao remover dos favoritos", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Obtém horários de funcionamento de um mercado
     */
    @GetMapping("/{id}/horarios")
    @Operation(
        summary = "Obter horários de funcionamento",
        description = "Retorna os horários de funcionamento de um mercado"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Horários retornados com sucesso",
            content = @Content(schema = @Schema(implementation = List.class))
        ),
        @ApiResponse(responseCode = "404", description = "Mercado não encontrado")
    })
    public ResponseEntity<List<HorarioFuncionamentoResponse>> getHorarios(
            @Parameter(description = "ID do mercado")
            @PathVariable Long id) {
        try {
            log.debug("Obtendo horários do mercado: {}", id);
            List<HorarioFuncionamentoResponse> response = mercadoService.getHorarios(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao obter horários", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Cria horário de funcionamento para um mercado
     */
    @PostMapping("/{id}/horarios")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Criar horário de funcionamento",
        description = "Adiciona novo horário de funcionamento ao mercado"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Horário criado com sucesso",
            content = @Content(schema = @Schema(implementation = HorarioFuncionamentoResponse.class))
        ),
        @ApiResponse(responseCode = "404", description = "Mercado não encontrado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<HorarioFuncionamentoResponse> createHorario(
            @Parameter(description = "ID do mercado")
            @PathVariable Long id,
            @Valid @RequestBody CreateHorarioRequest request) {
        try {
            User user = getCurrentUser();
            log.info("Criando horário para mercado: {} por usuário: {}", id, user.getId());
            HorarioFuncionamentoResponse response = mercadoService.createHorario(id, request, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Erro ao criar horário", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Obtém o usuário autenticado do contexto de segurança
     */
    private User getCurrentUser() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return principal.getUser();
    }
}
