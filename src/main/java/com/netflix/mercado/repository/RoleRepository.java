package com.netflix.mercado.repository;

import com.netflix.mercado.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(Role.RoleName name);

    @Query("SELECT r FROM Role r WHERE r.active = true ORDER BY r.name")
    List<Role> findAllActive();

    boolean existsByName(Role.RoleName name);
}
