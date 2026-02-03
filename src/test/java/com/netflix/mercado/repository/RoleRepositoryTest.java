package com.netflix.mercado.repository;

import com.netflix.mercado.entity.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes de integração para RoleRepository
 * Coverage: 4 testes
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@DisplayName("RoleRepository Integration Tests")
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Role customerRole;
    private Role adminRole;
    private Role merchantRole;

    @BeforeEach
    void setUp() {
        customerRole = new Role();
        customerRole.setName(Role.RoleName.USER);
        customerRole.setDescription("Cliente do sistema");
        customerRole.setActive(true);
        customerRole = entityManager.persistAndFlush(customerRole);

        adminRole = new Role();
        adminRole.setName(Role.RoleName.ADMIN);
        adminRole.setDescription("Administrador do sistema");
        adminRole.setActive(true);
        adminRole = entityManager.persistAndFlush(adminRole);

        merchantRole = new Role();
        merchantRole.setName(Role.RoleName.SELLER);
        merchantRole.setDescription("Dono de mercado");
        merchantRole.setActive(true);
        merchantRole = entityManager.persistAndFlush(merchantRole);

        entityManager.clear();
    }

    @AfterEach
    void tearDown() {
        entityManager.clear();
    }

    @Test
    @DisplayName("Deve encontrar role por nome com sucesso")
    void testFindByNameSuccess() {
        // When
        Optional<Role> result = roleRepository.findByName(Role.RoleName.USER);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo(Role.RoleName.USER);
        assertThat(result.get().getDescription()).isEqualTo("Cliente do sistema");
        assertThat(result.get().getActive()).isTrue();
    }

    @Test
    @DisplayName("Não deve encontrar role por nome inexistente")
    void testFindByNameNotFound() {
        // When
        Optional<Role> result = roleRepository.findByName(Role.RoleName.MODERATOR);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Deve listar todas as roles ativas")
    void testFindAllActive() {
        // When
        List<Role> result = roleRepository.findAll();

        // Then
        assertThat(result).hasSize(3);
        assertThat(result).extracting(Role::getName)
                .containsExactlyInAnyOrder(Role.RoleName.USER, Role.RoleName.ADMIN, Role.RoleName.SELLER);
        assertThat(result).allMatch(Role::getActive);
    }

    @Test
    @DisplayName("Deve contar total de roles")
    void testCountRoles() {
        // When
        long count = roleRepository.count();

        // Then
        assertThat(count).isEqualTo(3);
    }
}
