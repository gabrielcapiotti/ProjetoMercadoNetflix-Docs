# Exemplos Avançados de Testes Unitários

## 🎯 Casos de Uso Complexos

Este documento fornece exemplos avançados que podem ser usados como referência para estender os testes existentes.

---

## 1. Teste com Múltiplos Mocks em Sequência

```java
@Test
void testCompleteUserRegistrationFlow() {
    // Arrange - Configurar toda a sequência
    when(userRepository.existsByEmail(anyString())).thenReturn(false);
    when(userRepository.existsByCpf(anyString())).thenReturn(false);
    when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
    when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
    when(userRepository.save(any(User.class))).thenReturn(testUser);
    when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());
    
    // Act - Executar fluxo completo
    User createdUser = userService.createUser(registerRequest);
    
    // Assert - Validar resultado
    assertThat(createdUser)
        .isNotNull()
        .hasFieldOrPropertyWithValue("email", "test@example.com")
        .hasFieldOrPropertyWithValue("active", true)
        .hasFieldOrPropertyWithValue("emailVerified", false);
    
    // Verify - Verificar ordem de chamadas
    InOrder inOrder = inOrder(userRepository, roleRepository, auditLogRepository);
    inOrder.verify(userRepository).existsByEmail(anyString());
    inOrder.verify(roleRepository).findByName("ROLE_USER");
    inOrder.verify(userRepository).save(any(User.class));
    inOrder.verify(auditLogRepository).save(any(AuditLog.class));
}
```

---

## 2. Teste com ArgumentCaptor para Validar Dados

```java
@Test
void testAuditLogContainsCorrectDetails() {
    // Arrange
    ArgumentCaptor<AuditLog> auditCaptor = ArgumentCaptor.forClass(AuditLog.class);
    when(userRepository.save(any(User.class))).thenReturn(testUser);
    
    // Act
    userService.createUser(registerRequest);
    
    // Assert - Capturar o argumento
    verify(auditLogRepository).save(auditCaptor.capture());
    AuditLog capturedLog = auditCaptor.getValue();
    
    // Validar detalhes do log
    assertThat(capturedLog)
        .extracting(
            AuditLog::getAction,
            AuditLog::getEntityType,
            AuditLog::getUser
        )
        .containsExactly(
            "CREATE",
            "USER",
            testUser
        );
    
    assertThat(capturedLog.getDescription())
        .contains("Novo usuário criado")
        .contains(testUser.getEmail());
}
```

---

## 3. Teste com Resposta Dinâmica (Answer)

```java
@Test
void testPasswordEncodingInUserCreation() {
    // Arrange - Mock que rastreia a senha
    when(userRepository.existsByEmail(anyString())).thenReturn(false);
    when(userRepository.existsByCpf(anyString())).thenReturn(false);
    when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
    
    // Mock Answer para codificar senha
    when(passwordEncoder.encode(anyString()))
        .thenAnswer(invocation -> {
            String password = invocation.getArgument(0);
            return "hashed_" + password + "_hashed";
        });
    
    when(userRepository.save(any(User.class)))
        .thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });
    
    // Act
    User createdUser = userService.createUser(registerRequest);
    
    // Assert
    assertThat(createdUser.getPasswordHash())
        .startsWith("hashed_")
        .endsWith("_hashed")
        .contains("password123");
}
```

---

## 4. Teste Parametrizado com Diferentes Cenários

