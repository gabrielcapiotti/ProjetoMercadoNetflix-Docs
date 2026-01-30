# 🎯 REFERÊNCIA RÁPIDA - 11 Services Netflix Mercados

## 📌 Quick Start

### Services Disponíveis

```java
@Autowired private UserService userService;                    // Usuários
@Autowired private AuthService authService;                    // Autenticação
@Autowired private MercadoService mercadoService;              // Mercados
@Autowired private AvaliacaoService avaliacaoService;          // Avaliações
@Autowired private ComentarioService comentarioService;        // Comentários
@Autowired private FavoritoService favoritoService;            // Favoritos
@Autowired private NotificacaoService notificacaoService;      // Notificações
@Autowired private PromocaoService promocaoService;            // Promoções
@Autowired private HorarioFuncionamentoService horarioService; // Horários
@Autowired private RefreshTokenService refreshTokenService;    // Refresh Tokens
@Autowired private AuditLogService auditLogService;            // Auditoria
```

---

## 1️⃣ UserService

### Criar Usuário
```java
User user = userService.createUser(new RegisterRequest(
    "João Silva", "joao@email.com", "11987654321", "Senha@123"
));
```

### Buscar Usuário
```java
User user = userService.findUserById(1L);
User user = userService.findUserByEmail("joao@email.com");
Page<User> users = userService.getAllUsers(pageable);
```

### Atualizar Usuário
```java
User updated = userService.updateUser(1L, new UserUpdateRequest(
    "João Silva Updated", "11987654321"
));
```

### Alterar Senha
```java
userService.changePassword(1L, new ChangePasswordRequest(
    "SenhaAtual@123", "NovaSenha@123"
));
```

### 2FA
```java
userService.enableTwoFactor(1L);   // Habilitar
userService.disableTwoFactor(1L);  // Desabilitar
```

---

## 2️⃣ AuthService

### Registrar
```java
JwtAuthenticationResponse response = authService.register(
    new RegisterRequest("João", "joao@email.com", "11987654321", "Senha@123")
);
String accessToken = response.getAccessToken();
String refreshToken = response.getRefreshToken();
```

### Login
```java
JwtAuthenticationResponse response = authService.login(
    new LoginRequest("joao@email.com", "Senha@123")
);
```

### Renovar Token
```java
JwtAuthenticationResponse newResponse = authService.refreshToken(refreshTokenString);
```

### Logout
```java
authService.logout(userId);
```

---

## 3️⃣ MercadoService

### Criar Mercado
```java
Mercado mercado = mercadoService.createMercado(
    new CreateMercadoRequest(
        "Carrefour", "Descrição...", -23.55, -46.63, "São Paulo", "SP"
    ), 
    usuario
);
```

### Obter Mercado
```java
Mercado mercado = mercadoService.getMercadoById(1L);
Page<Mercado> mercados = mercadoService.getAllMercados(pageable);
```

### Atualizar Mercado
```java
Mercado updated = mercadoService.updateMercado(1L, 
    new UpdateMercadoRequest("Novo Nome", "Nova descrição"), 
    usuario
);
```

### Buscar Próximos
```java
List<Mercado> proximos = mercadoService.buscarProximos(-23.55, -46.63, 5.0); // 5km
```

### Buscar por Cidade
```java
Page<Mercado> results = mercadoService.getAllMercados(pageable, null, null, "São Paulo", true);
```

### Aprovar/Rejeitar (Admin)
```java
mercadoService.aprovarMercado(1L, adminId);
mercadoService.rejeitarMercado(1L, "Documentos inválidos", adminId);
```

---

## 4️⃣ AvaliacaoService

### Criar Avaliação
```java
Avaliacao avaliacao = avaliacaoService.criarAvaliacao(
    new CreateAvaliacaoRequest(
        1L,              // mercadoId
        5,               // rating (1-5)
        "Ótimo!",        // título
        "Muito bom mesmo"  // descrição
    ),
    usuario
);
```

### Listar Avaliações
```java
Page<Avaliacao> avaliacoes = avaliacaoService.obterAvaliacoesPorMercado(1L, pageable);
Page<Avaliacao> minhas = avaliacaoService.obterAvaliacoesPorUsuario(userId, pageable);
```

### Atualizar/Deletar
```java
avaliacaoService.atualizarAvaliacao(1L, new UpdateAvaliacaoRequest(4, "Bom"), usuario);
avaliacaoService.deletarAvaliacao(1L, usuario);
```

### Estatísticas
```java
RatingStatsResponse stats = avaliacaoService.calcularEstatisticas(1L);
// stats.getMediaAvaliacoes() = 4.5
// stats.getTotalAvaliacoes() = 100
// stats.getCincoEstrelas() = 85
```

### Útil/Inútil
```java
avaliacaoService.marcarComoUtil(1L);
avaliacaoService.marcarComoInutil(1L);
```

