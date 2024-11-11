package com.sparta.jwt.application.dto;

public record ReissuedTokenDto(
        String accessToken,
        String refreshToken
) {
}
