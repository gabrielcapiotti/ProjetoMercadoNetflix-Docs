# 🎯 Exemplo Prático: AvaliacaoController Completamente Documentado

Este é um exemplo real de como o **AvaliacaoController** ficaria com todas as annotations Swagger/OpenAPI aplicadas seguindo as melhores práticas.

---

## 📄 AvaliacaoController.java (Versão Completa e Documentada)

```java
package com.netflix.mercados.controller;

import com.netflix.mercados.dto.avaliacao.request.CreateAvaliacaoRequest;
import com.netflix.mercados.dto.avaliacao.request.UpdateAvaliacaoRequest;
import com.netflix.mercados.dto.avaliacao.response.AvaliacaoResponse;
import com.netflix.mercados.dto.avaliacao.response.RatingStatsResponse;
import com.netflix.mercados.entity.User;
import com.netflix.mercados.security.UserPrincipal;
import com.netflix.mercados.service.AvaliacaoService;
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
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsável pelo gerenciamento de avaliações de mercados.
 * 
 * Permite que usuários autenticados criem, atualizem e deletem suas próprias avaliações,
 * além de consultar avaliações de qualquer mercado e estatísticas de rating.
 * 
 * @author Netflix Mercados Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/v1/avaliacoes")
@Slf4j
@RequiredArgsConstructor
@Transactional
@Tag(
    name = "Avaliações", 
    description = "Sistema de avaliações e ratings para mercados. " +
                  "Permite criar, editar e visualizar avaliações com ratings de 1 a 5 estrelas. " +
                  "Usuários podem avaliar cada mercado apenas uma vez."
)
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;

    /**
     * Cria uma nova avaliação para um mercado.
     * Usuário pode avaliar cada mercado apenas uma vez.
     * Rating deve ser entre 1 e 5.
     */
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('SELLER')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Criar nova avaliação",
        description = "Cria uma nova avaliação para um mercado. " +
                      "**Regras:**\n" +
                      "- Rating deve ser entre 1 e 5 estrelas\n" +
                      "- Usuário pode avaliar cada mercado apenas uma vez\n" +
                      "- Comentário é opcional mas recomendado\n" +
                      "- Avaliação fica visível para todos os usuários\n\n" +
                      "**Exemplo de uso:**\n" +
                      "Após comprar em um mercado, o cliente pode avaliar sua experiência " +
                      "dando nota de 1 a 5 e opcionalmente deixando um comentário detalhado."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Avaliação criada com sucesso. " +
                         "A média de rating do mercado será recalculada automaticamente.",
            content = @Content(
                schema = @Schema(implementation = AvaliacaoResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados de entrada inválidos. Possíveis causas:\n" +
                         "- Rating fora do range 1-5\n" +
                         "- Comentário muito longo (máx 1000 caracteres)\n" +
                         "- MercadoId inválido ou ausente"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Token JWT ausente ou inválido. Faça login em /api/v1/auth/login"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Mercado não encontrado com o ID informado"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Usuário já avaliou este mercado. Use PUT para atualizar a avaliação existente."
        )
    })
    public ResponseEntity<AvaliacaoResponse> createAvaliacao(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dados da avaliação a ser criada",
                required = true,
                content = @Content(
                    schema = @Schema(implementation = CreateAvaliacaoRequest.class),
                    example = """
                        {
                          "mercadoId": 1,
                          "rating": 5,
                          "comentario": "Excelente mercado! Produtos frescos e preços justos. Atendimento nota 10!"
                        }
                        """
                )
            )
            @Valid @RequestBody CreateAvaliacaoRequest request) {
        User user = getCurrentUser();
        log.info("Criando avaliação para mercado: {} por usuário: {}", 
                request.getMercadoId(), user.getId());
        
        AvaliacaoResponse response = avaliacaoService.createAvaliacao(request, user);
        log.info("Avaliação criada com sucesso: {}", response.getId());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Lista todas as avaliações do sistema com paginação e ordenação.
     */
    @GetMapping
    @Operation(
        summary = "Listar todas as avaliações",
        description = "Retorna lista paginada de todas as avaliações do sistema, " +
                      "com opções de ordenação e filtros.\n\n" +
                      "**Ordenação disponível:**\n" +
                      "- `createdAt,desc` (padrão): Mais recentes primeiro\n" +
                      "- `rating,desc`: Maior nota primeiro\n" +
                      "- `rating,asc`: Menor nota primeiro\n\n" +
                      "**Casos de uso:**\n" +
                      "- Listagem geral de avaliações\n" +
                      "- Feed de avaliações recentes\n" +
                      "- Análise de qualidade geral dos mercados"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Lista retornada com sucesso. Retorna página vazia se não houver avaliações.",
            content = @Content(
                schema = @Schema(implementation = Page.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Parâmetros de paginação inválidos (página negativa, size muito grande, etc)"
        )
    })
    public ResponseEntity<Page<AvaliacaoResponse>> listAvaliacoes(
            @Parameter(
                description = "Número da página (zero-indexed). Primeira página = 0",
                example = "0"
            )
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(
                description = "Quantidade de itens por página. Máximo recomendado: 100",
                example = "20"
            )
            @RequestParam(defaultValue = "20") int size,
            
            @Parameter(
                description = "Campo e direção de ordenação. Formato: campo,direção",
                example = "createdAt,desc"
            )
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        log.debug("Listando avaliações - page: {}, size: {}, sort: {}", page, size, sort);
        
        // Parse sort parameter
        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("asc") 
            ? Sort.Direction.ASC 
            : Sort.Direction.DESC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));
        Page<AvaliacaoResponse> response = avaliacaoService.listAvaliacoes(pageable);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Obtém uma avaliação específica por ID.
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Buscar avaliação por ID",
        description = "Retorna detalhes completos de uma avaliação específica.\n\n" +
                      "**Inclui:**\n" +
                      "- Rating (1-5 estrelas)\n" +
                      "- Comentário do usuário\n" +
                      "- Nome e dados do autor\n" +
                      "- Data de criação e última atualização\n" +
                      "- Dados do mercado avaliado"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Avaliação encontrada e retornada com sucesso",
            content = @Content(
                schema = @Schema(implementation = AvaliacaoResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Avaliação não encontrada com o ID informado"
        )
    })
    public ResponseEntity<AvaliacaoResponse> getAvaliacaoById(
            @Parameter(
                description = "ID único da avaliação",
                required = true,
                example = "1"
            )
            @PathVariable Long id
    ) {
        log.debug("Obtendo avaliação: {}", id);
        AvaliacaoResponse response = avaliacaoService.getAvaliacaoById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Atualiza uma avaliação existente.
     * Apenas o autor da avaliação ou um ADMIN pode atualizar.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('SELLER') or hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Atualizar avaliação existente",
        description = "Atualiza rating e/ou comentário de uma avaliação existente.\n\n" +
                      "**Permissões:**\n" +
                      "- Apenas o autor da avaliação pode atualizar\n" +
                      "- ADMIN pode atualizar qualquer avaliação\n\n" +
                      "**Comportamento:**\n" +
                      "- A média de rating do mercado será recalculada\n" +
                      "- Data de atualização será registrada\n" +
                      "- Campos não informados mantêm valores anteriores"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Avaliação atualizada com sucesso",
            content = @Content(
                schema = @Schema(implementation = AvaliacaoResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Dados de entrada inválidos"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Token JWT ausente ou inválido"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Usuário sem permissão para atualizar esta avaliação. " +
                         "Apenas o autor ou ADMIN podem atualizar."
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Avaliação não encontrada"
        )
    })
    public ResponseEntity<AvaliacaoResponse> updateAvaliacao(
            @Parameter(
                description = "ID da avaliação a ser atualizada",
                required = true,
                example = "1"
            )
            @PathVariable Long id,
            
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Novos dados da avaliação",
                required = true,
                content = @Content(
                    schema = @Schema(implementation = UpdateAvaliacaoRequest.class),
                    example = """
                        {
                          "rating": 4,
                          "comentario": "Muito bom! Apenas alguns produtos estavam em falta."
                        }
                        """
                )
            )
            @Valid @RequestBody UpdateAvaliacaoRequest request
    ) {
        User user = getCurrentUser();
        log.info("Atualizando avaliação: {} por usuário: {}", id, user.getId());
        
        AvaliacaoResponse response = avaliacaoService.updateAvaliacao(id, request, user);
        log.info("Avaliação atualizada com sucesso: {}", id);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Deleta uma avaliação (soft delete).
     * Apenas o autor ou ADMIN pode deletar.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('SELLER') or hasRole('ADMIN')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Deletar avaliação",
        description = "Remove uma avaliação do sistema (soft delete).\n\n" +
                      "**Permissões:**\n" +
                      "- Apenas o autor da avaliação pode deletar\n" +
                      "- ADMIN pode deletar qualquer avaliação\n\n" +
                      "**Comportamento:**\n" +
                      "- Soft delete: avaliação é marcada como deletada, não removida fisicamente\n" +
                      "- A média de rating do mercado será recalculada\n" +
                      "- Operação não pode ser desfeita pelo usuário (apenas ADMIN)"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Avaliação deletada com sucesso. Sem corpo de resposta."
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Token JWT ausente ou inválido"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Usuário sem permissão para deletar esta avaliação"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Avaliação não encontrada"
        )
    })
    public ResponseEntity<Void> deleteAvaliacao(
            @Parameter(
                description = "ID da avaliação a ser deletada",
                required = true,
                example = "1"
            )
            @PathVariable Long id
    ) {
        User user = getCurrentUser();
        log.info("Deletando avaliação: {} por usuário: {}", id, user.getId());
        
        avaliacaoService.deleteAvaliacao(id, user);
        log.info("Avaliação deletada com sucesso: {}", id);
        
        return ResponseEntity.noContent().build();
    }

    /**
     * Lista todas as avaliações de um mercado específico.
     */
    @GetMapping("/mercado/{mercadoId}")
    @Operation(
        summary = "Listar avaliações de um mercado",
        description = "Retorna lista paginada de avaliações de um mercado específico, " +
                      "ordenadas da mais recente para a mais antiga.\n\n" +
                      "**Casos de uso:**\n" +
                      "- Exibir avaliações na página do mercado\n" +
                      "- Permitir que usuários leiam opiniões antes de comprar\n" +
                      "- Análise de feedback do mercado\n\n" +
                      "**Retorna:**\n" +
                      "- Todas as avaliações ativas (não deletadas)\n" +
                      "- Com dados do autor (nome, foto)\n" +
                      "- Ordenadas por data de criação (mais recentes primeiro)"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Avaliações retornadas com sucesso. Retorna lista vazia se o mercado não tiver avaliações.",
            content = @Content(
                schema = @Schema(implementation = Page.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Mercado não encontrado com o ID informado"
        )
    })
    public ResponseEntity<Page<AvaliacaoResponse>> listAvaliacoesByMercado(
            @Parameter(
                description = "ID do mercado cujas avaliações serão listadas",
                required = true,
                example = "1"
            )
            @PathVariable Long mercadoId,
            
            @Parameter(
                description = "Número da página",
                example = "0"
            )
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(
                description = "Itens por página",
                example = "20"
            )
            @RequestParam(defaultValue = "20") int size
    ) {
        log.debug("Listando avaliações do mercado: {}, page: {}, size: {}", 
                mercadoId, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AvaliacaoResponse> response = avaliacaoService.listAvaliacoesByMercado(mercadoId, pageable);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Obtém estatísticas detalhadas de rating de um mercado.
     */
    @GetMapping("/mercado/{mercadoId}/stats")
    @Operation(
        summary = "Obter estatísticas de rating do mercado",
        description = "Retorna estatísticas completas de avaliações de um mercado.\n\n" +
                      "**Inclui:**\n" +
                      "- Média de rating (0.0 a 5.0)\n" +
                      "- Total de avaliações\n" +
                      "- Distribuição por estrelas (quantas avaliações 1⭐, 2⭐, 3⭐, 4⭐, 5⭐)\n" +
                      "- Percentual de cada categoria\n\n" +
                      "**Casos de uso:**\n" +
                      "- Exibir indicadores visuais na página do mercado\n" +
                      "- Gráficos de distribuição de ratings\n" +
                      "- Dashboard de análise do mercado\n" +
                      "- Comparação entre mercados"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Estatísticas calculadas e retornadas com sucesso",
            content = @Content(
                schema = @Schema(
                    implementation = RatingStatsResponse.class,
                    example = """
                        {
                          "mercadoId": 1,
                          "mercadoNome": "Mercado Central",
                          "mediaRating": 4.3,
                          "totalAvaliacoes": 127,
                          "distribuicao": {
                            "5estrelas": 65,
                            "4estrelas": 42,
                            "3estrelas": 15,
                            "2estrelas": 3,
                            "1estrela": 2
                          },
                          "percentuais": {
                            "5estrelas": 51.2,
                            "4estrelas": 33.1,
                            "3estrelas": 11.8,
                            "2estrelas": 2.4,
                            "1estrela": 1.6
                          }
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Mercado não encontrado"
        )
    })
    public ResponseEntity<RatingStatsResponse> getRatingStats(
            @Parameter(
                description = "ID do mercado",
                required = true,
                example = "1"
            )
            @PathVariable Long mercadoId
    ) {
        log.debug("Obtendo estatísticas de rating do mercado: {}", mercadoId);
        RatingStatsResponse response = avaliacaoService.getRatingStats(mercadoId);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtém a avaliação do usuário atual para um mercado específico.
     */
    @GetMapping("/mercado/{mercadoId}/minha")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('SELLER')")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(
        summary = "Obter minha avaliação para um mercado",
        description = "Retorna a avaliação do usuário autenticado para um mercado específico.\n\n" +
                      "**Casos de uso:**\n" +
                      "- Verificar se usuário já avaliou o mercado\n" +
                      "- Pré-preencher formulário de edição de avaliação\n" +
                      "- Exibir avaliação do usuário em destaque"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Avaliação do usuário retornada",
            content = @Content(
                schema = @Schema(implementation = AvaliacaoResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Token JWT ausente ou inválido"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Usuário ainda não avaliou este mercado ou mercado não encontrado"
        )
    })
    public ResponseEntity<AvaliacaoResponse> getMinhaAvaliacao(
            @Parameter(
                description = "ID do mercado",
                required = true,
                example = "1"
            )
            @PathVariable Long mercadoId
    ) {
        User user = getCurrentUser();
        log.debug("Obtendo avaliação do usuário {} para mercado {}", user.getId(), mercadoId);
        
        AvaliacaoResponse response = avaliacaoService.getAvaliacaoByUserAndMercado(user.getId(), mercadoId);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtém o usuário autenticado do contexto de segurança.
     */
    private User getCurrentUser() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return principal.getUser();
    }
}
```

