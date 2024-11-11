package com.sparta.jwt.application.dto;

import lombok.Builder;

@Builder
public record UserInfoDto(
        String username,
        String role,
        String token
) {
}
