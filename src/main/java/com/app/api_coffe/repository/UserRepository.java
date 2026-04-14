package com.app.api_coffe.repository;

import com.app.api_coffee.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Busca usuário por username
    Optional<User> findByUsername(String username);

    // Busca por email
    Optional<User> findByEmail(String email);

    // Verifica se username já existe
    boolean existsByUsername(String username);

    // Verificar se email já existe
    boolean existsByEmail(String email);
}
