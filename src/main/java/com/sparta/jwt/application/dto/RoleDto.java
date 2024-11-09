package com.sparta.jwt.application.dto;

import lombok.Builder;

@Builder
public record RoleDto(
        String authorityName
) {
}
