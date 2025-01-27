package com.sparta.jwt.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum RoleEnum {
    USER(Authority.USER),
    ADMIN(Authority.ADMIN);

    private final String authorityName;

    @JsonValue
    public String getAuthority() {
        return this.authorityName;
    }

    public static class Authority {
        public static final String USER = "ROLE_USER";
        public static final String ADMIN = "ROLE_ADMIN";
    }
}
