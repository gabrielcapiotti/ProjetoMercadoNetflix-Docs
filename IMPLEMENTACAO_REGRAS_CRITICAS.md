# 🔧 PLANO DE IMPLEMENTAÇÃO - Regras Faltando

## PRIORIDADE 1: CRÍTICO - Validação de Avaliação Duplicada

### Problema
```java
// ❌ ATUALMENTE: Permite múltiplas avaliações do mesmo usuário
POST /api/avaliacoes
{
  "mercadoId": 1,
  "rating": 5,
  "comentario": "Ótimo!"
}
// Segunda chamada com mesmo usuário? ✓ Aceita (ERRADO!)
```

### Solução

**Arquivo:** `src/main/java/com/netflix/mercado/service/AvaliacaoService.java`

```java
@Override
public AvaliacaoDTO criarAvaliacao(Long mercadoId, AvaliacaoDTO avaliacaoDTO, User usuario) {
    Mercado mercado = mercadoRepository.findById(mercadoId)
        .orElseThrow(() -> new ResourceNotFoundException("Mercado não encontrado"));
    
    // ✅ NOVO: Validar duplicidade
    Optional<Avaliacao> avaliacaoExistente = 
        avaliacaoRepository.findByMercadoAndCriadoPor(mercado, usuario);
    
    if (avaliacaoExistente.isPresent()) {
        throw new ValidationException(
            "Você já avaliou este mercado. Edite sua avaliação anterior em vez de criar uma nova."
        );
    }
    
    // Resto do código...
    Avaliacao avaliacao = new Avaliacao();
    avaliacao.setMercado(mercado);
    avaliacao.setRating(avaliacaoDTO.getRating());
    avaliacao.setComentario(avaliacaoDTO.getComentario());
    avaliacao.setCriadoPor(usuario);
    
    Avaliacao salva = avaliacaoRepository.save(avaliacao);
    
    // ✅ NOVO: Atualizar média
    atualizarAvaliacaoMedia(mercado);
    
    return modelMapper.map(salva, AvaliacaoDTO.class);
}

// ✅ NOVO: Método auxiliar
private void atualizarAvaliacaoMedia(Mercado mercado) {
    Double media = avaliacaoRepository.calcularMediaAvaliacoes(mercado);
    Long totalAvaliacoes = avaliacaoRepository.countByMercadoAndDeletedAtIsNull(mercado);
    
    mercado.setAvaliacaoMedia(media != null ? media : 0.0);
    mercado.setTotalAvaliacoes(totalAvaliacoes.intValue());
    
    mercadoRepository.save(mercado);
}
```

**Repository Method (se não existir):**
```java
@Query("SELECT a FROM Avaliacao a WHERE a.mercado = :mercado AND a.criadoPor = :usuario AND a.deletedAt IS NULL")
Optional<Avaliacao> findByMercadoAndCriadoPor(@Param("mercado") Mercado mercado, @Param("usuario") User usuario);
```

---

## PRIORIDADE 2: CRÍTICO - Atualização Automática de Média

### Problema
```java
// Avaliar mercado
POST /api/avaliacoes → Rating: 5
// Editar avaliação
PUT /api/avaliacoes/1 → Rating: 2
// Média NÃO atualiza automaticamente ❌
```

### Solução

**Adicionar ao AvaliacaoService:**

```java
@Override
public AvaliacaoDTO atualizarAvaliacao(Long avaliacaoId, AvaliacaoDTO avaliacaoDTO, User usuario) {
    Avaliacao avaliacao = avaliacaoRepository.findById(avaliacaoId)
        .orElseThrow(() -> new ResourceNotFoundException("Avaliação não encontrada"));
    
    // Verificar ownership
    if (!avaliacao.getCriadoPor().getId().equals(usuario.getId())) {
        throw new ForbiddenException("Você não pode editar essa avaliação");
    }
    
    // Update fields
    Integer ratingAntigo = avaliacao.getRating();
    avaliacao.setRating(avaliacaoDTO.getRating());
    avaliacao.setComentario(avaliacaoDTO.getComentario());
    
    Avaliacao atualizada = avaliacaoRepository.save(avaliacao);
    
    // ✅ AUTOMÁTICO: Atualizar média se rating mudou
    if (!ratingAntigo.equals(avaliacaoDTO.getRating())) {
        atualizarAvaliacaoMedia(avaliacao.getMercado());
    }
    
    return modelMapper.map(atualizada, AvaliacaoDTO.class);
}

@Override
public void deletarAvaliacao(Long avaliacaoId, User usuario) {
    Avaliacao avaliacao = avaliacaoRepository.findById(avaliacaoId)
        .orElseThrow(() -> new ResourceNotFoundException("Avaliação não encontrada"));
    
    if (!avaliacao.getCriadoPor().getId().equals(usuario.getId())) {
        throw new ForbiddenException("Você não pode deletar essa avaliação");
    }
    
    Mercado mercado = avaliacao.getMercado();
    avaliacao.setDeletedAt(LocalDateTime.now());
    avaliacaoRepository.save(avaliacao);
    
    // ✅ AUTOMÁTICO: Recalcular média após deleção
    atualizarAvaliacaoMedia(mercado);
}
```