---

## 5️⃣ ComentarioService

### Criar Comentário
```java
Comentario comentario = comentarioService.criarComentario(
    new CreateComentarioRequest(1L, "Ótima avaliação!"),  // avaliacaoId, conteúdo
    usuario
);
```

### Responder Comentário
```java
Comentario resposta = comentarioService.responderComentario(
    1L,  // comentarioPaiId
    new CreateComentarioRequest(null, "Concordo!"),
    usuario
);
```

### Listar Comentários
```java
Page<Comentario> comentarios = comentarioService.obterComentariosPorAvaliacao(1L, pageable);
Page<Comentario> respostas = comentarioService.obterRespostas(1L, pageable);
```

### Curtir/Descurtir
```java
comentarioService.adicionarCurtida(1L, usuario);
comentarioService.removerCurtida(1L, usuario);
```

---

## 6️⃣ FavoritoService

### Adicionar/Remover
```java
Favorito fav = favoritoService.adicionarFavorito(1L, usuario);  // Adicionar
favoritoService.removerFavorito(1L, usuario);                   // Remover
```

### Toggle
```java
Boolean agora = favoritoService.toggleFavorito(1L, usuario);  // true = adicionado
```

### Listar Favoritos
```java
Page<Favorito> favoritos = favoritoService.obterFavoritosDUsuario(userId, pageable);
List<Favorito> ordenados = favoritoService.obterFavoritosComPrioridade(userId);
```

### Verificar/Contar
```java
Boolean isFav = favoritoService.verificarFavorito(1L, usuario);
Long count = favoritoService.contarFavoritosDoUsuario(userId);
Long countMercado = favoritoService.contarFavoritosDomercado(1L);
```

### Prioridade
```java
favoritoService.definirPrioridade(1L, 8);  // 0-10
```

---

## 7️⃣ NotificacaoService

### Enviar Notificação
```java
Notificacao notif = notificacaoService.enviarNotificacao(
    usuario,
    "Nova Avaliação",
    "Seu mercado recebeu uma nova avaliação!",
    "AVALIACAO"
);
```

### Listar Notificações
```java
Page<Notificacao> notifs = notificacaoService.obterNotificacionesDoUsuario(userId, pageable);
Page<Notificacao> naoLidas = notificacaoService.obterNaoLidas(userId, pageable);
```

### Marcar como Lida
```java
notificacaoService.marcarComoLida(1L);
notificacaoService.marcarTodosComoLido(usuario);
```

### Contar/Deletar
```java
Long naoLidas = notificacaoService.contarNaoLidas(userId);
notificacaoService.deletarNotificacao(1L);
```

**Tipos:** AVALIACAO, COMENTARIO, PROMOCAO, SISTEMA, MERCADO

---

## 8️⃣ PromocaoService

### Criar Promoção
```java
Promocao promo = promocaoService.criarPromocao(
    new CreatePromocaoRequest(
        "PROMO10",                           // código
        "10% de Desconto",                   // título
        new BigDecimal("10"),                // desconto
        "PERCENTUAL",                        // tipo
        LocalDateTime.now().plusDays(30)     // expiração
    ),
    1L,    // mercadoId
    usuario
);
```

### Validar Código
```java
ValidatePromocaoResponse validation = promocaoService.validarCodigo("PROMO10");
if (validation.isValido()) {
    BigDecimal desconto = promocaoService.aplicarPromocao(
        validation.getPromocaoId(), 
        new BigDecimal("100.00")  // valor compra
    );
}
```

### Listar Promoções
```java
Page<Promocao> promos = promocaoService.obterPromocoesDoMercado(1L, pageable);
Page<Promocao> ativas = promocaoService.obterPromocoesAtivas(pageable);
```

### Atualizar/Deletar
```java
promocaoService.atualizarPromocao(1L, new UpdatePromocaoRequest(...), usuario);
promocaoService.deletarPromocao(1L, usuario);
```

---

## 9️⃣ HorarioFuncionamentoService

### Criar Horário
```java
HorarioFuncionamento horario = horarioService.criarHorario(
    1L,  // mercadoId
    new CreateHorarioRequest(
        "MONDAY",
        LocalTime.of(8, 0),   // abertura
        LocalTime.of(22, 0)   // fechamento
    )
);
```

### Verificar Horários
```java
Boolean aberto = horarioService.verificarSeEstaAberto(1L);
LocalDateTime proxima = horarioService.obterProximaAbertura(1L);
List<HorarioResponse> horarios = horarioService.obterHorariosPorMercado(1L);
```

### Atualizar/Deletar
```java
horarioService.atualizarHorario(1L, new UpdateHorarioRequest(...));
horarioService.deletarHorario(1L);
```

---

## 🔟 RefreshTokenService

