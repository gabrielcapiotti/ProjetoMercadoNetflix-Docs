# 🎯 Exemplos de Uso dos Services em Controllers

## Padrão de Controller Recomendado

```java
@Slf4j
@RestController
@RequestMapping("/api/v1/example")
@RequiredArgsConstructor  // Para usar final fields
public class ExampleController {

    private final ExampleService exampleService;
    private final UserService userService;
    
    @GetCurrentUser // Custom annotation
    private User getCurrentUser(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        return userService.getUserFromToken(token);
    }
}
```

---

## 1️⃣ UserController Examples

```java
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * GET /api/v1/users/{id}
     * Obter usuário por ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<UserResponse> getUser(
            @PathVariable Long id,
            HttpServletRequest request) {
        
        User currentUser = userService.getUserFromToken(extractToken(request));
        
        // Usuário só pode acessar seus próprios dados ou admin acessa qualquer um
        if (!id.equals(currentUser.getId()) && 
            !isAdmin(currentUser)) {
            return ResponseEntity.status(403).build();
        }
        
        User user = userService.findUserById(id);
        return ResponseEntity.ok(convertToResponse(user));
    }

    /**
     * PUT /api/v1/users/{id}
     * Atualizar usuário
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestBody UserUpdateRequest request,
            HttpServletRequest httpRequest) {
        
        User currentUser = userService.getUserFromToken(extractToken(httpRequest));
        
        // Autorização
        if (!id.equals(currentUser.getId()) && !isAdmin(currentUser)) {
            return ResponseEntity.status(403).build();
        }
        
        User updated = userService.updateUser(id, request);
        return ResponseEntity.ok(convertToResponse(updated));
    }

    /**
     * POST /api/v1/users/{id}/change-password
     * Alterar senha
     */
    @PostMapping("/{id}/change-password")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long id,
            @RequestBody ChangePasswordRequest request,
            HttpServletRequest httpRequest) {
        
        User currentUser = userService.getUserFromToken(extractToken(httpRequest));
        
        if (!id.equals(currentUser.getId())) {
            return ResponseEntity.status(403).build();
        }
        
        try {
            userService.changePassword(id, request);
            return ResponseEntity.ok().build();
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * POST /api/v1/users/{id}/2fa/enable
     * Habilitar 2FA
     */
    @PostMapping("/{id}/2fa/enable")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> enableTwoFactor(
            @PathVariable Long id,
            HttpServletRequest request) {
        
        User currentUser = userService.getUserFromToken(extractToken(request));
        
        if (!id.equals(currentUser.getId())) {
            return ResponseEntity.status(403).build();
        }
        
        userService.enableTwoFactor(id);
        return ResponseEntity.ok().build();
    }

    /**
     * GET /api/v1/users
     * Listar usuários com paginação (ADMIN)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<UserResponse> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    // Helper methods
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        return header != null ? header.replace("Bearer ", "") : null;
    }

    private boolean isAdmin(User user) {
        return user.getRoles().stream()
                .anyMatch(r -> r.getName().equals("ROLE_ADMIN"));
    }

    private UserResponse convertToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .active(user.isActive())
                .emailVerified(user.isEmailVerified())
                .twoFactorEnabled(user.isTwoFactorEnabled())
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                .createdAt(user.getCreatedAt())
                .build();
    }
}
```

---

## 2️⃣ AuthController Examples

```java
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * POST /api/v1/auth/register
     * Registrar novo usuário
     */
    @PostMapping("/register")
    public ResponseEntity<JwtAuthenticationResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        
        try {
            JwtAuthenticationResponse response = authService.register(request);
            return ResponseEntity.status(201).body(response);
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * POST /api/v1/auth/login
     * Login
     */
    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> login(
            @Valid @RequestBody LoginRequest request) {
        
        try {
            JwtAuthenticationResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (ValidationException e) {
            return ResponseEntity.status(401).body(null);
        }
    }

    /**
     * POST /api/v1/auth/refresh
     * Renovar access token
     */
    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthenticationResponse> refreshToken(
            @RequestBody RefreshTokenRequest request) {
        
        try {
            JwtAuthenticationResponse response = authService.refreshToken(request.getRefreshToken());
            return ResponseEntity.ok(response);
        } catch (ValidationException e) {
            return ResponseEntity.status(401).build();
        }
    }

    /**
     * POST /api/v1/auth/logout
     * Logout
     */
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            @RequestParam Long userId) {
        
        authService.logout(userId);
        return ResponseEntity.ok().build();
    }

    /**
     * GET /api/v1/auth/validate
     * Validar token
     */
    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(
            @RequestParam String token) {
        
        boolean isValid = authService.validateToken(token);
        return ResponseEntity.ok(isValid);
    }
}
```