---

## PRIORIDADE 3: CRÍTICO - Validação de Sobreposição de Horários

### Problema
```
Dia: SEGUNDA (0)
Período 1: 08:00 - 12:00
Período 2: 10:00 - 14:00  ← SOBREPOSTO! Deveria recusar
```

### Solução

**Arquivo:** `src/main/java/com/netflix/mercado/service/HorarioFuncionamentoService.java`

```java
@Override
public HorarioFuncionamentoDTO criarHorario(Long mercadoId, HorarioFuncionamentoDTO dto, User usuario) {
    Mercado mercado = mercadoRepository.findById(mercadoId)
        .orElseThrow(() -> new ResourceNotFoundException("Mercado não encontrado"));
    
    // Verificar ownership
    if (!mercado.getCriadoPor().getId().equals(usuario.getId())) {
        throw new ForbiddenException("Você não pode adicionar horários a este mercado");
    }
    
    // ✅ NOVO: Validar sobreposição
    validarSobreposicaoHorarios(mercado, dto);
    
    HorarioFuncionamento horario = new HorarioFuncionamento();
    horario.setMercado(mercado);
    horario.setDiaSemana(dto.getDiaSemana());
    horario.setHorarioAbertura(dto.getHorarioAbertura());
    horario.setHorarioFechamento(dto.getHorarioFechamento());
    
    HorarioFuncionamento salvo = horarioRepository.save(horario);
    return modelMapper.map(salvo, HorarioFuncionamentoDTO.class);
}

// ✅ NOVO: Método de validação
private void validarSobreposicaoHorarios(Mercado mercado, HorarioFuncionamentoDTO novoHorario) {
    List<HorarioFuncionamento> horariosExistentes = horarioRepository
        .findByMercadoAndDiaSemanaAndDeletedAtIsNull(mercado, novoHorario.getDiaSemana());
    
    for (HorarioFuncionamento existente : horariosExistentes) {
        if (temSobreposicao(existente.getHorarioAbertura(), 
                            existente.getHorarioFechamento(),
                            novoHorario.getHorarioAbertura(), 
                            novoHorario.getHorarioFechamento())) {
            throw new ValidationException(
                String.format("Horário sobreposto! Já existe período de %s às %s no dia %s",
                    existente.getHorarioAbertura(),
                    existente.getHorarioFechamento(),
                    getDiaSemanaName(novoHorario.getDiaSemana()))
            );
        }
    }
}

// Verificar se dois períodos se sobrepõem
private boolean temSobreposicao(LocalTime ab1, LocalTime fe1, LocalTime ab2, LocalTime fe2) {
    // Período 1: [ab1, fe1)  Período 2: [ab2, fe2)
    // Sobrepõe se: ab1 < fe2 E ab2 < fe1
    return ab1.isBefore(fe2) && ab2.isBefore(fe1);
}

private String getDiaSemanaName(Integer dia) {
    String[] dias = {"Domingo", "Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado"};
    return dia >= 0 && dia < dias.length ? dias[dia] : "Desconhecido";
}
```

**Repository Method:**
```java
@Query("SELECT h FROM HorarioFuncionamento h WHERE h.mercado = :mercado AND h.diaSemana = :dia AND h.deletedAt IS NULL")
List<HorarioFuncionamento> findByMercadoAndDiaSemanaAndDeletedAtIsNull(
    @Param("mercado") Mercado mercado, 
    @Param("dia") Integer diaSemana
);
```

---

