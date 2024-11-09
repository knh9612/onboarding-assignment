package com.sparta.jwt.application.dto.request;

public record LoginRequestDto(
        String username,
        String password
) {
}