```java
@ParameterizedTest
@CsvSource({
    "test@example.com,validPassword123,true",
    "user@domain.com,P@ssw0rd,true",
    "admin@company.com,SecurePass99,true",
    "invalid-email,short,false",
    "duplicate@mail.com,password,false"
})
void testLoginWithVariousCredentials(String email, String password, boolean shouldSucceed) {
    // Arrange
    LoginRequest request = new LoginRequest();
    request.setEmail(email);
    request.setPassword(password);
    
    if (shouldSucceed) {
        when(authenticationManager.authenticate(any()))
            .thenReturn(authentication);
        when(userRepository.findByEmail(email))
            .thenReturn(Optional.of(testUser));
        when(jwtTokenProvider.generateToken(any()))
            .thenReturn("token_" + email);
    } else {
        when(authenticationManager.authenticate(any()))
            .thenThrow(new BadCredentialsException("Invalid credentials"));
    }
    
    // Act & Assert
    if (shouldSucceed) {
        JwtAuthenticationResponse response = authService.login(request);
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).contains(email);
    } else {
        assertThatThrownBy(() -> authService.login(request))
            .isInstanceOf(ValidationException.class);
    }
}
```

---

## 5. Teste com Soft Assertions

```java
@Test
void testUserPropertiesMultiple() {
    // Arrange
    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    
    // Act
    User foundUser = userService.findUserById(1L);
    
    // Assert - Todas as assertions rodam, mesmo se uma falhar
    SoftAssertions softly = new SoftAssertions();
    softly.assertThat(foundUser).isNotNull();
    softly.assertThat(foundUser.getId()).isEqualTo(1L);
    softly.assertThat(foundUser.getEmail()).isEqualTo("test@example.com");
    softly.assertThat(foundUser.getFullName()).isEqualTo("Test User");
    softly.assertThat(foundUser.isActive()).isTrue();
    softly.assertThat(foundUser.isEmailVerified()).isFalse();
    softly.assertThat(foundUser.isTwoFactorEnabled()).isFalse();
    softly.assertAll(); // Falha se qualquer assertion falhou
}
```

---

## 6. Teste com Exception Customizada

```java
@Test
void testHandleSpecificExceptionDetails() {
    // Arrange
    ResourceNotFoundException expectedException = 
        new ResourceNotFoundException("Usuário 999 não encontrado");
    when(userRepository.findById(999L))
        .thenThrow(expectedException);
    
    // Act & Assert
    assertThatThrownBy(() -> userService.findUserById(999L))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("Usuário 999 não encontrado")
        .hasNoCause()
        .hasSuppressedExceptions(); // Verificar exceções suprimidas
}
```

---

## 7. Teste de Coleção com Filtros

```java
@Test
void testBuscarMercadosComFiltros() {
    // Arrange
    List<Mercado> allMercados = List.of(
        createMercado("Mercado A", "São Paulo", true),
        createMercado("Mercado B", "Rio de Janeiro", false),
        createMercado("Mercado C", "São Paulo", true),
        createMercado("Mercado D", "Belo Horizonte", false)
    );
    
    when(mercadoRepository.buscarPorProximidade(anyDouble(), anyDouble(), anyDouble()))
        .thenReturn(allMercados);
    
    // Act
    List<Mercado> resultado = mercadoService.buscarPorProximidade(-23.55, -46.63, 50.0);
    
    // Assert - Múltiplos filtros
    assertThat(resultado)
        .hasSize(4)
        .extracting(Mercado::getNome)
        .contains("Mercado A", "Mercado B", "Mercado C", "Mercado D");
    
    assertThat(resultado)
        .filteredOn(m -> m.getCidade().equals("São Paulo"))
        .hasSize(2)
        .extracting(Mercado::getNome)
        .contains("Mercado A", "Mercado C");
    
    assertThat(resultado)
        .anySatisfy(m -> assertThat(m.isAprovado()).isTrue())
        .allSatisfy(m -> assertThat(m.getNome()).isNotNull());
}
```

---

## 8. Teste de Validação de Regras Complexas

```java
@Test
void testValidateComplexBusinessRules() {
    // Arrange
    UpdateMercadoRequest updateRequest = new UpdateMercadoRequest();
    updateRequest.setNome(""); // Inválido
    updateRequest.setLatitude(91.0); // Fora do range (-90, 90)
    updateRequest.setLongitude(181.0); // Fora do range (-180, 180)
    
    // Act & Assert
    assertThatThrownBy(() -> mercadoService.updateMercado(1L, updateRequest, testOwner))
        .isInstanceOf(ValidationException.class)
        .satisfies(e -> {
            String message = e.getMessage();
            assertThat(message).contains("Nome");
            assertThat(message).containsAnyOf("latitude", "longitude");
        });
}
```

