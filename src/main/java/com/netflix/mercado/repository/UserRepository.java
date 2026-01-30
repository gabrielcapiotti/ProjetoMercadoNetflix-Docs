package com.netflix.mercado.repository;

import com.netflix.mercado.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByCpf(String cpf);

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.active = true")
    Optional<User> findByEmailActive(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.active = true")
    Page<User> findAllActive(Pageable pageable);

    @Query("SELECT COUNT(u) FROM User u WHERE u.active = true")
    long countActiveUsers();

    @Query("SELECT u FROM User u WHERE u.emailVerified = false AND u.active = true ORDER BY u.createdAt DESC")
    List<User> findUnverifiedUsers();

    @Query("SELECT u FROM User u WHERE u.twoFactorEnabled = true AND u.active = true")
    List<User> findUsersWithTwoFactor();

    @Modifying
    @Query("UPDATE User u SET u.lastLogin = :lastLogin WHERE u.id = :userId")
    void updateLastLogin(@Param("userId") Long userId, @Param("lastLogin") LocalDateTime lastLogin);

    @Modifying
    @Query("UPDATE User u SET u.emailVerified = true WHERE u.id = :userId")
    void verifyEmail(@Param("userId") Long userId);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u " +
            "WHERE u.email = :email AND u.id != :userId AND u.active = true")
    boolean existsOtherUserWithEmail(@Param("email") String email, @Param("userId") Long userId);

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);
}
