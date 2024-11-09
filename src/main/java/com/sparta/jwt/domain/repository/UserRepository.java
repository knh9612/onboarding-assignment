package com.sparta.jwt.domain.repository;

import com.sparta.jwt.domain.model.User;

import java.util.Optional;

public interface UserRepository {
    boolean existsByUsername(String username);

    User save(User user);

    Optional<User> findByUsername(String username);
}
