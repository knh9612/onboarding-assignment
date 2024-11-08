package com.sparta.jwt.domain.repository;

import com.sparta.jwt.domain.model.User;

public interface UserRepository {
    boolean existsByUsername(String username);

    User save(User user);
}