---

## 3️⃣ MercadoController Examples

```java
@Slf4j
@RestController
@RequestMapping("/api/v1/mercados")
public class MercadoController {

    @Autowired
    private MercadoService mercadoService;

    @Autowired
    private UserService userService;

    /**
     * POST /api/v1/mercados
     * Criar novo mercado
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MercadoResponse> createMercado(
            @Valid @RequestBody CreateMercadoRequest request,
            HttpServletRequest httpRequest) {
        
        User owner = userService.getUserFromToken(extractToken(httpRequest));
        Mercado mercado = mercadoService.createMercado(request, owner);
        
        return ResponseEntity.status(201).body(convertToResponse(mercado));
    }

    /**
     * GET /api/v1/mercados/{id}
     * Obter mercado por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<MercadoResponse> getMercadoById(@PathVariable Long id) {
        
        Mercado mercado = mercadoService.getMercadoById(id);
        return ResponseEntity.ok(convertToResponse(mercado));
    }

    /**
     * PUT /api/v1/mercados/{id}
     * Atualizar mercado
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<MercadoResponse> updateMercado(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMercadoRequest request,
            HttpServletRequest httpRequest) {
        
        User user = userService.getUserFromToken(extractToken(httpRequest));
        
        try {
            Mercado updated = mercadoService.updateMercado(id, request, user);
            return ResponseEntity.ok(convertToResponse(updated));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(403).build();
        }
    }

    /**
     * DELETE /api/v1/mercados/{id}
     * Deletar mercado
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> deleteMercado(
            @PathVariable Long id,
            HttpServletRequest request) {
        
        User user = userService.getUserFromToken(extractToken(request));
        
        try {
            mercadoService.deleteMercado(id, user);
            return ResponseEntity.noContent().build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(403).build();
        }
    }

    /**
     * GET /api/v1/mercados
     * Listar todos os mercados
     */
    @GetMapping
    public ResponseEntity<Page<MercadoResponse>> getAllMercados(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<MercadoResponse> mercados = mercadoService.getAllMercados(pageable);
        return ResponseEntity.ok(mercados);
    }

    /**
     * GET /api/v1/mercados/search/proximidade?lat=&lon=&raio=
     * Buscar mercados próximos
     */
    @GetMapping("/search/proximidade")
    public ResponseEntity<List<MercadoResponse>> buscarProximos(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "10") Double raio) {
        
        List<Mercado> mercados = mercadoService.buscarPorProximidade(latitude, longitude, raio);
        List<MercadoResponse> responses = mercados.stream()
                .map(this::convertToResponse)
                .toList();
        
        return ResponseEntity.ok(responses);
    }

    /**
     * GET /api/v1/mercados/search/nome?nome=
     * Buscar por nome
     */
    @GetMapping("/search/nome")
    public ResponseEntity<Page<Mercado>> buscarPorNome(
            @RequestParam String nome,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Mercado> mercados = mercadoService.buscarPorNome(nome, pageable);
        return ResponseEntity.ok(mercados);
    }

    /**
     * POST /api/v1/mercados/{id}/aprovar
     * Aprovar mercado (ADMIN)
     */
    @PostMapping("/{id}/aprovar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> aprovarMercado(@PathVariable Long id) {
        
        mercadoService.aprovarMercado(id);
        return ResponseEntity.ok().build();
    }

    /**
     * POST /api/v1/mercados/{id}/rejeitar
     * Rejeitar mercado (ADMIN)
     */
    @PostMapping("/{id}/rejeitar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> rejeitarMercado(
            @PathVariable Long id,
            @RequestParam String motivo) {
        
        mercadoService.rejeitarMercado(id, motivo);
        return ResponseEntity.ok().build();
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        return header != null ? header.replace("Bearer ", "") : null;
    }

    private MercadoResponse convertToResponse(Mercado mercado) {
        return MercadoResponse.builder()
                .id(mercado.getId())
                .nome(mercado.getNome())
                .descricao(mercado.getDescricao())
                .cidade(mercado.getCidade())
                .bairro(mercado.getBairro())
                .telefone(mercado.getTelefone())
                .latitude(mercado.getLatitude())
                .longitude(mercado.getLongitude())
                .avaliacaoMedia(mercado.getAvaliacaoMedia())
                .totalAvaliacoes(mercado.getTotalAvaliacoes())
                .aprovado(mercado.isAprovado())
                .createdAt(mercado.getCreatedAt())
                .build();
    }
}
```