## PRIORIDADE 4: CRÍTICO - Integração de Notificações

### Problema
```
Evento: Novo mercado criado
Resultado: Nenhuma notificação enviada ❌
```

### Solução

**Arquivo:** `src/main/java/com/netflix/mercado/service/MercadoService.java`

```java
@Autowired
private NotificacaoService notificacaoService;

@Override
public MercadoDTO criarMercado(MercadoDTO mercadoDTO, User usuario) {
    // ... validações e criação ...
    
    Mercado mercado = new Mercado();
    mercado.setNome(mercadoDTO.getNome());
    // ... outros campos ...
    mercado.setCriadoPor(usuario);
    
    Mercado salvo = mercadoRepository.save(mercado);
    
    // ✅ NOVO: Notificar administradores
    notificaAdmins(salvo, "Novo mercado criado: " + salvo.getNome());
    
    auditarOperacao("CRIAR_MERCADO", "Mercado criado: " + salvo.getNome(), usuario);
    
    return modelMapper.map(salvo, MercadoDTO.class);
}

@Override
public MercadoDTO aprovarMercado(Long mercadoId, User admin) {
    Mercado mercado = mercadoRepository.findById(mercadoId)
        .orElseThrow(() -> new ResourceNotFoundException("Mercado não encontrado"));
    
    mercado.setAprovado(true);
    Mercado aprovado = mercadoRepository.save(mercado);
    
    // ✅ NOVO: Notificar seller
    notificacaoService.criarNotificacao(
        mercado.getCriadoPor(),
        TipoNotificacao.MERCADO_APROVADO,
        "Seu mercado '" + mercado.getNome() + "' foi aprovado!"
    );
    
    auditarOperacao("APROVAR_MERCADO", "Mercado aprovado: " + mercado.getNome(), admin);
    
    return modelMapper.map(aprovado, MercadoDTO.class);
}

@Override
public MercadoDTO rejeitarMercado(Long mercadoId, String motivo, User admin) {
    Mercado mercado = mercadoRepository.findById(mercadoId)
        .orElseThrow(() -> new ResourceNotFoundException("Mercado não encontrado"));
    
    mercado.setAprovado(false);
    Mercado rejeitado = mercadoRepository.save(mercado);
    
    // ✅ NOVO: Notificar seller com motivo
    notificacaoService.criarNotificacao(
        mercado.getCriadoPor(),
        TipoNotificacao.MERCADO_REJEITADO,
        "Seu mercado foi rejeitado. Motivo: " + motivo
    );
    
    auditarOperacao("REJEITAR_MERCADO", "Mercado rejeitado: " + mercado.getNome() + " - Motivo: " + motivo, admin);
    
    return modelMapper.map(rejeitado, MercadoDTO.class);
}

// ✅ NOVO: Método auxiliar
private void notificaAdmins(Mercado mercado, String mensagem) {
    List<User> admins = userRepository.findByRole_NameAndDeletedAtIsNull(Role.RoleName.ADMIN);
    for (User admin : admins) {
        notificacaoService.criarNotificacao(
            admin,
            TipoNotificacao.NOVO_MERCADO,
            mensagem
        );
    }
}
```

**Também integrar em AvaliacaoService:**
```java
@Override
public AvaliacaoDTO criarAvaliacao(Long mercadoId, AvaliacaoDTO avaliacaoDTO, User usuario) {
    // ... criação ...
    
    // ✅ NOVO: Notificar seller
    notificacaoService.criarNotificacao(
        mercado.getCriadoPor(),
        TipoNotificacao.NOVA_AVALIACAO,
        usuario.getNome() + " avaliou seu mercado com " + avaliacaoDTO.getRating() + " estrelas"
    );
    
    return modelMapper.map(salva, AvaliacaoDTO.class);
}
```

---

## PRIORIDADE 5: MÉDIO - Cálculo de Percentual de Aprovação

### Problema
```
Mercado: ABC
Total avaliações: 10
Avaliações 4-5 estrelas: 8
Percentual de aprovação: ??? (não calculado)
```

### Solução

**Adicionar ao Mercado Entity:**
```java
@Column(name = "percentual_aprovacao")
private Double percentualAprovacao = 0.0;
```

