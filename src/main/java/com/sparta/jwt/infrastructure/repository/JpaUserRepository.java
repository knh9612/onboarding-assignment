package com.sparta.jwt.infrastructure.repository;

import com.sparta.jwt.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
}
