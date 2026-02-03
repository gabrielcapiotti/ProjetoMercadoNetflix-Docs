package com.netflix.mercado.service;

import com.netflix.mercado.entity.User;
import com.netflix.mercado.entity.Role;
import com.netflix.mercado.entity.AuditLog;
import com.netflix.mercado.exception.ResourceNotFoundException;
import com.netflix.mercado.exception.ValidationException;
import com.netflix.mercado.repository.UserRepository;
import com.netflix.mercado.repository.RoleRepository;
import com.netflix.mercado.repository.AuditLogRepository;
import com.netflix.mercado.dto.auth.RegisterRequest;
import com.netflix.mercado.dto.auth.UserResponse;
import com.netflix.mercado.dto.auth.ChangePasswordRequest;
import com.netflix.mercado.dto.auth.UserUpdateRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.logging.Logger;

/**
 * Service responsável por gerenciar operações relacionadas a usuários.
 * Implementa lógica de negócio para criar, atualizar, buscar e gerenciar usuários.
 */
@Service
@Transactional
public class UserService {

    private static final Logger log = Logger.getLogger(UserService.class.getName());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuditLogRepository auditLogRepository;

    /**
     * Cria um novo usuário a partir de um request de registro.
     *
     * @param request dados do novo usuário
     * @return o usuário criado
     * @throws ValidationException se email já existe
     */
    public User createUser(RegisterRequest request) {
        log.info("Criando novo usuário com email: " + request.getEmail());

        // Validar se email já existe
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warning("Tentativa de criar usuário com email existente: " + request.getEmail());
            throw new ValidationException("Email já cadastrado no sistema");
        }

        // Validar CPF único
        if (userRepository.existsByCpf(request.getCpf())) {
            log.warning("Tentativa de criar usuário com CPF existente: " + request.getCpf());
            throw new ValidationException("CPF já cadastrado no sistema");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setCpf(request.getCpf());
        user.setPhone(request.getPhone());
        user.setActive(true);
        user.setEmailVerified(false);
        user.setTwoFactorEnabled(false);

        // Adicionar role padrão (USER)
        Role userRole = roleRepository.findByName(Role.RoleName.USER)
                .orElseThrow(() -> new ResourceNotFoundException("Role USER não encontrada"));
        user.setRoles(Set.of(userRole));

        user = userRepository.save(user);

        // Registrar no audit log
        auditLogRepository.save(new AuditLog(
                user,
                AuditLog.TipoAcao.CRIACAO,
                "USER",
                user.getId(),
                "Novo usuário criado: " + user.getEmail(),
                null, null, null, null, 201
        ));

        log.info("Usuário criado com sucesso. ID: " + user.getId());
        return user;
    }