**Adicionar ao AvaliacaoService:**
```java
private void atualizarAvaliacaoMedia(Mercado mercado) {
    Double media = avaliacaoRepository.calcularMediaAvaliacoes(mercado);
    Long totalAvaliacoes = avaliacaoRepository.countByMercadoAndDeletedAtIsNull(mercado);
    
    // ✅ NOVO: Calcular aprovação
    Long avaliacoesBoas = avaliacaoRepository.countByMercadoAndRatingGreaterThanEqualAndDeletedAtIsNull(mercado, 4);
    Double percentualAprovacao = totalAvaliacoes > 0 
        ? (avaliacoesBoas.doubleValue() / totalAvaliacoes.doubleValue()) * 100 
        : 0.0;
    
    mercado.setAvaliacaoMedia(media != null ? media : 0.0);
    mercado.setTotalAvaliacoes(totalAvaliacoes.intValue());
    mercado.setPercentualAprovacao(percentualAprovacao);
    
    mercadoRepository.save(mercado);
}
```

**Repository Methods:**
```java
@Query("SELECT COUNT(a) FROM Avaliacao a WHERE a.mercado = :mercado AND a.rating >= :rating AND a.deletedAt IS NULL")
Long countByMercadoAndRatingGreaterThanEqualAndDeletedAtIsNull(
    @Param("mercado") Mercado mercado,
    @Param("rating") Integer rating
);
```

---

## PRIORIDADE 6: MÉDIO - Distribuição por Estrelas

### DTO Novo
```java
@Data
@AllArgsConstructor
public class RatingDistributionDTO {
    private Long total;
    private Map<Integer, Long> distribuicaoPorEstrela; // 1->10, 2->5, 3->2, 4->8, 5->15
    private Double media;
    private Double percentualAprovacao;
    
    public Map<String, Object> getPercentuais() {
        Map<String, Object> resultado = new LinkedHashMap<>();
        for (int i = 1; i <= 5; i++) {
            Long count = distribuicaoPorEstrela.getOrDefault(i, 0L);
            Double percentual = total > 0 ? (count.doubleValue() / total) * 100 : 0.0;
            resultado.put(i + " estrelas", percentual + "%");
        }
        return resultado;
    }
}
```

**Controller:**
```java
@GetMapping("/mercados/{mercadoId}/avaliacoes/distribuicao")
public ResponseEntity<RatingDistributionDTO> getDistribuicaoAvaliacoes(
    @PathVariable Long mercadoId
) {
    return ResponseEntity.ok(avaliacaoService.getDistribuicaoAvaliacoes(mercadoId));
}
```

**Service:**
```java
public RatingDistributionDTO getDistribuicaoAvaliacoes(Long mercadoId) {
    Mercado mercado = mercadoRepository.findById(mercadoId)
        .orElseThrow(() -> new ResourceNotFoundException("Mercado não encontrado"));
    
    Map<Integer, Long> distribuicao = new TreeMap<>();
    for (int i = 1; i <= 5; i++) {
        Long count = avaliacaoRepository.countByMercadoAndRatingAndDeletedAtIsNull(mercado, i);
        distribuicao.put(i, count);
    }
    
    Long total = avaliacaoRepository.countByMercadoAndDeletedAtIsNull(mercado);
    Double media = avaliacaoRepository.calcularMediaAvaliacoes(mercado);
    Double percentualAprovacao = mercado.getPercentualAprovacao();
    
    return new RatingDistributionDTO(total, distribuicao, media, percentualAprovacao);
}
```

---

## RESUMO DE IMPLEMENTAÇÕES NECESSÁRIAS

| Regra | Arquivo | Método | Tempo Est. |
|-------|---------|--------|-----------|
| Validar duplicidade | AvaliacaoService.java | criarAvaliacao() | 30 min |
| Atualizar média automática | AvaliacaoService.java | atualizarAvaliacao(), deletarAvaliacao() | 30 min |
| Validar sobreposição horários | HorarioFuncionamentoService.java | criarHorario() | 45 min |
| Integrar notificações | MercadoService.java, AvaliacaoService.java | vários métodos | 1 hora |
| Percentual aprovação | AvaliacaoService.java, Mercado.java | atualizarAvaliacaoMedia() | 30 min |
| Distribuição por estrelas | AvaliacaoService.java, AvaliacaoController.java | getDistribuicaoAvaliacoes() | 45 min |

**Total Estimado:** 4 horas

---

**Próximo Passo:** Implementar essas 6 melhorias e depois rodar os testes para validar