---

## 9. Teste com Verificação de Múltiplas Chamadas

```java
@Test
void testVerifyMultipleInteractions() {
    // Arrange
    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    when(userRepository.save(any(User.class))).thenReturn(testUser);
    when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());
    
    ChangePasswordRequest changeRequest = new ChangePasswordRequest();
    changeRequest.setCurrentPassword("password123");
    changeRequest.setNewPassword("newPassword456");
    
    when(passwordEncoder.matches("password123", testUser.getPasswordHash()))
        .thenReturn(true);
    when(passwordEncoder.encode("newPassword456"))
        .thenReturn("encoded_new_password");
    
    // Act
    userService.changePassword(1L, changeRequest);
    
    // Assert - Verificar múltiplas interações
    verify(userRepository, times(1)).findById(1L);
    verify(passwordEncoder, times(1)).matches("password123", testUser.getPasswordHash());
    verify(passwordEncoder, times(1)).encode("newPassword456");
    verify(userRepository, times(1)).save(any(User.class));
    verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    
    // Verificar que outros métodos NÃO foram chamados
    verify(userRepository, never()).delete(any(User.class));
    verify(userRepository, never()).deleteAll();
}
```

---

## 10. Teste com Dados Complexos

```java
@Test
void testMercadoWithComplexData() {
    // Arrange - Criar mercado com dados detalhados
    Mercado complexMercado = new Mercado();
    complexMercado.setId(1L);
    complexMercado.setNome("Mercado Premium");
    complexMercado.setDescricao("Mercado de alta qualidade com produtos selecionados");
    complexMercado.setCidade("São Paulo");
    complexMercado.setBairro("Jardins");
    complexMercado.setRua("Avenida Paulista");
    complexMercado.setNumero("1578");
    complexMercado.setLatitude(-23.561414);
    complexMercado.setLongitude(-46.656188);
    complexMercado.setAvaliacaoMedia(4.8);
    complexMercado.setTotalAvaliacoes(150L);
    
    when(mercadoRepository.findById(1L)).thenReturn(Optional.of(complexMercado));
    
    // Act
    Mercado found = mercadoService.getMercadoById(1L);
    
    // Assert - Validar todos os campos
    assertThat(found)
        .isNotNull()
        .hasFieldOrPropertyWithValue("nome", "Mercado Premium")
        .hasFieldOrPropertyWithValue("cidade", "São Paulo")
        .hasFieldOrPropertyWithValue("bairro", "Jardins")
        .hasFieldOrPropertyWithValue("latitude", -23.561414)
        .hasFieldOrPropertyWithValue("longitude", -46.656188)
        .hasFieldOrPropertyWithValue("avaliacaoMedia", 4.8)
        .hasFieldOrPropertyWithValue("totalAvaliacoes", 150L);
    
    assertThat(found.getDescricao())
        .isNotNull()
        .isNotEmpty()
        .contains("qualidade");
}
```

---

## 📝 Boas Práticas ao Usar Estes Exemplos

1. **Isolamento**: Cada teste deve ser independente
2. **Clareza**: Nomes descritivos e estrutura AAA
3. **Modularidade**: Reusar métodos helper para criação de dados
4. **Verificação**: Sempre verificar comportamento, não apenas retorno
5. **Documentação**: Adicionar comentários para testes complexos

---

## 🔧 Padrões de Naming

```java
// ✅ BOM - Descreve o quê está sendo testado
testCreateUserSuccess
testCreateUserWithInvalidEmail
testBuscarMercadosPorProximidadeWithNoResults

// ❌ RUIM - Genérico e não informativo
test1
testMethod
testXxx
```

---

**Versão:** 1.0  
**Data:** 30 de janeiro de 2026  
**Status:** Pronto para uso em testes avançados
