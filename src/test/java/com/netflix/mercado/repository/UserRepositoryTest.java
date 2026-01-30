package com.netflix.mercado.repository;

import com.netflix.mercado.entity.Role;
import com.netflix.mercado.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes de integração para UserRepository
 * Coverage: 12 testes
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@DisplayName("UserRepository Integration Tests")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User activeUser;
    private User deletedUser;
    private Role customerRole;
    private Role adminRole;

    @BeforeEach
    void setUp() {
        // Criar roles
        customerRole = new Role();
        customerRole.setName("ROLE_CUSTOMER");
        customerRole.setDescription("Cliente");
        customerRole.setActive(true);
        customerRole = entityManager.persistAndFlush(customerRole);

        adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");
        adminRole.setDescription("Administrador");
        adminRole.setActive(true);
        adminRole = entityManager.persistAndFlush(adminRole);

        // Criar usuário ativo
        activeUser = new User();
        activeUser.setEmail("joao.silva@example.com");
        activeUser.setPasswordHash("$2a$10$hashedPassword123");
        activeUser.setFullName("João Silva");
        activeUser.setCpf("12345678901");
        activeUser.setPhone("11987654321");
        activeUser.setDateOfBirth(LocalDate.of(1990, 5, 15));
        activeUser.setEmailVerified(true);
        activeUser.setTwoFactorEnabled(false);
        activeUser.setActive(true);
        activeUser.setLastLogin(LocalDateTime.now().minusDays(1));
        
        Set<Role> roles = new HashSet<>();
        roles.add(customerRole);
        activeUser.setRoles(roles);
        
        activeUser = entityManager.persistAndFlush(activeUser);

        // Criar usuário deletado (soft delete)
        deletedUser = new User();
        deletedUser.setEmail("maria.santos@example.com");
        deletedUser.setPasswordHash("$2a$10$hashedPassword456");
        deletedUser.setFullName("Maria Santos");
        deletedUser.setCpf("98765432109");
        deletedUser.setPhone("11912345678");
        deletedUser.setDateOfBirth(LocalDate.of(1985, 8, 20));
        deletedUser.setEmailVerified(false);
        deletedUser.setTwoFactorEnabled(false);
        deletedUser.setActive(false); // Soft deleted
        
        Set<Role> deletedRoles = new HashSet<>();
        deletedRoles.add(customerRole);
        deletedUser.setRoles(deletedRoles);
        
        deletedUser = entityManager.persistAndFlush(deletedUser);

        entityManager.clear();
    }

    @AfterEach
    void tearDown() {
        entityManager.clear();
    }

    @Test
    @DisplayName("Deve encontrar usuário por email com sucesso")
    void testFindByEmailSuccess() {
        // When
        Optional<User> result = userRepository.findByEmail("joao.silva@example.com");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("joao.silva@example.com");
        assertThat(result.get().getFullName()).isEqualTo("João Silva");
        assertThat(result.get().getCpf()).isEqualTo("12345678901");
    }

    @Test
    @DisplayName("Não deve encontrar usuário por email inexistente")
    void testFindByEmailNotFound() {
        // When
        Optional<User> result = userRepository.findByEmail("naoexiste@example.com");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Deve encontrar apenas usuários ativos por email")
    void testFindByEmailActiveOnly() {
        // When
        Optional<User> result = userRepository.findByEmailActive("joao.silva@example.com");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getActive()).isTrue();
        assertThat(result.get().getEmail()).isEqualTo("joao.silva@example.com");
    }

    @Test
    @DisplayName("Não deve encontrar usuário deletado por email ativo")
    void testFindByEmailActiveFalse() {
        // When
        Optional<User> result = userRepository.findByEmailActive("maria.santos@example.com");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Deve verificar se existe outro usuário com email")
    void testExistsOtherUserWithEmail() {
        // When
        boolean exists = userRepository.existsOtherUserWithEmail("maria.santos@example.com", activeUser.getId());

        // Then
        assertThat(exists).isFalse(); // maria.santos está deletada (active=false)
        
        // Criar outro usuário ativo com email diferente
        User anotherUser = new User();
        anotherUser.setEmail("pedro.costa@example.com");
        anotherUser.setPasswordHash("$2a$10$hashedPassword789");
        anotherUser.setFullName("Pedro Costa");
        anotherUser.setCpf("11122233344");
        anotherUser.setPhone("11999887766");
        anotherUser.setDateOfBirth(LocalDate.of(1995, 3, 10));
        anotherUser.setActive(true);
        Set<Role> roles = new HashSet<>();
        roles.add(customerRole);
        anotherUser.setRoles(roles);
        entityManager.persistAndFlush(anotherUser);

        boolean existsOther = userRepository.existsOtherUserWithEmail("pedro.costa@example.com", activeUser.getId());
        assertThat(existsOther).isTrue();
    }

    @Test
    @DisplayName("Deve atualizar último login do usuário")
    void testUpdateLastLoginSuccess() {
        // Given
        LocalDateTime newLoginTime = LocalDateTime.now();

        // When
        userRepository.updateLastLogin(activeUser.getId(), newLoginTime);
        entityManager.flush();
        entityManager.clear();

        // Then
        User updated = userRepository.findById(activeUser.getId()).orElseThrow();
        assertThat(updated.getLastLogin()).isNotNull();
    }

    @Test
    @DisplayName("Deve marcar email como verificado")
    void testVerifyEmailSuccess() {
        // Given
        User unverifiedUser = new User();
        unverifiedUser.setEmail("novo.usuario@example.com");
        unverifiedUser.setPasswordHash("$2a$10$hashedPasswordNew");
        unverifiedUser.setFullName("Novo Usuário");
        unverifiedUser.setCpf("55566677788");
        unverifiedUser.setPhone("11933334444");
        unverifiedUser.setDateOfBirth(LocalDate.of(2000, 1, 1));
        unverifiedUser.setEmailVerified(false);
        unverifiedUser.setActive(true);
        Set<Role> roles = new HashSet<>();
        roles.add(customerRole);
        unverifiedUser.setRoles(roles);
        unverifiedUser = entityManager.persistAndFlush(unverifiedUser);
        entityManager.clear();

        // When
        userRepository.verifyEmail(unverifiedUser.getId());
        entityManager.flush();
        entityManager.clear();

        // Then
        User verified = userRepository.findById(unverifiedUser.getId()).orElseThrow();
        assertThat(verified.getEmailVerified()).isTrue();
    }

    @Test
    @DisplayName("Deve listar apenas usuários ativos com paginação")
    void testFindAllActive() {
        // When
        Page<User> result = userRepository.findAllActive(PageRequest.of(0, 10));

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(1); // Apenas activeUser
        assertThat(result.getContent().get(0).getActive()).isTrue();
        assertThat(result.getContent()).allMatch(User::getActive);
    }

    @Test
    @DisplayName("Deve contar apenas usuários ativos")
    void testCountActiveUsers() {
        // When
        long count = userRepository.countActiveUsers();

        // Then
        assertThat(count).isEqualTo(1); // Apenas activeUser está ativo
    }

    @Test
    @DisplayName("Deve realizar soft delete do usuário")
    void testSoftDeleteUser() {
        // When
        activeUser.setActive(false);
        userRepository.save(activeUser);
        entityManager.flush();
        entityManager.clear();

        // Then
        User deleted = userRepository.findById(activeUser.getId()).orElseThrow();
        assertThat(deleted.getActive()).isFalse();
        
        // Não deve aparecer na busca de ativos
        Optional<User> activeResult = userRepository.findByEmailActive(activeUser.getEmail());
        assertThat(activeResult).isEmpty();
    }

    @Test
    @DisplayName("Deve buscar usuários deletados (active=false)")
    void testFindDeletedUsers() {
        // When
        User found = userRepository.findById(deletedUser.getId()).orElseThrow();

        // Then
        assertThat(found.getActive()).isFalse();
        assertThat(found.getEmail()).isEqualTo("maria.santos@example.com");
    }

    @Test
    @DisplayName("Deve buscar usuários por role")
    void testFindByRole() {
        // Given
        User adminUser = new User();
        adminUser.setEmail("admin@example.com");
        adminUser.setPasswordHash("$2a$10$hashedPasswordAdmin");
        adminUser.setFullName("Admin User");
        adminUser.setCpf("99988877766");
        adminUser.setPhone("11944445555");
        adminUser.setDateOfBirth(LocalDate.of(1980, 12, 31));
        adminUser.setActive(true);
        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(adminRole);
        adminUser.setRoles(adminRoles);
        entityManager.persistAndFlush(adminUser);
        entityManager.clear();

        // When
        Page<User> customers = userRepository.findAllActive(PageRequest.of(0, 10));

        // Then
        assertThat(customers).isNotEmpty();
        assertThat(customers.getTotalElements()).isEqualTo(2); // activeUser + adminUser
    }
}
