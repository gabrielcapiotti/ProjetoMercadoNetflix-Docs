# Guia Completo - Testes Unitários com JUnit 5, Mockito e AssertJ

## 📚 Índice

1. [Padrões e Boas Práticas](#padrões-e-boas-práticas)
2. [Exemplos Práticos](#exemplos-práticos)
3. [Mocking Avançado](#mocking-avançado)
4. [Assertions Avançadas](#assertions-avançadas)
5. [Troubleshooting](#troubleshooting)

---

## Padrões e Boas Práticas

### 1. Estrutura de Classe de Teste

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    // Dependências mockadas
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    // Serviço a ser testado com injeção de mocks
    @InjectMocks
    private UserService userService;
    
    // Dados de teste
    private User testUser;
    private RegisterRequest registerRequest;
    
    // Inicialização
    @BeforeEach
    void setUp() {
        // Inicializar dados de teste
        testUser = new User();
        // ... configurar dados
    }
    
    // Testes
    @Test
    void testXxx() {
        // Teste
    }
}
```

### 2. Padrão AAA

```java
@Test
void testUserCreation() {
    // ARRANGE - Preparar dados e comportamento esperado
    when(userRepository.existsByEmail(anyString()))
        .thenReturn(false);
    when(userRepository.save(any(User.class)))
        .thenReturn(testUser);
    
    // ACT - Executar o método sendo testado
    User createdUser = userService.createUser(registerRequest);
    
    // ASSERT - Verificar resultados
    assertThat(createdUser)
        .isNotNull()
        .extracting(User::getEmail)
        .isEqualTo("test@example.com");
    
    // VERIFY - Verificar interações com mocks
    verify(userRepository, times(1)).save(any(User.class));
    verify(userRepository, never()).delete(any(User.class));
}
```

---

## Exemplos Práticos

### Exemplo 1: Testando Validação

```java
@Test
void testCreateUserWithInvalidEmail() {
    // Arrange
    RegisterRequest invalidRequest = new RegisterRequest();
    invalidRequest.setEmail(""); // Email vazio
    invalidRequest.setPassword("password123");
    invalidRequest.setFullName("Test User");
    
    // Act & Assert
    assertThatThrownBy(() -> userService.createUser(invalidRequest))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("Email")
        .hasNoCause();
    
    // Garantir que nada foi salvo
    verify(userRepository, never()).save(any(User.class));
}
```

### Exemplo 2: Testando com Múltiplos Cenários

```java
@ParameterizedTest
@ValueSource(strings = {"test@example.com", "user@test.com", "admin@domain.com"})
void testCreateUserWithDifferentEmails(String email) {
    // Arrange
    registerRequest.setEmail(email);
    when(userRepository.existsByEmail(email)).thenReturn(false);
    when(userRepository.save(any(User.class))).thenReturn(testUser);
    
    // Act
    User createdUser = userService.createUser(registerRequest);
    
    // Assert
    assertThat(createdUser.getEmail()).isEqualTo(email);
}
```

### Exemplo 3: Testando Comportamento de Exceção

```java
@Test
void testFindUserByIdThrowsException() {
    // Arrange
    when(userRepository.findById(999L))
        .thenThrow(new ResourceNotFoundException("Não encontrado"));
    
    // Act & Assert
    assertThatThrownBy(() -> userService.findUserById(999L))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("Não encontrado")
        .hasNoCause();
}
```

### Exemplo 4: Testando com ArgumentCaptor

```java
@Test
void testAuditLogCapturesCorrectData() {
    // Arrange
    ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
    when(userRepository.save(any(User.class))).thenReturn(testUser);
    
    // Act
    userService.createUser(registerRequest);
    
    // Assert
    verify(auditLogRepository).save(captor.capture());
    AuditLog capturedLog = captor.getValue();
    
    assertThat(capturedLog)
        .extracting(AuditLog::getAction)
        .isEqualTo("CREATE");
    assertThat(capturedLog.getUser()).isEqualTo(testUser);
}
```

### Exemplo 5: Testando Sequência de Chamadas

```java
@Test
void testLoginSequence() {
    // Arrange
    InOrder inOrder = inOrder(authenticationManager, userRepository, auditLogRepository);
    when(authenticationManager.authenticate(any()))
        .thenReturn(authentication);
    when(userRepository.findByEmail(anyString()))
        .thenReturn(Optional.of(testUser));
    
    // Act
    authService.login(loginRequest);
    
    // Assert - Verificar ordem
    inOrder.verify(authenticationManager).authenticate(any());
    inOrder.verify(userRepository).findByEmail(anyString());
    inOrder.verify(auditLogRepository).save(any(AuditLog.class));
}
```

---

## Mocking Avançado

### 1. Configurar Múltiplos Retornos

```java
// Retorno diferente a cada chamada
when(userRepository.findById(1L))
    .thenReturn(Optional.of(testUser))
    .thenReturn(Optional.empty())
    .thenReturn(Optional.of(testUser));

// Ou usar varargs
when(userRepository.findById(1L))
    .thenReturn(
        Optional.of(testUser),
        Optional.empty(),
        Optional.of(testUser)
    );
```

### 2. Usar ArgumentMatcher Personalizado

```java
@Test
void testWithCustomMatcher() {
    // Arrange
    when(userRepository.save(argThat(user -> 
        user.getEmail().contains("@example.com"))))
        .thenReturn(testUser);
    
    // Act & Assert
    User result = userService.createUser(registerRequest);
    assertThat(result).isNotNull();
}
```

### 3. Mock com Resposta Dinâmica (Answer)

```java
@Test
void testDynamicMocking() {
    // Arrange - Mock que retorna o mesmo objeto recebido
    when(userRepository.save(any(User.class)))
        .thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });
    
    // Act
    User result = userService.createUser(registerRequest);
    
    // Assert
    assertThat(result.getId()).isNotNull();
}
```

### 4. Spy em Objetos Reais

```java
@Test
void testWithSpy() {
    // Arrange - Spy permite rastrear chamadas ao objeto real
    UserRepository spyRepository = spy(new UserRepositoryImpl());
    UserService service = new UserService(spyRepository);
    
    // Act
    service.findUserById(1L);
    
    // Assert - Verificar chamada ao método real
    verify(spyRepository, times(1)).findById(1L);
}
```

---

## Assertions Avançadas

### 1. Assertions em Coleções

```java
@Test
void testCollectionAssertions() {
    // Arrange
    List<Mercado> mercados = List.of(
        createMercado("Mercado A", -23.55),
        createMercado("Mercado B", -23.56)
    );
    
    // Act & Assert
    assertThat(mercados)
        .hasSize(2)
        .extracting(Mercado::getNome)
        .contains("Mercado A", "Mercado B");
    
    assertThat(mercados)
        .anySatisfy(m -> assertThat(m.getLatitude()).isNegative());
    
    assertThat(mercados)
        .filteredOn(m -> m.getNome().contains("A"))
        .hasSize(1);
}
```

### 2. Assertions com Extração

```java
@Test
void testExtractingAssertions() {
    // Arrange
    User user = testUser;
    
    // Assert - Extrair campo específico
    assertThat(user)
        .extracting(User::getEmail)
        .isEqualTo("test@example.com");
    
    // Assert - Extrair múltiplos campos
    assertThat(user)
        .extracting(User::getEmail, User::getFullName)
        .containsExactly("test@example.com", "Test User");
    
    // Assert - Extrair de coleção
    List<User> users = List.of(user);
    assertThat(users)
        .extracting(User::getId)
        .contains(1L);
}
```

### 3. Soft Assertions

```java
@Test
void testWithSoftAssertions() {
    // Arrange & Act
    User user = userService.findUserById(1L);
    
    // Assert - Todas as assertions são executadas
    SoftAssertions softly = new SoftAssertions();
    softly.assertThat(user).isNotNull();
    softly.assertThat(user.getEmail()).isEqualTo("test@example.com");
    softly.assertThat(user.isActive()).isTrue();
    softly.assertThat(user.isEmailVerified()).isFalse();
    softly.assertAll(); // Falha se alguma assertion falhar
}
```

### 4. Assertions de Exceção Personalizada

```java
@Test
void testExceptionDetails() {
    // Assert
    assertThatThrownBy(() -> userService.findUserById(999L))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Usuário")
        .hasNoCause()
        .hasStackTraceContaining("UserService");
}
```

---

## Troubleshooting

### Problema 1: Mock Não Está Funcionando

**Sintoma:** `NullPointerException` ao usar mock

```java
// ❌ ERRADO
class UserServiceTest {
    @Mock
    private UserRepository userRepository; // Não é inicializado
    
    @Test
    void test() {
        when(userRepository.findById(1L)).thenReturn(...); // Null!
    }
}

// ✅ CORRETO
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository; // Inicializado pelo MockitoExtension
    
    @Test
    void test() {
        when(userRepository.findById(1L)).thenReturn(...); // Funciona!
    }
}
```

### Problema 2: @InjectMocks Não Injeta Mocks

**Sintoma:** Campo nulo mesmo com `@InjectMocks`

```java
// ❌ ERRADO - Constructor privado
class UserService {
    private UserRepository userRepository;
    
    private UserService() {} // Constructor privado bloqueador
}

// ✅ CORRETO - Permitir injeção
class UserService {
    private UserRepository userRepository;
    
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
```

### Problema 3: Verify Falha Inesperadamente

**Sintoma:** `verify()` falha mesmo que método tenha sido chamado

```java
// ❌ ERRADO - Matcher incompatível
when(userRepository.findById(anyLong()))
    .thenReturn(Optional.of(testUser));

// ... código ...

verify(userRepository).findById(1L); // Pode falhar!

// ✅ CORRETO - Usar mesmo matcher
verify(userRepository).findById(anyLong());
// ou
verify(userRepository).findById(eq(1L));
```

### Problema 4: Teste Não Limpa Estado

**Sintoma:** Testes falham quando rodados juntos, mas passam isolados

```java
// ❌ ERRADO - Estado compartilhado
class UserServiceTest {
    private static User sharedUser; // Compartilhado entre testes!
    
    @Test
    void test1() { sharedUser.setId(1L); }
    
    @Test
    void test2() { 
        // sharedUser.getId() pode ser 1L!
    }
}

// ✅ CORRETO - Estado isolado
class UserServiceTest {
    private User testUser; // Nova instância a cada teste
    
    @BeforeEach
    void setUp() {
        testUser = new User(); // Criar novo a cada teste
    }
    
    @Test
    void test1() { testUser.setId(1L); }
    
    @Test
    void test2() { 
        // testUser.getId() é null
    }
}
```

---

## Checklist Final

- [ ] Todos os testes usam `@Test` do JUnit 5
- [ ] Classe usa `@ExtendWith(MockitoExtension.class)`
- [ ] Mocks são declarados com `@Mock`
- [ ] Serviço testado usa `@InjectMocks`
- [ ] Setup é feito em `@BeforeEach`
- [ ] Testes seguem padrão AAA
- [ ] Assertions usam AssertJ (`assertThat()`)
- [ ] Interações verificadas com `verify()`
- [ ] Testes são isolados (sem estado compartilhado)
- [ ] Nomes são descritivos (`testXxxSuccess`, `testXxxError`)
- [ ] Testes executam com `mvn test`

---

**Criado:** 30 de janeiro de 2026
**Versão:** 1.0