---

## 📊 Resumo das Melhorias Aplicadas

### ✅ Annotations Adicionadas/Melhoradas:

1. **@Tag** - Descrição detalhada do controller
2. **@Operation** - Cada endpoint com:
   - Summary conciso
   - Description detalhada com casos de uso
   - Exemplos práticos

3. **@ApiResponses** - Todos os códigos HTTP possíveis:
   - 200, 201, 204 (Sucessos)
   - 400, 401, 403, 404, 409 (Erros)
   - Descrições específicas para cada caso

4. **@Parameter** - Todos os parâmetros documentados:
   - Description clara
   - Examples realistas
   - Required quando necessário

5. **@RequestBody** - Bodies documentados:
   - Description
   - Examples em JSON
   - Schema implementation

6. **@SecurityRequirement** - JWT documentado em endpoints protegidos

### ✅ Novos Endpoints Adicionados:

- **GET /mercado/{mercadoId}/minha** - Obter avaliação do usuário atual

### ✅ Melhorias de Código:

- Logs estruturados
- Parse de sort parameter
- Ordenação configurável
- Comments em JavaDoc

---

## 🎯 Como Aplicar nos Outros Controllers

1. **Copie este exemplo** como referência
2. **Adapte** para cada controller:
   - Altere nome da Tag
   - Ajuste Operations
   - Adapte ApiResponses
   - Customize Parameters
3. **Use os templates** do arquivo SWAGGER_TEMPLATES.md
4. **Teste** cada endpoint no Swagger UI

---

## 📝 Próximos Controllers para Documentar

Aplique o mesmo padrão em:
- [ ] ComentarioController
- [ ] FavoritoController
- [ ] HorarioController
- [ ] NotificacaoController
- [ ] PromocaoController

**Tempo estimado por controller:** 20-30 minutos usando templates

---

**Versão:** 1.0.0  
**Última atualização:** 30 de Janeiro de 2024