---

## 4️⃣ AvaliacaoController Examples

```java
@Slf4j
@RestController
@RequestMapping("/api/v1/avaliacoes")
public class AvaliacaoController {

    @Autowired
    private AvaliacaoService avaliacaoService;

    @Autowired
    private UserService userService;

    /**
     * POST /api/v1/avaliacoes
     * Criar avaliação
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> criarAvaliacao(
            @Valid @RequestBody CreateAvaliacaoRequest request,
            HttpServletRequest httpRequest) {
        
        User usuario = userService.getUserFromToken(extractToken(httpRequest));
        
        try {
            Avaliacao avaliacao = avaliacaoService.criarAvaliacao(request, usuario);
            return ResponseEntity.status(201).body(avaliacao);
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * GET /api/v1/avaliacoes/{id}
     * Obter avaliação por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Avaliacao> obterAvaliacao(@PathVariable Long id) {
        
        Avaliacao avaliacao = avaliacaoService.obterAvaliacaoPorId(id);
        return ResponseEntity.ok(avaliacao);
    }

    /**
     * GET /api/v1/avaliacoes/mercado/{mercadoId}
     * Listar avaliações do mercado
     */
    @GetMapping("/mercado/{mercadoId}")
    public ResponseEntity<Page<Avaliacao>> obterAvaliacoesPorMercado(
            @PathVariable Long mercadoId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Avaliacao> avaliacoes = avaliacaoService.obterAvaliacoesPorMercado(mercadoId, pageable);
        return ResponseEntity.ok(avaliacoes);
    }

    /**
     * GET /api/v1/avaliacoes/mercado/{mercadoId}/stats
     * Estatísticas de avaliação
     */
    @GetMapping("/mercado/{mercadoId}/stats")
    public ResponseEntity<RatingStatsResponse> calcularEstatisticas(@PathVariable Long mercadoId) {
        
        RatingStatsResponse stats = avaliacaoService.calcularEstatisticas(mercadoId);
        return ResponseEntity.ok(stats);
    }

    /**
     * POST /api/v1/avaliacoes/{id}/util
     * Marcar como útil
     */
    @PostMapping("/{id}/util")
    public ResponseEntity<Void> marcarComoUtil(@PathVariable Long id) {
        
        avaliacaoService.marcarComoUtil(id);
        return ResponseEntity.ok().build();
    }

    /**
     * DELETE /api/v1/avaliacoes/{id}
     * Deletar avaliação
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> deletarAvaliacao(
            @PathVariable Long id,
            HttpServletRequest request) {
        
        User usuario = userService.getUserFromToken(extractToken(request));
        
        try {
            avaliacaoService.deletarAvaliacao(id, usuario);
            return ResponseEntity.noContent().build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(403).build();
        }
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        return header != null ? header.replace("Bearer ", "") : null;
    }
}
```

---

## 5️⃣ FavoritoController Examples