### Criar Token
```java
RefreshToken token = refreshTokenService.criarRefreshToken(user);
```

### Validar Token
```java
boolean isValid = refreshTokenService.validarRefreshToken(tokenString);
```

### Renovar Access Token
```java
String novoAccessToken = refreshTokenService.renovarAccessToken(refreshTokenString);
```

### Revogar Token
```java
refreshTokenService.revogarRefreshToken(tokenString);
refreshTokenService.revogarTodosOsTokensDoUsuario(usuario);
```

---

## 1️⃣1️⃣ AuditLogService

### Registrar Ação
```java
auditLogService.registrarAcao(
    usuario,
    "CREATE",           // tipo ação
    "MERCADO",          // tipo entidade
    1L,                 // id entidade
    "Mercado criado"
);

auditLogService.registrarAcaoComValores(
    usuario,
    "UPDATE",
    "MERCADO",
    1L,
    "nome=Carrefour",
    "nome=Carrefour Extra"
);
```

### Consultar Auditoria
```java
Page<AuditLog> auditoria = auditLogService.obterAuditoriaDoUsuario(userId, pageable);
List<AuditLog> entidade = auditLogService.obterAuditoriaEntidade("MERCADO", 1L);
Page<AuditLog> periodo = auditLogService.obterAuditoriaEntreData(inicio, fim, pageable);
```

### Relatórios
```java
Page<AuditLog> creates = auditLogService.obterPorTipoAcao("CREATE", pageable);
Page<AuditLog> mercados = auditLogService.obterPorTipoEntidade("MERCADO", pageable);
Long total = auditLogService.contarAcoesDoUsuario(userId);

// Detectar atividades suspeitas
List<AuditLog> suspeitas = auditLogService.obterAtividadeSuspeita(userId, 30, 5);
```

---

## ⚡ Fluxo Completo: Criar e Avaliar Mercado

```java
// 1. Usuário se registra
JwtAuthenticationResponse authResponse = authService.register(registerRequest);

// 2. Criar mercado
User user = userService.findUserByEmail(authResponse.getUser().getEmail());
Mercado mercado = mercadoService.createMercado(createMercadoRequest, user);

// 3. Admin aprova
mercadoService.aprovarMercado(mercado.getId(), admin.getId());

// 4. Usuário avalia
Avaliacao avaliacao = avaliacaoService.criarAvaliacao(createAvaliacaoRequest, user2);

// 5. Outro usuário comenta
Comentario comentario = comentarioService.criarComentario(
    new CreateComentarioRequest(avaliacao.getId(), "Ótima avaliação!"),
    user3
);

// 6. Responder comentário
comentarioService.responderComentario(
    comentario.getId(),
    new CreateComentarioRequest(null, "Concordo!"),
    user2
);

// 7. Notificar usuário
notificacaoService.enviarNotificacao(
    user,
    "Nova resposta",
    "Sua avaliação recebeu uma resposta!",
    "COMENTARIO"
);

// 8. Mercado é adicionado aos favoritos
favoritoService.adicionarFavorito(mercado.getId(), user);

// 9. Admin cria promoção
Promocao promo = promocaoService.criarPromocao(createPromocaoRequest, mercado.getId(), admin);

// 10. Auditar tudo automaticamente
List<AuditLog> logs = auditLogService.obterAuditoriaEntidade("MERCADO", mercado.getId());
```

---

## 🔐 Tratamento de Exceções

### GlobalExceptionHandler

```java
try {
    // Qualquer operação
} catch (ResourceNotFoundException e) {
    // HTTP 404 - Recurso não encontrado
} catch (ValidationException e) {
    // HTTP 400 - Validação falhou
} catch (UnauthorizedException e) {
    // HTTP 401 - Sem permissão
} catch (MethodArgumentNotValidException e) {
    // HTTP 400 - DTO inválido
} catch (Exception e) {
    // HTTP 500 - Erro genérico
}
```

---

## 📝 Anotações Importantes

```java
@Service              // Registra como bean
@Transactional        // Cria transação (create/update/delete)
@Transactional(readOnly = true)  // Apenas leitura
@Slf4j               // Logging automático
@Scheduled           // Execução agendada
@Autowired           // Injeção de dependência
```

---

## 🎯 Dicas

1. **Sempre usar @Transactional em métodos que modificam dados**
2. **Usar @Transactional(readOnly = true) em consultas**
3. **Validar dados no Service, não apenas no DTO**
4. **Registrar ações importantes no AuditLogService**
5. **Enviar notificações através do NotificacaoService**
6. **Sempre verificar autorização (isOwnerOrAdmin)**
7. **Usar Page/Pageable para listas grandes**
8. **Exceptions são tratadas globalmente pelo GlobalExceptionHandler**

---

**Production-Ready | Java 21 | Spring Boot 3.x**