    /**
     * Busca um usuário pelo ID.
     *
     * @param id ID do usuário
     * @return o usuário encontrado
     * @throws ResourceNotFoundException se usuário não existe
     */
    @Transactional(readOnly = true)
    public User findUserById(Long id) {
        log.fine("Buscando usuário com ID: " + id );
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warning("Usuário não encontrado com ID: " + id );
                    return new ResourceNotFoundException("Usuário não encontrado com ID: " + id);
                });
    }

    /**
     * Busca um usuário pelo email.
     *
     * @param email email do usuário
     * @return o usuário encontrado
     * @throws ResourceNotFoundException se usuário não existe
     */
    @Transactional(readOnly = true)
    public User findUserByEmail(String email) {
        log.fine("Buscando usuário com email: " + email );
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warning("Usuário não encontrado com email: " + email );
                    return new ResourceNotFoundException("Usuário não encontrado com email: " + email);
                });
    }

    /**
     * Atualiza os dados de um usuário.
     *
     * @param id ID do usuário
     * @param request dados a atualizar
     * @return o usuário atualizado
     * @throws ResourceNotFoundException se usuário não existe
     */
    public User updateUser(Long id, UserUpdateRequest request) {
        log.info("Atualizando usuário com ID: " + id );

        User user = findUserById(id);

        String valoresAnteriores = String.format("nome=%s, telefone=%s", user.getFullName(), user.getPhone());

        // Atualizar campos
        if (request.getNome() != null && !request.getNome().isBlank()) {
            user.setFullName(request.getNome());
        }
        if (request.getTelefone() != null && !request.getTelefone().isBlank()) {
            user.setPhone(request.getTelefone());
        }

        user = userRepository.save(user);

        String valoresNovos = String.format("nome=%s, telefone=%s", user.getFullName(), user.getPhone());

        // Registrar no audit log
        auditLogRepository.save(new AuditLog(
                user,
                AuditLog.TipoAcao.ATUALIZACAO,
                "USER",
                user.getId(),
                "Alterações: " + valoresAnteriores + " -> " + valoresNovos,
                null,
                null,
                null,
                null,
                200
        ));

        log.info("Usuário atualizado com sucesso. ID: " + id );
        return user;
    }

    /**
     * Altera a senha de um usuário.
     *
     * @param id ID do usuário
     * @param request dados da alteração de senha
     * @throws ValidationException se senha atual está incorreta
     */
    public void changePassword(Long id, ChangePasswordRequest request) {
        log.info("Alterando senha do usuário com ID: " + id );

        User user = findUserById(id);

        // Validar senha atual
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            log.warning("Tentativa de alteração de senha com senha atual incorreta para ID: " + id );
            throw new ValidationException("Senha atual está incorreta");
        }

        // Validar que nova senha é diferente da atual
        if (request.getOldPassword().equals(request.getNewPassword())) {
            throw new ValidationException("Nova senha não pode ser igual à senha atual");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Registrar no audit log
        auditLogRepository.save(new AuditLog(user, AuditLog.TipoAcao.ATUALIZACAO, "USER", user.getId(), "Senha alterada", null, null, null, null, 200));

        log.info("Senha alterada com sucesso para usuário ID: " + id );
    }

    /**
     * Habilita autenticação de dois fatores para um usuário.
     *
     * @param id ID do usuário
     */
    public void enableTwoFactor(Long id) {
        log.info("Habilitando 2FA para usuário ID: " + id );

        User user = findUserById(id);
        user.setTwoFactorEnabled(true);
        userRepository.save(user);

        auditLogRepository.save(new AuditLog(user, AuditLog.TipoAcao.ATUALIZACAO, "USER", user.getId(), "Autenticação de dois fatores habilitada", null, null, null, null, 200));

        log.info("2FA habilitado para usuário ID: " + id );
    }

    /**
     * Desabilita autenticação de dois fatores para um usuário.
     *
     * @param id ID do usuário
     */
    public void disableTwoFactor(Long id) {
        log.info("Desabilitando 2FA para usuário ID: " + id );

        User user = findUserById(id);
        user.setTwoFactorEnabled(false);
        userRepository.save(user);

        auditLogRepository.save(new AuditLog(user, AuditLog.TipoAcao.ATUALIZACAO, "USER", user.getId(), "Autenticação de dois fatores desabilitada", null, null, null, null, 200));

        log.info("2FA desabilitado para usuário ID: " + id );
    }

    /**
     * Verifica o email de um usuário.
     *
     * @param id ID do usuário
     */
    public void verifyEmail(Long id) {
        log.info("Verificando email do usuário ID: " + id );

        User user = findUserById(id);
        user.setEmailVerified(true);
        userRepository.save(user);

        auditLogRepository.save(new AuditLog(user, AuditLog.TipoAcao.ATUALIZACAO, "USER", user.getId(), "Email verificado", null, null, null, null, 200));

        log.info("Email verificado para usuário ID: " + id );
    }

    /**
     * Obtém todos os usuários com paginação.
     *
     * @param pageable informações de paginação
     * @return página de usuários
     */
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        log.fine("Buscando todos os usuários com paginação: " + pageable );
        return userRepository.findAll(pageable)
                .map(this::convertToResponse);
    }

    /**
     * Converte entidade User para DTO UserResponse.
     *
     * @param user entidade User
     * @return DTO UserResponse
     */
    private UserResponse convertToResponse(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
        
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getEmail(), // username é o email
                user.getFullName(),
                roleNames,
                user.isActive(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
    public UserService() {
    }

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, AuditLogRepository auditLogRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditLogRepository = auditLogRepository;
    }

    public UserRepository getUserRepository() {
        return this.userRepository;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public RoleRepository getRoleRepository() {
        return this.roleRepository;
    }

    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public PasswordEncoder getPasswordEncoder() {
        return this.passwordEncoder;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public AuditLogRepository getAuditLogRepository() {
        return this.auditLogRepository;
    }

    public void setAuditLogRepository(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

}