```java
@Slf4j
@RestController
@RequestMapping("/api/v1/favoritos")
public class FavoritoController {

    @Autowired
    private FavoritoService favoritoService;

    @Autowired
    private UserService userService;

    /**
     * POST /api/v1/favoritos/{mercadoId}
     * Adicionar aos favoritos
     */
    @PostMapping("/{mercadoId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> adicionarFavorito(
            @PathVariable Long mercadoId,
            HttpServletRequest request) {
        
        User usuario = userService.getUserFromToken(extractToken(request));
        
        try {
            Favorito favorito = favoritoService.adicionarFavorito(mercadoId, usuario);
            return ResponseEntity.status(201).body(favorito);
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * DELETE /api/v1/favoritos/{mercadoId}
     * Remover dos favoritos
     */
    @DeleteMapping("/{mercadoId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> removerFavorito(
            @PathVariable Long mercadoId,
            HttpServletRequest request) {
        
        User usuario = userService.getUserFromToken(extractToken(request));
        favoritoService.removerFavorito(mercadoId, usuario);
        
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/v1/favoritos
     * Listar favoritos do usuário
     */
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<Favorito>> obterFavoritos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {
        
        User usuario = userService.getUserFromToken(extractToken(request));
        Pageable pageable = PageRequest.of(page, size);
        
        Page<Favorito> favoritos = favoritoService.obterFavoritosDUsuario(usuario.getId(), pageable);
        return ResponseEntity.ok(favoritos);
    }

    /**
     * GET /api/v1/favoritos/check/{mercadoId}
     * Verificar se é favorito
     */
    @GetMapping("/check/{mercadoId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Boolean> verificarFavorito(
            @PathVariable Long mercadoId,
            HttpServletRequest request) {
        
        User usuario = userService.getUserFromToken(extractToken(request));
        Boolean isFavorito = favoritoService.verificarFavorito(mercadoId, usuario);
        
        return ResponseEntity.ok(isFavorito);
    }

    /**
     * POST /api/v1/favoritos/{mercadoId}/toggle
     * Alternar (adicionar/remover)
     */
    @PostMapping("/{mercadoId}/toggle")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Boolean> toggleFavorito(
            @PathVariable Long mercadoId,
            HttpServletRequest request) {
        
        User usuario = userService.getUserFromToken(extractToken(request));
        Boolean agora = favoritoService.toggleFavorito(mercadoId, usuario);
        
        return ResponseEntity.ok(agora);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        return header != null ? header.replace("Bearer ", "") : null;
    }
}
```

---

## 📝 Padrão de Request/Response

```java
// Request
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMercadoRequest {
    @NotBlank(message = "Nome é obrigatório")
    private String nome;
    
    private String descricao;
    
    @NotBlank(message = "Cidade é obrigatória")
    private String cidade;
    
    @NotNull(message = "Latitude é obrigatória")
    private Double latitude;
    
    @NotNull(message = "Longitude é obrigatória")
    private Double longitude;
    
    private String telefone;
    private String email;
}

// Response
@Data
@Builder
public class MercadoResponse {
    private Long id;
    private String nome;
    private String descricao;
    private String cidade;
    private String bairro;
    private String telefone;
    private Double latitude;
    private Double longitude;
    private Double avaliacaoMedia;
    private Long totalAvaliacoes;
    private Boolean aprovado;
    private LocalDateTime createdAt;
}
```

---

## ✅ Best Practices Implementadas

1. **Validação em cascata**
   - @Valid em RequestBody
   - Validação de negócio nos Services

2. **Tratamento de exceções**
   - Try-catch para exceções esperadas
   - ResponseEntity com códigos apropriados

3. **Autorização**
   - @PreAuthorize para verificar roles
   - Verificação adicional de ownership

4. **Extração de usuário**
   - Helper method `extractToken()`
   - Buscar usuário autenticado do token

5. **Logging**
   - @Slf4j nos controllers
   - Log de operações importantes

6. **Paginação**
   - PageRequest.of() com sort
   - Limites de tamanho de página

7. **Mensagens de erro**
   - Mensagens claras e específicas
   - Códigos HTTP apropriados

---

**Developed with ❤️ for Netflix Mercados**
